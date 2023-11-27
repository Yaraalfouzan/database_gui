import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class SQL_GUI extends JFrame {
    private JLabel positionLabel, idLabel;
    private JComboBox<String> positionComboBox;
    private JTextField idTextField;
    private JButton loginButton, updatePointsButton, generateInvoiceButton, showStockButton, contactSupplierButton, trackEmployeesButton;
    private JTabbedPane tabbedPane;
    private Connection connection;

    public SQL_GUI() {
        // Set up the frame
        setTitle("Position Selection");
        setSize(600, 400); // Increased size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        positionLabel = new JLabel("Select Your Position:");
        idLabel = new JLabel("Enter Your ID:");
        positionComboBox = new JComboBox<>(new String[]{"Cashier", "Store Manager"});
        idTextField = new JTextField(20); // Set the size of the text field
        loginButton = new JButton("Log In");

        // Buttons for Cashier
        updatePointsButton = new JButton("Update Points");
        generateInvoiceButton = new JButton("Generate Invoice");

        // Buttons for Store Manager
        showStockButton = new JButton("Show Product Stock");
        contactSupplierButton = new JButton("Contact Supplier");
        trackEmployeesButton = new JButton("Track Employees");

        // Set layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased spacing

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
        loginButton.setForeground(Color.BLACK); // Set text color to black
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setFocusPainted(false);

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String position = (String) positionComboBox.getSelectedItem();
                String id = idTextField.getText();

                // Validate ID input
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(SQL_GUI.this,
                            "Please enter your ID.",
                            "ID Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // Perform actions based on selected position and entered ID
                    handlePositionActions(position);
                }
            }
        });
    }
    

    private void initializeDatabaseConnection() {
        // Database connection parameters
        String url = "jdbc:mysql://127.0.0.1:3306/whatever";
        String username = "root";
        String password = "";

        try {
            // Connect to the database
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle database connection errors
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

        // Set up the main frame with the tabbed pane
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

        generateInvoiceButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                generateInvoice();
            }
        });
        cashierPanel.add(generateInvoiceButton);

        tabbedPane.addTab("Cashier", cashierPanel);
    }
    private void generateInvoice() {
    String customerID = idTextField.getText(); // Assuming the entered ID is the customer's ID

    // Fetch customer information and points from the database
    String fetchCustomerQuery = "SELECT * FROM Account WHERE userName = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchCustomerQuery)) {
        preparedStatement.setString(1, customerID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String accountPhone = resultSet.getString("acc_phonenum");
            int accountPoints = resultSet.getInt("acc_points");

            // Your logic to generate the invoice based on customer information
            // Fetch the purchased items and their details from the database
            // Update the invoice table in the database with relevant information

            // Update customer points (considering 10 SR spent earns 1 point)
            int invoiceAmount = 100;
            int pointsEarned = calculatePointsEarnedForInvoiceAmount(invoiceAmount);
            int updatedPoints = accountPoints + pointsEarned;

            // Update the customer's points in the database
            String updatePointsQuery = "UPDATE Account SET acc_points = ? WHERE userName = ?";
            try (PreparedStatement updatePointsStatement = connection.prepareStatement(updatePointsQuery)) {
                updatePointsStatement.setInt(1, updatedPoints);
                updatePointsStatement.setString(2, customerID);
                updatePointsStatement.executeUpdate();
            }

            JOptionPane.showMessageDialog(SQL_GUI.this, "Invoice generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error generating invoice: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
private int calculatePointsEarnedForInvoiceAmount(int invoiceAmount) {
    // Assuming 10 SR spent earns 1 point
    int pointsPer10SR = 1;
    return invoiceAmount / 10 * pointsPer10SR;
}

    private void showStoreManagerButtons() {
        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new FlowLayout());

        managerPanel.add(showStockButton);
        managerPanel.add(contactSupplierButton);
        managerPanel.add(trackEmployeesButton);

        tabbedPane.addTab("Store Manager", managerPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                // Use the Event Dispatch Thread (EDT) for Swing components
                new SQL_GUI().setVisible(true);
            }
        });
                }
}
    