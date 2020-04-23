package codes.arnold.inputwrapper.view.behaviours

import codes.arnold.inputwrapper.R

class TextCaseBehaviour(
    wrapper: InputWrapperDelegate
): InputWrapperBehaviour(wrapper) {

    private var isUpperCase = false

    override fun getState(): BehaviourState {
        return BehaviourState(
            alignment = BehaviourAlignment.END,
            drawableRes = if (isUpperCase) R.drawable.ic_lower_case else R.drawable.ic_upper_case
        )
    }

    override fun onClick(delegate: EditTextDelegate) {
        val text = delegate.getText()
        val newText = when(isUpperCase) {
            true -> text.toLowerCase()
            else -> text.toUpperCase()
        }
        delegate.setText(newText)
        isUpperCase = !isUpperCase
        wrapper.setBehaviour(this)
    }

    override fun onChange(text: String) { /* no-op */ }
}