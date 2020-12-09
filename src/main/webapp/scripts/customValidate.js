function customValidate(button) {

  var descriptorUrl = button.getAttribute(
      'data-validate-button-descriptor-url');
  var method = button.getAttribute('data-validate-button-method');
  var checkUrl = descriptorUrl + "/" + method;
  var spinner = button.nextSibling;
  var target = spinner.nextSibling;

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
        applyErrorMessage(target, "failed to evaluate " + s + "\n" + e.message);
      }
    }
  });
}

function getParameters(button) {
  var securityPolicyConfigs = [];
  var id = findPreviousFormItem(button, 'id').value;
  var name = findPreviousFormItem(button, 'name').value;
  var webhook = findPreviousFormItem(button, 'webhook').value;
  var robotDom = button.closest('.dt-robot-config');
  var typeDoms = robotDom.querySelectorAll('[name=type]');
  var valueDoms = robotDom.querySelectorAll('[name="_.value"]');
  typeDoms.forEach((item, index) => {
    securityPolicyConfigs.push({
      checked: true,
      type: item.value,
      value: valueDoms[index].value
    });
  })
  var toJSON = Array.prototype.toJSON;
  delete Array.prototype.toJSON;
  var result = {
    id: id,
    name: name,
    webhook: webhook,
    securityPolicyConfigs: JSON.stringify(securityPolicyConfigs)
  };
  Array.prototype.toJSON = toJSON;

  return result;
}