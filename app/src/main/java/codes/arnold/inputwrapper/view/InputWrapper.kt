package codes.arnold.inputwrapper.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.view.isInvisible
import codes.arnold.inputwrapper.R
import codes.arnold.inputwrapper.view.behaviours.*

class InputWrapper @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), InputWrapperDelegate {

    private companion object {
        const val END_BEHAVIOUR_NONE = 0
        const val END_BEHAVIOUR_CLEAR = 1
        const val END_BEHAVIOUR_PASSWORD = 2
        const val END_BEHAVIOUR_CUSTOM = 3
    }

    private lateinit var editText: EditText

    private var isViewEnabled = true
    private var isViewFocused = false
    private var isError = false
    private val behaviours = mutableMapOf<BehaviourAlignment, InputWrapperBehaviour>()

    private val labelView = TextView(context)
    private val inputLayout = FrameLayout(context)
    private val startBehaviourLayout = FrameLayout(context)
    private val endBehaviourLayout = FrameLayout(context)
    private val backgroundDrawable = InputWrapperBackgroundDrawable(context)

    private val stateResolver = InputWrapperStateResolver()

    private val editTextDelegate = object : EditTextDelegate {
        override fun getText(): String = editText.text.toString()
        override fun setText(text: String) = editText.setText(text)
    }

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
        initStartBehaviour()
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
            val endBehaviour = getInt(R.styleable.InputWrapper_endBehaviour, END_BEHAVIOUR_NONE)
            setEndBehaviourFromAttr(endBehaviour)
        }
    }

    private fun setEndBehaviourFromAttr(value: Int) {
        when (value) {
            END_BEHAVIOUR_CLEAR -> setBehaviour(ClearTextBehaviour(this))
        }
    }

    private fun initEditText() {
        editText.background = backgroundDrawable
        editText.setOnFocusChangeListener { _, hasFocus ->
            isViewFocused = hasFocus
            updateState()
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                behaviours.values.forEach { it.onChange(s.toString()) }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /* no-op */ }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /* no-op */ }
        })
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

    override fun setBehaviour(behaviour: InputWrapperBehaviour) {

        val state = behaviour.getState()
        behaviours[state.alignment] = behaviour
        val layout = when(state.alignment) {
            BehaviourAlignment.START -> startBehaviourLayout
            BehaviourAlignment.END -> endBehaviourLayout
        }
        layout.isInvisible = !state.isVisible
        state.drawableRes?.let { layout.setBackgroundResource(it) }

        layout.setOnClickListener { behaviour.onClick(editTextDelegate) }
    }

}