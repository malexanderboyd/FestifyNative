package com.opensource.boyd.festifynative.REST.Services

import com.opensource.boyd.festifynative.Model.User
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Created by Boyd on 9/28/2017.
 */
interface SpotifyService {

    @GET("/v1/me")
    fun getUser(@Header("Authorization") authToken : String) : Flowable<User.SpotifyInfo>
}