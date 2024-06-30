package dev.poropi.gpt4oscouter.repository

import dev.poropi.gpt4oscouter.service.web.OpenAiService
import dev.poropi.gpt4oscouter.service.web.request.ChatRequest
import dev.poropi.gpt4oscouter.service.web.request.Content
import dev.poropi.gpt4oscouter.service.web.request.ImageUrl
import dev.poropi.gpt4oscouter.service.web.request.Message
import dev.poropi.gpt4oscouter.service.web.response.ChatCompletionResponse
import retrofit2.Response
import javax.inject.Inject

class OpenAiRepositoryImpl @Inject constructor(private val service: OpenAiService): OpenAiRepository {
    override suspend fun fetchChat(base64Image: String): Result<ChatCompletionResponse> {
        val request = ChatRequest(
            model = "gpt-4o",
            messages = listOf(
                Message(
                    role = "system",
                    content = listOf(
                        Content(
                            type = "text",
                            text = """
                                画像に表示されているオブジェクトから以下を抽出してください。
                                名前：画像イメージから名前を割り出してください。名前が不明な場合は不明にしてください。
                                職業：画像イメージから職業を割り出してください。
                                種族：画像イメージから種族を割り出してください。
                                戦闘力：画像に表示されているオブジェクトをドラゴンボールのスカウターのように戦闘力で答えてください。カンマ(,)は除外してください。
                                解説：戦闘力の値についての根拠をお願いします。
                                それぞれ日本語でご回答お願いします。
                                返答は以下の順番でCSV形式で出力してください。見出し行は不要です。
                                ・名前
                                ・職業
                                ・種族
                                ・戦闘力
                                ・解説
                            """.trimIndent()
                        ),
                    )
                ),
                Message(
                    role = "user",
                    content = listOf(
                        Content(
                            type = "image_url",
                            image_url = ImageUrl(
                                url = "data:image/jpeg;base64,$base64Image"
                            )
                        ),
                    )
                ),
            )
        )
        val res = kotlin.runCatching {
            val response = service.fetchChat(request)
            response.body() ?: throw Exception("response body is null")
        }
        return res
    }
}