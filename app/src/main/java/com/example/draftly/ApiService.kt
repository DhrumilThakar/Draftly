package com.example.draftly

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("generate-pdf")
    suspend fun generatePdf(
        @Body request: Map<String, String>
    ): Response<ResponseBody>
}