package com.opensource.boyd.festifynative.REST.Services

import io.reactivex.Flowable
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by Boyd on 9/28/2017.
 */
interface FestifyService {

    @Multipart
    @POST("/v1/upload")
    fun uploadImage(@Part image : MultipartBody.Part, @Part("name") name : RequestBody) : Flowable<ResponseBody>

    @FormUrlEncoded
    @POST("/v1/authMe")
    fun authUser(@Field("authToken") authToken: String, @Field("username") username: String) : Flowable<ResponseBody>

}