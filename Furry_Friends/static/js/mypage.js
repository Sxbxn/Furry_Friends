if (user_id == null) {
	alert('로그인이 필요합니다!');
	history.back()
}

function createProfile(data) {
	console.log("?");
	console.log("animal id: ", data.animal_id);
	const idx = data.animal_id;
	const name = data.animal_name;
	const sex = data.sex;
	const birth = data.bday;
	const weight = data.weight;
	const neutering = data.neutered;
	const img_url = data.image;

	let no_gutters = document.createElement("div");
	no_gutters.setAttribute('class', 'row no-gutters')
	no_gutters.setAttribute('id', 'ng' + idx);
	document.getElementById('wrap').append(no_gutters);

	let col_7 = document.createElement("div");
	col_7.setAttribute('class', 'col-md-7')
	col_7.setAttribute('id', 'col_7_' + idx);
	document.getElementById('ng' + idx).append(col_7);

	let cw = document.createElement("div");
	cw.setAttribute('class', 'contact-wrap w-100 p-md-5 p-4')
	cw.setAttribute('id', 'cw' + idx);
	document.getElementById('col_7_' + idx).append(cw);

	let mb_4 = document.createElement("h3");
	mb_4.setAttribute('class', 'mb-4')
	mb_4.textContent = "Pet Profile " + idx;
	document.getElementById('cw' + idx).append(mb_4);

	let myPageDiv = document.createElement("div");
	myPageDiv.setAttribute('class', 'myPageDiv')
	myPageDiv.setAttribute('name', 'myPageDiv')
	myPageDiv.setAttribute('id', 'myPageDiv' + idx);
	document.getElementById('cw' + idx).append(myPageDiv);

	let row = document.createElement("div");
	row.setAttribute('class', 'row')
	row.setAttribute('id', 'row' + idx);
	document.getElementById('myPageDiv' + idx).append(row);

	// name
	let p_name = document.createElement("div");
	p_name.setAttribute('class', 'col-md-6')
	p_name.setAttribute('id', 'p_name' + idx);
	document.getElementById('row' + idx).append(p_name);

	let name_form = document.createElement("div");
	name_form.setAttribute('class', 'form-group')
	name_form.setAttribute('id', 'name_form' + idx);
	document.getElementById('p_name' + idx).append(name_form);

	let name_label = document.createElement("label");
	name_label.setAttribute('class', 'label')
	name_label.setAttribute('for', 'puppy-name' + idx);
	name_label.textContent = 'Name';
	document.getElementById('name_form' + idx).append(name_label);

	let name_div = document.createElement("div");
	name_div.setAttribute('id', 'puppy-name' + idx);
	name_div.textContent = name;
	document.getElementById('name_form' + idx).append(name_div);

	// sex
	let p_sex = document.createElement("div");
	p_sex.setAttribute('class', 'col-md-6')
	p_sex.setAttribute('id', 'p_sex' + idx);
	document.getElementById('row' + idx).append(p_sex);

	let sex_form = document.createElement("div");
	sex_form.setAttribute('class', 'form-group')
	sex_form.setAttribute('id', 'sex_form' + idx);
	document.getElementById('p_sex' + idx).append(sex_form);

	let sex_label = document.createElement("label");
	sex_label.setAttribute('class', 'label')
	sex_label.setAttribute('for', 'puppy-sex' + idx);
	sex_label.textContent = 'Sex';
	document.getElementById('sex_form' + idx).append(sex_label);

	let sex_div = document.createElement("div");
	sex_div.setAttribute('id', 'puppy-sex' + idx);
	sex_div.textContent = sex;
	document.getElementById('sex_form' + idx).append(sex_div);

	// birth
	let p_birth = document.createElement("div");
	p_birth.setAttribute('class', 'col-md-6')
	p_birth.setAttribute('id', 'p_birth' + idx);
	document.getElementById('row' + idx).append(p_birth);

	let birth_form = document.createElement("div");
	birth_form.setAttribute('class', 'form-group')
	birth_form.setAttribute('id', 'birth_form' + idx);
	document.getElementById('p_birth' + idx).append(birth_form);

	let birth_label = document.createElement("label");
	birth_label.setAttribute('class', 'label')
	birth_label.setAttribute('for', 'puppy-birth' + idx);
	birth_label.textContent = 'Date of birth';
	document.getElementById('birth_form' + idx).append(birth_label);

	let birth_div = document.createElement("div");
	birth_div.setAttribute('id', 'puppy-birth' + idx);
	birth_div.textContent = birth;
	document.getElementById('birth_form' + idx).append(birth_div);

	// weight
	let p_weight = document.createElement("div");
	p_weight.setAttribute('class', 'col-md-6')
	p_weight.setAttribute('id', 'p_weight' + idx);
	document.getElementById('row' + idx).append(p_weight);

	let weight_form = document.createElement("div");
	weight_form.setAttribute('class', 'form-group')
	weight_form.setAttribute('id', 'weight_form' + idx);
	document.getElementById('p_weight' + idx).append(weight_form);

	let weight_label = document.createElement("label");
	weight_label.setAttribute('class', 'label')
	weight_label.setAttribute('for', 'puppy-weight' + idx);
	weight_label.textContent = 'Weight';
	document.getElementById('weight_form' + idx).append(weight_label);

	let weight_div = document.createElement("div");
	weight_div.setAttribute('id', 'puppy-weight' + idx);
	weight_div.textContent = weight;
	document.getElementById('weight_form' + idx).append(weight_div);

	// neutering
	let p_neutering = document.createElement("div");
	p_neutering.setAttribute('class', 'col-md-6')
	p_neutering.setAttribute('id', 'p_neutering' + idx);
	document.getElementById('row' + idx).append(p_neutering);

	let neutering_form = document.createElement("div");
	neutering_form.setAttribute('class', 'form-group')
	neutering_form.setAttribute('id', 'neutering_form' + idx);
	document.getElementById('p_neutering' + idx).append(neutering_form);

	let neutering_label = document.createElement("label");
	neutering_label.setAttribute('class', 'label')
	neutering_label.setAttribute('for', 'puppy-neutering' + idx);
	neutering_label.textContent = 'Neutering';
	document.getElementById('neutering_form' + idx).append(neutering_label);

	let neutering_div = document.createElement("div");
	neutering_div.setAttribute('id', 'puppy-neutering' + idx);
	neutering_div.textContent = neutering;
	document.getElementById('neutering_form' + idx).append(neutering_div);


	// img
	let img_div = document.createElement("div");
	img_div.setAttribute('class', 'col-md-5 d-flex align-items-stretch');
	img_div.setAttribute('id', 'img_div' + idx);
	document.getElementById('ng' + idx).append(img_div);

	let img_div_in = document.createElement("div");
	img_div_in.setAttribute('class', 'info-wrap w-100 p-5 img');
	img_div_in.setAttribute('style', 'background-image: url(' + img_url + ');');
	document.getElementById('img_div' + idx).append(img_div_in);
}

function showProfile() {
	fetch('/pet/management', {
		method: 'GET'
	})
		.catch(error => console.log(error))
		.then((response) => response.json())
		// .then((data) => console.log(data))
		.then((data) => callcreateProfile(data))
}

function callcreateProfile(data) {
	for(let i = 0; i < data.length; i++) {
		console.log(data[i]);
		createProfile(data[i]);
	}
}

showProfile();