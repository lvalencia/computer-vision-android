package com.shootthebit.computervision

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import android.view.Surface
import androidx.core.app.ActivityCompat


private const val TAG: String = "CV\$CAMERA_OPERATOR"

class CameraCapture(
    private val context: Context,
    private val cameraManager: CameraManager
) {
    private val deviceStateCallback: CameraDevice.StateCallback
    private val captureCallback: CaptureCallback

    private lateinit var targetSurfaces: List<Surface>
    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequest: CaptureRequest
    private lateinit var captureSession: CameraCaptureSession

    private val hasPermission: Boolean
        get() {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }

    init {
        deviceStateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) {
                cameraDevice = device
                build()
            }

            override fun onDisconnected(device: CameraDevice) {
                device.close()
            }

            override fun onError(device: CameraDevice, error: Int) {
                device.close()
            }
        }
        captureCallback = object : CaptureCallback() {
            override fun onCaptureProgressed(
                session: CameraCaptureSession,
                request: CaptureRequest,
                partialResult: CaptureResult
            ) {
                // Do nothing for now
            }

            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                // Do nothing for now
            }
        }
    }


    fun start(cameraId: String, surfaces: List<Surface>) {
        if (::captureSession.isInitialized) {
            startCapture()
        } else {
            targetSurfaces = surfaces
            openCamera(cameraId)
        }
    }

    fun stop() {
        if (::captureSession.isInitialized) {
            stopCapture()
        }
    }

    fun destroy() {
        captureSession.close()
        cameraDevice.close()
    }

    private fun build() {
        val captureRequestBuilder: CaptureRequest.Builder =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        for (surface: Surface in targetSurfaces) {
            captureRequestBuilder.addTarget(surface)
        }

        cameraDevice.createCaptureSession(
            targetSurfaces,
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Configuration Failed")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session

                    captureRequestBuilder.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )

                    captureRequestBuilder.set(
                        CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                    )

                    captureRequest = captureRequestBuilder.build()
                    startCapture()

                }
            },
            backgroundHandler
        )
    }

    private fun startCapture() {
        captureSession.setRepeatingRequest(
            captureRequest,
            captureCallback,
            backgroundHandler
        )
    }

    private fun stopCapture() {
        try {
            captureSession.stopRepeating()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera(cameraId: String) {
        if (hasPermission) {
            backgroundHandler = createHandler()

            cameraManager.openCamera(
                cameraId,
                deviceStateCallback,
                backgroundHandler
            )
        }
    }

    private fun createHandler(): Handler {
        cleanupThread()

        backgroundThread = HandlerThread("camera_session", Process.THREAD_PRIORITY_BACKGROUND)
        backgroundThread.start()
        return Handler(backgroundThread.looper)
    }

    private fun cleanupThread() {
        if (::backgroundThread.isInitialized) {
            backgroundThread.quitSafely()
        }
    }
}
