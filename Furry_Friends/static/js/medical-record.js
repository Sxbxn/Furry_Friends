if (user_id == null) {
	alert('로그인이 필요합니다!');
	history.back()
}

function createMedicalRecord(data) {
	// img_url = data[i].img_url;
	// title = data[i].title;
	// content = data[i].content;
	// date = data[i].date;

	img_url = data.thumbnailUrl;
	title = data.title;
	content = data.title;
	date = data.id;

	console.log(img_url, title, content, date)

	let col_div = document.createElement("div");
	col_div.setAttribute('class', 'col-md-4 d-flex ftco-animate');
	col_div.setAttribute('id', 'col_div' + date);
	document.getElementById('diary').append(col_div);

	let blog_div = document.createElement("div");
	blog_div.setAttribute('class', 'blog-entry align-self-stretch');
	blog_div.setAttribute('id', 'blog_div' + date);
	document.getElementById('col_div' + date).append(blog_div);

	let img_div = document.createElement("div");
	img_div.setAttribute('class', 'block-20 rounded');
	img_div.setAttribute('id', 'img_div' + date);
	img_div.setAttribute('style', 'background-image: url(' + img_url + ');');
	document.getElementById('blog_div' + date).append(img_div);

	let text_div = document.createElement("div");
	text_div.setAttribute('class', 'text p-4');
	text_div.setAttribute('id', 'text_div' + date);
	document.getElementById('blog_div' + date).append(text_div);

	let meta_div = document.createElement("div");
	meta_div.setAttribute('class', 'meta mb-2');
	meta_div.setAttribute('id', 'meta_div' + date);
	document.getElementById('text_div' + date).append(meta_div);

	let date_div = document.createElement("div");
	date_div.setAttribute('id', 'date' + date);
	date_div.textContent = date;
	document.getElementById('meta_div' + date).append(date_div);

	let title_div = document.createElement("div");
	title_div.textContent = title;
	// title_div.setAttribute();
	document.getElementById('meta_div' + date).append(title_div);

	let content_h3 = document.createElement("h3");
	content_h3.setAttribute('class', 'heading');
	content_h3.textContent = content;
	document.getElementById('text_div' + date).append(content_h3);
}

function showMedicalRecord() {
	fetch('https://jsonplaceholder.typicode.com/photos')
		.then((response) => response.json())
		// .then((data) =>(console.log(data.length)))
		// .then((data) => console.log(data[0]))
		.then((data) => callCreateMedicalRecord(data));
}

function callCreateMedicalRecord(data) {
	for(let i = 0; i < 5; i++) {
		console.log(data[i]);
		createMedicalRecord(data[i]);
	}
}

showMedicalRecord();

// https://jsonplaceholder.typicode.com/photos
// https://www.objgen.com/json/local/T3trUbCRX