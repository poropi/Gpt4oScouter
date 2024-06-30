package dev.poropi.gpt4oscouter.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume

/**
 * Extension function to take a picture using the ImageCapture use case.
 *
 * This function suspends until the picture is taken and returns the ImageProxy.
 *
 * @param executor The executor to run the callback on.
 * @return The ImageProxy of the captured image.
 */
suspend fun ImageCapture.takePicture(executor: Executor): ImageProxy =
    suspendCancellableCoroutine { continuation ->
        takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    continuation.resume(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    continuation.cancel(exception)
                }
            },
        )
    }

/**
 * Extension function to convert an ImageProxy to a rotated Bitmap.
 */
fun ImageProxy.toRotationBitmap(): Bitmap {
    val planeProxy = this.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer[bytes]
    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val matrix = Matrix()
    matrix.postRotate(this.imageInfo.rotationDegrees.toFloat())
    val bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, false)
    return bmp2
}