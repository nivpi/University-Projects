const express = require('express');
const app = express();
const path = require('path');
const crud = require("../PartC/db/crud");
const port = 3000;
const BodyParser = require("body-parser");
const cookieParser = require("cookie-parser");
const CreateDB_CRUD = require('./db/CreateDB_CRUD');

app.use(BodyParser.json());
app.use(cookieParser());
app.use(express.static(path.join(__dirname,"static")));
app.use(BodyParser.urlencoded({extended:true}));
app.set('views',path.join(__dirname,'views'));
app.set('view engine','pug');

app.use(express.static(__dirname + '/views'));

app.get('/index',(req,res)=>{
    res.render("index");
});

app.get('/',(req,res)=>{
    res.render("index");
});

app.get('/login',(req,res)=>{
    res.render("login");
});

app.get('/myAccount',(req,res)=>{
    crud.getOrders(req,res);
    //res.render("myAccount");
});

app.get('/register',(req,res)=>{
    res.render("register");
});

app.get('/shop',(req,res)=>{
    if (req.query.supplier) {
        crud.supplierProducts(req,res);
    } else {
        crud.getAllProducts(req,res);
    }
});

//create tables in DB
app.get('/createTables', CreateDB_CRUD.createTables);

//drop Tables in DB
app.get('/dropTables', CreateDB_CRUD.dropTables);

app.get('/insertUsers',crud.insertUsers);

app.get('/insertProducts',crud.insertProducts);

app.get('/insertOrders',crud.insertOrders);

app.get('/insertOrder',crud.insertOrder);

app.get('/getAllProducts',crud.getAllProducts);

app.post('/login', crud.loginUser);

app.post('/register', crud.registerUser);

app.post('/myAccount', crud.updatePassword);

app.listen(port, ()=> {
    console.log("Server is running on port:",port);
})