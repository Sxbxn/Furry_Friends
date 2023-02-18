if (sessionId == null) {
	alert('로그인이 필요합니다!');
	history.back()
}

// // function createCalendar(events, data) {
// // 	id = data.index;
// // 	title = data.title;
// // 	// start = data.date; //2000-00-00
// //     start = '2023-02-08'

// //     events.push({
// //         id: data.id,
// //         title: data.title,
// //         start: '2023-02-08'
// //     })
// //     print(events);


// // }

// function showCalendar() {
// 	fetch('https://jsonplaceholder.typicode.com/photos/')
// 		.then((response) => response.json())

// 		.then((data) => callCreateCalendar(data));
// }

// function callCreateCalendar(data) {
//     // var events = [];
// 	for(let i = 0; i < 1; i++) {
//         calendar.addEvent({
//             id: data.id,
//             title: data.title,
//             start: '2023-02-08'
//         })
// 		// console.log(data[i]);
// 		// createCalendar(events, data[i]);
// 	}
// }

// showCalendar();