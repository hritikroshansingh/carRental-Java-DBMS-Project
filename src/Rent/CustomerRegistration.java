package Rent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import java.sql.*;
import org.eclipse.swt.custom.*;

public class CustomerRegistration {

    private Shell shell;
    private Table table;
    private Text textCustomerID, textCustomerName, textMobile;
    private Text textAddress;

    public static void main(String[] args) {
        try {
        	Display display = new Display();
            CustomerRegistration window = new CustomerRegistration();
            window.open(display);
            display.dispose();
            }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(Display display) {
        shell = new Shell(display);
        shell.setSize(600, 400);
        shell.setText("Customer Registration");

        shell.setLayout(new GridLayout(2, false));

        // Customer ID
        new Label(shell, SWT.NONE).setText("Customer ID");
        textCustomerID = new Text(shell, SWT.BORDER);
        textCustomerID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Customer Name
        new Label(shell, SWT.NONE).setText("Customer Name");
        textCustomerName = new Text(shell, SWT.BORDER);
        textCustomerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Address
        new Label(shell, SWT.NONE).setText("Address");
        textAddress = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData addressData = new GridData(SWT.FILL, SWT.FILL, true, true);
        textAddress.setLayoutData(addressData);

        // Mobile
        new Label(shell, SWT.NONE).setText("Mobile");
        textMobile = new Text(shell, SWT.BORDER);
        textMobile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Table for displaying customers
        table = new Table(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        table.setHeaderVisible(true);
        String[] titles = { "Customer ID", "Customer Name", "Address", "Mobile" };
        for (String title : titles) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title);
            column.pack();
        }

        // Add Button
        Button btnAdd = new Button(shell, SWT.PUSH);
        btnAdd.setText("Add");
        btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addCustomer();
            }
        });

        // Cancel Button
        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText("Cancel");
        btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
                display.dispose();
                Main mainPage = new Main(); // Open main page
                mainPage.open();
            }
        });

        // Load the customer data into the table
        loadCustomerData();

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    private void loadCustomerData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentcar", "root", "12345");
             PreparedStatement pst = con.prepareStatement("SELECT * FROM customer;");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(new String[] {
                        rs.getString("cust_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("mobile")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addCustomer() {
        String custID = textCustomerID.getText();
        String custName = textCustomerName.getText();
        String address = textAddress.getText();
        String mobile = textMobile.getText();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rentcar", "root", "12345");
             PreparedStatement pst = con.prepareStatement("INSERT INTO customer(cust_id, name, address, mobile) VALUES (?, ?, ?, ?)")) {

            pst.setString(1, custID);
            pst.setString(2, custName);
            pst.setString(3, address);
            pst.setString(4, mobile);
            pst.executeUpdate();
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            messageBox.setMessage("Customer Added Successfully!");
            messageBox.open();

            textCustomerID.setText("");
            textCustomerName.setText("");
            textAddress.setText("");
            textMobile.setText("");

            loadCustomerData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            messageBox.setMessage("Error while adding customer");
            messageBox.open();
        }
    }
}
