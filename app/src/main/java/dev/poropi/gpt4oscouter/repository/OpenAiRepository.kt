package dev.poropi.gpt4oscouter.repository

import dev.poropi.gpt4oscouter.service.web.response.ChatCompletionResponse
import retrofit2.Response

/**
 * OpenAI APIとの通信を抽象化するためのリポジトリインターフェース。
 *
 * このインターフェースは、OpenAI APIからデータを取得するためのメソッドを定義します。
 */
interface OpenAiRepository {
    /**
     * OpenAI APIからチャットの完了レスポンスを取得します。
     *
     * この関数は、指定されたbase64形式の画像を使用してOpenAI APIからチャットの完了レスポンスを非同期に取得します。
     *
     * @param base64Image チャットの完了レスポンスを取得するためのbase64形式の画像。
     * @return チャットの完了レスポンスの結果。
     */
    suspend fun fetchChat(base64Image: String): Result<ChatCompletionResponse>
}