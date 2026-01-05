package com.example.semestralproject

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class WeightChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.chart_primary)
        strokeWidth = 6f
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val pointPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.chart_primary)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val pointStrokePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 4f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val gridPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.chart_grid)
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.md_theme_light_onSurfaceVariant)
        textSize = 32f
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    private val labelTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.md_theme_light_onSurface)
        textSize = 36f
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val fillPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.chart_primary)
        alpha = 40
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
            // Empty state
            val emptyText = "Žádná data"
            val emptyTextWidth = textPaint.measureText(emptyText)
            canvas.drawText(
                emptyText,
                (width - emptyTextWidth) / 2f,
                height / 2f,
                textPaint
            )
            return
        }

        val padding = 100f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding

        // Najdeme min a max hodnoty
        val weights = weightData.map { it.weight }
        val minWeight = weights.minOrNull() ?: 0f
        val maxWeight = weights.maxOrNull() ?: 100f
        val weightRange = maxWeight - minWeight
        val safeRange = if (weightRange < 1) 10f else weightRange * 1.2f // Přidáme 20% pro lepší vzhled

        // Vykreslení mřížky
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + (chartHeight * i / gridLines)

            // Horizontální čára
            canvas.drawLine(padding, y, width - padding, y, gridPaint)

            // Popisek váhy
            val weight = maxWeight - (safeRange * i / gridLines)
            val weightText = String.format("%.1f", weight)
            canvas.drawText(
                weightText,
                20f,
                y + 12f,
                textPaint
            )
        }

        // Vykreslení dat
        if (weightData.size == 1) {
            // Jeden bod
            val x = width / 2f
            val normalizedY = (maxWeight - weightData[0].weight) / safeRange
            val y = padding + normalizedY * chartHeight

            // Bod s outline
            canvas.drawCircle(x, y, 14f, pointStrokePaint)
            canvas.drawCircle(x, y, 10f, pointPaint)

            // Label
            val labelText = String.format("%.1f kg", weightData[0].weight)
            val labelWidth = labelTextPaint.measureText(labelText)
            canvas.drawText(labelText, x - labelWidth / 2f, y - 20f, labelTextPaint)
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

                // Bod s outline
                canvas.drawCircle(x, y, 12f, pointStrokePaint)
                canvas.drawCircle(x, y, 8f, pointPaint)

                // Label na první a poslední bod
                if (index == 0 || index == weightData.size - 1) {
                    val labelText = String.format("%.1f kg", record.weight)
                    val labelWidth = labelTextPaint.measureText(labelText)
                    canvas.drawText(labelText, x - labelWidth / 2f, y - 20f, labelTextPaint)
                }
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
            canvas.drawText(firstDate, padding, height - 30f, textPaint)

            // Poslední datum
            val lastDate = weightData.last().date.split(" ")[0]
            val textWidth = textPaint.measureText(lastDate)
            canvas.drawText(lastDate, width - padding - textWidth, height - 30f, textPaint)
        }
    }
}