package ec.edu.puce.githubclient.models

import com.google.gson.annotations.SerializedName

data class GithubUser(
    val id: String,
    @SerializedName("login")
    val login: String,
    @SerializedName( value = "avatar_url")
    val avatarUrl: String
)