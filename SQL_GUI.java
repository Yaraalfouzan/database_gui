import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SQL_GUI extends JFrame {
    //components for GUI
    private JLabel positionLabel, idLabel;
    private JComboBox<String> positionComboBox;
    private JTextField idTextField;
    private JButton loginButton, generateInvoiceButton, showStockButton, contactSupplierButton, trackEmployeesButton;
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
       // updatePointsButton = new JButton("Update Points");
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
    
    //method to intialize db connection (1)
    private void initializeDatabaseConnection() {

         String url = "jdbc:mariaDB://localhost:3306/whatever";
        String username = "root";
        String password = "";
    
        try {
             // Load the MariaDB JDBC driver (make sure the MariaDB JDBC driver JAR is in your classpath)
          Class.forName("org.mariadb.jdbc.Driver");
    
             // Connect to the database
           Connection con = DriverManager.getConnection(url, username, password);

            JOptionPane.showMessageDialog(this, "Connected to the database.", "Success", JOptionPane.INFORMATION_MESSAGE);
          } 
       catch (ClassNotFoundException ex) {
             ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "MariaDB JDBC driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
           ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the MariaDB database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
     }

   
//do need this ???
//     // Close the database resources 
//     rs.close(); 
//     stmt.close(); 
//     conn.close(); 
//  } catch (Exception e) { 
//     e.printStackTrace(); 
//  } 
  
    
    
    private void handlePositionActions(String position) {
       //initializeDatabaseConnection();
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



//called by handlePositionActions
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

//1)inserting a new invoice useing selcated acc id 
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


//7)retrieve points from given acc id 
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

    //2)Insert a new product with the provided information
 private void addNewProduct(String p_id, int quantity, String p_brand, double Price,String p_type, String Ex_date, String pro_date, String SUP_id) {

    
String insertProductQuery = "INSERT INTO Product (P_id, P_quantity, P_brand, price, P_type, Ex_date, Pro_date, S_ID) " +
"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
try (PreparedStatement preparedStatement = connection.prepareStatement(insertProductQuery)) {
preparedStatement.setString(1,  p_id);
preparedStatement.setInt(2, quantity);
preparedStatement.setString(3, p_brand);
preparedStatement.setDouble(4, Price);
preparedStatement.setString(5, p_type);
preparedStatement.setString(6, Ex_date);
preparedStatement.setString(7, pro_date);
preparedStatement.setString(8, SUP_id);

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
//3) Update the shift for the given Employee ID
private void updateShift(String employeeID, String newShift) {
   
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
 //4) Fetch employee information based on the given Employee ID
private void trackEmployees(String employeeID) {
   
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
  // 5)Fetch supplier information for the given branch city
private void contactSupplier(String branchCity) {
  
    String fetchSupplierQuery = "SELECT DISTINCT S.* FROM Supplier S " + //fetchSupplierQuery
            "JOIN Product P ON S.S_ID = P.S_ID " +
            "JOIN Branch B ON P.B_id = B.B_id " +
            "WHERE B.city = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchSupplierQuery)) {
        preparedStatement.setString(1, branchCity);
        ResultSet resultSet = preparedStatement.executeQuery();

        StringBuilder supplierInfo = new StringBuilder();
        while (resultSet.next()) {
            String  S_ID = resultSet.getString("S_ID");
            String S_name = resultSet.getString("S_name");
            String S_phonenum = resultSet.getString("S_phone");
            String S_location = resultSet.getString("S_location");//duplicate?

            supplierInfo.append("Supplier ID: ").append(S_ID).append(", ");
            supplierInfo.append("Name: ").append(S_name).append(", ");
            supplierInfo.append("Phone: ").append(S_phonenum).append(", ");
            supplierInfo.append("Location: ").append(S_location).append("\n");
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
// 6)Fetch product information based on the given Product ID
private void showProductStock(String productID) {
    
    String fetchProductQuery = "SELECT * FROM Product WHERE P_id = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(fetchProductQuery)) {
        preparedStatement.setString(1, productID);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String P_type = resultSet.getString("P_type");
            int P_quantity = resultSet.getInt("P_quantity");
            String P_brand = resultSet.getString("P_brand");
            double price = resultSet.getDouble("price");
            String Ex_date = resultSet.getString("Ex_date");
            String Pro_date = resultSet.getString("Pro_date");
            String S_ID= resultSet.getString("S_ID");

            // Display or use the retrieved product information as needed
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
    