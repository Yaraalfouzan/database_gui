import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SQL_GUI extends JFrame {
    private JLabel positionLabel, idLabel;
    private JComboBox<String> positionComboBox;
    private JTextField idTextField;
    private JButton loginButton,retrievePointsButton, generateInvoiceButton,insertProductButton, showStockButton, contactSupplierButton, trackEmployeesButton;
    private JTabbedPane tabbedPane;
    private Connection connection;
    public SQL_GUI() {

        setTitle("Position Selection");
        setSize(600, 400); // Increased size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        positionLabel = new JLabel("Select Your Position:");
        idLabel = new JLabel("Enter Your ID:");
        positionComboBox = new JComboBox<>(new String[]{"Cashier", "Store Manager"});
        idTextField = new JTextField(20); // Set the size of the text field
        loginButton = new JButton("Log In");
       
        // Buttons for Cashier
        generateInvoiceButton = new JButton("Generate Invoice");
        retrievePointsButton = new JButton("Retrieve Points");
       
        // Buttons for Store Manager
        showStockButton = new JButton("Show Product Stock");
        contactSupplierButton = new JButton("Contact Supplier");
        trackEmployeesButton = new JButton("Track Employees");

        // Set layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased spacing

        
        gbc.gridx = 0;//GridBagConstraints
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

        
        loginButton.setForeground(Color.BLACK); // Set text color to black
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setFocusPainted(false);

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String position = (String) positionComboBox.getSelectedItem();
                String id = idTextField.getText();

                // Validate id input
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
        String url = "jdbc:mysql://localhost:3306/mysql1";
        String username = "root";
        String password = "11111111";
    
        try {
            // Load the MariaDB JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to the database
            connection = DriverManager.getConnection(url, username, password);
    
            JOptionPane.showMessageDialog(this, "Connected to the database.", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "MariaDB JDBC driver not found.", "Error", 
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the MariaDB database: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
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
    
        generateInvoiceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int invoiceNumber = Integer.parseInt(invoiceNumberTextField.getText());
            int totalPrice = Integer.parseInt(totalPriceTextField.getText());
            generateInvoice(usernameTextField.getText(),invoiceNumber, totalPrice);
            }
        });

     retrievePointsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = usernameTextField.getText();
                int points = retrievePointsFromAccount(userName);
                JOptionPane.showMessageDialog(SQL_GUI.this, "Points for " + userName + ": " + points, "Points Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        cashierPanel.add(generateInvoiceButton);
        cashierPanel.add(generateInvoiceButton);
        cashierPanel.add(retrievePointsButton); 
        tabbedPane.addTab("Cashier", cashierPanel);
    }
    

//inserting a new invoice useing selected acc id 
private void generateInvoice(String userName, int invoiceNumber, int totalPrice) {
    String url = "jdbc:mysql://localhost:3306/mysql1";
    String username = "root";
    String password =  "11111111";

    try (Connection connection = DriverManager.getConnection(url, username, password)) {
        // Fetch customer information and points from the database
        String fetchCustomerQuery = "SELECT * FROM ACCOUNT WHERE userName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(fetchCustomerQuery)) {
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int points = resultSet.getInt("points");

                // Update customer points (considering 10 SR spent earns 1 point)
                int pointsEarned = calculatePointsEarnedForInvoiceAmount(totalPrice);
                int updatedPoints = points + pointsEarned;

                // Update the customer's points in the database
                String updatePointsQuery = "UPDATE ACCOUNT SET points = ? WHERE userName = ?";
                try (PreparedStatement updatePointsStatement = connection.prepareStatement(updatePointsQuery)) {
                    updatePointsStatement.setInt(1, updatedPoints);
                    updatePointsStatement.setString(2, userName);
                    updatePointsStatement.executeUpdate();
                }

                // Insert a new invoice into the INVOICE table
                String insertInvoiceQuery = "INSERT INTO INVOICE (InvoiceNum, Totl_Price , Acc_userName) VALUES (?, ?, ?)";
                try (PreparedStatement insertInvoiceStatement = connection.prepareStatement(insertInvoiceQuery)) {
                    insertInvoiceStatement.setInt(1, invoiceNumber);
                    insertInvoiceStatement.setInt(2, totalPrice);
                    insertInvoiceStatement.setString(3, userName);
                    insertInvoiceStatement.executeUpdate();
                }

                JOptionPane.showMessageDialog(SQL_GUI.this, "Invoice generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(SQL_GUI.this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error generating invoice: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
 

private int calculatePointsEarnedForInvoiceAmount(int invoiceAmount) {
    // 10 SR spent earns 1 point
    int pointsPer10SR = 1;
    return invoiceAmount / 10 * pointsPer10SR;
}

//retrieve points from given acc id 
private int retrievePointsFromAccount(String customerID) {
    int points = 0;

    String url = "jdbc:mysql://localhost:3306/mysql1";
    String username = "root";
    String password = "11111111";

    // get customer points from the databas
    String fetchPointsQuery = "SELECT points FROM ACCOUNT WHERE userName = ?";
    try (Connection connection = DriverManager.getConnection(url, username, password);
         PreparedStatement preparedStatement = connection.prepareStatement(fetchPointsQuery)) {

        preparedStatement.setString(1, customerID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            points = resultSet.getInt("points");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return points;
}

private void showStoreManagerButtons() {
    JPanel managerPanel = new JPanel();
    managerPanel.setLayout(new FlowLayout());

    showStockButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String productID = JOptionPane.showInputDialog(SQL_GUI.this, "Enter Product ID:");
            showProductStock(productID);
        }
    });

    contactSupplierButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String branchCity = JOptionPane.showInputDialog(SQL_GUI.this, "Enter Branch City:");
            contactSupplier(branchCity);
        }
    });

    trackEmployeesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String employeeID = JOptionPane.showInputDialog(SQL_GUI.this, "Enter Employee ID:");
            trackEmployees(employeeID);
        }
    });

    insertProductButton = new JButton("Insert New Product");
    insertProductButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            addNewProduct();
        }
    });

    managerPanel.add(showStockButton);
    managerPanel.add(contactSupplierButton);
    managerPanel.add(trackEmployeesButton);
    managerPanel.add(insertProductButton);

    tabbedPane.addTab("Store Manager", managerPanel);
}

