if (user_id == null) {
	alert('로그인이 필요합니다!');
	history.back()
}

const animal_id = sessionStorage.getItem('animal_id');

function createDiary(data) {
	index = data.index;
	img_url = data.image;
	title = data.title;
	content = data.content;
	date = data.currdate;

	let col_div = document.createElement("div");
	col_div.setAttribute('class', 'col-md-4 d-flex ftco-animate fadeInUp ftco-animated');
	col_div.setAttribute('id', 'col_div' + index);
	document.getElementById('diary').append(col_div);

	let blog_div = document.createElement("div");
	blog_div.setAttribute('class', 'blog-entry align-self-stretch');
	blog_div.setAttribute('id', 'blog_div' + index);
	document.getElementById('col_div' + index).append(blog_div);


	let img_div = document.createElement("div");
	img_div.setAttribute('class', 'block-20 rounded');
	img_div.setAttribute('id', 'img_div' + index);
	img_div.setAttribute('style', 'background-image: url(' + img_url + '); cursor: pointer');
	document.getElementById('blog_div' + index).append(img_div);

	let text_div = document.createElement("div");
	text_div.setAttribute('class', 'text p-4');
	text_div.setAttribute('id', 'text_div' + index);
	document.getElementById('blog_div' + index).append(text_div);

	let meta_div = document.createElement("div");
	meta_div.setAttribute('class', 'meta mb-2');
	meta_div.setAttribute('id', 'meta_div' + index);
	document.getElementById('text_div' + index).append(meta_div);

	// let title_div = document.createElement("div");
	// title_div.textContent = user_id;
	// document.getElementById('meta_div' + index).append(title_div);

	let date_div = document.createElement("div");
	date_div.setAttribute('id', 'date' + index);
	date_div.textContent = date;
	document.getElementById('meta_div' + index).append(date_div);

	let content_h3 = document.createElement("h3");
	content_h3.setAttribute('class', 'heading');
	content_h3.textContent = title;
	document.getElementById('text_div' + index).append(content_h3);
}

function showDiary() {
	fetch('/journal/journals', {
		method: 'GET',
		headers: {
			'animal_id': animal_id
		}
	})
		.then((response) => response.json())
		.then((data) => {
			callCreateDiary(data)
		});
}

function callCreateDiary(data) {
	for (let i = 0; i < data.length; i++) {
		createDiary(data[i]);
	}
}

showDiary();


// 이미지 클릭 시 세부 페이지로 이동
document.addEventListener('click', function (e) {
	const targetStr = String(e.target.id);
	const imgStr = 'img_div'

	const idx = targetStr.substring(7);

	sessionStorage.setItem('d_content_idx', idx);

	// console.log(e.target.id);
	if (targetStr.indexOf(imgStr) != -1) {
		location.href = "/diary-single"
	}
});