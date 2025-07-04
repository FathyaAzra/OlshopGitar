package net.fazra.olshopgitar.api

import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgurApi {
    @Multipart
    @POST("3/image")
    suspend fun uploadImage(
        @Header("Authorization") authHeader: String,
        @Part image: MultipartBody.Part
    ): ImgurResponse
}

data class ImgurResponse(val data: ImgurData, val success: Boolean)
data class ImgurData(val link: String)
