package com.example.semestralproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WeightChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint().apply {
        color = Color.parseColor("#6200EE")
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = Color.parseColor("#6200EE")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 1f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.GRAY
        textSize = 30f
        isAntiAlias = true
    }

    private val fillPaint = Paint().apply {
        color = Color.parseColor("#336200EE")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var weightData: List<WeightRecord> = emptyList()

    fun setData(data: List<WeightRecord>) {
        weightData = data.sortedBy { it.timestamp }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (weightData.isEmpty()) {
            canvas.drawText("Žádná data", width / 2f - 80f, height / 2f, textPaint)
            return
        }

        val padding = 80f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding

        // Najdeme min a max hodnoty
        val weights = weightData.map { it.weight }
        val minWeight = weights.minOrNull() ?: 0f
        val maxWeight = weights.maxOrNull() ?: 100f
        val weightRange = maxWeight - minWeight
        val safeRange = if (weightRange < 1) 10f else weightRange

        // Vykreslení mřížky
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + (chartHeight * i / gridLines)
            canvas.drawLine(padding, y, width - padding, y, gridPaint)

            val weight = maxWeight - (safeRange * i / gridLines)
            canvas.drawText(
                String.format("%.1f", weight),
                10f,
                y + 10f,
                textPaint
            )
        }

        // Vykreslení dat
        if (weightData.size == 1) {
            // Jeden bod
            val x = width / 2f
            val normalizedY = (maxWeight - weightData[0].weight) / safeRange
            val y = padding + normalizedY * chartHeight
            canvas.drawCircle(x, y, 10f, pointPaint)
        } else {
            // Cesta pro čáru a výplň
            val linePath = Path()
            val fillPath = Path()

            weightData.forEachIndexed { index, record ->
                val x = padding + (chartWidth * index / (weightData.size - 1))
                val normalizedY = (maxWeight - record.weight) / safeRange
                val y = padding + normalizedY * chartHeight

                if (index == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, height - padding)
                    fillPath.lineTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }

                // Bod
                canvas.drawCircle(x, y, 8f, pointPaint)
            }

            // Uzavření výplně
            val lastX = padding + chartWidth
            fillPath.lineTo(lastX, height - padding)
            fillPath.close()

            // Vykreslení výplně a čáry
            canvas.drawPath(fillPath, fillPaint)
            canvas.drawPath(linePath, linePaint)
        }

        // Popisky na ose X (datum)
        if (weightData.size >= 2) {
            // První datum
            val firstDate = weightData.first().date.split(" ")[0]
            canvas.drawText(firstDate, padding, height - 20f, textPaint)

            // Poslední datum
            val lastDate = weightData.last().date.split(" ")[0]
            val textWidth = textPaint.measureText(lastDate)
            canvas.drawText(lastDate, width - padding - textWidth, height - 20f, textPaint)
        }
    }
}