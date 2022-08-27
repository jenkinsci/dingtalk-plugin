(function ($) {
  if (!$) {
    throw new Error('jQuery 插件加载失败将无法校验机器人配置，但不影响正常使用')
  }

  $(function () {
    $(document).on('click', '.robot-config-validate-btn', validateRobotConfig)
  })

  function validateRobotConfig() {
    var $btn = $(this)

    var checkUrl = $btn.attr('data-validate-button-descriptor-url') +
        "/" +
        $btn.attr('data-validate-button-method')

    var $robot = $btn.parents('.robot-config-container')
    var $msg = $robot.find('.robot-config-validate-msg')

    var startRequest = function () {
      $msg.empty()
    }

    var completeRequest = function (res) {
      applyErrorMessage($msg.get(0), res)
      layoutUpdateCallback.call()

      var s = res.getResponseHeader("script")

      try {
        eval(s)
      } catch (e) {
        applyErrorMessage(
            $msg.get(0),
            "failed to evaluate " +
            s +
            "\n" +
            e.message
        )
      }
    }

    startRequest()

    new Ajax.Request(checkUrl, {
      parameters: getParameters($robot),
      onComplete: function (res) {
        setTimeout(function () {
          completeRequest(res)
        }, 300)
      }
    })
  }

  function getParameters($robot) {
    /**
     * 代理信息
     */
    var $proxy = $('#proxyConfigContainer')
    var proxyConfig = {
      type: $proxy.find('select[name="type"]').val(),
      host: $proxy.find('input[name="host"]').val(),
      port: $proxy.find('input[name="port"]').val()
    }

    /**
     * 机器人配置
     */
    var id = $robot.find('input[name="id"]').val()
    var name = $robot.find('input[name="name"]').val()
    var webhook = $robot.find('input[name="webhook"]').val()
    // 安全策略
    var securityPolicyConfigs = $.map(
        $robot.find('.security-config-container'),
        function (t) {
          return {
            type: $(t).find('input[name=type]').val(),
            value: $(t).find('input[name=value]').val()
          }
        })

    var toJSON = Array.prototype.toJSON
    Array.prototype.toJSON = undefined

    var result = {
      id: id,
      name: name,
      webhook: webhook,
      securityPolicyConfigs: JSON.stringify(securityPolicyConfigs),
      proxy: JSON.stringify(proxyConfig)
    }

    Array.prototype.toJSON = toJSON

    return result
  }
})(jQuery3 || jQuery)
