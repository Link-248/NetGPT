package ca.algomau.cosc3596.dalnemri.finalproject.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object Response {
    fun response(prompt: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(180, TimeUnit.SECONDS) // connect timeout
            .readTimeout(180, TimeUnit.SECONDS) // socket timeout
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val json = JSONObject()
        json.put("model", "gpt-3.5-turbo")
        val messages = JSONArray()
        json.put("messages", messages)
        val message = JSONObject()
        message.put("role", "user")
        message.put("content", prompt)
        messages.put(message)

        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://INSERT_URL/v1/chat/completions") //ADD OPENAI OR OPENAI PROXY API ENDPOINT HERE
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer Add API-KEY HERE")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseData = JSONObject(response.body?.string())
                val choices = responseData.getJSONArray("choices")
                val firstChoice = choices.getJSONObject(0)
                val message = firstChoice.getJSONObject("message")
                val content = message.getString("content")

                content
            }
        } catch (e: SocketTimeoutException) {

            print(e.toString())
            // Handle the timeout exception as you need
            "Timeout occurred"
        }
    }
}