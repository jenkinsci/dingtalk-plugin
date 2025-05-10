function applyErrorMessage(elt, rsp) {
	if (rsp.status == 200) {
		elt.innerHTML = rsp.responseText
    if (rsp.responseText.startsWith('Error:')) {
      elt.classList.add('jenkins-alert', 'jenkins-alert-danger')
    } else {
      elt.classList.add('jenkins-alert', 'jenkins-alert-success')
    }
  }
	Behaviour.applySubtree(elt)
}

async function validateRobotConfig(btn) {
	var checkUrl = btn.dataset['validateButtonDescriptorUrl'] +
			'/' +
			btn.dataset['validateButtonMethod']

	var $robot = btn.closest('.robot-config-container')
	var $msg = $robot.querySelector('.robot-config-validate-msg')

	$msg.innerHTML = ''
	$msg.className = 'robot-config-validate-msg'

	var url = new URL(checkUrl, window.location.origin)
	getParameters($robot).forEach(function (v, k) {
		url.searchParams.set(k, v)
	})

	var res = await fetch(url, {
		method: 'GET'
	})

	var resText = await res.text()
	applyErrorMessage($msg, {
		status: res.status,
		responseText: resText
	})
}

function getParameters($robot) {
	/**
	 * 代理信息
	 */
	var $proxy = document.querySelector('#dt-proxyConfigContainer')
	var proxyConfig = {
		type: $proxy.querySelector('select[name="type"]').value,
		host: $proxy.querySelector('input[name="host"]').value,
		port: $proxy.querySelector('input[name="port"]').value
	}

	/**
	 * 机器人配置
	 */
	var id = $robot.querySelector('input[name="id"]').value
	var name = $robot.querySelector('input[name="name"]').value
	var webhook = $robot.querySelector('input[name="webhook"]').value
	// 安全策略
	var securityPolicyConfigs = []

	$robot.querySelectorAll('.dt-security-config-container').forEach(
			function (item) {
				securityPolicyConfigs.push({
					type: item.querySelector('input[name=type]').value,
					value: item.querySelector('input[name=value]').value
				})
			})

	var params = new URLSearchParams()

	params.set('id', id)
	params.set('name', name)
	params.set('webhook', webhook)
	params.set('securityPolicyConfigs', JSON.stringify(securityPolicyConfigs))
	params.set('proxy', JSON.stringify(proxyConfig))

	return params
}
