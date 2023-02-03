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
function create_diary(data) {
	img_url = data[i].img_url;
	title = data[i].title;
	content = data[i].content;
	date = data[i].date;

	let col_div = document.createElement("div");
	col_div.setAttribute('class', 'col-md-4 d-flex ftco-animate')
	document.getElementById('diary').appendChild(col_div);

	let blog_div = document.createElement("div");
	blog_div.setAttribute('class', 'blog-entry align-self-stretch')
	document.getElementById('col_div').appendChild(blog_div);

	let img_div = document.createAttribute("div");
	img_div.setAttribute('style', 'background-image: url(' + img_url + ');');
	img_div.setAttribute('class', 'block-20 rounded');







	let one = document.createElement("li");
	one.setAttribute('id', 'one' + id);
	document.getElementById('webtoon_list').appendChild(one);

	let thumbnail = document.createElement("div");
	thumbnail.setAttribute('id', 'thumbnail' + id);
	document.getElementById("one" + id).appendChild(thumbnail);

	// let img_a = document.createElement("a");
	// img_a.setAttribute('id', 'img_a' + id);
	// img_a.setAttribute('href', web_url);
	// document.getElementById('thumbnail' + id).appendChild(img_a);

	let img_src = document.createElement("img");
	img_src.setAttribute('src', img_url);
	img_src.setAttribute('width', '83');
	img_src.setAttribute('height', '90');
	document.getElementById('img_a' + id).appendChild(img_src);
	//
	//
	let m_dl = document.createElement("dl");
	m_dl.setAttribute('id', 'dl' + id);
	document.getElementById("one" + id).appendChild(m_dl);

	let m_dt = document.createElement("dt");
	m_dt.setAttribute('id', 'dt' + id);
	document.getElementById("dl" + id).appendChild(m_dt);

	let m_a = document.createElement("a");
	m_a.setAttribute('href', web_url);
	m_a.textContent = title;
	document.getElementById("dt" + id).appendChild(m_a);

	let m_dd1 = document.createElement("dd");
	m_dd1.setAttribute('id', 'author' + id);
	document.getElementById("dl" + id).appendChild(m_dd1);

	let m_p = document.createElement("p");
	m_p.innerText = author;
	document.getElementById("author" + id).appendChild(m_p);


	let m_dd2 = document.createElement("dd");
	m_dd2.setAttribute('id', 'wish' + id);
	document.getElementById("dl" + id).appendChild(m_dd2);

}