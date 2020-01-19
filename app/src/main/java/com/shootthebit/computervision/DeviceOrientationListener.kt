package com.shootthebit.computervision

import android.content.Context
import android.util.Log
import android.view.OrientationEventListener

private const val TAG = "CV\$ORIENTATION_LISTENER"

class DeviceOrientationListener(context: Context) : OrientationEventListener(context) {
    override fun onOrientationChanged(orientation: Int) {
        Log.i(TAG, "#onOrientationChanged: $orientation")
        if (orientation == ORIENTATION_UNKNOWN) return
    }
}
