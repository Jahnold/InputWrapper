package codes.arnold.inputwrapper.view.behaviours

data class BehaviourState(
    val alignment: BehaviourAlignment = BehaviourAlignment.END,
    val drawableRes: Int? = null,
    val drawableTint: Int? = null,
    val text: String? = null,
    val textColour: Int? = null,
    val isVisible: Boolean = true
)

enum class BehaviourAlignment {
    START, END
}