private void addNewProduct() {
    JTextField pIdField = new JTextField(10);
    JTextField quantityField = new JTextField(10);
    JTextField pBrandField = new JTextField(10);
    JTextField priceField = new JTextField(10);
    JTextField pTypeField = new JTextField(10);
    JTextField exDateField = new JTextField(10);
    JTextField proDateField = new JTextField(10);
    JTextField supIdField = new JTextField(10);

    JButton addButton = new JButton("Add Product");
    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JLabel("Product ID:"));
    panel.add(pIdField);
    panel.add(new JLabel("Quantity:"));
    panel.add(quantityField);
    panel.add(new JLabel("Brand:"));
    panel.add(pBrandField);
    panel.add(new JLabel("Price:"));
    panel.add(priceField);
    panel.add(new JLabel("Type:"));
    panel.add(pTypeField);
    panel.add(new JLabel("Expiry Date:"));
    panel.add(exDateField);
    panel.add(new JLabel("Production Date:"));
    panel.add(proDateField);
    panel.add(new JLabel("Supplier ID:"));
    panel.add(supIdField);
    panel.add(addButton);

    addButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String pId = pIdField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            String pBrand = pBrandField.getText();
            double price = Double.parseDouble(priceField.getText());
            String pType = pTypeField.getText();
            String exDate = exDateField.getText();
            String proDate = proDateField.getText();
            String supId = supIdField.getText();

            insertProductIntoDatabase(pId, quantity, pBrand, price, pType, exDate, proDate, supId);
        }
    });

    int result = JOptionPane.showConfirmDialog(null, panel, "Add New Product",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
    }
}


//insert
private void insertProductIntoDatabase(String pId, int quantity, String pBrand, double price, String pType, String exDate, String proDate, String supId) {
     String url = "jdbc:mysql://localhost:3306/mysql1";
     String username = "root";                         
     String password = "11111111";
     String insertProductQuery = "INSERT INTO Product (P_id, quantity, P_brand, Price, P_type, Ex_date, Pro_date, SUP_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(url, username, password);
        Statement st = connection.createStatement();
    PreparedStatement preparedStatement = connection.prepareStatement(insertProductQuery)) {
     
            preparedStatement.setString(1, pId);
        preparedStatement.setInt(2, quantity);
        preparedStatement.setString(3, pBrand);
        preparedStatement.setDouble(4, price);
        preparedStatement.setString(5, pType);
        preparedStatement.setString(6, exDate);
        preparedStatement.setString(7, proDate);
        preparedStatement.setString(8, supId);

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


// Update the shift for the given Employee ID

private void updateShift(String employeeID, String newShift) {
    String updateShiftQuery = "UPDATE Employee SET E_Shift = ? WHERE E_ID = ?";
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql1", "root", "11111111");
         PreparedStatement preparedStatement = conn.prepareStatement(updateShiftQuery)) {
        
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




// Fetch employee information based on the given Employee ID
private void trackEmployees(String employeeID) {
    initializeDatabaseConnection();

    JButton showEmployeeInfoButton = new JButton("Show Employee Information");
    JButton updateEmployeeShiftButton = new JButton("Update Employee Shift");

    showEmployeeInfoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            showEmployeeInformation(employeeID);
        }
    });

    // Action listener for updating employee shift
    updateEmployeeShiftButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String newShift = JOptionPane.showInputDialog(SQL_GUI.this, "Enter New Shift:");
            updateShift(employeeID, newShift);
        }
    });

    JButton[] buttons = {showEmployeeInfoButton, updateEmployeeShiftButton};

    // Display option dialog for the user to choose the action
    int choice = JOptionPane.showOptionDialog(
            SQL_GUI.this,
            "Select an action:",
            "Track Employee",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            buttons,
            buttons[0]);

    // Handle the chosen action
    if (choice == 0) {
        showEmployeeInfoButton.doClick(); // Simulate button click for showing employee information
    } else if (choice == 1) {
        updateEmployeeShiftButton.doClick(); // Simulate button click for updating employee shift
    }
}

