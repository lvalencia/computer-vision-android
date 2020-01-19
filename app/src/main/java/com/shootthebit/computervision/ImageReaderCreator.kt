package com.shootthebit.computervision

import android.graphics.ImageFormat
import android.media.ImageReader
import android.util.Size

const val YUV_IMAGE = ImageFormat.YUV_420_888 // Supported Format
const val MAX_IMAGES_READS =  2

fun createImageReader(imageSize: Size, format: Int = YUV_IMAGE, maxReads: Int = MAX_IMAGES_READS): ImageReader {
    return ImageReader.newInstance(
        imageSize.getWidth(),
        imageSize.getHeight(),
        format,
        maxReads
    )
}