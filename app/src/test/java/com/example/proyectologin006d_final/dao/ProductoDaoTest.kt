package com.example.proyectologin006d_final.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.proyectologin006d_final.data.database.ProductoDatabase
import com.example.proyectologin006d_final.data.model.Producto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Clase de test para ProductoDao usando el ejecutor estándar de Robolectric.
 * Esta configuración es robusta y evita problemas con los runners de test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Especifica un SDK para Robolectric, 34 es una buena opción reciente.
class ProductoDaoTest {

    private lateinit var database: ProductoDatabase
    private lateinit var productoDao: ProductoDao

    /**
     * Este método se ejecuta ANTES de cada test.
     * Crea una base de datos en memoria, que es rápida y se aísla para cada prueba.
     */
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProductoDatabase::class.java
        ).allowMainThreadQueries() // Se permite solo para tests, para simplificar.
            .build()
        productoDao = database.productoDao()
    }

    /**
     * Este método se ejecuta DESPUÉS de cada test.
     * Cierra la conexión a la base de datos para liberar recursos y evitar fugas de memoria.
     */
    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun alInsertarUnProducto_laBaseDeDatosLoContiene() = runTest {
        // Preparación (Arrange)
        val producto = Producto(id = 1, nombre = "Producto de Prueba", codigo = "P001", categoria = "Producto Test", precio = "9.99", descripcion = "producto de test", cantidad = "10", direccion = "Los Alamos")

        // Acción (Act)
        productoDao.insertarProducto(producto)

        // Verificación (Assert)
        val productos = productoDao.obtenerProductos().first()
        assertEquals("La lista debe contener 1 producto.", 1, productos.size)
        assertEquals("El producto en la lista debe ser el que se insertó.", producto, productos[0])
    }

    @Test
    fun alIniciar_laBaseDeDatosDebeEstarVacia() = runTest {
        // Acción (Act)
        val productos = productoDao.obtenerProductos().first()

        // Verificación (Assert)
        assertTrue("La lista de productos debería estar vacía.", productos.isEmpty())
    }

    @Test
    fun alInsertarVariosProductos_laBaseDeDatosLosContieneTodos() = runTest {
        // Preparación (Arrange)
        val producto1 = Producto(id = 1, nombre = "Producto 1", codigo = "P01", categoria = "Test", precio = "10.00", descripcion = "Desc1", cantidad = "5", direccion = "Dir1")
        val producto2 = Producto(id = 2, nombre = "Producto 2", codigo = "P02", categoria = "Test", precio = "20.00", descripcion = "Desc2", cantidad = "3", direccion = "Dir2")

        // Acción (Act)
        productoDao.insertarProducto(producto1)
        productoDao.insertarProducto(producto2)

        // Verificación (Assert)
        val productos = productoDao.obtenerProductos().first()
        assertEquals("La lista debe contener 2 productos.", 2, productos.size)
        assertTrue("La lista debe contener el Producto 1.", productos.contains(producto1))
        assertTrue("La lista debe contener el Producto 2.", productos.contains(producto2))
    }

    @Test
    fun alInsertarUnProducto_suIdSeAutogeneraCorrectamente() = runTest {
        // Preparación (Arrange)
        // El id se establece en 0 para que Room lo autogenere.
        val productoSinId = Producto(id = 0, nombre = "Producto Autogen", codigo = "P-AUTO", categoria = "Test", precio = "5.00", descripcion = "Desc", cantidad = "1", direccion = "Dir")

        // Acción (Act)
        productoDao.insertarProducto(productoSinId)

        // Verificación (Assert)
        val productos = productoDao.obtenerProductos().first()
        val productoInsertado = productos[0]

        assertEquals("Debe haber 1 producto en la lista.", 1, productos.size)
        assertEquals("El ID generado debería ser 1.", 1, productoInsertado.id)
        assertEquals("El nombre debe coincidir con el del producto insertado.", "Producto Autogen", productoInsertado.nombre)
    }
}
//comentario