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
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.opensource.boyd.festifynative.REST.Services.Retrofit.FestifyApi
import okhttp3.ResponseBody


/**
 * Created by Boyd on 9/28/2017.
 */
class LoginActivityViewModel(application : Application) : AndroidViewModel(application) {

    interface RetryAuthListener {
        fun handleExpiredAuth()
    }

    private val REQUEST_CODE = 6337
    private val spotifyAPI : SpotifyApi = SpotifyApi.instance
    private val data : MutableLiveData<User.SpotifyInfo> = MutableLiveData()
    lateinit var user : User


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
                user = User(response.accessToken)
                user.authToken?.let {
                    val request: Flowable<User.SpotifyInfo> = spotifyAPI.getUser(it)
                    request
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Consumer<User.SpotifyInfo> {
                                this.user.spotifyData = it
                                this.data.postValue(it)
                            }, this.ErrorMsg
                            )

                }
            }
        }
    }

    fun getUser(accessToken : String) : LiveData<User> {
        val userData : MutableLiveData<User> = MutableLiveData()
        val request : Flowable<User.SpotifyInfo> = spotifyAPI.getUser(accessToken)
            request
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val currentUser = User(it)
                        currentUser.authToken = accessToken
                        this.user = currentUser
                        userData.postValue(user)
                    }, {
                        val exception = it as? HttpException
                        exception?.let {
                            when (exception.code()) {
                                401 -> {
                                    val newUser = User()
                                    newUser.isTokenExpired = true
                                    userData.postValue(newUser)
                                }
                                else ->
                                    Log.e("getUser", exception.message())
                            }
                        }
                    })
        return userData
    }


    private val ErrorMsg : Consumer<Throwable> = Consumer {
        Log.e(LoginActivity::class.toString(), it.localizedMessage)
    }

    fun festifyAuthorization() : Flowable<ResponseBody> {
        user.authToken?.let {
            val authToken = it
            user.spotifyData?.id?.let {
            return FestifyApi.instance.authorizeUser(authToken, it)
            }
        }
        throw Exception("No auth token")
    }
}

