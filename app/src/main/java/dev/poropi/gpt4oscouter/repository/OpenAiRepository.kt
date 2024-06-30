package dev.poropi.gpt4oscouter.repository

import dev.poropi.gpt4oscouter.service.web.response.ChatCompletionResponse
import retrofit2.Response

interface OpenAiRepository {
    suspend fun fetchChat(base64Image: String): Result<ChatCompletionResponse>
}