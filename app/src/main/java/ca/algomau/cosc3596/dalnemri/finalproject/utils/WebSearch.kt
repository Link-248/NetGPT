package ca.algomau.cosc3596.dalnemri.finalproject.utils

import android.util.Log
import ca.algomau.cosc3596.dalnemri.finalproject.utils.Response.summarizeContent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.lang.reflect.TypeVariable
import java.net.SocketTimeoutException
import java.util.Dictionary
import java.util.concurrent.TimeUnit

object WebSearch {
    suspend fun search(query: String): Map<String, String> {
        val client = OkHttpClient.Builder()
            .connectTimeout(180, TimeUnit.SECONDS) // connect timeout
            .readTimeout(180, TimeUnit.SECONDS) // socket timeout
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val json = JSONObject()
        json.put("q", query)

        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://google.serper.dev/search")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-API-KEY", "67e8074c3d3b7018370349df2bfd44f23707f32b")
            .build()

        val links = mutableListOf<String>()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseData = JSONObject(response.body?.string())
                val organics = responseData.getJSONArray("organic")

                for (i in 0 until organics.length()) {
                    var value: Any = organics[i]
                    when (value) {
                        is JSONObject -> links.add(value.get("link").toString())
                    }
                }
            }
        } catch (e: SocketTimeoutException) {

            print(e.toString())
            // Handle the timeout exception as you need
        }

        return scrapeWebsite(links[0])
    }

    suspend fun scrapeWebsite(url: String): Map<String, String> {
        return try {
            val doc: Document = Jsoup.connect(url)
                .data("query", "Java")
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(3000)
                .get()
            val parsedDoc = Jsoup.parse(doc.body().toString())
            var summary = parsedDoc.select("p").toString()
            if (summary.length > 8000) {
                try {
                    summary = summarizeContent(summary)
                }catch(e: Exception){
                    summary = "MENTION TO THE USER THAT: the content was too long and the information may be cut off \n\n use this content\n" +
                            summary.substring(0, summary.length.coerceAtMost(7500));
                }
            }
            val result: Map<String, String> = mapOf(url to summary)
            result
        } catch (e: Exception) {
            print(e.toString())
            // Handle the timeout exception as you need
            mapOf<String, String>(url to "Error")
        }
    }
}