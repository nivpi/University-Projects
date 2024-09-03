let rightItems = document.querySelector('.right-items');

function initApp(){
    // Get cookies
    const username = getCookie(document,'username');
    const name = getCookie(document,'name');

    if (name) {
        // const user = sessionStorage.getItem('username');
        let userLabel = document.createElement('li');
        userLabel.innerHTML = `Hello ${name}`;
        rightItems.appendChild(userLabel);
        let myAccountBtn = document.createElement('li');
        myAccountBtn.innerHTML = `
        <a href="/myAccount">My Account</a>`;
        rightItems.appendChild(myAccountBtn);
        let logoutBtn = document.createElement('li');
        logoutBtn.classList.add('button-logout');
        logoutBtn.innerHTML = `
        <button id="logout-button" onclick="logout()">Logout</button>`;
        rightItems.appendChild(logoutBtn);
    }
    else {
        let loginBtn = document.createElement('li');
        loginBtn.innerHTML = `
        <a href="/login">Login</a>`;
        rightItems.appendChild(loginBtn);
    }
}

function getCookie(doc, cookieName) {
    let a = `; ${doc.cookie}`.match(`;\\s*${cookieName}=([^;]+)`);
    let str = a ? a[1] : '';
    str = str.replaceAll("%20"," ");
    return str;
}

initApp();