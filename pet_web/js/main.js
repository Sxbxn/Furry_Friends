(function ($) {
	"use strict";

	$(window).stellar({
		responsive: true,
		parallaxBackgrounds: true,
		parallaxElements: true,
		horizontalScrolling: false,
		hideDistantElements: false,
		scrollProperty: 'scroll'
	});

	var fullHeight = function () {

		$('.js-fullheight').css('height', $(window).height());
		$(window).resize(function () {
			$('.js-fullheight').css('height', $(window).height());
		});

	};
	fullHeight();

	// loader
	var loader = function () {
		setTimeout(function () {
			if ($('#ftco-loader').length > 0) {
				$('#ftco-loader').removeClass('show');
			}
		}, 1);
	};
	loader();

	var carousel = function () {
		$('.carousel-testimony').owlCarousel({
			center: true,
			loop: true,
			items: 1,
			margin: 30,
			stagePadding: 0,
			nav: false,
			navText: ['<span class="ion-ios-arrow-back">', '<span class="ion-ios-arrow-forward">'],
			responsive: {
				0: {
					items: 1
				},
				600: {
					items: 2
				},
				1000: {
					items: 3
				}
			}
		});

	};
	carousel();

	$('nav .dropdown').hover(function () {
		var $this = $(this);
		// 	 timer;
		// clearTimeout(timer);
		$this.addClass('show');
		$this.find('> a').attr('aria-expanded', true);
		// $this.find('.dropdown-menu').addClass('animated-fast fadeInUp show');
		$this.find('.dropdown-menu').addClass('show');
	}, function () {
		var $this = $(this);
		// timer;
		// timer = setTimeout(function(){
		$this.removeClass('show');
		$this.find('> a').attr('aria-expanded', false);
		// $this.find('.dropdown-menu').removeClass('animated-fast fadeInUp show');
		$this.find('.dropdown-menu').removeClass('show');
		// }, 100);
	});


	$('#dropdown04').on('show.bs.dropdown', function () {
		console.log('show');
	});

	// magnific popup
	$('.image-popup').magnificPopup({
		type: 'image',
		closeOnContentClick: true,
		closeBtnInside: false,
		fixedContentPos: true,
		mainClass: 'mfp-no-margins mfp-with-zoom', // class to remove default margin from left and right side
		gallery: {
			enabled: true,
			navigateByImgClick: true,
			preload: [0, 1] // Will preload 0 - before current, and 1 after the current image
		},
		image: {
			verticalFit: true
		},
		zoom: {
			enabled: true,
			duration: 300 // don't foget to change the duration also in CSS
		}
	});

	$('.popup-youtube, .popup-vimeo, .popup-gmaps').magnificPopup({
		disableOn: 700,
		type: 'iframe',
		mainClass: 'mfp-fade',
		removalDelay: 160,
		preloader: false,

		fixedContentPos: false
	});


	var counter = function () {

		$('#section-counter').waypoint(function (direction) {

			if (direction === 'down' && !$(this.element).hasClass('ftco-animated')) {

				var comma_separator_number_step = $.animateNumber.numberStepFactories.separator(',')
				$('.number').each(function () {
					var $this = $(this),
						num = $this.data('number');
					console.log(num);
					$this.animateNumber(
						{
							number: num,
							numberStep: comma_separator_number_step
						}, 7000
					);
				});

			}

		}, { offset: '95%' });

	}
	counter();

	var contentWayPoint = function () {
		var i = 0;
		$('.ftco-animate').waypoint(function (direction) {

			if (direction === 'down' && !$(this.element).hasClass('ftco-animated')) {

				i++;

				$(this.element).addClass('item-animate');
				setTimeout(function () {

					$('body .ftco-animate.item-animate').each(function (k) {
						var el = $(this);
						setTimeout(function () {
							var effect = el.data('animate-effect');
							if (effect === 'fadeIn') {
								el.addClass('fadeIn ftco-animated');
							} else if (effect === 'fadeInLeft') {
								el.addClass('fadeInLeft ftco-animated');
							} else if (effect === 'fadeInRight') {
								el.addClass('fadeInRight ftco-animated');
							} else {
								el.addClass('fadeInUp ftco-animated');
							}
							el.removeClass('item-animate');
						}, k * 50, 'easeInOutExpo');
					});

				}, 100);

			}

		}, { offset: '95%' });
	};
	contentWayPoint();

	$('.appointment_date').datepicker({
		'format': 'm/d/yyyy',
		'autoclose': true
	});

	$('.appointment_time').timepicker();

})(jQuery);




// JS 이벤트
function createDiary(data) {
	// img_url = data[i].img_url;
	// title = data[i].title;
	// content = data[i].content;
	// date = data[i].date;

	// img_url = data.thumbnailUrl;
	img_url = 'https://assets.goal.com/v3/assets/bltcc7a7ffd2fbf71f5/blt33f405afcb51730d/63d99ad0ce01a9413960b3cc/Image_20230131_193955_051.png';
	title = data.title;
	content = data.title;
	date = data.id;

	console.log(img_url, title, content, date)

	// let test_div = document.createElement("div");
	// test_div.textContent = "??????????";
	// document.getElementById('diary').append(test_div);

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
	// img_div.setAttribute('style', 'background-image: https://via.placeholder.com/150/771796');
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

function showDiary() {
	fetch('https://jsonplaceholder.typicode.com/photos')
		.then((response) => response.json())
		// .then((data) =>(console.log(data.length)))
		// .then((data) => console.log(data[0]))
		.then((data) => callCreateDiary(data));
		// .then((data) => create_diary(data.data));
}

function callCreateDiary(data) {
	for(let i = 0; i < 5; i++) {
		console.log(data[i]);
		createDiary(data[i]);
	}
}

showDiary();

// https://jsonplaceholder.typicode.com/photos
// https://www.objgen.com/json/local/T3trUbCRX