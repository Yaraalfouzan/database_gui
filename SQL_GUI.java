import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class SQL_GUI extends JFrame {
<<<<<<< HEAD
    
=======
    //components for GUI
>>>>>>> 7b9c52b2f1265dc75147895524925d6b1403bc82
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
    
    //method to intialize db connection
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
            //int accountPoints = retrievePointsFromAccount(customerID);


            // Your logic to generate the invoice based on customer information
            // Fetch the purchased items and their details from the database
            // Update the invoice table in the database with relevant information

            // Update customer points (considering 10 SR spent earns 1 point)
             // Read invoice amount from the user
             String invoiceAmountString = JOptionPane.showInputDialog(SQL_GUI.this, "Enter Invoice Amount:");
             if (invoiceAmountString == null || invoiceAmountString.isEmpty()) {
                 // Handle case where the user cancels or enters an empty string
                 JOptionPane.showMessageDialog(SQL_GUI.this, "Invoice generation canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
                 return;
             }
 
             int invoiceAmount = Integer.parseInt(invoiceAmountString);
 
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
private int retrievePointsFromAccount(String customerID) {
    int points = 0;

    // Fetch customer points from the database
    String fetchPointsQuery = "SELECT acc_points FROM Account WHERE userName = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchPointsQuery)) {
        preparedStatement.setString(1, customerID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            points = resultSet.getInt("acc_points");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        // Handle database query errors...
    }

    return points;
}


    private void showStoreManagerButtons() {
        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new FlowLayout());

        managerPanel.add(showStockButton);
        managerPanel.add(contactSupplierButton);
        managerPanel.add(trackEmployeesButton);

        tabbedPane.addTab("Store Manager", managerPanel);
    }

    private void addNewProduct(String productID, int productQuantity, String productBrand, double price,
    String productType, String expiryDate, String productionDate, String supplierID) {
// Insert a new product with the provided information
String insertProductQuery = "INSERT INTO Product (P_id, P_quantity, P_brand, price, P_type, Ex_date, Pro_date, S_ID) " +
"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
try (PreparedStatement preparedStatement = connection.prepareStatement(insertProductQuery)) {
preparedStatement.setString(1, productID);
preparedStatement.setInt(2, productQuantity);
preparedStatement.setString(3, productBrand);
preparedStatement.setDouble(4, price);
preparedStatement.setString(5, productType);
preparedStatement.setString(6, expiryDate);
preparedStatement.setString(7, productionDate);
preparedStatement.setString(8, supplierID);

int rowsInserted = preparedStatement.executeUpdate();

if (rowsInserted > 0) {
JOptionPane.showMessageDialog(SQL_GUI.this, "New product added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
} else {
JOptionPane.showMessageDialog(SQL_GUI.this, "Error adding new product.", "Error", JOptionPane.ERROR_MESSAGE);
}
} catch (SQLException ex) {
ex.printStackTrace();
JOptionPane.showMessageDialog(SQL_GUI.this, "Error adding new product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}
}
private void updateShift(String employeeID, String newShift) {
    // Update the shift for the given Employee ID
    String updateShiftQuery = "UPDATE Employee SET E_Shift = ? WHERE E_ID = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(updateShiftQuery)) {
        preparedStatement.setString(1, newShift);
        preparedStatement.setString(2, employeeID);
        int rowsUpdated = preparedStatement.executeUpdate();

        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Shift updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Employee not found or shift not updated.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error updating shift: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
private void trackEmployees(String employeeID) {
    // Fetch employee information based on the given Employee ID
    String fetchEmployeeQuery = "SELECT * FROM Employee WHERE E_ID = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchEmployeeQuery)) {
        preparedStatement.setString(1, employeeID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String employeeName = resultSet.getString("E_name");
            String employeePhone = resultSet.getString("E_phonenum");
            double employeeSalary = resultSet.getDouble("E_salary");
            String employeePosition = resultSet.getString("E_Position");
            String employeeShift = resultSet.getString("E_Shift");

            // Display or use the retrieved employee information as needed
            JOptionPane.showMessageDialog(SQL_GUI.this, "Employee Info:\nName: " + employeeName +
                    "\nPhone: " + employeePhone +
                    "\nSalary: " + employeeSalary +
                    "\nPosition: " + employeePosition +
                    "\nShift: " + employeeShift, "Employee Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error fetching employee information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
private void contactSupplier(String branchCity) {
    // Fetch supplier information for the given branch city
    String fetchSupplierQuery = "SELECT DISTINCT S.* FROM Supplier S " +
            "JOIN Product P ON S.S_ID = P.S_ID " +
            "JOIN Branch B ON P.B_id = B.B_id " +
            "WHERE B.city = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchSupplierQuery)) {
        preparedStatement.setString(1, branchCity);
        ResultSet resultSet = preparedStatement.executeQuery();

        StringBuilder supplierInfo = new StringBuilder();
        while (resultSet.next()) {
            String supplierID = resultSet.getString("S_ID");
            String supplierName = resultSet.getString("S_name");
            String supplierPhone = resultSet.getString("S_phone");
            String supplierLocation = resultSet.getString("S_location");

            supplierInfo.append("Supplier ID: ").append(supplierID).append(", ");
            supplierInfo.append("Name: ").append(supplierName).append(", ");
            supplierInfo.append("Phone: ").append(supplierPhone).append(", ");
            supplierInfo.append("Location: ").append(supplierLocation).append("\n");
        }

        if (supplierInfo.length() > 0) {
            // Display or use the retrieved supplier information as needed
            JOptionPane.showMessageDialog(SQL_GUI.this, "Suppliers for Branch in " + branchCity + ":\n" + supplierInfo.toString(), "Supplier Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "No suppliers found for the given branch city.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error fetching supplier information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
private void showProductStock(String productID) {
    // Fetch product information based on the given Product ID
    String fetchProductQuery = "SELECT * FROM Product WHERE P_id = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchProductQuery)) {
        preparedStatement.setString(1, productID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String productType = resultSet.getString("P_type");
            int productQuantity = resultSet.getInt("P_quantity");
            String productBrand = resultSet.getString("P_brand");
            double productPrice = resultSet.getDouble("price");
            String expiryDate = resultSet.getString("Ex_date");
            String productionDate = resultSet.getString("Pro_date");
            String supplierID = resultSet.getString("S_ID");

            // Display or use the retrieved product information as needed
            JOptionPane.showMessageDialog(SQL_GUI.this, "Product Info:\nType: " + productType +
                    "\nQuantity: " + productQuantity +
                    "\nBrand: " + productBrand +
                    "\nPrice: " + productPrice +
                    "\nExpiry Date: " + expiryDate +
                    "\nProduction Date: " + productionDate +
                    "\nSupplier ID: " + supplierID, "Product Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error fetching product information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
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
    