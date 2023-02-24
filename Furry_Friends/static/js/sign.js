let container = document.getElementById('container')

toggle = () => {
  container.classList.toggle('sign-in')
  container.classList.toggle('sign-up')
}

setTimeout(() => {
  container.classList.add('sign-in')
}, 200)

// 회원가입
const signUpForm = document.querySelector('#sign-up-form');

signUpForm.addEventListener("submit", event => {
  event.preventDefault();

  const formData = new FormData(signUpForm);
  const data = Object.fromEntries(formData);
  // console.log(data);

  fetch('/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
    // .then(res => res.json())
    .then(data => console.log(data))
    .catch(error => console.log(error))
  .then(location.href = "/sign")
});

// 로그인
const signInForm = document.querySelector('#sign-in-form');

signInForm.addEventListener("submit", event => {
  event.preventDefault();

  const formData = new FormData(signInForm);
  const data = Object.fromEntries(formData);
  // console.log(data);

  fetch('/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
    .then(res => res.json())
    .then(data => {
      const user_id = data.user_id;
      if (user_id == 'user unregistered' || user_id == 'wrong password') {
        alert(user_id);
      } else {
        sessionStorage.setItem('user_id', user_id)
        sessionStorage.setItem('animal_id', data.animal_id)
        sessionStorage.setItem('vet', data.vet);
        console.log(data)
        location.href = "/"
      }
    })
    .catch(error => console.log(error))
});
