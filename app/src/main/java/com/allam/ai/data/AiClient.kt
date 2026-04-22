package com.allam.ai.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface GeminiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

data class Content(val parts: List<Part>)
data class Part(val text: String)

data class GeminiResponse(val candidates: List<Candidate>)
data class Candidate(val content: Content)

object AiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GeminiService = retrofit.create(GeminiService::class.java)

    suspend fun askAi(apiKey: String, question: String): String {
        val systemPrompt = "أنت 'علام'، مساعد صوتي متطور للموبايل، وشخصيتك مرحة وكاجوال جداً. بتتكلم بالمصري الشعبي اللطيف زي 'يا باشا'، 'يا ريس'، 'عيني ليك'. لما حد يطلب منك حاجة، رد بذكاء وخفة دم بس خلي الردود قصيرة عشان تتقرأ بسرعة بصوت الموبايل. لو حد قال حاجة مش مفهومة، رده بهزار بسيط."
        
        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(question)))),
            systemInstruction = Content(parts = listOf(Part(systemPrompt)))
        )
        
        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "مش سامعك كويس يا باشا، ممكن تعيد؟"
        } catch (e: Exception) {
            "معلش يا ريس، مخي مهنج شوية دلوقتي، جرب كمان دقيقة."
        }
    }
}
