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
 * MainScreenのViewModel。
 * このViewModelは、指定された画像のバトルポイントを取得し、それに応じて状態変数を更新する責任があります。
 *
 * @property repository バトルポイントを取得するために使用されるリポジトリ。
 * @property bpState キャラクターのバトルポイントの状態変数。
 * @property nameState キャラクターの名前の状態変数。
 * @property descState キャラクターの説明の状態変数。
 * @property imageState キャラクターの画像の状態変数。
 * @property enableCameraState カメラの有効/無効の状態変数。
 * @property isLoading ローディング状態の状態変数。
 *
 * @constructor 指定されたリポジトリを使用して新しいMainViewModelを作成します。
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: OpenAiRepository): ViewModel() {

    val bpState = mutableStateOf("0")
    val nameState = mutableStateOf("")
    val descState = mutableStateOf("")
    val imageState: MutableState<Bitmap?> = mutableStateOf(null)
    val enableCameraState: MutableState<Boolean> = mutableStateOf(true)
    val isLoading = mutableStateOf(false)

    val toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME)

    /**
     * 指定されたビットマップ画像のバトルポイントを取得します。
     *
     * この関数はビットマップ画像を取り、それをbase64文字列に変換し、チャットを取得するためにリポジトリに送信します。
     * リポジトリからの応答は解析され、名前、職業、種族、バトルポイント（bp）、説明を抽出します。
     * これらの値は、対応する状態変数を更新するために使用されます。
     *
     * @param bitmap バトルポイントを取得するためのビットマップ画像。
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
     * ビットマップ画像をbase64文字列にエンコードします。
     *
     * この関数はビットマップ画像を取り、それをbase64文字列に変換します。
     *
     * @param bitmap エンコードするビットマップ画像。
     * @return ビットマップ画像のbase64文字列表現。
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