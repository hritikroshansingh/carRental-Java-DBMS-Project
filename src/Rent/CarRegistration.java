package Rent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import java.sql.*;

public class CarRegistration {
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;

    private Text txtCarNo, txtBrand, txtModel;
    private Combo comboAvailable;
    private Table table;

    public void open(Display display) {
        Shell shell = new Shell(display);
        shell.setText("Car Registration");
        shell.setSize(650, 500);
        shell.setLayout(new GridLayout(2, false));

        Label lblCarNo = new Label(shell, SWT.NONE);
        lblCarNo.setText("Car No:");
        txtCarNo = new Text(shell, SWT.BORDER);
        txtCarNo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtCarNo.setEditable(false);

        Label lblBrand = new Label(shell, SWT.NONE);
        lblBrand.setText("Brand:");
        txtBrand = new Text(shell, SWT.BORDER);
        txtBrand.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label lblModel = new Label(shell, SWT.NONE);
        lblModel.setText("Model:");
        txtModel = new Text(shell, SWT.BORDER);
        txtModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label lblAvailable = new Label(shell, SWT.NONE);
        lblAvailable.setText("Available:");
        comboAvailable = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboAvailable.setItems(new String[]{"Yes", "No"});
        comboAvailable.select(0);
        comboAvailable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite buttonComp = new Composite(shell, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
        buttonComp.setLayout(new RowLayout());

        Button btnAdd = new Button(buttonComp, SWT.PUSH);
        btnAdd.setText("Add");

        Button btnUpdate = new Button(buttonComp, SWT.PUSH);
        btnUpdate.setText("Update");

        Button btnDelete = new Button(buttonComp, SWT.PUSH);
        btnDelete.setText("Delete");

        Button btnCancel = new Button(buttonComp, SWT.PUSH);
        btnCancel.setText("Cancel");
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
                display.dispose();
                Main mainPage = new Main(); // Open main page
                mainPage.open();
            }
        });
        
//        Button btnCancel = new Button(buttonComp, SWT.PUSH);
//        btnCancel.setText("Cancel");
//        btnCancel.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//            	shell.dispose();
//                Login login = new Login();
//                login.open();
//            }
//        });

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        String[] headers = {"Car No", "Brand", "Model", "Available"};
        for (String header : headers) {
            TableColumn col = new TableColumn(table, SWT.NONE);
            col.setText(header);
            col.pack();
        }

        // Events
        btnAdd.addListener(SWT.Selection, e -> addCar());
        btnUpdate.addListener(SWT.Selection, e -> updateCar());
        btnDelete.addListener(SWT.Selection, e -> deleteCar());
        table.addListener(SWT.Selection, e -> populateFields());

        connect();
        autoID();
        loadTable();

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        closeConnection(); // Close DB connection when shell closes
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentcar", "root", "12345");
        } catch (Exception e) {
            showError("Database Connection Failed", e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void autoID() {
        try {
            pst = con.prepareStatement("SELECT MAX(car_no) FROM carregistration");
            rs = pst.executeQuery();
            if (rs.next()) {
                String maxId = rs.getString(1);
                if (maxId == null) {
                    txtCarNo.setText("C0001");
                } else {
                    long id = Long.parseLong(maxId.substring(2)) + 1;
                    txtCarNo.setText("C0" + String.format("%03d", id));
                }
            }
        } catch (Exception e) {
            showError("Error Generating Car ID", e.getMessage());
        }
    }

    private void loadTable() {
        table.removeAll();
        try {
            pst = con.prepareStatement("SELECT * FROM carregistration");
            rs = pst.executeQuery();
            while (rs.next()) {
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(new String[]{
                        rs.getString("car_no"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("available")
                });
            }
            for (TableColumn col : table.getColumns()) col.pack();
        } catch (Exception e) {
            showError("Error Loading Data", e.getMessage());
        }
    }

    private void addCar() {
        try {
            pst = con.prepareStatement("INSERT INTO carregistration (car_no, brand, model, available) VALUES (?, ?, ?, ?)");
            pst.setString(1, txtCarNo.getText());
            pst.setString(2, txtBrand.getText());
            pst.setString(3, txtModel.getText());
            pst.setString(4, comboAvailable.getText());
            pst.executeUpdate();
            MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
            msg.setMessage("Car Added Successfully!");
            msg.open();
            clearForm();
            loadTable();
            autoID();
        } catch (Exception e) {
            showError("Error Adding Car", e.getMessage());
        }
    }

    private void updateCar() {
        try {
            TableItem[] selection = table.getSelection();
            if (selection.length == 0) return;
            pst = con.prepareStatement("UPDATE carregistration SET brand = ?, model = ?, available = ? WHERE car_no = ?");
            pst.setString(1, txtBrand.getText());
            pst.setString(2, txtModel.getText());
            pst.setString(3, comboAvailable.getText());
            pst.setString(4, txtCarNo.getText());
            pst.executeUpdate();
            MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
            msg.setMessage("Car Updated!");
            msg.open();
            clearForm();
            loadTable();
            autoID();
        } catch (Exception e) {
            showError("Error Updating Car", e.getMessage());
        }
    }

    private void deleteCar() {
        try {
            TableItem[] selection = table.getSelection();
            if (selection.length == 0) return;
            pst = con.prepareStatement("DELETE FROM carregistration WHERE car_no = ?");
            pst.setString(1, txtCarNo.getText());
            pst.executeUpdate();
            MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
            msg.setMessage("Car Deleted!");
            msg.open();
            clearForm();
            loadTable();
            autoID();
        } catch (Exception e) {
            showError("Error Deleting Car", e.getMessage());
        }
    }

    private void populateFields() {
        TableItem[] selection = table.getSelection();
        if (selection.length == 0) return;
        txtCarNo.setText(selection[0].getText(0));
        txtBrand.setText(selection[0].getText(1));
        txtModel.setText(selection[0].getText(2));
        comboAvailable.setText(selection[0].getText(3));
    }

    private void clearForm() {
        txtBrand.setText("");
        txtModel.setText("");
        comboAvailable.select(0);
    }

    private void showError(String title, String message) {
        MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
        msg.setText(title);
        msg.setMessage(message);
        msg.open();
    }

    public static void main(String[] args) {
        Display display = new Display(); // ✅ Safe Display creation
        CarRegistration window = new CarRegistration();
        window.open(display);
        display.dispose(); // ✅ Dispose after shell is closed
    }
}
