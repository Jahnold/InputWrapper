package codes.arnold.inputwrapper.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    private val errorView = TextView(context)
    private val inputLayout = FrameLayout(context)
    private val backgroundDrawable = InputWrapperBackgroundDrawable(context)

    var label: String? = null
        set(value) {
            field = value
            labelView.isGone = label == null
            labelView.text = label
        }

    var labelTextStyle: Int = R.style.labelTextStyle
        set(value) {
            field = value
            labelView.setTextAppearance(context, labelTextStyle)
        }

    var errorText: String? = null
        set(value) {
            field = value
            errorView.isGone = errorText == null
            errorView.text = errorText
        }

    var errorTextStyle: Int = R.style.labelTextStyle
        set(value) {
            field = value
            errorView.setTextAppearance(context, errorTextStyle)
        }

    init {
        orientation = VERTICAL
        addView(inputLayout)
        attrs?.let { initAttrs(it) }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initEditText()
        initLabel()
        initErrorText()
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
            label = getString(R.styleable.InputWrapper_android_label)
            labelTextStyle = getResourceId(R.styleable.InputWrapper_labelTextStyle, R.style.labelTextStyle)
            errorTextStyle = getResourceId(R.styleable.InputWrapper_errorTextStyle, R.style.errorTextStyle)
        }
    }

    private fun initEditText() {
        editText.background = backgroundDrawable
    }

    private fun initLabel() {
        labelView.isGone = label == null
        labelView.text = label
        labelView.setTextAppearance(context, labelTextStyle)
        addView(labelView, 0)
    }

    private fun initErrorText() {
        errorView.isGone = errorText == null
        errorView.text = errorText
        errorView.setTextAppearance(context, errorTextStyle)
        addView(errorView)
    }

    private fun Int.toColor(): Int {
        return ContextCompat.getColor(context, this)
    }

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}