package ec.edu.puce.githubclient.models

import kotlinx.serialization.descriptors.SerialDescriptor

data class Repository(
    val id: String,
    val name: String,
    val description: String?,
    val language: String?,
    val owner: GithubUser,
)
