let loggedIn = false;
const setUserInfo = userInfo => {
    const imageElem = document.createElement("img");
    imageElem.className = "img-thumbnail rounded";
    imageElem.src = userInfo.profileImagePath ?? "/static/img/user.png";
    imageContainer.innerHTML = "";
    imageContainer.appendChild(imageElem);

    document.getElementById("greeting").innerText = `WellCome, ${userInfo.nickname ?? userInfo.username}.`
    const UserRole = {
        INACTIVE: "ROLE_INACTIVE",
        USER: "ROLE_USER",
        BUSINESS: "ROLE_BUSINESS",
        ADMIN: "ROLE_ADMIN",
    };
    const isAdmin = userInfo.role.includes(UserRole.ADMIN);
    const isUser = userInfo.role.includes(UserRole.USER);
    const isBusiness = userInfo.role.includes(UserRole.BUSINESS);

    const summary = document.getElementById("summary");
    if (isAdmin) {
        summary.innerText = "You are: ADMIN";
        document.getElementById("admin-menu").classList.remove("d-none");
    }
    else if (isBusiness) {
        summary.innerText = "You are: BUSINESS";
        document.getElementById("business-menu").classList.remove("d-none");
    }
    else if (isUser) {
        summary.innerText = "You are: USER";
        document.getElementById("user-menu").classList.remove("d-none");
    }
    else summary.innerText = "You are: INACTIVE";
}

const setBaseView = () => {
    if (loggedIn) document.getElementById("authenticated").classList("d-none");
    else document.getElementById("anonymous").classList.add("d-none");
}

const jwt = localStorage.getItem("token");
if (jwt) {
    fetch("/users/get-user-info", {
        headers: {
            "Authentication": `Bearer ${jwt}`,
        },
    })
    .then(setUserInfo);
}
else setBaseView();

const logoutButton = document.getElementById("loggout-button");
logoutButton.addEventListener("click", e => {
    if (loggedIn) localStorage.removeItem("token");
    location.href = "/views";
});