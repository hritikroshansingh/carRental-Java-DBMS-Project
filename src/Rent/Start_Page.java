package Rent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class Start_Page {

    protected Shell shell;

    public static void main(String[] args) {
        Display display = new Display();
        Start_Page window = new Start_Page();
        window.open(display);
        display.dispose();
    }

    public void open(Display display) {
        shell = new Shell(display);
        shell.setSize(400, 300);
        shell.setText("Car Rental Portal");
        shell.setLayout(new GridLayout(1, false));

        Label title = new Label(shell, SWT.CENTER);
        title.setText("Car Rental Portal");
        title.setFont(display.getSystemFont());

        Group group = new Group(shell, SWT.NONE);
        group.setText("Welcome");
        group.setLayout(new GridLayout(2, false));

        Label lblNewAdmin = new Label(group, SWT.NONE);
        lblNewAdmin.setText("New Admin?");

        Button btnRegister = new Button(group, SWT.PUSH);
        btnRegister.setText("Register");
        btnRegister.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Admin_Register register = new Admin_Register();
                register.open(display);
            }
        });

        Label lblLogin = new Label(group, SWT.NONE);
        lblLogin.setText("Already a Member?");

        Button btnLogin = new Button(group, SWT.PUSH);
        btnLogin.setText("Log In");
        btnLogin.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Login login = new Login();
                login.open();
            }
        });

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}
