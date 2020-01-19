package com.shootthebit.computervision

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Log
import android.util.Size

private const val NO_CAMERA = "NO_CAMERA"
private const val TAG = "CV\$CAMERA_INFO"

class CameraInfo(
    val imageSize: Size,
    val cameraId: String
)

fun extractCameraInfo(cameraManager: CameraManager, cameraFacing: Int): CameraInfo {
    val cameraIdList: Array<String> = cameraManager.cameraIdList
    val numberOfCameras = cameraIdList.size
    Log.i(TAG, "Number of cameras on device: $numberOfCameras")

    var previewSize: Size? = null
    var cameraId: String? = null

    for (camera: String in cameraIdList) {
        Log.v(TAG, "Camera Id: $camera")
        val cameraCharacteristics: CameraCharacteristics =
            cameraManager.getCameraCharacteristics(camera)

        val lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
        Log.v(TAG, "Lens facing: $lensFacing")

        if (lensFacing == cameraFacing) {
            val streamConfigurationMap: StreamConfigurationMap? =
                cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            previewSize = bestResolution(streamConfigurationMap!!.getOutputSizes(SurfaceTexture::class.java))
            cameraId = camera
            break
        }
    }

    return CameraInfo(
        previewSize ?: Size(0,0),
        cameraId ?: NO_CAMERA
    )
}



private fun bestResolution(outputSizes: Array<Size>): Size {
    // Best Resolution for outputSizes is first element
    return outputSizes.first()
}