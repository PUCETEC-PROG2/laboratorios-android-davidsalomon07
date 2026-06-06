package ec.edu.puce.githubclient.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.githubclient.models.Repository
import ec.edu.puce.githubclient.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel encargado de la lógica de negocio para la lista de repositorios.
 * Implementa la obtención y eliminación de datos mediante Retrofit y Corrutinas.
 */
class RepoListViewModel : ViewModel() {
    private val _repos = MutableStateFlow<List<Repository>>(emptyList())
    val repos: StateFlow<List<Repository>> = _repos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    init {
        // Carga inicial al crear el ViewModel
        fetchRepos()
    }

    /**
     * Sincroniza la lista local con los datos remotos de GitHub (GET).
     */
    fun fetchRepos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                // Llamada suspendida a la API
                _repos.value = RetrofitClient.apiService.getRepository()
            } catch (e: Exception) {
                handleException(e, "Error al cargar repositorios")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Ejecuta la eliminación de un repositorio en el servidor (DELETE).
     * @param owner El nombre de usuario dueño del repositorio.
     * @param repoName El nombre del repositorio a eliminar.
     */
    fun deleteRepository(owner: String, repoName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                // Llamada a la API
                RetrofitClient.apiService.deleteRepository(owner, repoName)
                
                // Refresco automático: Una vez eliminado en el servidor, 
                // pedimos la lista de nuevo para que la UI se actualice.
                fetchRepos()
            } catch (e: Exception) {
                handleException(e, "Error al eliminar el repositorio")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Centraliza el manejo de excepciones para mantener el código limpio.
     * Útil para diferenciar errores de red de errores de lógica de servidor.
     */
    private fun handleException(e: Exception, prefix: String) {
        val message = when (e) {
            is HttpException -> when (e.code()) {
                401 -> "Token inválido o expirado"
                403 -> "Límite de API excedido o permisos insuficientes (El token debe tener permisos 'delete_repo')"
                404 -> "El repositorio no fue encontrado"
                else -> "Error del servidor: ${e.code()}"
            }
            is IOException -> "Sin conexión a internet"
            else -> e.localizedMessage ?: "Ocurrió un error inesperado"
        }
        _errorMsg.value = "$prefix: $message"
        
        // Registro en Logcat para depuración del desarrollador
        println("DEBUG_REPO: $prefix -> ${e.printStackTrace()}")
    }
}
