if (user_id == null) {
	alert('로그인이 필요합니다!');
	history.back()
}

const idx = sessionStorage.getItem("m_content_idx");

fetch('/health/content', {
	method: 'GET',
	headers: {
		'index': idx
	}
})
	.then((response) => response.json())
	.then((data) => {
		console.log(data);
		createMedicalDetail(data);
	}
	);

function createMedicalDetail(data) {
	console.log(data);

	const image = data.image;
	const title = data.comment;
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