package net.fazra.olshopgitar.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImgurService {
    private const val BASE_URL = "https://api.imgur.com/"
    private const val CLIENT_ID = "Client-ID YOUR_CLIENT_ID"
    //TODO: JANGAN LETAKKAN ID

    val api: ImgurApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgurApi::class.java)
    }

    fun getAuthHeader() = CLIENT_ID
}
