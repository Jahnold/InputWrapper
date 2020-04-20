package codes.arnold.inputwrapper.view.behaviours

import codes.arnold.inputwrapper.R

class ClearTextBehaviour(wrapper: InputWrapperDelegate): InputWrapperBehaviour(wrapper) {

    private var isVisible = false

    override fun getState(): BehaviourState {
        return BehaviourState(
            alignment = BehaviourAlignment.END,
            drawableRes = R.drawable.ic_clear,
            drawableTint = if (isError) R.color.red else null,
            isVisible = isVisible
        )
    }

    override fun onClick(delegate: EditTextDelegate) {
        delegate.setText("")
    }

    override fun onChange(text: String) {
        isVisible = text.isNotEmpty()
        wrapper.setBehaviour(this)
    }
}