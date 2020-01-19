package com.shootthebit.computervision

import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import kotlin.math.ceil

private const val TAG = "CV\$IMAGE_ORIENTATION"

class ImageOrientation(private val surfaceRotation: Int, private val cameraInfo: CameraInfo) {
    private val deviceOrientation: Int
        get() {
            return when (surfaceRotation) {
                Surface.ROTATION_270 -> 270
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_90 -> 90
                else -> 0
            }
        }

    fun calculate(): Int {
        Log.i(TAG, "Sensor Orientation: ${cameraInfo.sensorOrientation}")

        var deviceOrientation = deviceOrientation
        Log.i(TAG, "Device Orientation: $deviceOrientation")

        if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) return 0
        // Round device orientation to a multiple of 90
        deviceOrientation = ceil((deviceOrientation.toFloat() + 45f) / 90f).toInt() * 90
        // Reverse device orientation for front-facing cameras
        val facingFront = cameraInfo.lensFacing == CameraCharacteristics.LENS_FACING_FRONT
        if (facingFront) deviceOrientation = -deviceOrientation
        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        val imageOrientation = (cameraInfo.sensorOrientation + deviceOrientation + 360) % 360
        Log.i(TAG, "Image Orientation: $imageOrientation")

        return imageOrientation
    }
}

