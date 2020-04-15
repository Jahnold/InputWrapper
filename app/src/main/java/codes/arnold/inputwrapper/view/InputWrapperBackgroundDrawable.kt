package codes.arnold.inputwrapper.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import codes.arnold.inputwrapper.R

class InputWrapperBackgroundDrawable(private val context: Context): Drawable() {

    companion object {
        private const val RADIUS = 8f
        private const val BORDER_WIDTH = 2f
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    @ColorRes var backgroundColour: Int = R.color.white
        set(value) {
            field = value
            initPaints()
        }
    @ColorRes var borderColour: Int = R.color.elephant_grey
        set(value) {
            field = value
            initPaints()
        }

    init {
        initPaints()
    }

    override fun draw(canvas: Canvas) {
        rect.set(bounds)
        canvas.drawRoundRect(rect, RADIUS, RADIUS, backgroundPaint)
        canvas.drawRoundRect(rect, RADIUS, RADIUS, borderPaint)
    }

    private fun initPaints() {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = ContextCompat.getColor(context, borderColour)
        borderPaint.strokeWidth = BORDER_WIDTH
        borderPaint.strokeCap = Paint.Cap.ROUND

        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = ContextCompat.getColor(context, backgroundColour)
    }

    override fun setAlpha(alpha: Int) { /* no-op */ }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) { /* no-op */ }
}