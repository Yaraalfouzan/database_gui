 
1.Overview 

We are building a Database Management System for a supermarket that: 

Keeps track of product stock, price, and information. 
keeps track of employees’ information and working hours. 
keeps track of customer’s accounts and loyalty program points. 
keeps track of suppliers.  
Keeps track of availability of specific products in each branch and the employees working for it. 

2.REQUIREMENTS Specification 

A. Product: Each product type (ex milk) can have several brands(ex almarai) and using the  will be 
in one or more branches  and distributed by only one Supplier. Each product in the market has a unique 
P_id (product id)  and it also has a price, P_brand, Ex_date and Pro_date,P_type. 
B. Account: Each account has a unique userName. It also shows the acc_phonenum the account will earn 
points after each purchase (will earn a point with every 10 sr spent) from the market . Then the  account 
will exchange the points for credit(each 100 point is worth 10credits). An account can exist without 
making any purchases yet. 
C. Invoice: a paper receipt given from a store to the customer after purchasing, will belong to the customers 
account if he has one, the details of the purchased items are listed on the invoice such as price, quantity of  
each purchased  product, etc. Each invoice has a unique invoice number and the account´s points will be 
checked for valid credit before receiving the invoice, then the points will be updated (added/subtracted).  
D. Supplier: will provide andf stock product’s according to specific schedule . Each supplier supplies 
many products and will have a unique id (S_ID )and it has S_name, S_phone number ,S_ location. 
E. employee: is a worker who works in only one branch and position(an employee can either work for , or 
manage a branch), each employee has working hours (shift), E_ID, E_name, E_phonenum, 
E_salary,E_Position, E_Shift: 
F. Branch: The market has many branches that contains many products , employees and a manager (a 
branch has at least 5 and up to 15 employees ) , each branch has an  address (zip,city) ,B_phonenum and a 
unique B_id. 
