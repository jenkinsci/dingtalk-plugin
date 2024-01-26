Behaviour.specify('.dt-notifier-config-raw', 'dt-notifier-config-raw', 0, function (element) {
    element.onchange = function () {
        if (element.checked) {
            document.querySelector('.dt-raw-content-builtin').style.display = 'none'
            document.querySelector('.dt-raw-content-custom').style.display = ''
        } else {
            document.querySelector('.dt-raw-content-builtin').style.display = ''
            document.querySelector('.dt-raw-content-custom').style.display = 'none'
        }
    }
})
