const jwt = localStorage.getItem("token") ?? null;
if (jwt) fetch("/users/get-user-info"), {
    headers: {
        "Authorization": `Bearer: ${jwt}`,
    },
}.then(response => {
    if (response.ok) location.href = "/views";
})

const signupForm = document.getElementById("signup-form");
const usernameInput = document.getElementById("username-input");
const passwordInput = document.getElementById("password-input");
const confirmPassInput = document.getElementById("confirmPass-input");
signupForm.addEventListener("submit", e => {
    e.preventDefault();
    const username = usernameInput.value;
    const password = passwordInput.value;
    const confirmPassword = confirmPassInput.value;
    fetch("/users/register", {
    method: "POST",
    headers: {
        "Content-Type": "application/json",
    },
    body: JSON.stringify({username, password, confirmPassword}),
    })
    .then(response => {
        if (response.ok) location.href = "/views/login";
        else throw Error("failed to signup");
    })
    .catch(error => alert(error.message));
});