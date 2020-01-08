package com.shootthebit.computervision

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.TextureView
import androidx.core.app.ActivityCompat

const val CAMERA_REQUEST_CODE = 1

class MainActivity : Activity() {
    lateinit var cameraView: TextureView
    lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView = findViewById(R.id.camera_view)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )

        cameraManager = (getSystemService(Context.CAMERA_SERVICE) as CameraManager)
    }
}
