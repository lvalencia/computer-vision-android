package com.shootthebit.computervision

import android.graphics.Matrix
import android.graphics.RectF
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import kotlin.math.max

private const val TAG = "CV\$VIEW_TRANSFORM"

class ViewTransform(
    private val windowManager: WindowManager,
    private val view: TextureView,
    private val cameraInfo: CameraInfo
) {
    private val rotation: Int
        get() {
            return windowManager.defaultDisplay.rotation
        }

    private val previewSize: Size
        get() {
            return cameraInfo.imageSize
        }

    private val rotationInDegrees: Int
        get() {
            return when (rotation) {
                Surface.ROTATION_270 -> 270
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_90 -> 90
                else -> 0
            }
        }

    fun transform(width: Int, height: Int) {
        Log.i(TAG, "#transform")
        Log.v(TAG, "Current rotation: $rotationInDegrees")

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val previewHeight = previewSize.height.toFloat()
        val previewWidth = previewSize.width.toFloat()

        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth, viewHeight)
        val bufferRect = RectF(0f, 0f, previewHeight, previewWidth)

        val viewCenterX: Float = viewRect.centerX()
        val viewCenterY: Float = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            val bufferCenterX: Float = bufferRect.centerX()
            val bufferCenterY: Float = bufferRect.centerY()

            bufferRect.offset(viewCenterX - bufferCenterX, viewCenterY - bufferCenterY)
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)

            val scale: Float = max(
                viewHeight / previewHeight,
                viewWidth / previewWidth
            )
            matrix.postScale(scale, scale, viewCenterX, viewCenterY)

            val postRotation = 90f * (rotation - 2).toFloat()
            matrix.postRotate(postRotation, viewCenterX, viewCenterY)

            Log.d(TAG, "Rotated $postRotation degrees")
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, viewCenterX, viewCenterY)
            Log.d(TAG, "Rotated 180 degrees")
        }

        view.setTransform(matrix)
    }
}

