package codes.arnold.inputwrapper.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import androidx.core.view.doOnLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import codes.arnold.inputwrapper.R
import codes.arnold.inputwrapper.view.behaviours.*
import java.lang.IllegalStateException

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

        const val START_BEHAVIOUR_NONE = 0
        const val START_BEHAVIOUR_PREFIX = 1

        const val PASSWORD_TYPE_TEXT = 0
        const val PASSWORD_TYPE_ICON = 1
    }

    private lateinit var editText: EditText

    private var isViewEnabled = true
    private var isViewFocused = false
    private var isError = false
    private val behaviours = mutableMapOf<BehaviourAlignment, InputWrapperBehaviour>()
    private var validator: (String) -> Boolean = { true }

    private val labelView = TextView(context)
    private val inputLayout = FrameLayout(context)
    private val innerInputLayout = FrameLayout(context)
    private val startBehaviourLayout = TextView(context)
    private val endBehaviourLayout = TextView(context)
    private val backgroundDrawable = InputWrapperBackgroundDrawable(context)

    private val stateResolver = InputWrapperStateResolver()

    private val editTextDelegate = object : EditTextDelegate {
        override fun getEditText(): EditText  = editText
        override fun getText(): String = editText.text.toString()
        override fun setText(text: String) = editText.setText(text)
        override fun passwordVisible(visible: Boolean) {
            editText.transformationMethod = when (visible) {
                true -> HideReturnsTransformationMethod.getInstance()
                else -> PasswordTransformationMethod.getInstance()
            }
        }
    }

    var label: String? = null
        set(value) {
            field = value
            labelView.isGone = label == null
            labelView.text = label
        }

    init {
        orientation = VERTICAL
        inputLayout.background = backgroundDrawable
        inputLayout.addView(innerInputLayout)
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
            innerInputLayout.addView(child, flp)
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

    fun setError(isError: Boolean) {
        this.isError = isError
        behaviours.values.forEach { it.onError(isError) }
    }

    private fun initAttrs(attrs: AttributeSet) {
        context.withStyledAttributes(attrs, R.styleable.InputWrapper) {
            label = getString(R.styleable.InputWrapper_iw_label)
            isViewEnabled = getBoolean(R.styleable.InputWrapper_android_enabled, true)

            setStartBehaviourFromAttr(this)
            setEndBehaviourFromAttr(this)
        }
    }

    private fun setStartBehaviourFromAttr(typedArray: TypedArray) {

        when (typedArray.getInt(R.styleable.InputWrapper_startBehaviour, START_BEHAVIOUR_NONE)) {
            START_BEHAVIOUR_PREFIX -> {
                val prefix = typedArray.getString(R.styleable.InputWrapper_prefix).orEmpty()
                val behaviour = PrefixBehaviour(this).also { it.prefix = prefix }
                setBehaviour(behaviour)
            }
        }
    }

    private fun setEndBehaviourFromAttr(typedArray: TypedArray) {

        when (typedArray.getInt(R.styleable.InputWrapper_endBehaviour, END_BEHAVIOUR_NONE)) {
            END_BEHAVIOUR_CLEAR -> {
                setBehaviour(ClearTextBehaviour(this))
            }
            END_BEHAVIOUR_PASSWORD -> {
                val passwordToggleType = typedArray.getInt(
                    R.styleable.InputWrapper_passwordToggleType,
                    PASSWORD_TYPE_TEXT
                )
                val mode = when (passwordToggleType) {
                    PASSWORD_TYPE_TEXT -> PasswordBehaviour.Mode.TEXT
                    PASSWORD_TYPE_ICON -> PasswordBehaviour.Mode.ICON
                    else -> throw IllegalStateException("invalid mode $passwordToggleType")
                }
                setBehaviour(PasswordBehaviour(this, mode))
            }
        }
    }

    private fun initEditText() {
        editText.background = null
        editText.setOnFocusChangeListener { _, hasFocus ->
            isViewFocused = hasFocus
            updateState()
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                behaviours.values.forEach { it.onChange(s.toString()) }
                setError(!validator.invoke(s.toString()))
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

        layout.background?.mutate()?.colorFilter = when (state.drawableTint) {
            null -> null
            else -> PorterDuffColorFilter(state.drawableTint.toColor(), PorterDuff.Mode.SRC_ATOP)
        }

        layout.text = state.text
        layout.setTextColor(state.textColour.toColor())

        layout.setOnClickListener { behaviour.onClick(editTextDelegate) }
        layout.doOnLayout {
            adjustPaddingForBehaviour(behaviour)
        }
    }

    private fun adjustPaddingForBehaviour(behaviour: InputWrapperBehaviour) {

        val top = innerInputLayout.paddingTop
        val bottom = innerInputLayout.paddingBottom

        val left = when (behaviour.getState().alignment) {
            BehaviourAlignment.START -> startBehaviourLayout.width
            BehaviourAlignment.END -> innerInputLayout.paddingLeft
        }

        val right = when (behaviour.getState().alignment) {
            BehaviourAlignment.END -> endBehaviourLayout.width
            BehaviourAlignment.START -> innerInputLayout.paddingLeft
        }
        innerInputLayout.setPadding(left, top, right, bottom)
    }

    fun setValidator(validator: (String) -> Boolean) {
        this.validator = validator
    }

    private fun Int.toColor(): Int {
        return ContextCompat.getColor(context, this)
    }
}