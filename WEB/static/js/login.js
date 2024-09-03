let form = document.getElementById('form');

function initLogin() {
    let loginBtn = document.createElement('div');
    loginBtn.innerHTML = `<button type="submit" form="form">Login</button>`;
    form.appendChild(loginBtn);
}
initLogin();