(function($) {
  $(function() {
    $(document).on('change', '.notifier-config-raw input[name="_.raw"]',
      function(event) {
        if (event.target.checked) {
          $('.raw-content').css('display', '')
          $('.none-raw-content').css('display', 'none')
        } else {
          $('.raw-content').css('display', 'none')
          $('.none-raw-content').css('display', '')
        }
      })
  })

})(jQuery3 || jQuery)
