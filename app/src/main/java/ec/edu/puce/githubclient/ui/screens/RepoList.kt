package ec.edu.puce.githubclient.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.puce.githubclient.ui.components.RepoItem
import ec.edu.puce.githubclient.ui.theme.GithubClientTheme
import ec.edu.puce.githubclient.viewmodels.RepoListViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ec.edu.puce.githubclient.models.Repository

@Composable
fun RepoList(
    modifier: Modifier = Modifier,
    viewModel : RepoListViewModel = viewModel(),
    onNavigateToForm: () -> Unit = {}
) {
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()

    var repoToDelete by remember {
        mutableStateOf<Repository?>(null)
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(all = 16.dp)
                )
            }
            if (!isLoading && errorMsg.isNullOrBlank()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(count = repos.size) { i ->

                        RepoItem(
                            repository = repos[i],

                            onEdit = { repo ->

                                viewModel.updateRepository(
                                    owner = repo.owner.login,
                                    repoName = repo.name,
                                    newName = repo.name,
                                    newDescription = repo.description ?: ""
                                )

                            },

                            onDelete = { repo ->

                                repoToDelete = repo

                            }

                        )

                    }
                }
            }
        }
    }

    repoToDelete?.let { repo ->

        AlertDialog(
            onDismissRequest = {
                repoToDelete = null
            },

            title = {
                Text("Eliminar repositorio")
            },

            text = {
                Text(
                    "¿Seguro que deseas eliminar \"${repo.name}\"?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteRepository(
                            owner = repo.owner.login,
                            repoName = repo.name
                        )

                        repoToDelete = null

                    }
                ) {

                    Text("Eliminar")

                }

            },

            dismissButton = {

                TextButton(
                    onClick = {
                        repoToDelete = null
                    }
                ) {

                    Text("Cancelar")

                }

            }

        )

    }

}


// ==============================
// PREVIEW PARA ANDROID STUDIO
// ==============================

@Preview(showBackground = true)
@Composable
fun RepoListPreview() {

    // Aplica el tema visual personalizado
    GithubClientTheme {

        // Muestra la pantalla
        RepoList()
    }
}