package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.model.Credential
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test/**
 * Clase de test unitario para AuthRepository.
 *
 * El objetivo es verificar que la lógica de la función `login` se comporta
 * como se espera en diferentes escenarios.
 */
class AuthRepositoryTest {

    // Declaramos la clase que vamos a probar
    private lateinit var authRepository: AuthRepository

    // Datos de prueba que usaremos en los tests
    private val credencialAdmin = Credential.Admin

    /**
     * Este método se ejecuta ANTES de cada test.
     * Es el lugar perfecto para inicializar la clase a probar.
     */
    @Before
    fun setUp() {
        // Creamos una nueva instancia del repositorio antes de cada test
        // para asegurar que cada prueba se ejecuta de forma aislada.
        // Como el constructor tiene un valor por defecto, no necesitamos pasarle nada.
        authRepository = AuthRepository()
    }

    @Test
    fun `login con credenciales correctas debe devolver true`() {
        // Arrange (Preparación): Obtenemos el username y password válidos.
        val usernameCorrecto = credencialAdmin.username
        val passwordCorrecta = credencialAdmin.password

        // Act (Acción): Llamamos a la función que queremos probar.
        val resultado = authRepository.login(usernameCorrecto, passwordCorrecta)

        // Assert (Verificación): Comprobamos que el resultado es el esperado.
        assertTrue("El login debería ser exitoso con credenciales correctas", resultado)
    }

    @Test
    fun `login con contraseña incorrecta debe devolver false`() {
        // Arrange
        val usernameCorrecto = credencialAdmin.username
        val passwordIncorrecta = "contraseña_equivocada_123"

        // Act
        val resultado = authRepository.login(usernameCorrecto, passwordIncorrecta)

        // Assert
        assertFalse("El login debería fallar con una contraseña incorrecta", resultado)
    }

    @Test
    fun `login con username incorrecto debe devolver false`() {
        // Arrange
        val usernameIncorrecto = "usuario_inexistente"
        val passwordCorrecta = credencialAdmin.password

        // Act
        val resultado = authRepository.login(usernameIncorrecto, passwordCorrecta)

        // Assert
        assertFalse("El login debería fallar con un nombre de usuario incorrecto", resultado)
    }

    @Test
    fun `login con ambas credenciales incorrectas debe devolver false`() {
        // Arrange
        val usernameIncorrecto = "usuario_malo"
        val passwordIncorrecta = "password_mala"

        // Act
        val resultado = authRepository.login(usernameIncorrecto, passwordIncorrecta)

        // Assert
        assertFalse("El login debería fallar con ambas credenciales incorrectas", resultado)
    }
}
//comentario