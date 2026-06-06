package ec.edu.puce.githubclient.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.githubclient.models.Repository
import ec.edu.puce.githubclient.models.RepositoryPayload
import ec.edu.puce.githubclient.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel para gestionar la lógica del formulario de repositorios.
 * Soporta tanto la creación (POST) como la actualización (PATCH) de repositorios.
 * 
 * Sigue los principios de State Management usando StateFlow para notificar a la UI.
 */
class RepoFormViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // Estado interno para manejar el modo de operación
    private var isEditMode = false
    private var originalOwner: String? = null
    private var originalRepoName: String? = null

    /**
     * Configura el ViewModel para el modo edición con los datos de un repositorio existente.
     * @param repository El repositorio que se desea editar.
     */
    fun setEditMode(repository: Repository) {
        isEditMode = true
        originalOwner = repository.owner.login
        originalRepoName = repository.name
    }

    /**
     * Guarda el repositorio en GitHub. Decide entre crear o actualizar basándose en el modo.
     * 
     * @param name El nombre (nuevo o actualizado) del repositorio.
     * @param description La descripción (nueva o actualizada) del repositorio.
     */
    fun saveRepo(name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                val payload = RepositoryPayload(name, description)
                
                if (isEditMode) {
                    // Ejecuta PATCH en la API de GitHub
                    // Endpoint: /repos/{owner}/{repo}
                    RetrofitClient.apiService.updateRepository(
                        owner = originalOwner ?: "",
                        repo = originalRepoName ?: "",
                        repository = payload
                    )
                } else {
                    // Ejecuta POST en la API de GitHub
                    // Endpoint: /user/repos
                    RetrofitClient.apiService.createRepository(repository = payload)
                }
                
                _isSuccess.value = true
            } catch (e: HttpException) {
                _errorMsg.value = mapHttpErrorMessage(e)
            } catch (e: IOException) {
                _errorMsg.value = "Error de conexión: Verifica tu internet"
            } catch (e: Exception) {
                _errorMsg.value = "Error inesperado: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mapea los códigos de respuesta de Retrofit a mensajes amigables.
     * Crucial para que el estudiante entienda por qué falló la petición.
     */
    private fun mapHttpErrorMessage(e: HttpException): String {
        return when (e.code()) {
            400 -> "Solicitud incorrecta: Revisa los datos enviados."
            401 -> "No autorizado: Tu Token de GitHub podría ser inválido."
            403 -> "Prohibido: No tienes permisos suficientes para realizar esta acción."
            404 -> "No encontrado: El repositorio destino no existe."
            422 -> "Error de validación: Probablemente el nombre ya está en uso."
            500 -> "Error interno del servidor de GitHub."
            else -> "Error del servidor (${e.code()}): Intenta nuevamente."
        }
    }

    /**
     * Limpia el estado de éxito para permitir nuevas operaciones.
     */
    fun resetSuccess() {
        _isSuccess.value = false
    }
}
