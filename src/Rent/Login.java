package Rent;

import java.sql.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class Login {
    protected Shell shell;

    public static void main(String[] args) {
        try {
            Login window = new Login();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        Display display = Display.getDefault();
        shell = new Shell(display);
        shell.setSize(400, 300);
        shell.setText("Admin Login");

        createContents();
        shell.open();
        shell.layout();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    private void createContents() {
        Label lblUsername = new Label(shell, SWT.NONE);
        lblUsername.setText("Username:");
        lblUsername.setBounds(50, 60, 100, 25);

        Text txtUsername = new Text(shell, SWT.BORDER);
        txtUsername.setBounds(160, 60, 180, 25);

        Label lblPassword = new Label(shell, SWT.NONE);
        lblPassword.setText("Password:");
        lblPassword.setBounds(50, 100, 100, 25);

        Text txtPassword = new Text(shell, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setBounds(160, 100, 180, 25);

        Button btnLogin = new Button(shell, SWT.PUSH);
        btnLogin.setText("Login");
        btnLogin.setBounds(150, 160, 100, 30);

        btnLogin.addListener(SWT.Selection, e -> {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT * FROM login WHERE username = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        box.setText("Success");
                        box.setMessage("Login successful!");
                        box.open();

                        shell.dispose(); // Close login window
                        Main mainPage = new Main(); // Open main page
                        mainPage.open();
                    } else {
                        MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                        box.setText("Error");
                        box.setMessage("Invalid credentials!");
                        box.open();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                box.setMessage("Database error: " + ex.getMessage());
                box.open();
            }
        });   
    }
}
