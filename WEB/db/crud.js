const conn = require("../db/db");
const path = require('path');
const csv=require('csvtojson');


const registerUser = (req, res) => { // Create a new user function
    if (!req.body) {
        res.status(400).send({ message: "Input cannot be empty." });
        return;
    }
    const newUser = {
        username: req.body.username,
        password: req.body.password,
        name: req.body.name
    };
    const checkExistingUserQuery = "SELECT * FROM users WHERE username = ?";
    conn.query(checkExistingUserQuery, newUser.username, (err, result) => {
        if (err) {
            res.status(500).send({ message: "An error has occurred, please try again." });
            return;
        }
        if (result.length > 0) { // Check if the given username already exists in the system
            res.send(`<script> alert('Username is already in use, Please try a different email address.');
                    window.location.href = '/register';
                </script>
            `);
            return;
        }
        const createUserQuery = "INSERT INTO users SET ?";
        conn.query(createUserQuery, newUser, (err, sqlres) => {
            if (err) {
                console.log("Error creating new user:", err);
                res
                    .status(500)
                    .send({ message: "New user signup failed, please try again" });
                return;
            }
            console.log("Created new user");
            res.send(`<script> 
                        alert('Registered successfully, please log in.');
                        window.location.href = '/login';
                      </script>
            `);
        });
    });
};

const updatePassword = (req, res) => { // Update password function
    if (!req.body) {
        res.status(400).send({ message: "Input cannot be empty." });
        return;
    }
    const { username, oldPassword, newPassword } = req.body;

    const checkMatchingPassword = "SELECT * FROM users WHERE username = ? AND password = ?";
    conn.query(checkMatchingPassword, [username, oldPassword], (err, result) => {
        if (err) {
            res.status(401).send({ message: "Invalid old password was given, please try again." });
            return;
        }
        if (result.length === 0) { // Check if the given old password is matching to the one in the database
            res.send(`<script> alert('Invalid old password was given, please try again.');
                    window.location.href = '/myAccount';
                </script>
            `);
            return;
        }
        const updatePasswordQuery = "UPDATE users SET password = ? WHERE username = ? ";
        conn.query(updatePasswordQuery, [newPassword, username], (err, sqlres) => {
            if (err) {
                console.log("Error updating user's password:", err);
                res
                    .status(500)
                    .send({ message: "Password update failed, please try again" });
                return;
            }
            console.log("Updated user's password");
            res.send(`<script> 
                        alert("Updated user's password successfully.");
                        window.location.href = '/myAccount';
                      </script>
            `);
        });
    });
};

const loginUser = (req, res) => {  //function for users login
    const { username, password } = req.body;
    const q3 = "SELECT * FROM users WHERE username = ? AND password = ?";
    conn.query(q3, [username, password], (err, sqlres) => {
        if (err) {
            console.log("Error in query3:", err);
            res.status(401).send(`<script> alert('Login Failed! Please Try again'); //pop message for failed login
                    window.location.href = '/login';
                </script>
            `);
            return;
        }
        if (sqlres.length > 0) {
            const user = sqlres[0];
            // Save user cookies:
            res.cookie("username", username);
            res.cookie("name", user.name);
            res.send(`<script> window.location.href = '/index'; </script> `);
        } else {
            res.send(`<script> alert('Login Failed! Please Try again'); //pop message for failed login
                    window.location.href = '/login';
                </script>
            `);}
    });}


const insertUsers = (req, res)=>{
    const Q1 = "INSERT INTO users SET ?";
    const csvFilePath= path.join(__dirname, "../db/csv/users.csv");
    console.log("CSV File Path:", csvFilePath);
    csv().fromFile(csvFilePath)
        .then((jsonObj)=>{
            jsonObj.forEach(element => {
                conn.query(Q1, element, (err,mysqlres)=>{
                    if (err) {
                        const errorMessage = "Error in inserting a new user.";
                        console.log(errorMessage, err);
                        res.status(500).send(`<script> alert(errorMessage); // Pop up message for failed insertion
                                                window.location.href = '/index';
                                            </script>
                                        `);
                    }
                    console.log("Created a new user in the database.");
                });
            });
        })
    res.send("Inserted user data successfully")
};

const insertProducts = (req, res)=>{ // Function to insert the products to the database
    const Q5 = "INSERT INTO products SET ?";
    const csvFilePath= path.join(__dirname, "../db/csv/products.csv");
    console.log("CSV File Path:", csvFilePath);
    csv().fromFile(csvFilePath).then((jsonObj)=>{
            jsonObj.forEach(element => {
                conn.query(Q5, element, (err,mysqlres)=>{
                    if (err) {
                        const errorMessage = "An Error has occurred while inserting a new product.";
                        console.log(errorMessage, err);
                        res.status(500).send(`<script> alert(errorMessage); // Pop up message for failed insertion
                                                window.location.href = '/index';
                                            </script>
                                        `);
                    }
                    console.log("Created a new product in the database.");
                });
            });
        })
    res.send("Inserted products successfully.")
};

