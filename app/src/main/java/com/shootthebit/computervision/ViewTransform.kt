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

    fun transform(viewWidth: Int, viewHeight: Int) {
        Log.i(TAG, "#transform")
        Log.v(TAG, "Current rotation: $rotationInDegrees")

        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale: Float = max(
                viewHeight.toFloat() / previewSize.height,
                viewWidth.toFloat() / previewSize.width
            )
            matrix.postScale(scale, scale, centerX, centerY)
            val calculatedRotation = 90 * (rotation - 2).toFloat()
            matrix.postRotate(calculatedRotation, centerX, centerY)
            Log.d(TAG, "#transform Rotated $calculatedRotation degrees")
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
            Log.d(TAG, "#transform Rotated 180 degrees")
        }
        view.setTransform(matrix)
    }
}

