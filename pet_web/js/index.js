function nav() {

    const nav = document.getElementById('ftco-navbar');

    fetch('./header.html')
        .then(res => res.text())
        .then(data => nav.innerHTML = data)
        .then(selected());
}

function selected() {
    let n = document.getElementsByClassName('nav-item');
    console.log(n);
    // console.log(c);
    // console.log(c.childNodes);
    // const navArray = c.childNodes;
    // console.log(typeof c);
    // console.log(c.length);

    // // const navArray = Array.from(c);
    // console.log(typeof c);
    // console.log(navArray);
    // let l1 = n.getElementById('l1');
    
    console.log(n[0]);
    // console.log(l1);

    // c.item[0].setAttribute('class', 'active');
}

nav();