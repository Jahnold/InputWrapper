package codes.arnold.inputwrapper.view

interface CustomStateResolver {

    fun resolveState(isFocused: Boolean, isEnabled: Boolean, isError: Boolean): InputWrapperState
}