if (vet != 1) {
    alert('의료 관계자만 이용 가능합니다.');
    history.back()
}

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

    function showResult() {
        const about = document.querySelector('.about');
        about.setAttribute('style', 'display: none');
    }

    return {
        handleFiles
    };
}

const dropFile = new DropFile("drop-file", "files");

// 옵션 제한 기능
const optionBox = document.getElementById('opt');

optionBox.addEventListener("click", event => {
    // event.preventDefault();

    const opt1 = document.querySelector('input[name="options1"]:checked').value;
    const opt2 = document.querySelector('input[name="options2"]:checked').value;

    // 위치
    const optMu = document.getElementById('a_mu') // mu

    // 자세
    const optLateral = document.getElementById('p_lateral') // lateral
    const optVD = document.getElementById('p_VD') // VD
    const optAP = document.getElementById('p_AP') // AP


    // 고양이 선택 시
    if (opt1 == "cat") {
        optMu.setAttribute('style', 'display: none') //근골격 옵션 제거
        optAP.setAttribute('style', 'display: none') // 근골격 옵션 제거 시, AP 옵션도 같이 제거

        optVD.setAttribute('style', 'display: none') //VD 옵션 제거        

    } else { // 강아지 선택 시,
        optMu.removeAttribute('style')
        optAP.removeAttribute('style')
        optVD.removeAttribute('style')
    }

    // 복부, 흉부 선택 시
    if (opt2 == "ab" || opt2 == "ch") {
        optAP.setAttribute('style', 'display: none'); // AP 옵션 제거
        optLateral.removeAttribute('style');
        optVD.removeAttribute('style');

        // 고양이 & 흉부 선택 시
        if (opt1 == "cat" && opt2 == "ch") {
            optVD.setAttribute('style', 'display: none'); // VD 옵션 제거
        } else {
            optVD.removeAttribute('style');
        }

    } else {
        optLateral.setAttribute('style', 'display: none'); // lateral 옵션 제거
        optVD.setAttribute('style', 'display: none'); // VD 옵션 제거
        optAP.removeAttribute('style');
    }
})

// 시작 버튼
const startBtn = document.getElementById('start-btn');

startBtn.addEventListener("click", event => {
    event.preventDefault();

    const imgDiv = document.getElementById('upload-img');

    const imgData = imgDiv.getAttribute('src');
    const opt1 = document.querySelector('input[name="options1"]:checked').value;
    const opt2 = document.querySelector('input[name="options2"]:checked').value;
    const opt3 = document.querySelector('input[name="options3"]:checked').value;

    var dataObj = new Object();

    dataObj['kind'] = opt1;
    dataObj['affected_area'] = opt2;
    dataObj['posture'] = opt3;
    const dataJson = JSON.stringify(dataObj);

    let formData = new FormData();
    formData.append('file', imgData);
    formData.append('data', dataJson);

    fetch('/health/check', {
        method: 'POST',
        // headers: {
        // 'Content-Type': 'application/x-www-form-urlencoded'
        // },
        body: formData
    })
        .then((response) => response.json())
        .then((data) => {
            cycle(data);
        }
        )
        // .then(result())
        // .then(setTimeout(() => {
        //     result();
        //     reload();
        //     console.log("??");
        // }, 40000));
});

async function cycle(data) {
    saveCheckResult(data);
    await callResultPage();

}

// function result() {
//     const dl = sessionStorage.getItem('d_list');

//     if (dl === undefined || dl === null) {
//         const c2 = document.getElementById('c2');
//         c2.style.display = 'none';

//         const rc = document.getElementById('result-content');
//         rc.style.display = 'none';

//     } else {
//         const c1 = document.getElementById('c1');
//         c1.style.display = 'none';

//         const cld = document.getElementById('col-lg-div');
//         cld.style.display = 'none';
//     }

//     // reload();
// }

// function reload() {
//     // window.location.reload();
//     sessionStorage.removeItem('image');
//     sessionStorage.removeItem('d_list');
//     sessionStorage.removeItem('ab_list');
// }

function callResultPage() {
    return new Promise(function(resolve, reject) {
        location.href = "/check-result";
        resolve();
    })
}

// 결과 저장
function saveCheckResult(data) {
    const image = data.image;
    const diseases = data.diseases;

    d_name = Object.keys(diseases);
    // console.log(d_name);

    abnormal_list = [];
    for (let i = 0; i < d_name.length; i++) {
        // console.log(d_name[i]);
        // console.log(diseases[d_name[i]]);
        const tmp = diseases[d_name[i]];

        if (tmp == "abnormal") {
            abnormal_list.push(d_name[i]);
        }
    }
    console.log(diseases);
    console.log(abnormal_list);

    sessionStorage.setItem('image', image);
	sessionStorage.setItem('d_list', d_name);
	sessionStorage.setItem('ab_list', abnormal_list);
}