package com.opensource.boyd.festifynative.REST.Services.Retrofit

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.opensource.boyd.festifynative.Model.User
import com.opensource.boyd.festifynative.REST.Services.FestifyService
import io.reactivex.Flowable
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FestifyApi private constructor() {

    private val festify : FestifyService
    private lateinit var authToken: String

    private object Holder { val INSTANCE = FestifyApi() }

    companion object {
        val instance: FestifyApi by lazy { Holder.INSTANCE }
    }

    init {

        val retroFit = Retrofit.Builder()
                .baseUrl("https://stormy-headland-78067.herokuapp.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        festify = retroFit.create(FestifyService::class.java)
    }

    fun uploadImage(body: MultipartBody.Part, name: RequestBody) : Flowable<ResponseBody>  {
        return festify.uploadImage(body, name)
    }

    fun authorizeUser(authToken: String, username : String) : Flowable<ResponseBody> {
        return festify.authUser(authToken, username)
    }


}