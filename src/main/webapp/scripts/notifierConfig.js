Behaviour.specify('.dt-notifier-config-raw', 'dt-notifier-config-raw', 0, function (element) {
    // Every configured robot renders this same markup, so the blocks have to be resolved within
    // the notifier this checkbox belongs to rather than document-wide. Both paths that produce a
    // notifier put it in its own `repeated-chunk`: the job property's view for the ones already
    // saved, and the hetero-list script for the ones added afterwards, which sets the class
    // before applying behaviours to the new subtree.
    var scope = element.closest('.repeated-chunk')
    var builtin = scope && scope.querySelector('.dt-raw-content-builtin')
    var custom = scope && scope.querySelector('.dt-raw-content-custom')
    if (!builtin || !custom) {
        return
    }

    function apply() {
        builtin.style.display = element.checked ? 'none' : ''
        custom.style.display = element.checked ? '' : 'none'
    }

    // Behaviour.specify invokes this once per matching element as the form renders, which is the
    // only chance to show the block that the saved configuration actually uses. Without it the
    // view's own default wins and a notifier saved with the built-in message disabled comes back
    // offering the textarea it no longer sends, so edits land in a field nothing reads.
    apply()
    element.onchange = apply
})
