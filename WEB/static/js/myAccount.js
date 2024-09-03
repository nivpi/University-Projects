// myAccount.js
let formHeader = document.getElementById('form-header');

function initMyAccount() {
    const name = getCookie(document,'name');
    if (name) {
        formHeader.innerHTML += ": " + name;
    }
}

function getCookie(doc, cookieName) {
    let a = `; ${doc.cookie}`.match(`;\\s*${cookieName}=([^;]+)`);
    let str = a ? a[1] : '';
    str = str.replaceAll("%20"," ");
    return str;
}

initMyAccount();