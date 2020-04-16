package codes.arnold.inputwrapper.view.behaviours

abstract class InputWrapperBehaviour(
    protected val wrapper: InputWrapperDelegate
) {
    abstract fun getState(): BehaviourState
    abstract fun onClick(delegate: EditTextDelegate)
    abstract fun onChange(text: String)
    abstract fun onError(isError: Boolean)
}

