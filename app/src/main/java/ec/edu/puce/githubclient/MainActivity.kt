package ec.edu.puce.githubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.puce.githubclient.models.Repository
import ec.edu.puce.githubclient.ui.screens.RepoForm
import ec.edu.puce.githubclient.ui.screens.RepoList
import ec.edu.puce.githubclient.ui.theme.GithubClientTheme
import ec.edu.puce.githubclient.viewmodels.RepoListViewModel

/**
 * Actividad principal que funciona como el controlador de navegación de la app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubClientTheme {
                // Estado de navegación simple
                var currentScreen by remember { mutableStateOf("repoList") }
                
                // Estado para el repositorio que se está editando (null si es creación)
                var repositoryToEdit by remember { mutableStateOf<Repository?>(null) }
                
                // Compartimos el ViewModel de la lista para refrescar datos al volver
                val listViewModel: RepoListViewModel = viewModel()

                when (currentScreen) {
                    "repoList" -> RepoList(
                        viewModel = listViewModel,
                        onNavigateToForm = { repo ->
                            repositoryToEdit = repo // Si es null, es modo creación
                            currentScreen = "repoForm"
                        }
                    )
                    
                    "repoForm" -> RepoForm(
                        repository = repositoryToEdit,
                        onBackClick = {
                            repositoryToEdit = null
                            currentScreen = "repoList"
                        },
                        onSaveSuccess = {
                            // Al guardar con éxito, refrescamos y volvemos
                            listViewModel.fetchRepos()
                            repositoryToEdit = null
                            currentScreen = "repoList"
                        }
                    )
                }
            }
        }
    }
}
