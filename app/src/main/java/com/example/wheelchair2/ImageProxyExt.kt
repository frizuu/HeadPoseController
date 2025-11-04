package com.example.headposecontroller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy

fun ImageProxy.toBitmap(): Bitmap? {
    return when (format) {
        ImageFormat.YUV_420_888 -> yuv420888ToBitmap(this)
        ImageFormat.JPEG -> {
            val buffer = planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        else -> null
    }
}

private fun yuv420888ToBitmap(image: ImageProxy): Bitmap? {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)

    // Susun NV21 (VU interleaved)
    val uvPixelStride = image.planes[1].pixelStride
    val uvRowStride = image.planes[1].rowStride
    val width = image.width
    val height = image.height

    val uRow = ByteArray(uvRowStride)
    val vRow = ByteArray(uvRowStride)
    var pos = ySize

    for (row in 0 until height / 2) {
        uBuffer.get(uRow, 0, uvRowStride)
        vBuffer.get(vRow, 0, uvRowStride)
        var col = 0
        while (col < width / 2) {
            nv21[pos++] = vRow[col * uvPixelStride]
            nv21[pos++] = uRow[col * uvPixelStride]
            col++
        }
    }

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 90, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
