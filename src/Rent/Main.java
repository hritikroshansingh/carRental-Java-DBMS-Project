package Rent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Main {
    protected Shell shell;

    public static void main(String[] args) {
        try {
            Main window = new Main();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        Display display = Display.getDefault();
        shell = new Shell(display);
        shell.setSize(400, 400);
        shell.setText("Main Menu");

        createContents();
        shell.open();
        shell.layout();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createContents() {
        shell.setLayout(new GridLayout(1, false));

        // Title Label
        Label lblTitle = new Label(shell, SWT.NONE);
        lblTitle.setText("Main Menu");
        lblTitle.setFont(new org.eclipse.swt.graphics.Font(Display.getDefault(), "Tibetan Machine Uni", 24, SWT.BOLD));
        lblTitle.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        // Create Buttons
        Button btnCarRegistration = new Button(shell, SWT.PUSH);
        btnCarRegistration.setText("Car Registration");
        btnCarRegistration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnCarRegistration.addListener(SWT.Selection, e -> openCarRegistration());

        Button btnCustomerRegistration = new Button(shell, SWT.PUSH);
        btnCustomerRegistration.setText("Customer Registration");
        btnCustomerRegistration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnCustomerRegistration.addListener(SWT.Selection, e -> openCustomerRegistration());

        Button btnRental = new Button(shell, SWT.PUSH);
        btnRental.setText("Rental");
        btnRental.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnRental.addListener(SWT.Selection, e -> openRentCar());

        Button btnReturn = new Button(shell, SWT.PUSH);
        btnReturn.setText("Return");
        btnReturn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnReturn.addListener(SWT.Selection, e -> openReturnCar());

        Button btnLogout = new Button(shell, SWT.PUSH);
        btnLogout.setText("Logout");
        btnLogout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnLogout.addListener(SWT.Selection, e -> logout());
    }

    public void openCarRegistration() {
        Display display = Display.getCurrent();  // Get the current Display instance
        CarRegistration window = new CarRegistration();
        window.open(display);
        display.dispose();
    }


    private void openCustomerRegistration() {
    	Display display = Display.getCurrent();
        CustomerRegistration customerReg = new CustomerRegistration();
        customerReg.open(display);
        display.dispose();
    }

    private void openRentCar() {
    	Display display = Display.getCurrent();
        Rent_a_car window = new Rent_a_car();
        window.open(display);
        display.dispose();
    }

    private void openReturnCar() {
        ReturnCar returnCar = new ReturnCar();
        returnCar.open();
    }

    private void logout() {
    	Display display = Display.getCurrent();
//        Start_Page window = new Start_Page();
//        window.open(display);
        display.dispose();
        shell.dispose(); // Close current window
    }
}
