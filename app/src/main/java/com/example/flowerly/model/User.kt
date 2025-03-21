package com.example.flowerly.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey var id: String = "",
    var username: String = "",
    var profilePictureUrl: String = "",
) {

    companion object {

        private const val ID_KEY = "id"
        private const val USERNAME_KEY = "username"
        private const val PROFILE_PICTURE_URL_KEY = "profilePictureUrl"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val username = json[USERNAME_KEY] as? String ?: ""
            val profilePictureUrl = json[PROFILE_PICTURE_URL_KEY] as? String ?: ""

            return User(
                id = id,
                username = username,
                profilePictureUrl = profilePictureUrl,
            )
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                USERNAME_KEY to username,
                PROFILE_PICTURE_URL_KEY to profilePictureUrl
            )
        }
}