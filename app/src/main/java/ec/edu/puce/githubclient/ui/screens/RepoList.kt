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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ec.edu.puce.githubclient.models.Repository

/**
 * Pantalla principal que muestra la lista de repositorios.
 * Implementa la lógica de eliminación con confirmación y la navegación al formulario.
 */
@Composable
fun RepoList(
    modifier: Modifier = Modifier,
    viewModel: RepoListViewModel = viewModel(),
    onNavigateToForm: (Repository?) -> Unit = {} // null significa "Nuevo Repositorio"
) {
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()

    // Estado local para manejar el diálogo de confirmación de borrado
    var repoToDelete by remember { mutableStateOf<Repository?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) }, // Modo creación
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Repositorio"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Indicador de carga global
            if (isLoading && repos.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Mensaje de error (si existe)
            errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            // Lista de Repositorios
            if (repos.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(repos) { repo ->
                        RepoItem(
                            repository = repo,
                            onEdit = { onNavigateToForm(repo) }, // Modo edición
                            onDelete = { repoToDelete = it } // Dispara el diálogo
                        )
                    }
                }
            } else if (!isLoading && errorMsg == null) {
                Text(
                    text = "No hay repositorios disponibles.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Diálogo de confirmación para eliminar
    repoToDelete?.let { repo ->
        AlertDialog(
            onDismissRequest = { repoToDelete = null },
            title = { Text("Eliminar repositorio") },
            text = { 
                Text("¿Estás seguro de que deseas eliminar \"${repo.name}\"?\n\nEsta acción es irreversible y se reflejará en tu cuenta de GitHub.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRepository(repo.owner.login, repo.name)
                        repoToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { repoToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RepoListPreview() {
    GithubClientTheme {
        RepoList()
    }
}
