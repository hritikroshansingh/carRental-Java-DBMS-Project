package Rent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import java.sql.*;
import java.time.LocalDate;

public class ReturnCar {

    protected Shell shell;
    private Table table;
    private Text txtCarId;
    private Text txtCustomerId;
    private DateTime dateTime;

    public static void main(String[] args) {
        try {
            ReturnCar window = new ReturnCar();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    protected void createContents() {
        shell = new Shell();
        shell.setSize(600, 400);
        shell.setText("Return Car");
        shell.setLayout(new GridLayout(2, false));

        Label lblCarId = new Label(shell, SWT.NONE);
        lblCarId.setText("Car ID:");

        txtCarId = new Text(shell, SWT.BORDER);
        txtCarId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblCustomerId = new Label(shell, SWT.NONE);
        lblCustomerId.setText("Customer ID:");

        txtCustomerId = new Text(shell, SWT.BORDER);
        txtCustomerId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblReturnDate = new Label(shell, SWT.NONE);
        lblReturnDate.setText("Return Date:");

        dateTime = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
        dateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button btnReturn = new Button(shell, SWT.PUSH);
        btnReturn.setText("Return Car");
        btnReturn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnReturn.addListener(SWT.Selection, e -> handleReturn());
        
     // Cancel Button
        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText("Cancel");
        btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
                Main mainPage = new Main(); // Open main page
                mainPage.open();
            }
        });

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        TableColumn col1 = new TableColumn(table, SWT.NONE);
        col1.setText("Car ID");
        col1.setWidth(100);

        TableColumn col2 = new TableColumn(table, SWT.NONE);
        col2.setText("Customer ID");
        col2.setWidth(100);

        TableColumn col3 = new TableColumn(table, SWT.NONE);
        col3.setText("Return Date");
        col3.setWidth(150);

        tableUpdate();
    }

    private void handleReturn() {
        String carId = txtCarId.getText();
        String customerId = txtCustomerId.getText();

        if (carId.isEmpty() || customerId.isEmpty()) {
            MessageBox msg = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            msg.setMessage("Car ID and Customer ID must not be empty.");
            msg.open();
            return;
        }

        String returnDateStr = String.format("%04d-%02d-%02d",
                dateTime.getYear(), dateTime.getMonth() + 1, dateTime.getDay());

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentcar", "root", "12345");

            // Fetch return_date date from rental table
            String dueQuery = "SELECT return_date FROM car_rentals WHERE car_id = ? AND cust_id = ?";
            PreparedStatement duePst = con.prepareStatement(dueQuery);
            duePst.setString(1, carId);
            duePst.setString(2, customerId);
            ResultSet rs = duePst.executeQuery();

            if (!rs.next()) {
                MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                msg.setMessage("No such rental found. Check Car ID and Customer ID.");
                msg.open();
                return;
            }

            Date dueDate = rs.getDate("return_date");
            LocalDate actualReturnDate = LocalDate.of(dateTime.getYear(), dateTime.getMonth() + 1, dateTime.getDay());
            LocalDate dueLocalDate = dueDate.toLocalDate();

            int elap = 0;
            int fine = 0;
            if (actualReturnDate.isAfter(dueLocalDate)) {
                elap = (int) java.time.temporal.ChronoUnit.DAYS.between(dueLocalDate, actualReturnDate);
                fine = elap * 100; // ₹100 per day
            }

            // Delete from rental
            String deleteQuery = "DELETE FROM car_rentals WHERE car_id = ? AND cust_id = ?";
            PreparedStatement deletePst = con.prepareStatement(deleteQuery);
            deletePst.setString(1, carId);
            deletePst.setString(2, customerId);

            int rows = deletePst.executeUpdate();

            if (rows > 0) {
                // Insert into returncar
                String insertQuery = "INSERT INTO returncar (carid, custid, return_date, elap, fine) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertPst = con.prepareStatement(insertQuery);
                insertPst.setString(1, carId);
                insertPst.setString(2, customerId);
                insertPst.setString(3, returnDateStr);
                insertPst.setInt(4, elap);
                insertPst.setInt(5, fine);
                insertPst.executeUpdate();
                insertPst.close();

                MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                msg.setMessage("Car returned successfully.\nLate days: " + elap + "\nFine: ₹" + fine);
                msg.open();
                tableUpdate();
            } else {
                MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                msg.setMessage("Failed to return car. Try again.");
                msg.open();
            }

            rs.close();
            duePst.close();
            deletePst.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            msg.setMessage("Database error: " + ex.getMessage());
            msg.open();
        }
    }



    private void tableUpdate() {
        try {
            table.removeAll();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentcar", "root", "12345");
            String query = "SELECT car_id, cust_id, return_date FROM car_rentals WHERE return_date IS NOT NULL";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(new String[]{
                        rs.getString("car_id"),
                        rs.getString("cust_id"),
                        rs.getString("return_date")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
