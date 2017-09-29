package com.opensource.boyd.festifynative

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.opensource.boyd.festifynative.Model.User
import com.opensource.boyd.festifynative.REST.Services.Retrofit.SpotifyApi
import kotlinx.android.synthetic.main.instructions_layout.*

/**
 * Created by Boyd on 9/28/2017.
 */
class InstructionsFragment : Fragment() {

    lateinit var userData : User.SpotifyInfo

    override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        textView2.text = userData.display_name
    }

    companion object {
        fun newInstance(user : User.SpotifyInfo) : InstructionsFragment {
            val f : InstructionsFragment = InstructionsFragment()
            f.userData = user
            return f
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        inflater?.let {
            return  it.inflate(R.layout.instructions_layout, container, false)
        }
        throw Exception("No layout inflater onCreateView()")
    }


}