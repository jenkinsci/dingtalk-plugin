Behaviour.specify('.robot-config-validate-btn', 'validate-robot-config', 0, function(element) {
    element.addEventListener('click', function() {
        validateRobotConfig(this);
    });
});
