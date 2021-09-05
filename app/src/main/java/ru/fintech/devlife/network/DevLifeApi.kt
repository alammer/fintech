package ru.fintech.devlife.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.fintech.devlife.BuildConfig

val contentType = "application/type".toMediaType()

private val json: Json by lazy {
    Json { ignoreUnknownKeys = true }
}

@ExperimentalSerializationApi
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .addConverterFactory(json.asConverterFactory(contentType))
    .build()

interface DevLifeApi {
    @GET("{category}/{page}")
    suspend fun getPosters(
        @Path("category") category: String,
        @Path("page") page_id: Int,
        @Query("json") json: String = "true"
    ): Response<DevLifeResponse?>
}

object DevLifeService {
    @ExperimentalSerializationApi
    val retrofitService: DevLifeApi by lazy {
        retrofit.create(DevLifeApi::class.java)
    }
}