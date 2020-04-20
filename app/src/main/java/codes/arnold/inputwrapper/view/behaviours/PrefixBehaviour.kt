package codes.arnold.inputwrapper.view.behaviours

class PrefixBehaviour(wrapper: InputWrapperDelegate): InputWrapperBehaviour(wrapper) {

    var prefix: String = ""
        set(value) {
            field = value
            wrapper.setBehaviour(this)
        }

    override fun getState(): BehaviourState {
        return BehaviourState(
            alignment = BehaviourAlignment.START,
            text = prefix,
            isVisible = true
        )
    }

    override fun onClick(delegate: EditTextDelegate) { /* no-op */  }
    override fun onChange(text: String) { /* no-op */ }
}