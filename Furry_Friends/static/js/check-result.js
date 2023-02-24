myalert();

function myalert() {
	Swal.fire({
		icon: 'success',
		title: '검사 완료',
		text: '검사가 완료되었습니다!',
	});
}


function createCheckResult() {
	let image = sessionStorage.getItem('image');
	// const title = data.title;
	let label = (sessionStorage.getItem('d_name_list'));
	label = label.split(',');
	let value = sessionStorage.getItem('d_value_list');
	value = value.split(',')
	let ab = sessionStorage.getItem('ab_list');
	console.log(label, value);

	

	// let d_split = dl.split(',');
	let num = label.length;

	let ab_split = ab.split(',');
	let d_num = ab_split.length;
	console.log(ab_split);

	let d_join = label.join(", ");
	let ab_join = ab_split.join(", ");

	// console.log(image, dl, ab);

	let img_p = document.createElement("p");
	img_p.setAttribute('id', 'img_p');
	document.getElementById('ximg').append(img_p);

	let img = document.createElement("img");
	img.setAttribute('class', 'img-fluid')

	img.setAttribute('src', image);
	document.getElementById('img_p').append(img);
	
	// let d_num_p = document.createElement("h4");
	// d_num_p.innerHTML = d_join + '<br/>' + '질병 ' + num + '가지 항목을 확인해보았습니다.'
	// document.getElementById("result-text").append(d_num_p);

	// let result = document.createElement("h2");
	// result.setAttribute('class', 'mb-3');
	// result.innerHTML = '그 결과 ' + '<div style="color:red; display:inline;">' + ab_join + '</div>' + '이 의심됩니다. 진료에 참고 바랍니다.';
	// document.getElementById("result-text").append(result);

	for (let i = 0; i < d_num; i++) {
		let tmp_btn = document.getElementById('btn' + (i + 1));
		tmp_btn.innerText = ab_split[i];
		tmp_btn.style.display = "block";
	}

	// let content_p = document.createElement("p");
	// content_p.textContent = content;
	// document.getElementById('col-lg-div').append(content_p);

	Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
	Chart.defaults.global.defaultFontColor = '#292b2c';

	var ctx = document.getElementById("myBarChart");
	
	var myLineChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: label,
			datasets: [{
				label: "Percent (%)",
				backgroundColor: "rgba(0,189,86,1)",
				borderColor: "rgba(2,117,216,1)",
				data: value
			}],
		},
		options: {
			scales: {
				xAxes: [{
					time: {
						unit: 'month'
					},
					gridLines: {
						display: false
					},
					ticks: {
						maxTicksLimit: 10
					}
				}],
				yAxes: [{
					ticks: {
						min: 0,
						max: 100,
						maxTicksLimit: 5
					},
					gridLines: {
						display: true
					}
				}],
			},
			legend: {
				display: false
			}
		}
	});
	// clear();
}

// clear();
createCheckResult();