package com.opensource.boyd.festifynative.Model

import java.util.*

/**
 * Created by Boyd on 9/28/2017.
 */
class User(val authToken : String) {



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



}