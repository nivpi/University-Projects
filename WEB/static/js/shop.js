let openShopping = document.querySelector('.shopping');
let closeShopping = document.querySelector('.closeShopping');
let list = document.querySelector('.list');
let listCard = document.querySelector('.listCard');
let body = document.querySelector('body');
let total = document.querySelector('.total');
let quantity = document.querySelector('.quantity');

openShopping.addEventListener('click', ()=>{
    body.classList.add('active');
})
closeShopping.addEventListener('click', ()=>{
    body.classList.remove('active');
})

total.addEventListener('click', () => {
    let result = confirm("Are you sure you want to complete the purchase?");
    if (result === true) {
        fetch('/insertOrder')
            .then(data => {
                if (data.ok) {
                    alert("Purchase completed successfully.");
                    deleteCartCookies(document);
                    listCards = [];
                    reloadCard();
                }
                else {
                    alert('Error, user must be logged in to insert orders.');
                }
            })
            .catch(error => {
                alert(error);
            });
    }
})

let listCards  = [];
let productsList = [];
function initShop(){
    listCards = getCartCookies(document);
    reloadCard();

    for (var i = 0; i < list.children.length; i++) {
        var childDiv = list.children[i];
        var product = {};
        product.id = Number(childDiv.id);
        product.name = childDiv.children[1].innerHTML;
        product.image = childDiv.children[0].src;
        product.price = Number(childDiv.children[2].innerHTML.split(' ')[0]);
        productsList.push(product);
    }
}
initShop();
function addToCart(id){
    let key = undefined;
    for (var i = 0; i < productsList.length; i++) {
        if (productsList[i].id === id)//String(id))
            key = i;
    }
    console.log(key);
    // if (key === undefined) {
    //     key = listCards.length;
    // }
    if(listCards[key] == null){
        // copy product form list to list card
        listCards[key] = JSON.parse(JSON.stringify(productsList[key]));
        listCards[key].quantity = 1;
    }
    reloadCard();
}
function reloadCard(){
    listCard.innerHTML = '';
    let count = 0;
    let totalPrice = 0;
    listCards.forEach((value, key)=>{
        totalPrice = totalPrice + Number(value.price);
        count = count + Number(value.quantity);
        if(value != null){
            let newDiv = document.createElement('li');
            newDiv.innerHTML = `
                <div><img src="${value.image}"/></div>
                <div>${value.name}</div>
                <div>${value.price.toLocaleString()}</div>
                <div>
                    <button onclick="changeQuantity(${key}, ${value.quantity - 1})">-</button>
                    <div class="count">${value.quantity}</div>
                    <button onclick="changeQuantity(${key}, ${value.quantity + 1})">+</button>
                </div>`;
            listCard.appendChild(newDiv);
        }
    })
    total.innerText = totalPrice.toLocaleString();
    quantity.innerText = count;

    // Update cart cookies:
    deleteCartCookies(document);
    listCards.forEach((value, key) => {
        setCookie(document,"cart"+key,JSON.stringify(value))
    })
}
function changeQuantity(key, quantity){
    if(quantity == 0){
        delete listCards[key];
    }else{
        listCards[key].quantity = quantity;
        listCards[key].price = quantity * productsList[key].price;
    }
    reloadCard();
}


function getCookie(doc, cookieName) {
    let a = `; ${doc.cookie}`.match(`;\\s*${cookieName}=([^;]+)`);
    let str = a ? a[1] : '';
    str = str.replaceAll("%20"," ");
    return str;
}

function setCookie(doc, cookieName, value) {
    let date = new Date();
    date.setTime(date.getTime() + (24 * 60 * 60 * 1000));
    const expires = "expires=" + date.toUTCString();
    doc.cookie = cookieName + "=" + value + "; " + expires + "; path=/";
}

function deleteCookie(doc, cookieName) {
    doc.cookie =/^[^=]+/.exec(cookieName)[0]+"=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
}

function getCartCookies(doc) {
    var result = [];
    var c = doc.cookie.split("; ");
    for (i in c)
        if(c[i].startsWith('cart')) {
            let cartKeyValue = c[i].split("cart")[1];
            let cartKey = cartKeyValue.substring(0, cartKeyValue.indexOf('='));
            let cartValue = cartKeyValue.substring(cartKeyValue.indexOf('=')+1);
            result[cartKey] = JSON.parse(cartValue);
            result[cartKey].id = Number(result[cartKey].id);
            result[cartKey].price = Number(result[cartKey].price);
            result[cartKey].quantity = Number(result[cartKey].quantity);
        }
    return result;
}

function deleteCartCookies(doc) {
    var c = doc.cookie.split("; ");
    for (i in c) {
        if (c[i] !== undefined) {
            if (c[i].startsWith("cart")) {
                doc.cookie = /^[^=]+/.exec(c[i])[0] + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
            }
        }
    }
}