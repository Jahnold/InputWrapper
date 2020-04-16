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
import codes.arnold.inputwrapper.view.behaviours.InputWrapperBehaviour
import codes.arnold.inputwrapper.view.behaviours.InputWrapperDelegate

class InputWrapper @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), InputWrapperDelegate {

    private lateinit var editText: EditText

    private var isViewEnabled = true
    private var isViewFocused = false
    private var isError = false

    private val labelView = TextView(context)
    private val inputLayout = FrameLayout(context)
    private val startBehaviourLayout = FrameLayout(context)
    private val endBehaviourLayout = FrameLayout(context)
    private val backgroundDrawable = InputWrapperBackgroundDrawable(context)

    private val stateResolver = InputWrapperStateResolver()

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
        initEndBehaviour()
        initLabel()
        updateState()
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams) {
        if (child is EditText) {

            val flp = FrameLayout.LayoutParams(params)
            flp.gravity = Gravity.CENTER_VERTICAL or (flp.gravity and Gravity.VERTICAL_GRAVITY_MASK.inv())
            inputLayout.addView(child, flp)
            inputLayout.layoutParams = params
            editText = child
            editText.isEnabled = isViewEnabled
        } else {
            super.addView(child, index, params)
        }
    }


    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        isViewEnabled = enabled
        updateState()
    }

    private fun initAttrs(attrs: AttributeSet) {
        context.withStyledAttributes(attrs, R.styleable.InputWrapper) {
            label = getString(R.styleable.InputWrapper_iw_label)
            isViewEnabled = getBoolean(R.styleable.InputWrapper_android_enabled, true)
        }
    }

    private fun initEditText() {
        editText.background = backgroundDrawable
        editText.setOnFocusChangeListener { _, hasFocus ->
            isViewFocused = hasFocus
            updateState()
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun initEndBehaviour() {
        endBehaviourLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.END or Gravity.RIGHT or Gravity.CENTER_VERTICAL
        )
        inputLayout.addView(endBehaviourLayout)
    }

    @SuppressLint("RtlHardcoded")
    private fun initStartBehaviour() {
        startBehaviourLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.START or Gravity.LEFT or Gravity.CENTER_VERTICAL
        )
        inputLayout.addView(startBehaviourLayout)
    }

    private fun initLabel() {
        labelView.isGone = label == null
        labelView.text = label
        addView(labelView, 0)
    }

    private fun updateState() {
        val state = stateResolver.resolveState(isViewFocused, isViewEnabled, isError)
        backgroundDrawable.borderColour = state.borderColour
        backgroundDrawable.backgroundColour = state.backgroundColour
        editText.setTextColor(state.textColour)
    }

    override fun update(behaviour: InputWrapperBehaviour) {
        TODO("not implemented")
    }

}