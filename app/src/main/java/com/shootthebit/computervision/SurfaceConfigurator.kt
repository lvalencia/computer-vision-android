package com.shootthebit.computervision

import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import android.view.TextureView

fun configureCameraViewSurface(size: Size, view: TextureView): Surface {
    val viewSurfaceTexture: SurfaceTexture = view.surfaceTexture
    viewSurfaceTexture.setDefaultBufferSize(
        size.width,
        size.height
    )
    return Surface(viewSurfaceTexture)
}