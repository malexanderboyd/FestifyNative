package com.opensource.boyd.festifynative

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.opensource.boyd.festifynative.ViewModel.LoginActivityViewModel

import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity : AppCompatActivity() {

    lateinit private var viewModel : LoginActivityViewModel
    private val SPOTIFY_ACCESS_TOKEN_PREF: String
        get() = "festify_spotify_token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(LoginActivityViewModel::class.java)


        toggleLoginComponents()

        getUser()

    }

    fun getSavedUser(accessToken : String) {
        viewModel.getUser(accessToken).observe(this, Observer {
            it?.let {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, InstructionsFragment.newInstance(it))
                        .commit()
            }
        })
    }

    fun StartNewUser() {
        val sharedPref : SharedPreferences = getPreferences(Context.MODE_PRIVATE)
        viewModel.spotifyLogin(this).observe(this, Observer {
            it?.let {
                toggleLoginComponents()
                sharedPref.edit().putString(SPOTIFY_ACCESS_TOKEN_PREF, viewModel.userTkn.authToken).apply()
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, InstructionsFragment.newInstance(it))
                        .commit()
            }
        })
    }

    fun getUser() {
        val sharedPref : SharedPreferences = getPreferences(Context.MODE_PRIVATE)
        if(sharedPref.contains(SPOTIFY_ACCESS_TOKEN_PREF)) {
            val accessToken = sharedPref.getString(SPOTIFY_ACCESS_TOKEN_PREF, "")
            if(!accessToken.isEmpty()) {
                getSavedUser(accessToken)
            }
        } else {
            toggleLoginComponents()
            fab.setOnClickListener { view ->
                StartNewUser()
            }
        }
    }
    fun toggleLoginComponents() {
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
