package com.example.proyectologin006d_final.data.repository

import com.example.proyectologin006d_final.data.dao.ProductoDao
import com.example.proyectologin006d_final.data.model.Producto
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Clase de test unitario para ProductoRepository.
 *
 * El objetivo es verificar que el repositorio delega correctamente las llamadas
 * a su dependencia, que es ProductoDao. Para ello, usamos MockK para crear
 * un "doble de prueba" (un mock) del DAO.
 */
class ProductoRepositoryTest {

    // 1. Declarar las dependencias que vamos a simular (mock)
    private lateinit var productoDao: ProductoDao

    // 2. Declarar la clase que estamos probando
    private lateinit var repository: ProductoRepository

    /**
     * Este método se ejecuta ANTES de cada test.
     * Es el lugar perfecto para inicializar nuestros mocks y la clase a probar.
     */
    @Before
    fun setUp() {
        // Creamos un mock del ProductoDao. Este objeto simulará ser el DAO
        // pero sin tener una base de datos real detrás. `relaxed = true`
        // evita tener que definir el comportamiento de cada función si no es necesario.
        productoDao = mockk(relaxed = true)

        // Creamos una instancia del repositorio, inyectándole el DAO falso.
        repository = ProductoRepository(productoDao)
    }

    @Test
    fun `al llamar a insertarProducto, se debe llamar al metodo insertarProducto del DAO`() = runTest {
        // Arrange (Preparación): Creamos un producto de prueba.
        val productoDePrueba = Producto(id = 1, nombre = "Anillo Test", codigo = "P01", categoria = "a", precio = "1", descripcion = "d", cantidad = "c", direccion = "f")

        // Act (Acción): Llamamos a la función del repositorio que queremos probar.
        repository.insertarProducto(productoDePrueba)

        // Assert (Verificación): Verificamos que el método `insertarProducto` del DAO
        // fue llamado exactamente una vez con el producto de prueba.
        // `coVerify` se usa para funciones `suspend`.
        coVerify(exactly = 1) { productoDao.insertarProducto(productoDePrueba) }
    }

    @Test
    fun `al llamar a obtenerProductos, se debe llamar al metodo obtenerProductos del DAO`() = runTest {
        // Arrange: No se necesita preparación especial para este test.

        // Act: Llamamos a la función del repositorio.
        repository.obtenerProductos()

        // Assert: Verificamos que el método `obtenerProductos` del DAO
        // fue llamado exactamente una vez.
        // `verify` se usa para funciones que no son `suspend`.
        verify(exactly = 1) { productoDao.obtenerProductos() }
    }
}
//comentario