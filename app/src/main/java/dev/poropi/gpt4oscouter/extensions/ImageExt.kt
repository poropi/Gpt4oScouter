package dev.poropi.gpt4oscouter.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import java.io.ByteArrayOutputStream

/**
 * Image拡張関数
 *
 * この関数は、ImageをBitmapに変換します。
 * 必要に応じて、Bitmapを回転させることもできます。
 *
 * @param rotationDegrees Bitmapを回転させる角度（デフォルトは0）。
 * @return 変換されたBitmap。
 */
fun Image.toBitmap(rotationDegrees: Int = 0): Bitmap {
    val data = imageToByteArray(this)
    val bitmap = BitmapFactory.decodeByteArray(data, 0, (data?.size?:0))
    return if (rotationDegrees == 0) {
        bitmap
    } else {
        rotateBitmap(bitmap, rotationDegrees)
    }
}

/**
 * Bitmapの回転
 *
 * この関数は、指定された角度でBitmapを回転させます。
 *
 * @param bitmap 回転させるBitmap。
 * @param rotationDegrees Bitmapを回転させる角度。
 * @return 回転させたBitmap。
 */
fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    val mat = Matrix()
    mat.postRotate(rotationDegrees.toFloat())
    return Bitmap.createBitmap(
        bitmap, 0, 0,
        bitmap.width, bitmap.height, mat, true
    )
}

/**
 * Image → JPEGのバイト配列
 *
 * この関数は、ImageをJPEGのバイト配列に変換します。
 *
 * @param image 変換するImage。
 * @return 変換されたJPEGのバイト配列。
 */
fun imageToByteArray(image: Image): ByteArray? {
    var data: ByteArray? = null
    if (image.format == ImageFormat.JPEG) {
        val planes = image.planes
        val buffer = planes[0].buffer
        data = ByteArray(buffer.capacity())
        buffer[data]
        return data
    } else if (image.format == ImageFormat.YUV_420_888) {
        data = NV21toJPEG(
            YUV_420_888toNV21(image),
            image.width, image.height
        )
    }
    return data
}

/**
 * YUV_420_888 → NV21
 *
 * この関数は、ImageFormatがYUV_420_888のImageをNV21形式のバイト配列に変換します。
 *
 * @param image 変換するImage。
 * @return 変換されたNV21形式のバイト配列。
 */
fun YUV_420_888toNV21(image: Image): ByteArray {
    val nv21: ByteArray
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer[nv21, 0, ySize]
    vBuffer[nv21, ySize, vSize]
    uBuffer[nv21, ySize + vSize, uSize]
    return nv21
}

/**
 * NV21 → JPEG
 *
 * この関数は、NV21形式のバイト配列をJPEGに変換します。
 *
 * @param nv21 変換するNV21形式のバイト配列。
 * @param width 画像の幅。
 * @param height 画像の高さ。
 * @return 変換されたJPEGのバイト配列。
 */
fun NV21toJPEG(nv21: ByteArray, width: Int, height: Int): ByteArray {
    val out = ByteArrayOutputStream()
    val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
    return out.toByteArray()
}