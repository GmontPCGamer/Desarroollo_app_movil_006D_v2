package com.example.proyectologin006d_final.ui.register

import android.util.Patterns
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.proyectologin006d_final.MainDispatcherRule
import com.example.proyectologin006d_final.data.model.User
import com.example.proyectologin006d_final.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch

/**
 * Clase de test unitario para RegisterViewModel.
 *
 * El objetivo es verificar toda la lógica de validación de campos y el flujo de registro,
 * tanto el caso de éxito como el de fallo, interactuando con un mock de UserRepository.
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class) // <-- PASO 1 APLICADO: Se añade el ejecutor de Robolectric
@Config(sdk = [34])          // <-- BUENA PRÁCTICA: Evita que Robolectric busque un Manifest, haciendo el test más rápido
class RegisterViewModelTest {

    // Regla para ejecutar corrutinas de forma síncrona en los tests.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Regla para ejecutar tareas de LiveData/StateFlow de forma síncrona.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Dependencia que vamos a simular (mock).
    private lateinit var userRepository: UserRepository

    // La clase que estamos probando.
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        // Creamos un mock "relajado" para no tener que definir el comportamiento de cada función.
        // `relaxed = true` evita errores si se llama a una función no esperada.
        userRepository = mockk(relaxed = true)
        viewModel = RegisterViewModel(userRepository)
    }

    // --- Tests de Validaciones ---

    @Test
    fun `registerUser con username vacío debe mostrar error`() {
        // Arrange: Solo llenamos los campos necesarios para que falle donde queremos.
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("123456")
        viewModel.onConfirmPasswordChange("123456")

        // Act
        viewModel.registerUser { } // El callback no se debe llamar

        // Assert
        assertEquals("El nombre de usuario es requerido", viewModel.uiState.value.error)
    }

    @Test
    fun `registerUser con email invalido debe mostrar error`() {
        // Arrange
        viewModel.onUsernameChange("testuser")
        viewModel.onEmailChange("email-invalido") // Email con formato incorrecto
        viewModel.onPasswordChange("123456")
        viewModel.onConfirmPasswordChange("123456")

        // Act
        viewModel.registerUser { }

        // Assert
        assertEquals("El formato del correo electrónico no es válido", viewModel.uiState.value.error)
    }

    @Test
    fun `registerUser con contraseñas que no coinciden debe mostrar error`() {
        // Arrange
        viewModel.onUsernameChange("testuser")
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password456") // Contraseña diferente

        // Act
        viewModel.registerUser { }

        // Assert
        assertEquals("Las contraseñas no coinciden", viewModel.uiState.value.error)
    }

    @Test
    fun `registerUser con contraseña corta debe mostrar error`() {
        // Arrange
        viewModel.onUsernameChange("testuser")
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("123") // Contraseña corta
        viewModel.onConfirmPasswordChange("123")

        // Act
        viewModel.registerUser { }

        // Assert
        assertEquals("La contraseña debe tener al menos 6 caracteres", viewModel.uiState.value.error)
    }

    // --- Tests de Flujo de Registro ---

    @Test
    fun `registerUser con datos válidos y repositorio exitoso debe llamar a onSuccess`() = runTest {
        // Arrange (Preparación)
        val username = "nuevoUsuario"
        val email = "nuevo@usuario.com"
        val password = "passwordValida"
        var onSuccessLlamado = false
        var usuarioRecibido = ""

        // Simulamos la respuesta exitosa del repositorio
        coEvery { userRepository.registerUser(any()) } returns Result.success(1L) // Devuelve un ID de usuario de ejemplo

        // Llenamos el ViewModel con datos válidos
        viewModel.onUsernameChange(username)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onConfirmPasswordChange(password)

        // Act (Acción)
        viewModel.registerUser {
            onSuccessLlamado = true
            usuarioRecibido = it
        }

        // Assert (Verificación)
        // 1. Verificamos que se llamó al repositorio con un objeto User correcto
        coVerify(exactly = 1) {
            userRepository.registerUser(
                match { it.username == username && it.email == email }
            )
        }

        // 2. Verificamos que el callback onSuccess fue invocado
        assertTrue("El callback onSuccess debería haber sido llamado", onSuccessLlamado)
        assertEquals(username, usuarioRecibido)

        // 3. Verificamos que el estado de la UI es de éxito
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertTrue(finalState.isSuccess)
        assertNull(finalState.error)
    }

    @Test
    fun `registerUser con datos válidos y repositorio fallido debe mostrar error`() = runTest {
        // Arrange
        val username = "usuarioExistente"
        val email = "existente@usuario.com"
        val password = "passwordValida"
        val mensajeError = "El nombre de usuario ya existe"
        var onSuccessLlamado = false

        // Simulamos la respuesta fallida del repositorio
        coEvery { userRepository.registerUser(any()) } returns Result.failure(Exception(mensajeError))

        // Llenamos el ViewModel
        viewModel.onUsernameChange(username)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onConfirmPasswordChange(password)

        // Act
        viewModel.registerUser { onSuccessLlamado = true }

        // Assert
        // 1. Verificamos que el callback onSuccess NUNCA fue llamado
        assertFalse("El callback onSuccess no debería haber sido llamado", onSuccessLlamado)

        // 2. Verificamos que el estado de la UI refleja el error del repositorio
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertFalse(finalState.isSuccess)
        assertEquals(mensajeError, finalState.error)
    }
}
