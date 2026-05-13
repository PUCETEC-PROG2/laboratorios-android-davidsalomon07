package ec.edu.puce.githubclient.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ec.edu.puce.githubclient.ui.components.RepoItem

@Composable
fun Repolist(
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ){

        RepoItem(
            name = "Repositorio de Android",
            description = "Repositorio creado en Ktolin para el desarrollo movil",
            avatarUrl = "https://avatars.githubusercontent.com/u/216223767?v=4",
            language = "Kotlin"
        )
        RepoItem(
            name = "Repositorio de Ios",
            description = "Repositorio creado en Django para el desarrollo movil",
            avatarUrl = "https://avatars.githubusercontent.com/u/216223767?v=4",
            language = "Django"
        )
        RepoItem(
            name = "Repositorio de Django",
            description = "Repositorio creado en React para el desarrollo movil",
            avatarUrl = "https://avatars.githubusercontent.com/u/216223767?v=4",
            language = "React"
        )
        RepoItem(
            name = "Repositorio de React",
            description = "Repositorio creado en Swift para el desarrollo movil",
            avatarUrl = "https://avatars.githubusercontent.com/u/216223767?v=4",
            language = "Swift"
            //MI NOMBRE ES DAVID SALOMON
        )
    }
}