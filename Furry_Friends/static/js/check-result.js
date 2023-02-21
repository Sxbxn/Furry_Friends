// let img = sessionStorage.getItem('image');
// let d = sessionStorage.getItem('d_list');
// let ab = sessionStorage.getItem('ab_list');

// console.log(img);
// console.log(d, ab);

function createCheckResult() {
	let image = sessionStorage.getItem('image');
	// const title = data.title;
	let dl = sessionStorage.getItem('d_list');
	let ab = sessionStorage.getItem('ab_list');

	let d_split = dl.split(',');
	let num = d_split.length;

	let ab_split = ab.split(',');

	let d_join = d_split.join(", ");
	let ab_join = ab_split.join(", ");

	console.log(image, dl, ab);

	let img_p = document.createElement("p");
	img_p.setAttribute('id', 'img_p');
	document.getElementById('col-lg-div').append(img_p);

	let img = document.createElement("img");
	img.setAttribute('class', 'img-fluid')
	img.setAttribute('src', image);
	document.getElementById('img_p').append(img);
	
	let d_num_p = document.createElement("h4");
	d_num_p.innerHTML = d_join + '<br/>' + '질병 ' + num + '가지 항목을 확인해보았습니다.'
	document.getElementById('col-lg-div').append(d_num_p);

	let result = document.createElement("h2");
	result.setAttribute('class', 'mb-3');
	result.innerHTML = '그 결과 ' + '<div style="color:red; display:inline; c">' + ab_join + '</div>'+ '이 의심됩니다. 진료에 참고 바랍니다.';
	document.getElementById('col-lg-div').append(result);

	// let content_p = document.createElement("p");
	// content_p.textContent = content;
	// document.getElementById('col-lg-div').append(content_p);

	clear();
}

// function clear() {
//     sessionStorage.removeItem('image');
//     sessionStorage.removeItem('d_list');
//     sessionStorage.removeItem('ab_list');
// }

// clear();
createCheckResult();