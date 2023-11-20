(function ($) {
	if (!$) {
		throw new Error('jQuery 插件加载失败将无法校验机器人配置，但不影响正常使用')
	}

	$(function () {
		$(document).on('click', '.robot-config-validate-btn', validateRobotConfig)
	})

	function applyErrorMessage(elt, rsp) {
		if (rsp.status == 200) {
			elt.innerHTML = rsp.responseText
		} else {
			var id = 'valerr' + (iota++)
			elt.innerHTML = '<a href="" onclick="document.getElementById(\'' + id
					+ '\').style.display=\'block\';return false">ERROR</a><div id="'
					+ id + '" style="display:none">' + rsp.responseText + '</div>'
			var error = document.getElementById('error-description') // cf. oops.jelly
			if (error) {
				var div = document.getElementById(id)
				while (div.firstElementChild) {
					div.removeChild(div.firstElementChild)
				}
				div.appendChild(error)
			}
		}
		Behaviour.applySubtree(elt)
	}

	async function validateRobotConfig() {
		var $btn = $(this)

		var checkUrl = $btn.attr('data-validate-button-descriptor-url') +
				'/' +
				$btn.attr('data-validate-button-method')

		var $robot = $btn.parents('.robot-config-container')
		var $msg = $robot.find('.robot-config-validate-msg')

		$msg.empty()

		var url = new URL(checkUrl, window.location.origin)
		getParameters($robot).forEach(function (v, k) {
			url.searchParams.set(k, v)
		})

		var res = await fetch(url, {
			method: 'GET'
		})

		var resText = await res.text()
		applyErrorMessage($msg.get(0), {
			status: res.status,
			responseText: resText
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

		var params = new URLSearchParams()

		params.set('id', id)
		params.set('name', name)
		params.set('webhook', webhook)
		params.set('securityPolicyConfigs', JSON.stringify(securityPolicyConfigs))
		params.set('proxy', JSON.stringify(proxyConfig))

		return params
	}
})(jQuery3 || jQuery)
