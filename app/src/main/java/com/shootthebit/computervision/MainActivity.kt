package com.shootthebit.computervision

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat
import java.util.*

private const val TAG: String = "CV\$MAIN_ACTIVITY"
private const val CAMERA_REQUEST_CODE = 1

class MainActivity : Activity() {
    private lateinit var cameraView: TextureView
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraCapture: CameraCapture
    private lateinit var cameraInfo: CameraInfo
    private var cameraFacing = CameraCharacteristics.LENS_FACING_FRONT

    val surfaces: List<Surface>
        get() {
            val cameraViewSurface = configureCameraViewSurface(cameraInfo.imageSize, cameraView)
            return Arrays.asList(cameraViewSurface)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "#onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        cameraView = findViewById(R.id.camera_view)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        cameraInfo = extractCameraInfo(
            cameraManager,
            cameraFacing
        )

        cameraCapture = CameraCapture(
            this,
            cameraManager
        )
    }

    override fun onResume() {
        Log.d(TAG, "#onResume")
        super.onResume()

        if (cameraView.isAvailable) {
            cameraCapture.start(cameraInfo.cameraId, surfaces)
        } else {
            cameraView.surfaceTextureListener = object: TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    Log.d(TAG, "#onSurfaceTextureAvailable")
                    cameraCapture.start(cameraInfo.cameraId, surfaces)
                }
                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    Log.d(TAG, "#onSurfaceTextureDestroyed")
                    cameraCapture.stop()
                    return true
                }
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }
        }
    }

    override fun onPause() {
        Log.d(TAG, "#onPause")

        super.onPause()
        cameraCapture.stop()
    }

    override fun onDestroy() {
        Log.d(TAG, "#onDestroy")

        super.onDestroy()
        cameraCapture.destroy()
    }
}
