package com.example.proyectologin006d_final.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.proyectologin006d_final.MainDispatcherRule
import com.example.proyectologin006d_final.data.model.User
import com.example.proyectologin006d_final.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Clase de test unitario para LoginViewModel.
 *
 * El objetivo es verificar la lógica de la UI y la interacción con UserRepository
 * de forma aislada, usando mocks para simular las dependencias.
 */
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    // Regla para ejecutar corrutinas de forma síncrona en los tests.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Regla para ejecutar tareas de LiveData de forma síncrona.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Dependencia que vamos a simular (mock).
    private lateinit var userRepository: UserRepository

    // La clase que estamos probando.
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        // Creamos un mock del UserRepository.
        userRepository = mockk()
        // Creamos la instancia del ViewModel inyectando el mock.
        viewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `onUsernameChange - actualiza el estado con el nuevo username`() {
        // Arrange
        val nuevoUsername = "nuevo.usuario"

        // Act
        viewModel.onUsernameChange(nuevoUsername)

        // Assert
        assertEquals(nuevoUsername, viewModel.uiState.username)
        assertNull(viewModel.uiState.error) // Verificar que el error se limpia.
    }

    @Test
    fun `onPasswordChange - actualiza el estado con la nueva password`() {
        // Arrange
        val nuevaPassword = "password123"

        // Act
        viewModel.onPasswordChange(nuevaPassword)

        // Assert
        assertEquals(nuevaPassword, viewModel.uiState.password)
        assertNull(viewModel.uiState.error) // Verificar que el error se limpia.
    }

    @Test
    fun `onEmailChange - con correo @duocuc, actualiza el estado y isDuocUser es true`() {
        // Arrange
        val emailDuoc = "usuario@duocuc.cl"

        // Act
        viewModel.onEmailChange(emailDuoc)

        // Assert
        assertEquals(emailDuoc, viewModel.uiState.email)
        assertTrue(viewModel.uiState.isDuocUser)
        assertNull(viewModel.uiState.error)
    }

    @Test
    fun `onEmailChange - con correo no duoc, actualiza el estado y isDuocUser es false`() {
        // Arrange
        val emailNormal = "usuario@gmail.com"

        // Act
        viewModel.onEmailChange(emailNormal)

        // Assert
        assertEquals(emailNormal, viewModel.uiState.email)
        assertFalse(viewModel.uiState.isDuocUser)
    }

    @Test
    fun `submit con credenciales correctas - debe llamar a onSuccess`() = runTest {
        // Arrange (Preparación)
        val username = "testuser"
        val password = "password"
        val usuarioValido = User(1, username, "test@duocuc.cl",password,true,System.currentTimeMillis())
        var onSuccessLlamado = false

        // Simulamos la respuesta exitosa del repositorio
        coEvery { userRepository.loginUser(username, password) } returns Result.success(usuarioValido)

        // Rellenamos el estado del ViewModel
        viewModel.onUsernameChange(username)
        viewModel.onPasswordChange(password)

        // Act (Acción)
        viewModel.submit { returnedUser ->
            onSuccessLlamado = true
            assertEquals(username, returnedUser) // Verificamos que el lambda recibe el username correcto
        }

        // Assert (Verificación)
        // 1. Verificamos que se llamó al repositorio.
        coVerify(exactly = 1) { userRepository.loginUser(username, password) }

        // 2. Verificamos que el callback onSuccess fue invocado.
        assertTrue(onSuccessLlamado)

        // 3. Verificamos que el estado de la UI es correcto (no hay carga, no hay error).
        assertFalse(viewModel.uiState.isLoading)
        assertNull(viewModel.uiState.error)
    }

    @Test
    fun `submit con credenciales incorrectas - debe actualizar el estado con un error`() = runTest {
        // Arrange
        val username = "baduser"
        val password = "badpassword"
        val mensajeError = "Credenciales inválidas"
        var onSuccessLlamado = false

        // Simulamos la respuesta fallida del repositorio
        coEvery { userRepository.loginUser(username, password) } returns Result.failure(Exception(mensajeError))

        viewModel.onUsernameChange(username)
        viewModel.onPasswordChange(password)

        // Act
        viewModel.submit { onSuccessLlamado = true }

        // Assert
        // 1. Verificamos que se llamó al repositorio.
        coVerify(exactly = 1) { userRepository.loginUser(username, password) }

        // 2. Verificamos que onSuccess NUNCA fue llamado.
        assertFalse(onSuccessLlamado)

        // 3. Verificamos que el estado de la UI refleja el error.
        assertFalse(viewModel.uiState.isLoading)
        assertEquals(mensajeError, viewModel.uiState.error)
    }

    @Test
    fun `clearError - debe limpiar el mensaje de error en el estado`() {
        // Arrange: Primero, forzamos un estado de error.
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("pass")
        coEvery { userRepository.loginUser(any(), any()) } returns Result.failure(Exception("Error inicial"))
        runTest { viewModel.submit {} } // Ejecutamos el submit para que se establezca el error.

        // Verificamos que el error existe antes de limpiarlo.
        assertNotNull(viewModel.uiState.error)

        // Act
        viewModel.clearError()

        // Assert
        assertNull(viewModel.uiState.error)
    }
}
//comentario