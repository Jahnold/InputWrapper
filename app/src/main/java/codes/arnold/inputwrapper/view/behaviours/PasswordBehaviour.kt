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
            textColour = getTextColour(),
            drawableRes = getDrawable(),
            drawableTint = getDrawableTint()
        )
    }

    private fun getText(): String? {
        if (mode == Mode.ICON) return null
        return if (isPasswordVisible) "hide" else "show"
    }

    private fun getTextColour(): Int? {
        return when {
            mode == Mode.ICON -> null
            isError -> R.color.red
            else -> null
        }
    }

    private fun getDrawable(): Int? {
        if (mode == Mode.TEXT) return null
        return when (isPasswordVisible) {
            true -> R.drawable.ic_password_hide
            else -> R.drawable.ic_password_show
        }
    }

    private fun getDrawableTint(): Int? {
        return when {
            mode == Mode.TEXT -> null
            isError -> R.color.red
            else -> null
        }
    }

    override fun onClick(delegate: EditTextDelegate) {
        isPasswordVisible = !isPasswordVisible
        delegate.passwordVisible(isPasswordVisible)
        wrapper.setBehaviour(this)
    }

    override fun onChange(text: String) { /* no-op */ }

    enum class Mode {
        TEXT, ICON
    }
}