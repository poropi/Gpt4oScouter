package dev.poropi.gpt4oscouter.screens.main

import android.media.ToneGenerator
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.tbsten.cameraxcompose.CameraPreview
import com.github.tbsten.cameraxcompose.usecasehelper.imageAnalysisUseCase
import com.github.tbsten.cameraxcompose.usecasehelper.imageCaptureUseCase
import com.github.tbsten.cameraxcompose.usecasehelper.previewUseCase
import com.github.tbsten.cameraxcompose.usecasehelper.videoCaptureUseCase
import dev.poropi.gpt4oscouter.extensions.takePicture
import dev.poropi.gpt4oscouter.extensions.toRotationBitmap
import dev.poropi.gpt4oscouter.repository.OpenAiRepository
import dev.poropi.gpt4oscouter.service.web.response.ChatCompletionResponse
import dev.poropi.gpt4oscouter.service.web.response.Choice
import dev.poropi.gpt4oscouter.service.web.response.Message
import dev.poropi.gpt4oscouter.service.web.response.Usage
import dev.poropi.gpt4oscouter.ui.theme.Gpt4oScouterTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MainScreenのコンポーザブル関数。
 *
 * このコンポーザブルは、アプリケーションのメイン画面を表示する責任があります。
 * カメラプレビュー、画像キャプチャロジック、キャラクターのバトルポイント、名前、説明を表示するためのUI要素を含みます。
 *
 * @param viewModel メイン画面の状態を管理するためのMainViewModel。デフォルトではhiltViewModel()が使用されます。
 */
@OptIn(ExperimentalGetImage::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val scope = rememberCoroutineScope()
    val executor = ContextCompat.getMainExecutor(context)

    val bp = remember { viewModel.bpState }
    val name = remember { viewModel.nameState }
    val image = remember { viewModel.imageState }
    val desc = remember { viewModel.descState }
    val enableCamera = remember { viewModel.enableCameraState }
    val isLoading = remember { viewModel.isLoading }

    LaunchedEffect(key1 = isLoading.value){
        while (isLoading.value){
            delay(50)
            bp.value = "${(10000..99999).random()}"
            // ビープ音を鳴らす
            viewModel.toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 30)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
        ) {
        CameraPreview(
            onBind = {
                // ユースケース
                val preview = previewUseCase()
                val analysis = imageAnalysisUseCase(executor) {}
                imageCapture = imageCaptureUseCase()
                val videoCapture = videoCaptureUseCase()
                // ユースケースをbindします
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis,
                    imageCapture,
                    videoCapture
                )
            },
        )
        image.value?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = "",
                modifier = Modifier,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0, 255, 0, 125)
                )
                .clickable {
                    if (!enableCamera.value) {
                        enableCamera.value = true
                        image.value = null
                        bp.value = "0"
                        name.value = ""
                        desc.value = ""
                    } else {
                        enableCamera.value = false
                        scope.launch {
                            val bitmap = imageCapture
                                ?.takePicture(executor)
                                ?.toRotationBitmap() ?: return@launch
                            viewModel.fetchBattlePoint(bitmap)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "POWER",
                    fontSize = 30.sp,
                    color = Color.Yellow,
                )
                Text(
                    text = bp.value,
                    fontSize = 30.sp,
                    color = Color.Yellow,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = name.value,
                    fontSize = 30.sp,
                    color = Color.Yellow,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    text = desc.value,
                    fontSize = 24.sp,
                    color = Color.Yellow
                )
            }
        }
    }
}

// プレビュー用
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val repository = object: OpenAiRepository {
        override suspend fun fetchChat(base64Image: String): Result<ChatCompletionResponse> {
            return Result.success(ChatCompletionResponse(
                id = "",
                `object` = "text_completion",
                created = 0,
                model = "gpt-4o",
                choices = listOf(
                    Choice(
                        message = Message(
                            content = "name,job", role = "user"
                        ), finish_reason = "ok", index = 0, logprobs = null),
                ),
                usage = Usage(0, 0, 0),
                system_fingerprint = ""
            )
            )
        }

    }
    val viewModel = MainViewModel(repository)
    Gpt4oScouterTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            MainScreen(viewModel)
        }
    }
}