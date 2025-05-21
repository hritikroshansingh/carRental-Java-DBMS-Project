package Rent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.sql.*;
import java.time.LocalDate;

public class Rent_a_car {

    protected Shell shell;
    private Text txtCarID;
    private Text txtCustomerID;
    private Text txtFee;  // Add a Text field for the fee input
    private DateTime dateRentDate;
    private DateTime dateReturnDate;

    public static void main(String[] args) {
        Display display = new Display();
        Rent_a_car window = new Rent_a_car();
        window.open(display);
        display.dispose();
    }

    public void open(Display display) {
        createContents(display);
        shell.open();
        shell.layout();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    protected void createContents(Display display) {
        shell = new Shell(display);
        shell.setSize(550, 400);
        shell.setText("Rent a Car");
        shell.setLayout(new GridLayout(2, false));

        // Car ID
        Label lblCarID = new Label(shell, SWT.NONE);
        lblCarID.setText("Car ID:");
        txtCarID = new Text(shell, SWT.BORDER);
        txtCarID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Customer ID
        Label lblCustomerID = new Label(shell, SWT.NONE);
        lblCustomerID.setText("Customer ID:");
        txtCustomerID = new Text(shell, SWT.BORDER);
        txtCustomerID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Fee
        Label lblFee = new Label(shell, SWT.NONE);
        lblFee.setText("Fee:");
        txtFee = new Text(shell, SWT.BORDER);
        txtFee.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Rent Date Label
        Label lblRentDate = new Label(shell, SWT.NONE);
        lblRentDate.setText("Rent Date:");
        dateRentDate = new DateTime(shell, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
        dateRentDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Return Date Label
        Label lblReturnDate = new Label(shell, SWT.NONE);
        lblReturnDate.setText("Return Date:");
        dateReturnDate = new DateTime(shell, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
        dateReturnDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Spacer
        new Label(shell, SWT.NONE);

        // Submit Button
        Button btnSubmit = new Button(shell, SWT.PUSH);
        btnSubmit.setText("Submit");
        btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
     // Spacer
        new Label(shell, SWT.NONE);
        
     // Cancel Button
        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText("Cancel");
        btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
                display.dispose();
                Main mainPage = new Main(); // Open main page
                mainPage.open();
            }
        });

        // Add selection listener to the button
        btnSubmit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String carID = txtCarID.getText().trim();
                String customerID = txtCustomerID.getText().trim();
                String fee = txtFee.getText().trim();

                // Get rent and return dates from DateTime widgets
                int rentDay = dateRentDate.getDay();
                int rentMonth = dateRentDate.getMonth() + 1; // Months are 0-based, so add 1
                int rentYear = dateRentDate.getYear();

                int returnDay = dateReturnDate.getDay();
                int returnMonth = dateReturnDate.getMonth() + 1;
                int returnYear = dateReturnDate.getYear();

                // Convert dates to LocalDate format
                LocalDate rentDate = LocalDate.of(rentYear, rentMonth, rentDay);
                LocalDate returnDate = LocalDate.of(returnYear, returnMonth, returnDay);

                // Display the data in the console
//                System.out.println("Car ID: " + carID);
//                System.out.println("Customer ID: " + customerID);
//                System.out.println("Fee: " + fee);
//                System.out.println("Rent Date: " + rentDate);
//                System.out.println("Return Date: " + returnDate);

                // Insert into the database
                insertRentDetails(carID, customerID, fee, rentDate, returnDate);

                // Confirmation MessageBox
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                messageBox.setMessage("Car Rent Submitted Successfully!");
                messageBox.open();
            }
        });
    }

    // Method to insert rent details into MySQL
    private void insertRentDetails(String carID, String customerID, String fee, LocalDate rentDate, LocalDate returnDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Establish connection to the MySQL database
            String url = "jdbc:mysql://localhost:3306/rentcar"; // Change to your database URL
            String username = "root"; // Change to your MySQL username
            String password = "12345"; // Change to your MySQL password

            connection = DriverManager.getConnection(url, username, password);

            // SQL query to insert rent details
            String query = "INSERT INTO car_rentals (car_id, cust_id, fee, rent_date, return_date) VALUES (?, ?, ?, ?, ?)";

            // Create prepared statement
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, carID);
            preparedStatement.setString(2, customerID);
            preparedStatement.setString(3, fee);
            preparedStatement.setDate(4, java.sql.Date.valueOf(rentDate));
            preparedStatement.setDate(5, java.sql.Date.valueOf(returnDate));

            // Execute the update
            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Rent details inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            messageBox.setMessage("Error inserting rent details into database.");
            messageBox.open();
        } finally {
            // Close the resources
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
