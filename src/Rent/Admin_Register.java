package Rent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import java.sql.*;

public class Admin_Register {

    public void open(Display display) {
        Shell shell = new Shell(display);
        shell.setText("Admin Registration");
        shell.setSize(300, 250);
        shell.setLayout(new GridLayout(2, false));

        new Label(shell, SWT.NONE).setText("Username:");
        Text username = new Text(shell, SWT.BORDER);

        new Label(shell, SWT.NONE).setText("Password:");
        Text password = new Text(shell, SWT.BORDER | SWT.PASSWORD);

        new Label(shell, SWT.NONE); // for spacing

        Button register = new Button(shell, SWT.PUSH);
        register.setText("Register");

        register.addListener(SWT.Selection, e -> {
            String user = username.getText();
            String pass = password.getText();

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO login (username, password) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, user);
                stmt.setString(2, pass);
                stmt.executeUpdate();

                MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
                box.setMessage("Registered successfully!");
                box.open();
                shell.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        shell.open();
    }
}
