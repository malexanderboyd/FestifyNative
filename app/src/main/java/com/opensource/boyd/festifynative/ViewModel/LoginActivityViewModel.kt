package com.opensource.boyd.festifynative.ViewModel

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.util.Log
import com.opensource.boyd.festifynative.LoginActivity
import com.opensource.boyd.festifynative.Model.User
import com.opensource.boyd.festifynative.R
import com.opensource.boyd.festifynative.REST.Services.Retrofit.SpotifyApi
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Created by Boyd on 9/28/2017.
 */
class LoginActivityViewModel(application : Application) : AndroidViewModel(application) {

    private val REQUEST_CODE = 6337
    private val spotifyAPI : SpotifyApi = SpotifyApi.instance
    lateinit var user : User.SpotifyInfo
    private val data : MutableLiveData<User.SpotifyInfo> = MutableLiveData()
    lateinit var userTkn : User
    fun spotifyLogin(activity: Activity) : LiveData<User.SpotifyInfo> {
        val resources = activity.resources
        val loginBuilder = AuthenticationRequest.Builder(resources.getString(R.string.spotify_client_key),
                AuthenticationResponse.Type.TOKEN, resources.getString(R.string.spotify_callback_uri))
        loginBuilder.setScopes(arrayOf("playlist-modify-public", "user-read-email", "user-read-private", "user-read-birthdate"))

        val request = loginBuilder.build()

        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request)
        return data
    }

    fun handleLoginResponse(requestCode: Int, resultCode: Int, data : Intent?) {
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            if (response.type == AuthenticationResponse.Type.TOKEN) {
                userTkn = User(response.accessToken)
                val request: Flowable<User.SpotifyInfo> = spotifyAPI.getUser(userTkn.authToken)
                    request
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Consumer<User.SpotifyInfo> {
                                this.user = it
                                this.data.postValue(it)
                            }, this.ErrorMsg)

            }
        }
    }

    fun getUser(accessToken : String) : LiveData<User.SpotifyInfo> {
        val userData : MutableLiveData<User.SpotifyInfo> = MutableLiveData()
        var request : Flowable<User.SpotifyInfo> = spotifyAPI.getUser(accessToken)
            request
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer<User.SpotifyInfo> {
                        userData.postValue(it)
                        this.user = user
                    }, this.ErrorMsg)

        return userData
    }


    val ErrorMsg : Consumer<Throwable> = Consumer {
        Log.e(LoginActivity::class.toString(), it.localizedMessage)
    }
}