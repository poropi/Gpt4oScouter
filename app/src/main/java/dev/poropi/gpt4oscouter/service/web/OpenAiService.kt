package dev.poropi.gpt4oscouter.service.web

import dev.poropi.gpt4oscouter.service.web.request.ChatRequest
import dev.poropi.gpt4oscouter.service.web.response.ChatCompletionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * OpenAiService
 */
interface OpenAiService {
    @POST("v1/chat/completions")
    suspend fun fetchChat(@Body request: ChatRequest): Response<ChatCompletionResponse>
}