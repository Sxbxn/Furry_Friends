if (sessionId == null) {
	alert('로그인이 필요합니다!');
	history.back()
}

function createDiary(data) {
	// img_url = data[i].img_url;
	// title = data[i].title;
	// content = data[i].content;
	// date = data[i].date;

	index = data.index
	img_url = data.thumbnailUrl;
	// img_url = data.img_url;
	title = data.title;
	content = data.title;
	// content = data.content;
	date = data.id;
	// date = data.date;

	// console.log(img_url, title, content, date);

	let col_div = document.createElement("div");
	col_div.setAttribute('class', 'col-md-4 d-flex ftco-animate');
	col_div.setAttribute('id', 'col_div' + date);
	// col_div.setAttribute('id', 'col_div' + index);
	document.getElementById('diary').append(col_div);

	let blog_div = document.createElement("div");
	blog_div.setAttribute('class', 'blog-entry align-self-stretch');
	blog_div.setAttribute('id', 'blog_div' + date);
	// blog_div.setAttribute('id', 'blog_div' + index);
	document.getElementById('col_div' + date).append(blog_div);
	// document.getElementById('col_div' + index).append(blog_div);


	let img_div = document.createElement("div");
	img_div.setAttribute('class', 'block-20 rounded');
	img_div.setAttribute('id', 'img_div' + date);
	// img_div.setAttribute('id', 'img_div' + index);
	img_div.setAttribute('style', 'background-image: url(' + img_url + '); cursor: pointer');
	// img_div.setAttribute('style', 'cursor: pointer')
	document.getElementById('blog_div' + date).append(img_div);
	// document.getElementById('blog_div' + index).append(img_div);

	let text_div = document.createElement("div");
	text_div.setAttribute('class', 'text p-4');
	text_div.setAttribute('id', 'text_div' + date);
	// text_div.setAttribute('id', 'text_div' + index);
	document.getElementById('blog_div' + date).append(text_div);
	// document.getElementById('blog_div' + index).append(text_div);

	let meta_div = document.createElement("div");
	meta_div.setAttribute('class', 'meta mb-2');
	meta_div.setAttribute('id', 'meta_div' + date);
	// meta_div.setAttribute('id', 'meta_div' + index);
	document.getElementById('text_div' + date).append(meta_div);
	// document.getElementById('text_div' + index).append(meta_div);

	let date_div = document.createElement("div");
	date_div.setAttribute('id', 'date' + date);
	// date_div.setAttribute('id', 'date' + index);
	date_div.textContent = date;
	document.getElementById('meta_div' + date).append(date_div);
	// document.getElementById('meta_div' + index).append(date_div);

	let title_div = document.createElement("div");
	title_div.textContent = title;
	// title_div.setAttribute();
	document.getElementById('meta_div' + date).append(title_div);
	// document.getElementById('meta_div' + index).append(title_div);


	let content_h3 = document.createElement("h3");
	content_h3.setAttribute('class', 'heading');
	content_h3.textContent = content;
	document.getElementById('text_div' + date).append(content_h3);
	// document.getElementById('text_div' + index).append(content_h3);
}

function showDiary() {
	fetch('https://jsonplaceholder.typicode.com/photos/')
		.then((response) => response.json())
		// .then((data) =>(console.log(data.length)))
		// .then((data) => console.log(data[0]))
		.then((data) => callCreateDiary(data))
	// .then((data) => create_diary(data.data));
	// .then(prepare());
}

function callCreateDiary(data) {
	for (let i = 0; i < 5; i++) {
		// console.log(data[i]);
		createDiary(data[i]);

	}
}

showDiary();


// 이미지 클릭 시 세부 페이지로 이동
document.addEventListener('click', function (e) {
	// const imgNodeList = document.querySelectorAll("[id^='img_div']");
	// const imgArray = Array.from(imgNodeList);

	const targetStr = String(e.target.id);
	const imgStr = 'img_div'

	const idx = targetStr.substring(7);
	console.log(idx);

	localStorage.setItem('content_idx', idx);

	console.log(e.target.id);
	if (targetStr.indexOf(imgStr) != -1) {
		clickedImgDiv = document.getElementById(targetStr)
		console.log(clickedImgDiv);
		console.log("hi", idx);


		location.href = "diary-single.html"
	}
});



// function prepare() {
// 	// const img = document.querySelectorAll("[id^='img_div']");
// 	const img = document.querySelector("#img_div1");
// 	console.log(img);
// }

// function clickImage() {

// }
