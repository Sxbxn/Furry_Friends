// "animal_id": int,
// "content": str,
// "currdate": str,
// "image": str,
// "index": int,
// "title": str,
// "user_id": str

if (user_id == null) {
	alert('로그인이 필요합니다!');
	history.back()
}

const idx = localStorage.getItem("content_idx");

fetch('/journal/content', {
	method: 'GET',
	headers: {
		'index': idx
	}
})
	.then((response) => response.json())
	.then((data) => {
		console.log(data);
		createDiaryDetail(data)
	}
	);




// // fetch('/journal/content', {
// fetch('https://jsonplaceholder.typicode.com/photos/', {
// 	method: 'GET',
// 	// headers: { 'Content-Type': 'application/json' },
// 	// body: JSON.stringify(obj),
// })
// 	// .then(console.log(JSON.stringify(obj)))
// 	.then(response => response.json())
// 	.then((data) => callCreateDiary(data))
// // .then((data) => createDiaryDetail(data))

// function callCreateDiary(data) {
// 	for (let i = 0; i < 1; i++) {
// 		console.log(data);
// 		createDiaryDetail(data[i]);

// 	}
// }

function createDiaryDetail(data) {
	// const image = data.image;
	// const date = data.currdate;
	// const title = data.title;
	// const content = data.content;

	console.log(data);

	const image = data.image;
	const title = data.title;
	const content = data.content;
	const date = data.currdate;

	let img_p = document.createElement("p");
	img_p.setAttribute('id', 'img_p');
	document.getElementById('col-lg-div').append(img_p);

	let img = document.createElement("img");
	img.setAttribute('class', 'img-fluid')
	img.setAttribute('src', image);
	document.getElementById('img_p').append(img);

	let date_p = document.createElement("p");
	// date_p.textContent(date);
	date_p.textContent = date;
	document.getElementById('col-lg-div').append(date_p);

	let title_h2 = document.createElement("h2");
	title_h2.setAttribute('class', 'mb-3');
	title_h2.textContent = title;
	document.getElementById('col-lg-div').append(title_h2);

	let content_p = document.createElement("p");
	content_p.textContent = content;
	document.getElementById('col-lg-div').append(content_p);
}

// https://jsonplaceholder.typicode.com/photos
// https://www.objgen.com/json/local/T3trUbCRX