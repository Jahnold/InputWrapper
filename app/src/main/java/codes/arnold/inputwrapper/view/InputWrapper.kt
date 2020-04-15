package codes.arnold.inputwrapper.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.view.isGone
import codes.arnold.inputwrapper.R

class InputWrapper @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var editText: EditText

    private val labelView = TextView(context)
    private val inputLayout = FrameLayout(context)
    private val endButtonLayout = FrameLayout(context)
    private val backgroundDrawable = InputWrapperBackgroundDrawable(context)

    var label: String? = null
        set(value) {
            field = value
            labelView.isGone = label == null
            labelView.text = label
        }

    init {
        orientation = VERTICAL
        addView(inputLayout)
        attrs?.let { initAttrs(it) }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initEditText()
        initEndButton()
        initLabel()
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams) {
        if (child is EditText) {

            val flp = FrameLayout.LayoutParams(params)
            flp.gravity = Gravity.CENTER_VERTICAL or (flp.gravity and Gravity.VERTICAL_GRAVITY_MASK.inv())
            inputLayout.addView(child, flp)
            inputLayout.layoutParams = params
            editText = child
        } else {
            super.addView(child, index, params)
        }
    }

    private fun initAttrs(attrs: AttributeSet) {
        context.withStyledAttributes(attrs, R.styleable.InputWrapper) {
            label = getString(R.styleable.InputWrapper_iw_label)
        }
    }

    private fun initEditText() {
        editText.background = backgroundDrawable
        editText.setOnFocusChangeListener { _, hasFocus -> setFocus(hasFocus) }
    }

    @SuppressLint("RtlHardcoded")
    private fun initEndButton() {
        endButtonLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.END or Gravity.RIGHT or Gravity.CENTER_VERTICAL
        )
        inputLayout.addView(endButtonLayout)
    }

    private fun initLabel() {
        labelView.isGone = label == null
        labelView.text = label
        addView(labelView, 0)
    }

    private fun setFocus(hasFocus: Boolean) {

    }
}