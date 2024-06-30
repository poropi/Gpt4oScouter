package dev.poropi.gpt4oscouter.service.web.response

import java.io.Serializable

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    val system_fingerprint: String
): Serializable

data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?, // 型が不明なので Any? にしています
    val finish_reason: String
): Serializable

data class Message(
    val role: String,
    val content: String
): Serializable

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
): Serializable