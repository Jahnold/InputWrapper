package codes.arnold.inputwrapper.view.behaviours

import codes.arnold.inputwrapper.R

data class BehaviourState(
    val alignment: BehaviourAlignment = BehaviourAlignment.END,
    val drawableRes: Int? = null,
    val drawableTint: Int? = null,
    val text: String? = null,
    val textColour: Int = R.color.default_text,
    val isVisible: Boolean = true
)

enum class BehaviourAlignment {
    START, END
}