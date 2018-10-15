/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emergent.photosharingapp.api

import android.util.Log
import com.emergent.photosharingapp.api.dto.requestDTO.CommentRequestDTO
import com.emergent.photosharingapp.api.dto.requestDTO.SignInRequestDTO
import com.emergent.photosharingapp.domain.Comments
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.domain.User
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * API communication setup
 */
interface MediaSharingApi {
    @POST("/tokensignin")
    fun login(
            @Body signInRequestDTO: SignInRequestDTO
    ) : Call<User>

    @GET("/{user_id}/feed")
    fun getTop(
            @Path("user_id") userId: Long,
            @Query("limit") limit: Int): Call<ListingResponse<Media>>

    // for after/before param, either get from RedditDataResponse.after/before,
    // or pass RedditNewsDataResponse.name (though this is technically incorrect)
    @GET("/{user_id}/feed")
    fun getTopAfter(
            @Path("user_id") userId: Long,
            @Query("after") after: Long,
            @Query("limit") limit: Int): Call<ListingResponse<Media>>

    @Multipart
    @POST("/{user_id}/media/")
    fun uploadMedia(
            @Path("user_id") userId: Long,
            @Part filePart : MultipartBody.Part): Call<Media>

    @GET("/{user_id}/media/{media_id}/comment")
    fun getTopComments(
            @Path("user_id") userId: Long,
            @Path("media_id") mediaId: Long,
            @Query("limit") limit: Int
    ) : Call<ListingResponse<Comments>>

    @GET("/{user_id}/media/{media_id}/comment")
    fun getTopCommentsAfter(
            @Path("user_id") userId: Long,
            @Path("media_id") mediaId: Long,
            @Query("after") after: Long,
            @Query("limit") limit: Int
    ) : Call<ListingResponse<Comments>>

    @PUT("/{user_id}/media/{media_id}/like")
    fun likeMedia(@Path("user_id") userId: Long,
                  @Path("media_id") mediaId: Long) : Call<Media>

    @DELETE("/{user_id}/media/{media_id}/like")
    fun unlikeMedia(@Path("user_id") userId: Long,
                  @Path("media_id") mediaId: Long) : Call<Media>

    @POST("/{user_id}/media/{media_id}/comment")
    fun commentOnMedia(@Path("user_id") userId: Long,
                  @Path("media_id") mediaId: Long,
                    @Body commentRequestDTO : CommentRequestDTO) : Call<Media>


    class ListingResponse<T>(val data: ListingData<T>)

    class ListingData<T>(
            val children: List<T>,
            val after: Long?,
            val before: Long?
    )

    companion object {
//        private const val BASE_URL = "http://192.168.0.2:8080/"
        private const val BASE_URL = "http://192.168.2.37:8080/"
        fun create(idToken : String): MediaSharingApi = create(HttpUrl.parse(BASE_URL)!!, idToken)
        fun create(httpUrl: HttpUrl, idToken : String): MediaSharingApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC
            val authorizationTokenInteceptor = Interceptor {
                val original = it.request()

                val request = original.newBuilder()
                        .header("Authorization", idToken)
                        .method(original.method(), original.body())
                        .build()

                return@Interceptor it.proceed(request)
            }
            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .addInterceptor(authorizationTokenInteceptor)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MediaSharingApi::class.java)
        }
    }
}