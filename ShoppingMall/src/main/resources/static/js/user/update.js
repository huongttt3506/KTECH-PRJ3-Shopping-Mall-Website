let userId;
const jwt = localStorage.getItem("token");
if (!jwt) location.href = "/views/login";

const nicknameInput = document.getElementById("nickname-input");
const firstnameInput = document.getElementById("firstname-input");
const lastnameInput = document.getElementById("lastname-input");
const ageInput = document.getElementById("age-input");
const emailInput = document.getElementById("email-input");
const phoneInput = document.getElementById("phone-input");
const imgContainer = document.getElementById("img-container");

const setBaseData = (userInfo) => {
  userId = userInfo.id;
  nicknameInput.value = userInfo.nickname;
  firstnameInput.value = userInfo.firstname;
  lastnameInput.value = userInfo.lastname;
  ageInput.value = userInfo.ageGroup;
  emailInput.value = userInfo.email;
  phoneInput.value = userInfo.phone;
  if(userInfo.profileImagePath) {
    const imageElem = document.createElement("img");
    imageElem.className = "img-thumbnail rounded";
    imageElem.src = userInfo.profileImgPath;
    imgContainer.innerHTML = "";
    imgContainer.appendChild(imageElem);
  }
}

fetch("/users/get-user-info", {
  headers: {
    "Authorization": `Bearer ${jwt}`,
  },
})
    .then(response => {
      loggedIn = response.ok;
      if (!loggedIn) {
        localStorage.removeItem("token");
        location.href = "/views/login";
      }
      return response.json();
    })
    .then(setBaseData);

const updateForm = document.getElementById("update-form");
updateForm.addEventListener("submit", e => {
  e.preventDefault();
  const nickname = nicknameInput.value;
  const firstname = firstnameInput.value;
  const lastname = lastnameInput.value;
  const ageGroup = parseInt(ageInput.value);
  const email = emailInput.value;
  const phone = phoneInput.value;
  const body = { nickname, firstname, lastname, ageGroup, email, phone };
  fetch(`/users/${userId}/updateEssentialInfo`, {
    method: "PATCH",
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  })
      .then(response => {
        if (response.ok) location.reload();
        else if (response.status === 403)
          location.href = "/views/login";
        else alert(response.status);
      });
});

const imageForm = document.getElementById("profile-img-form");
imageForm.addEventListener("submit", e => {
  e.preventDefault();

  const formData  = new FormData();
  const imageInput = imageForm.querySelector("input");
  formData.append("file", imageInput.files[0]); //input file to form data

  fetch(`/users/${userId}/updateProfileImage`, {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("token")}`,
    },
    body: formData,
  }).then(response => {
    if (response.ok) location.reload();
    else if (response.status === 403)
      location.href = "/views/login";
    else alert(response.status);
  });
});