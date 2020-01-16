package com.shootthebit.computervision

import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import android.view.TextureView

fun configureCameraViewSurface(size: Size, view: TextureView): Surface {
    val viewSurfaceTexture: SurfaceTexture = view.surfaceTexture
    viewSurfaceTexture.setDefaultBufferSize(
        size.getWidth(),
        size.getHeight()
    )
    return Surface(viewSurfaceTexture)
}