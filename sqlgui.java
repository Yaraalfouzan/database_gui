import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class sqlgui extends JFrame {
    private JLabel positionLabel, idLabel;
    private JComboBox<String> positionComboBox;
    private JTextField idTextField;
    private JButton loginButton;
    private JTabbedPane tabbedPane;
    private Connection connection;

    public sqlgui() {
        // Set up the frame
        setTitle("Position Selection");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        positionLabel = new JLabel("Select Your Position:");
        idLabel = new JLabel("Enter Your ID:");
        positionComboBox = new JComboBox<>(new String[]{"Cashier", "Store Manager"});
        idTextField = new JTextField(20);
        loginButton = new JButton("Log In");

        // Set layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Add components to the frame with GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(positionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(positionComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(idTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Customize the button
        loginButton.setForeground(Color.BLACK);
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setFocusPainted(false);

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String position = (String) positionComboBox.getSelectedItem();
                String id = idTextField.getText();

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(sqlgui.this,
                            "Please enter your ID.",
                            "ID Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    handlePositionActions(position);
                }
            }
        });
    }

    private void initializeDatabaseConnection() {
        String url = "jdbc:mariadb://localhost:3306/your_database";
        String username = "your_username";
        String password = "your_password";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);

            JOptionPane.showMessageDialog(this, "Connected to the database.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePositionActions(String position) {
        initializeDatabaseConnection();

        tabbedPane = new JTabbedPane();

        switch (position) {
            case "Cashier":
                showCashierButtons();
                break;
            case "Store Manager":
                showStoreManagerButtons();
                break;
        }

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        setTitle("Logged in as: " + position);
        revalidate();
        repaint();
    }

    private void showCashierButtons() {
        JPanel cashierPanel = new JPanel();
        cashierPanel.setLayout(new FlowLayout());

        // Add labels and text fields for invoice number and total price
        JLabel invoiceNumberLabel = new JLabel("Invoice Number:");
        JTextField invoiceNumberTextField = new JTextField(10);
        JLabel usernameLabel = new JLabel("Customer Username:");
        JTextField usernameTextField = new JTextField(10);
        JLabel totalPriceLabel = new JLabel("Total Price:");
        JTextField totalPriceTextField = new JTextField(10);

        cashierPanel.add(usernameLabel);
        cashierPanel.add(usernameTextField);
        cashierPanel.add(invoiceNumberLabel);
        cashierPanel.add(invoiceNumberTextField);
        cashierPanel.add(totalPriceLabel);
        cashierPanel.add(totalPriceTextField);

        JButton generateInvoiceButton = new JButton("Generate Invoice");
        generateInvoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int invoiceNumber = Integer.parseInt(invoiceNumberTextField.getText());
                int totalPrice = Integer.parseInt(totalPriceTextField.getText());
                generateInvoice(usernameTextField.getText(), invoiceNumber, totalPrice);
            }
        });

        cashierPanel.add(generateInvoiceButton);
        tabbedPane.addTab("Cashier", cashierPanel);
    }

    private void generateInvoice(String userName, int invoiceNumber, int totalPrice) {
        // Your existing code for generating an invoice...

        // Make sure to use the 'connection' variable for database operations
    }

    private void showStoreManagerButtons() {
        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new FlowLayout());

        JButton showStockButton = new JButton("Show Product Stock");
        JButton contactSupplierButton = new JButton("Contact Supplier");
        JButton trackEmployeesButton = new JButton("Track Employees");

        managerPanel.add(showStockButton);
        managerPanel.add(contactSupplierButton);
        managerPanel.add(trackEmployeesButton);

        tabbedPane.addTab("Store Manager", managerPanel);
    }

    // Your other existing methods...

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SQL_GUI().setVisible(true);
            }
        });
    }
}
