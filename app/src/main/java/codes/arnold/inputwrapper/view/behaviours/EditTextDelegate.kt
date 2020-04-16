package codes.arnold.inputwrapper.view.behaviours

import android.widget.EditText

interface EditTextDelegate {

    fun getEditText(): EditText
    fun getText(): String
    fun setText(text: String)
    fun passwordVisible(visible: Boolean)
}