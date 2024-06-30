package dev.poropi.gpt4oscouter.screens.main

import android.graphics.Bitmap
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Base64
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.poropi.gpt4oscouter.repository.OpenAiRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * ViewModel for the MainScreen.
 * This ViewModel is responsible for fetching the battle point for a given image and updating the state variables accordingly.
 * @param repository The repository used to fetch the battle point.
 * @constructor Creates a new MainViewModel with the given repository.
 *
 * @property repository The repository used to fetch the battle point.
 * @property bpState The state variable for the battle point.
 * @property nameState The state variable for the name of the character.
 * @property descState The state variable for the description of the character.
 * @property imageState The state variable for the image of the character.
 * @property enableCameraState The state variable for enabling/disabling the camera.
 * @property isLoading The state variable for loading state.
 *
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: OpenAiRepository): ViewModel() {

    /**
     * State variables for the name, description, and battle point (bp) of the character.
     */
    val bpState = mutableStateOf("0")
    val nameState = mutableStateOf("")
    val descState = mutableStateOf("")
    val imageState: MutableState<Bitmap?> = mutableStateOf(null)
    val enableCameraState: MutableState<Boolean> = mutableStateOf(true)
    val isLoading = mutableStateOf(false)

    val toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME)

    /**
     * Fetches the battle point for a given bitmap image.
     *
     * This function takes a bitmap image, converts it to a base64 string, and sends it to the repository to fetch the chat.
     * The response from the repository is then parsed to extract the name, job, race, battle point (bp), and description.
     * These values are then used to update the corresponding state variables.
     *
     * @param bitmap The bitmap image for which the battle point is to be fetched.
     */
    fun fetchBattlePoint(bitmap: Bitmap) {
        imageState.value = bitmap
        isLoading.value = true
        val base64Image = encodeImageToBase64(bitmap).getOrNull()?:return
        viewModelScope.launch {
            val res = repository.fetchChat(base64Image)
            isLoading.value = false
            if (res.isFailure) {
                Timber.e(res.exceptionOrNull())
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 1000)
                enableCameraState.value = true
                imageState.value = null
                bpState.value = "0"
                nameState.value = ""
                descState.value = ""
                return@launch
            }
            val body = res.getOrNull()?:return@launch
            val choice = body.choices.getOrNull(0)?:return@launch
            // 名前,職業,種族,戦闘力,解説
            val contents = choice.message.content.split(",").toTypedArray()
            val name = contents.getOrNull(0)
            val job = contents.getOrNull(1)
            val race = contents.getOrNull(2)
            val bp = contents.getOrNull(3)
            val description = contents.getOrNull(4)
            bpState.value = bp?:"0"
            if (name != null && name != "不明") {
                nameState.value = name
            } else {
                nameState.value = "$race($job)"
            }
            descState.value = description?:""
        }
    }

    /**
     * Encodes a bitmap image to a base64 string.
     *
     * This function takes a bitmap image and converts it to a base64 string.
     *
     * @param bitmap The bitmap image to be encoded.
     * @return The base64 string representation of the bitmap image.
     */
    private fun encodeImageToBase64(bitmap: Bitmap): Result<String> {
        return runCatching {
            // BitmapをByteArrayに変換
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()

            // ByteArrayをBase64にエンコード
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }
}