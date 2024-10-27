package com.firstprog.pose_detection_mlkit.main.ViewModel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.pose.PoseLandmark

class PoseOverlayView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null,  defStyleAttr: Int = 0
) : View(context,attrs, defStyleAttr) {

    private var landmarks: List<PoseLandmark> = emptyList()
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 8f
        style = Paint.Style.FILL
    }

    fun setPoseLandmarks(landmarks: List<PoseLandmark>) {
        this.landmarks = landmarks
        invalidate() // Redesenha a tela
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return

        landmarks.forEach { landmark ->
            canvas.drawCircle(landmark.position.x, landmark.position.y, 10f, paint)
        }

        if (landmarks.isNotEmpty()) {
            drawConnections(canvas, landmarks)
        }
    }

    private fun drawConnections(canvas: Canvas, landmarks: List<PoseLandmark>) {
        val connections = listOf(
            PoseLandmark.LEFT_SHOULDER to PoseLandmark.LEFT_ELBOW,
            PoseLandmark.LEFT_ELBOW to PoseLandmark.LEFT_WRIST,
            PoseLandmark.RIGHT_SHOULDER to PoseLandmark.RIGHT_ELBOW,
            PoseLandmark.RIGHT_ELBOW to PoseLandmark.RIGHT_WRIST,
            PoseLandmark.LEFT_SHOULDER to PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.LEFT_HIP to PoseLandmark.RIGHT_HIP,
        )

        val linePaint = Paint().apply {
            color = Color.BLUE
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

        for ((startType, endType) in connections) {
            val startLandmark = landmarks.find { it.landmarkType == startType }
            val endLandmark = landmarks.find { it.landmarkType == endType }

            if (startLandmark != null && endLandmark != null) {
                val start = startLandmark.position
                val end = endLandmark.position
                canvas.drawLine(start.x, start.y, end.x, end.y, linePaint)
            }
        }
    }
}