private void contactSupplier(String branchCity) {
    initializeDatabaseConnection();
  
    String fetchSupplierQuery = "SELECT DISTINCT S.* FROM Supplier S " + //fetchSupplierQuery
            "JOIN Product P ON S.S_ID = P.SUP_id " +
            "JOIN Branch B ON S.S_location = B.City " +
            "WHERE B.City = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchSupplierQuery)) {
        preparedStatement.setString(1, branchCity);
        ResultSet resultSet = preparedStatement.executeQuery();

        StringBuilder supplierInfo = new StringBuilder();
        while (resultSet.next()) {
            String  S_ID = resultSet.getString("S_ID");
            String S_name = resultSet.getString("S_name");
            String S_phonenum = resultSet.getString("S_phonenum");
            String S_location = resultSet.getString("S_location");

            supplierInfo.append("Supplier ID: ").append(S_ID).append(", ");
            supplierInfo.append("Name: ").append(S_name).append(", ");
            supplierInfo.append("Phone: ").append(S_phonenum).append(", ");
            supplierInfo.append("Location: ").append(S_location).append("\n");
        }

        if (supplierInfo.length() > 0) {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Suppliers for Branch in " + branchCity + ":\n" + supplierInfo.toString(), "Supplier Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "No suppliers found for the given branch city.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error fetching supplier information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Fetch product information based on the given Product ID
private void showProductStock(String productID) {
    Connection connection = null; 
    String fetchProductQuery = "SELECT * FROM Product WHERE P_id = ?";
    try {
        String url = "jdbc:mysql://localhost:3306/mysql1";
        String username = "root";
        String password = "11111111";
        connection = DriverManager.getConnection(url, username, password);

        PreparedStatement preparedStatement = connection.prepareStatement(fetchProductQuery);
        preparedStatement.setString(1, productID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String P_type = resultSet.getString("P_type");
            int P_quantity = resultSet.getInt("quantity");
            String P_brand = resultSet.getString("P_brand");
            double price = resultSet.getDouble("Price");
            String Ex_date = resultSet.getString("Ex_date");
            String Pro_date = resultSet.getString("Pro_date");
            String S_ID = resultSet.getString("SUP_id");

            JOptionPane.showMessageDialog(SQL_GUI.this, "Product Info:\nType: " + P_type +
                    "\nQuantity: " + P_quantity +
                    "\nBrand: " + P_brand +
                    "\nPrice: " + price +
                    "\nExpiry Date: " + Ex_date +
                    "\nProduction Date: " + Pro_date +
                    "\nSupplier ID: " + S_ID, "Product Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error fetching product information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Close the connection in the finally block to ensure it's always closed
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}


// Fetch employee information based on the given Employee ID
private void showEmployeeInformation(String employeeID) {

    initializeDatabaseConnection();
    String fetchEmployeeQuery = "SELECT * FROM Employee WHERE E_ID = ?";

    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchEmployeeQuery)) {
        preparedStatement.setString(1, employeeID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String employeeNamefirst = resultSet.getString("Fname");
            String employeeNamelast = resultSet.getString("Lname");
            String employeePhone = resultSet.getString("E_phonenum");
            double employeeSalary = resultSet.getDouble("E_salary");
            String employeePosition = resultSet.getString("E_Position");
            String employeeShift = resultSet.getString("E_Shift");

            JFrame employeeFrame = new JFrame("Employee Information");
            employeeFrame.setSize(400, 200);
            employeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            employeeFrame.setLocationRelativeTo(null);

            JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));

            infoPanel.add(new JLabel("Employee ID:"));
            infoPanel.add(new JLabel(employeeID));

            infoPanel.add(new JLabel("First Name:"));
            infoPanel.add(new JLabel(employeeNamefirst));
            
            infoPanel.add(new JLabel("Last Name:"));
            infoPanel.add(new JLabel(employeeNamelast));

            infoPanel.add(new JLabel("Phone:"));
            infoPanel.add(new JLabel(employeePhone));

            infoPanel.add(new JLabel("Salary:"));
            infoPanel.add(new JLabel(String.valueOf(employeeSalary)));

            infoPanel.add(new JLabel("Position:"));
            infoPanel.add(new JLabel(employeePosition));

            infoPanel.add(new JLabel("Shift:"));
            infoPanel.add(new JLabel(employeeShift));

            employeeFrame.add(infoPanel);

            employeeFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(SQL_GUI.this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(SQL_GUI.this, "Error fetching employee information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    


