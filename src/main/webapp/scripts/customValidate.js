function customValidate(yuiButton) {

  var button = yuiButton._button;
  var descriptorUrl = button.getAttribute(
      'data-validate-button-descriptor-url');
  var method = button.getAttribute('data-validate-button-method');
  var checkUrl = descriptorUrl + "/" + method;

  var spinner = $(button).up("DIV").next();
  var target = spinner.next();
  spinner.style.display = "block";

  new Ajax.Request(checkUrl, {
    parameters: getParameters(button),
    onComplete: function (rsp) {
      spinner.style.display = "none";
      applyErrorMessage(target, rsp);
      layoutUpdateCallback.call();
      var s = rsp.getResponseHeader("script");
      try {
        geval(s);
      } catch (e) {
        window.alert("failed to evaluate " + s + "\n" + e.message);
      }
    }
  });
}

function getParameters(button) {
  var securityPolicyConfigs = [];
  var id = findPreviousFormItem(button, 'id').value;
  var name = findPreviousFormItem(button, 'name').value;
  var webhook = findPreviousFormItem(button, 'webhook').value;
  document.querySelectorAll('.ding-talk-robot-config').forEach(item => {
    if (item.contains(button)) {
      var securityPolicyDom = item.querySelector('.ding-talk-security-policy');
      var checkedDoms = securityPolicyDom.querySelectorAll(
          'input[name="_.checked"]');
      var typeDoms = securityPolicyDom.querySelectorAll('input[name="_.type"]');
      var valueDoms = securityPolicyDom.querySelectorAll(
          'textarea[name="_.value"]');
      checkedDoms.forEach((item, index) => {
        if (item.checked) {
          securityPolicyConfigs.push({
            checked: true,
            type: typeDoms[index].value,
            value: valueDoms[index].value
          });
        }
      });
    }
  });

  var toJSON = Array.prototype.toJSON;
  delete Array.prototype.toJSON;
  var result ={
    id: id,
    name: name,
    webhook: webhook,
    securityPolicyConfigs: JSON.stringify(securityPolicyConfigs)
  };
  Array.prototype.toJSON = toJSON;

  return result;
}