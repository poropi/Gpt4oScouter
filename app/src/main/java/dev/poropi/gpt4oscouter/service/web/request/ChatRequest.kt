package dev.poropi.gpt4oscouter.service.web.request

import java.io.Serializable

/**
 * ChatRequest
 */
data class ChatRequest(
    val model: String,
    val messages: List<Message>
): Serializable

/**
 * Message
 */
data class Message(
    val role: String,
    val content: List<Content>
): Serializable

/**
 * Content
 */
data class Content(
    val type: String, // text or image_url
    val text: String? = null, // text
    val image_url: ImageUrl? = null // image_url
): Serializable

/**
 * ImageUrl
 */
data class ImageUrl(
    val url: String
): Serializable