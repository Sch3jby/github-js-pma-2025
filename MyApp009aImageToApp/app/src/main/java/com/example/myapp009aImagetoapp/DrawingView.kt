package com.example.myapp009aImagetoapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var drawPath = Path()
    private var drawPaint = Paint()
    private var canvasPaint = Paint(Paint.DITHER_FLAG)
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null
    private var backgroundBitmap: Bitmap? = null

    // Seznam všech cest pro možnost undo
    private val paths = mutableListOf<Pair<Path, Paint>>()

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint.color = Color.RED
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = 10f
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            drawCanvas = Canvas(canvasBitmap!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Nakreslit pozadí (obrázek)
        backgroundBitmap?.let {
            canvas.drawBitmap(it, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null)
        }

        // Nakreslit uložené kreslení
        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, canvasPaint)
        }

        // Nakreslit aktuální cestu
        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX, touchY)
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, drawPaint)
                // Uložit cestu pro undo
                val newPath = Path(drawPath)
                val newPaint = Paint(drawPaint)
                paths.add(Pair(newPath, newPaint))
                drawPath.reset()
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun setBackgroundImage(bitmap: Bitmap) {
        backgroundBitmap = bitmap
        invalidate()
    }

    fun setBrushColor(color: Int) {
        drawPaint.color = color
    }

    fun setBrushSize(size: Float) {
        drawPaint.strokeWidth = size
    }

    fun clearDrawing() {
        paths.clear()
        drawPath.reset()
        canvasBitmap?.let {
            drawCanvas = Canvas(it)
            drawCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }
        invalidate()
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            paths.removeAt(paths.size - 1)
            redrawAllPaths()
        }
    }

    private fun redrawAllPaths() {
        canvasBitmap?.let {
            drawCanvas = Canvas(it)
            drawCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            for ((path, paint) in paths) {
                drawCanvas?.drawPath(path, paint)
            }
        }
        invalidate()
    }

    fun getCombinedBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Nakreslit pozadí
        backgroundBitmap?.let {
            canvas.drawBitmap(it, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null)
        }

        // Nakreslit kreslení
        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        return bitmap
    }
}