const insertOrders = (req, res)=>{ // Function to insert the orders to the database
    const Q1 = "INSERT INTO orders SET ?";
    const csvFilePath= path.join(__dirname, "../db/csv/orders.csv");
    console.log("CSV File Path:", csvFilePath);
    csv().fromFile(csvFilePath)
        .then((jsonObj)=>{
            jsonObj.forEach(element => {
                conn.query(Q1, element, (err,mysqlres)=>{
                    if (err) {
                        const errorMessage = "An Error has occurred while inserting the orders.";
                        console.log(errorMessage, err);
                        res.status(500).send(`<script> alert(errorMessage); // Pop up message for failed insertion
                                                window.location.href = '/index';
                                            </script>
                                        `);
                    }
                    console.log("Created a new order in the database.");
                });
            });
        })
    res.send("Inserted orders successfully.")
};

const insertOrder = (req, res) => { // Function to insert an order to the database
    const username = req.cookies.username;
    if (req.cookies.username === null || req.cookies.username === undefined) {
        console.error('Error, user must be logged in to insert orders.');
        res.status(500).send('Error, user must be logged in to insert orders.');
        return;
    }
    let totalPrice = 0;
    let cart = getCartCookies(req);
    for(var i = 0; i < cart.length; i++) {
        if (cart[i] !== undefined) {
            totalPrice += cart[i].price;
        }
    }
    console.log('Total Price:', totalPrice);
    const insertOrderQuery = `INSERT INTO orders (datetime, totalPrice, username) VALUES (NOW(), ?, ?)`;
    console.log('Before executing insertOrderQuery');
    conn.query(insertOrderQuery, [totalPrice, username], (err, result) => {
        if (err) {
            console.error('Error adding order:', err);
            res.status(500).send('Error adding a new order');
            return;
        }
        console.log('Order added successfully');
        res.send('Order added successfully');
    });
};

const supplierNames = {
    1: "Aroma Trail",
    2: "Freshland",
    3: "Orchard View",
    4: "Green Valley"
}

const supplierProducts = (req, res) => { // Function to search products from the database, by supplier id
    const supplierSearch = req.query.supplier; // Get the search query from the URL parameter
    if (!supplierSearch) {
        console.error('Error, expected a supplier id in the query.');
        res.status(500).send('An error has occurred, expected a supplier id in the query.');
    }
    else {
        const query = `SELECT * FROM products WHERE supplier = '${supplierSearch}'`;
        console.log('Executing search query:', query);
        conn.query(query, (err, results) => {
            if (err) {
                console.error('Error retrieving products:', err);
                res.status(500).send('An error occurred while retrieving products');
                return;
            }
            res.render('shop', {productsResponse: results, supplier: {name: `${supplierSearch}` in supplierNames ? supplierNames[supplierSearch] : "Unknown Supplier"}});
        });
    }
};

const getAllProducts = (req, res) => { // Function to get all products from the database
    const query = "SELECT * FROM products ";
    conn.query(query, (err, results) => {
        if (err) {
            console.log("Error: Cannot get all products.", err);
            res.status(400).send({ message: "Error: Cannot get all products." });
            return; }
        res.render('shop', { productsResponse: results, supplier: {name: "All suppliers"} });
    });
};

const getOrders = (req, res) => {
    const username = req.cookies.username;
    const query = 'SELECT * FROM orders WHERE username = ?';
    conn.query(query, [username], (err, results) => {
        if (err) {
            console.error('Error retrieving orders:', err);
            res.status(500).send('An error has occurred while retrieving orders');
            return;
        }
        res.render('myAccount', { orders: results });
    });
}

const getUsers = (req, res) => {
    const query = 'SELECT * FROM users';
    conn.query(query, (err, results) => {
        if (err) {
            console.error('Error retrieving users:', err);
            res.status(500).send('An error occurred while retrieving users');
            return;
        }
        res.render('/users', { users: results });
    });
}

function getCartCookies(request) {
    var result = [];
    for (const key in request.cookies) {
        if(request.cookies.hasOwnProperty(key)) {
            if (key.startsWith('cart')) {
                let value = request.cookies[key];
                let cartKey = key.split("cart")[1]
                result[cartKey] = JSON.parse(value);
                result[cartKey].id = Number(result[cartKey].id);
                result[cartKey].price = Number(result[cartKey].price);
                result[cartKey].quantity = Number(result[cartKey].quantity);
            }
        }
    }
    return result;
}


module.exports = { registerUser, loginUser, updatePassword, insertUsers, getUsers,
    insertProducts, insertOrders, getOrders, supplierProducts, getAllProducts, insertOrder };

