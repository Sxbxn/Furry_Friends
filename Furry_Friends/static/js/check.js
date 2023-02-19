function DropFile(dropAreaId, fileListId) {
    let dropArea = document.getElementById(dropAreaId);
    let fileList = document.getElementById(fileListId);

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    function highlight(e) {
        preventDefaults(e);
        dropArea.classList.add("highlight");
    }

    function unhighlight(e) {
        preventDefaults(e);
        dropArea.classList.remove("highlight");
    }

    // 파일 업로드 (drop 이벤트)
    function handleDrop(e) {
        unhighlight(e);
        let dt = e.dataTransfer;
        let files = dt.files;

        handleFiles(files);

        const fileList = document.getElementById(fileListId);
        if (fileList) {
            fileList.scrollTo({ top: fileList.scrollHeight });
        }
    }

    function handleFiles(files) {
        files = [...files];
        // files.forEach(uploadFile);
        files.forEach(previewFile);
        showResult();
    }

    // 미리보기
    function previewFile(file) {
        console.log(file);
        renderFile(file);
    }

    // 파일 -> 이미지
    function renderFile(file) {
        let reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onloadend = function () {
            let img = dropArea.getElementsByClassName("preview")[0];
            img.src = reader.result;
            img.style.display = "block";
        };
    }

    dropArea.addEventListener("dragenter", highlight, false);
    dropArea.addEventListener("dragover", highlight, false);
    dropArea.addEventListener("dragleave", unhighlight, false);
    dropArea.addEventListener("drop", handleDrop, false);

    // dropArea.addEventListener(uploadEvent)

    // function uploadEvent() {
    //     const uploadImg = document.getElementById('upload-img');
    //     const state = uploadImg.getAttribute('src');
    //     console.log(state);
    //     if (state !== " ") {
    //         console.log('hi');
    //     }
    // }

    function showResult() {
        const about = document.querySelector('.about');
        about.setAttribute('style', 'display: none');
    }


    return {
        handleFiles
    };



}

const dropFile = new DropFile("drop-file", "files");

// 시작 버튼
const startBtn = document.getElementById('start-btn');

startBtn.addEventListener("click", event => {
    event.preventDefault();

    const imgDiv = document.getElementById('upload-img');

    const imgData = imgDiv.getAttribute('src');
    const opt1 = document.querySelector('input[name="options1"]:checked').value; 
    const opt2 = document.querySelector('input[name="options2"]:checked').value; 

    var dataObj = new Object();

    dataObj['species'] = opt1;
    dataObj['parts'] = opt2;

    const json = JSON.stringify(dataObj);
	console.log(json); // {"species":"cat","parts":"op3"}

    let formData = new FormData();
    formData.append('data', dataObj);
    formData.append('file', imgData);

    // key
    for (let key of formData.keys()) {
        console.log(key);
    }

    // value
    for (let value of formData.values()) {
    console.log(value);
    }

    fetch('/', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json'
        },
        body: formData
    })
  // .then(res => res.json())
    // .then(data => console.log(data))
    // .catch(error => console.log(error))
	// 	.then(localStorage.setItem('test', 1)) // 세션 아이디 설정
    // .then(history.back()); // 이전 페이지로 이동
});





