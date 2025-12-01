
//--------------------------------------------------------------------------------------
//Este código implementa una prueba unitaria para verificar el comportamiento del PostViewModel en la arquitectura MVVM.
// PROPÓSITO PRINCIPAL
// Validar que el ViewModel maneja correctamente el estado de los posts a través de su StateFlow, simulando el flujo de datos desde el //repositorio.
// Configuración de Corrutinas: Usa UnconfinedTestDispatcher para ejecución inmediata en tests
// ViewModel Real: Testea la instancia real de PostViewModel (no un mock)
// Manipulación Directa: Asigna datos fake directamente al StateFlow interno
// Verificaciones de Estado: Confirma que los datos se reflejan correctamente en el estado público
//--------------------------------------------------------------------------------------






// Declara el paquete donde se encuentra la clase de test
package com.example.proyectologin006d_final.viewmodel

// Importa el modelo de datos Post
import com.example.proyectologin006d_final.data.model.Post
// Importa los dispatchers de corrutinas
import kotlinx.coroutines.Dispatchers
// Importa la anotación para APIs experimentales de corrutinas
import kotlinx.coroutines.ExperimentalCoroutinesApi
// Importa el dispatcher de test sin confinamiento
import kotlinx.coroutines.test.UnconfinedTestDispatcher
// Importa función para resetear el dispatcher principal
import kotlinx.coroutines.test.resetMain
// Importa el builder para tests de corrutinas
import kotlinx.coroutines.test.runTest
// Importa función para establecer el dispatcher principal de test
import kotlinx.coroutines.test.setMain
// Importa anotación para método que se ejecuta después de cada test
import org.junit.jupiter.api.AfterEach
// Importa funciones de aserción para tests
import org.junit.jupiter.api.Assertions.*
// Importa anotación para método que se ejecuta antes de cada test
import org.junit.jupiter.api.BeforeEach
// Importa anotación para definir métodos de test
import org.junit.jupiter.api.Test

// Anotación para indicar uso de APIs experimentales de corrutinas
@OptIn(ExperimentalCoroutinesApi::class)
// Clase de tests para PostViewModel
class PostViewModelTest {

    // Crea un dispatcher de test sin confinamiento para ejecución inmediata
    private val testDispatcher = UnconfinedTestDispatcher()

    // Método que se ejecuta antes de cada test
    @BeforeEach
    fun setUp() {
        // Configurar el dispatcher de test antes de cada test
        // Establece el dispatcher principal para tests
        Dispatchers.setMain(testDispatcher)
    }

    // Método que se ejecuta después de cada test
    @AfterEach
    fun tearDown() {
        // Limpiar después de cada test
        // Restaura el dispatcher principal original
        Dispatchers.resetMain()
    }

    // Test que verifica que postList contiene los datos esperados
    @Test
    fun `postList contiene datos esperados`() = runTest(testDispatcher) {
        // Crea una lista falsa de posts para el test
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Título 1", body = "Contenido 1"),
            Post(userId = 2, id = 2, title = "Título 2", body = "Contenido 2")
        )

        // El ViewModel ahora usará el testDispatcher gracias a @BeforeEach
        // Crea una instancia del ViewModel bajo test
        val viewModel = PostViewModel()

        // Simulamos que fetchPosts() completó exitosamente
        // Asigna directamente los datos falsos al StateFlow mutable
        viewModel._postList.value = fakePosts

        // Verifica que el tamaño de la lista sea el esperado
        assertEquals(2, viewModel.postList.value.size)
        // Verifica que el título del primer post sea correcto
        assertEquals("Título 1", viewModel.postList.value[0].title)
        // Verifica que el cuerpo del segundo post sea correcto
        assertEquals("Contenido 2", viewModel.postList.value[1].body)
    }

    // Test básico de ejemplo sin lógica compleja
    @Test
    fun `test básico de ejemplo`() = runTest(testDispatcher) {
        // Test simple que no depende del ViewModel
        // Verifica una igualdad básica
        assertEquals(1, 1)
    }
}