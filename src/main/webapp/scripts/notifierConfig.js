window.addEventListener('load', function () {
	document.querySelector(
			'.dt-notifier-config-raw input[name="_.raw"]'
	).addEventListener('change',
			function (event) {
		console.log(document.querySelector('.raw-content'))
				if (event.target.checked) {
					document.querySelector('.dt-raw-content-builtin').style.display = 'none'
					document.querySelector('.dt-raw-content-custom').style.display = ''
				} else {
					document.querySelector('.dt-raw-content-builtin').style.display = ''
					document.querySelector('.dt-raw-content-custom').style.display = 'none'
				}
			})
})

