package com.opensource.boyd.festifynative

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.facebook.stetho.Stetho
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.opensource.boyd.festifynative.Model.User
import com.opensource.boyd.festifynative.REST.Services.Retrofit.FestifyApi
import com.opensource.boyd.festifynative.ViewModel.LoginActivityViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity : AppCompatActivity(), LoginActivityViewModel.RetryAuthListener {
    override fun handleExpiredAuth() {
        startNewUser()
    }

    lateinit private var viewModel : LoginActivityViewModel
    private val SPOTIFY_ACCESS_TOKEN_PREF: String
        get() = "festify_spotify_token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(this)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(LoginActivityViewModel::class.java)


        toggleLoginComponents()

        checkExistingUser()

        fab.setOnClickListener { view ->
            startNewUser()
        }

    }

    private fun getSavedUser(accessToken : String) {
        viewModel.getUser(accessToken).observe(this, Observer {
            it?.let {
                if(it.isTokenExpired) {
                    toggleLoginComponents()
                } else {
                    it.spotifyData?.let {
                        startCameraActivity()
                    }
                }
            }
        })
    }

    private fun startNewUser() {
        val sharedPref : SharedPreferences = getPreferences(Context.MODE_PRIVATE)
        viewModel.spotifyLogin(this).observe(this, Observer {
            it?.let {
                toggleLoginComponents()
                sharedPref.edit().putString(SPOTIFY_ACCESS_TOKEN_PREF, viewModel.user.authToken).apply()
                startCameraActivity()
            }
        })
    }


    private fun startCameraActivity() {
        viewModel.festifyAuthorization()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val cameraActivityIntent = Intent(this, PhotoActivity::class.java)
                    cameraActivityIntent.putExtra("current-user", viewModel.user)
                    startActivity(cameraActivityIntent)
                }, {
                    val exception = it as? HttpException
                    exception?.let {
                        Log.e("FestifyAuth: ", exception.localizedMessage)
                    }
                })
    }

    private fun checkExistingUser() {
        val sharedPref : SharedPreferences = getPreferences(Context.MODE_PRIVATE)
        if(sharedPref.contains(SPOTIFY_ACCESS_TOKEN_PREF)) {
            val accessToken = sharedPref.getString(SPOTIFY_ACCESS_TOKEN_PREF, "")
            if(!accessToken.isEmpty()) {
                getSavedUser(accessToken)
            }
        } else {
            toggleLoginComponents()
        }
    }
    private fun toggleLoginComponents() {
        loginInstructions.visibility = if(loginInstructions.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        fab.visibility = if(fab.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        titleLabel.visibility = if(titleLabel.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }


    //callback from viewModel.spotifyLogin()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.handleLoginResponse(requestCode, resultCode, data)
       }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
