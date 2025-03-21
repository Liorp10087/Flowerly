package com.example.flowerly

import android.util.Log
import com.example.flowerly.model.Message
import com.example.flowerly.model.OpenAIRequest
import com.example.flowerly.model.OpenAIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object OpenAIClient {

    private const val BASE_URL = "https://api.openai.com/v1/chat/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: OpenAIApiService = retrofit.create(OpenAIApiService::class.java)

    const val apiKey = BuildConfig.OPENAI_API_KEY


    interface OpenAIApiService {
        @Headers("Authorization: Bearer ${apiKey}")
        @POST("completions")
        fun generateDescription(@Body request: OpenAIRequest): Call<OpenAIResponse>
    }

    fun generateDescription(title: String, callback: (String?) -> Unit) {
        Log.d("API_KEY", "OpenAI API Key: $apiKey")
        val request = OpenAIRequest(
            messages = listOf(
                Message(
                    role = "user",
                    content = "Give a detailed description of the flower: $title"
                )
            )
        )
        apiService.generateDescription(request).enqueue(object : Callback<OpenAIResponse> {
            override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
                if (response.isSuccessful) {
                    val generatedText = response.body()?.choices?.get(0)?.message?.content
                    if (generatedText != null && generatedText.isNotEmpty()) {
                        callback(generatedText)
                    } else {
                        Log.e("OpenAIClient", "Received empty or null description")
                        callback(null)
                    }
                } else {
                    Log.e("OpenAIClient", "API request failed: ${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                Log.e("OpenAIClient", "Request failed: ${t}")
                callback(null)
            }
        })
    }
}
