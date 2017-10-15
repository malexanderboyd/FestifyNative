package com.opensource.boyd.festifynative.Model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by Boyd on 9/28/2017.
 */
class User() : Parcelable {

    constructor(spotifydata: SpotifyInfo) : this() {
        spotifyData = spotifydata
    }

    constructor(authToken : String) : this() {
        this.authToken = authToken
    }

    var isTokenExpired : Boolean = false
     var authToken : String? = null
     var spotifyData : SpotifyInfo? = null

    constructor(parcel: Parcel) : this() {
        isTokenExpired = parcel.readByte() != 0.toByte()
        authToken = parcel.readString()
    }

    data class SpotifyInfo(
            var birthdate : String?,
            var country : String?,
            var display_name : String?,
            var email : String?,
            var external_urls : ExternalUrl,
            var followers : Followers,
            var href : String?,
            var id : String?,
            var images: Array<Images>,
            var product: String?,
            var type: String?,
            var uri: String)

    data class ExternalUrl(
          var key : String?,
          var value: String?
    )

    data class Followers (
        var href : String?,
        var total : Int?
    )

    data class Images(
            var height: Int?,
            var url: String?,
            var width: Int?
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isTokenExpired) 1 else 0)
        parcel.writeString(authToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}