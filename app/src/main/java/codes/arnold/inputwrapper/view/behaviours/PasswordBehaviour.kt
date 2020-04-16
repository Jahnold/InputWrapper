package codes.arnold.inputwrapper.view.behaviours

import codes.arnold.inputwrapper.R

class PasswordBehaviour(
    wrapper: InputWrapperDelegate,
    private val mode: Mode
): InputWrapperBehaviour(wrapper) {

    private var isPasswordVisible = false

    override fun getState(): BehaviourState {
        return BehaviourState(
            alignment = BehaviourAlignment.END,
            text = getText(),
            drawableRes = getDrawable()
        )
    }

    private fun getText(): String? {
        if (mode == Mode.ICON) return null
        return if (isPasswordVisible) "hide" else "show"
    }

    private fun getDrawable(): Int? {
        if (mode == Mode.TEXT) return null
        return when (isPasswordVisible) {
            true -> R.drawable.ic_password_hide
            else -> R.drawable.ic_password_show
        }
    }

    override fun onClick(delegate: EditTextDelegate) {
        isPasswordVisible = !isPasswordVisible
        delegate.passwordVisible(isPasswordVisible)
        wrapper.setBehaviour(this)
    }

    override fun onChange(text: String) { /* no-op */ }

    override fun onError(isError: Boolean) {
        TODO("not implemented")
    }

    enum class Mode {
        TEXT, ICON
    }
}