document.addEventListener('DOMContentLoaded', ()=> {

    document.querySelectorAll('.color-change').forEach(button => {
        button.onclick = () => {
            document.querySelector('#firstBody').style.background = button.dataset.color;
            document.querySelector('#jsHeading').style.color = button.dataset.colour;
        };
    });
});