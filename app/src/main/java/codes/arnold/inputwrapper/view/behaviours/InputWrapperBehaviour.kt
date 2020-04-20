package codes.arnold.inputwrapper.view.behaviours

abstract class InputWrapperBehaviour(
    protected val wrapper: InputWrapperDelegate
) {

    protected var isError = false

    abstract fun getState(): BehaviourState
    abstract fun onClick(delegate: EditTextDelegate)
    abstract fun onChange(text: String)
    open fun onError(isError: Boolean) {
        this.isError = isError
        wrapper.setBehaviour(this)
    }
}

