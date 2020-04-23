package codes.arnold.inputwrapper.view

import codes.arnold.inputwrapper.R

class InputWrapperStateResolver {

    companion object {
        val DISABLED = InputWrapperState(
            borderColour = R.color.elephant_grey,
            backgroundColour = R.color.storm_grey,
            textColour = R.color.elephant_grey
        )

        val FOCUSED = InputWrapperState(
            borderColour = R.color.apple_green,
            backgroundColour = R.color.white,
            textColour = R.color.elephant_grey
        )

        val ERROR = InputWrapperState(
            borderColour = R.color.red,
            backgroundColour = R.color.white,
            textColour = R.color.elephant_grey
        )

        val DEFAULT = InputWrapperState(
            borderColour = R.color.elephant_grey,
            backgroundColour = R.color.white,
            textColour = R.color.elephant_grey
        )
    }

    fun resolveState(isFocused: Boolean, isEnabled: Boolean, isError: Boolean, custom: CustomStateResolver? = null): InputWrapperState {

        if (custom != null) {
            return custom.resolveState(isFocused, isEnabled, isError)
        }

        return when {
            !isEnabled -> DISABLED
            isError -> ERROR
            isFocused -> FOCUSED
            else -> DEFAULT
        }
    }
}