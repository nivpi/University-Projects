const conn = require('./db');
const createTables = (req, res, next) => {
    const q1 = `
    CREATE TABLE IF NOT EXISTS users (
      username varchar(255) NOT NULL,
      password varchar(30),
      name VARCHAR(255),
      PRIMARY KEY (username)
    )`;

    const q2 = `
    CREATE TABLE IF NOT EXISTS products (  
      productId VARCHAR(255) NOT NULL,
      supplier VARCHAR(255) NOT NULL,
      name VARCHAR(255),
      image VARCHAR(255),
      price decimal(10, 2),
      PRIMARY KEY (productId),
      INDEX idx_productId (productId)
    )`;

    const q3 = `
    CREATE TABLE IF NOT EXISTS orders ( 
      orderId int NOT NULL AUTO_INCREMENT,
      datetime DATETIME,
      totalPrice decimal(10, 2),
      username varchar(255),
      PRIMARY KEY (orderId, username),
      FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
    )`;

    conn.query(q1, (err, mysqlres) => {
        if (err) {
            throw err;
        }
     });

    conn.query(q2, (err, mysqlres) => {
        if (err) {
            throw err;
        }
    });

    conn.query(q3, (err, mysqlres) => {
        if (err) {
            throw err;
        }
    });
    res.send("Created tables: users, products, orders");
};

    const dropTables = (req, res, next) => {
        const q3 = `DROP TABLE IF EXISTS orders`;
        const q2 = `DROP TABLE IF EXISTS products`;
        const q1 = `DROP TABLE IF EXISTS users`;

        conn.query(q3, (err, mysqlres) => {
            if (err) {
                throw err;
            }
        });

        conn.query(q2, (err, mysqlres) => {
            if (err) {
                throw err;
            }
        });

        conn.query(q1, (err, mysqlres) => {
            if (err) {
                throw err;
            }
        });
        res.send("Deleted tables: users, products, orders");
    };

module.exports={createTables,dropTables};