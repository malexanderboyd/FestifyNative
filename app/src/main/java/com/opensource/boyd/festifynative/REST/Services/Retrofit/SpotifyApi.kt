package com.opensource.boyd.festifynative.REST.Services.Retrofit

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.opensource.boyd.festifynative.Model.User
import com.opensource.boyd.festifynative.REST.Services.SpotifyService
import io.reactivex.Flowable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Boyd on 9/28/2017.
 */
class SpotifyApi private constructor() {

    private val spotify : SpotifyService


    private object Holder { val INSTANCE = SpotifyApi() }

    companion object {
        val instance: SpotifyApi by lazy { Holder.INSTANCE }
    }

    init {
        val retroFit = Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        spotify = retroFit.create(SpotifyService::class.java)
    }

    fun getUser(authToken : String) : Flowable<User.SpotifyInfo> {
        return spotify.getUser("Authorization: Bearer " + authToken)
    }

}