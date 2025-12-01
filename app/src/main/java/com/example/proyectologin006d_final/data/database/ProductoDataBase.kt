package com.example.proyectologin006d_final.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectologin006d_final.data.dao.ProductoDao
import com.example.proyectologin006d_final.data.dao.LevelUpDao
import com.example.proyectologin006d_final.data.dao.UserDao
import com.example.proyectologin006d_final.data.dao.CartDao
import com.example.proyectologin006d_final.data.dao.DiscountDao
import com.example.proyectologin006d_final.data.model.Producto
import com.example.proyectologin006d_final.data.model.LevelUpPoints
import com.example.proyectologin006d_final.data.model.Referral
import com.example.proyectologin006d_final.data.model.User
import com.example.proyectologin006d_final.data.model.CartItem
import com.example.proyectologin006d_final.data.model.Discount
import com.example.proyectologin006d_final.data.model.PurchaseHistory

@Database(
    entities = [Producto::class, LevelUpPoints::class, Referral::class, User::class, CartItem::class, Discount::class, PurchaseHistory::class],
    version = 7,
    exportSchema = false // Agregar para evitar el warning
)
abstract class ProductoDatabase: RoomDatabase(){
    abstract fun productoDao(): ProductoDao
    abstract fun levelUpDao(): LevelUpDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun discountDao(): DiscountDao

    companion object{
        @Volatile
        private var INSTANCE: ProductoDatabase?=null
        
        // Migración de la versión 3 a la 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear la tabla users
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `users` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `username` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `password` TEXT NOT NULL,
                        `isDuocUser` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
                
                // Crear índices únicos
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)")
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`)")
            }
        }
        
        // Migración de la versión 4 a la 5 - Agregar tabla cart_items
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear la tabla cart_items
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `cart_items` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `productId` TEXT NOT NULL,
                        `productName` TEXT NOT NULL,
                        `productPrice` TEXT NOT NULL,
                        `priceValue` REAL NOT NULL,
                        `quantity` INTEGER NOT NULL DEFAULT 1,
                        `category` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `manufacturer` TEXT NOT NULL,
                        `username` TEXT NOT NULL,
                        `addedAt` INTEGER NOT NULL
                    )
                """)
            }
        }
        
        // Migración de la versión 5 a la 6 - Agregar tabla discounts
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear la tabla discounts
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `discounts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `code` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `percentage` INTEGER NOT NULL,
                        `username` TEXT NOT NULL,
                        `isUsed` INTEGER NOT NULL DEFAULT 0,
                        `scannedAt` INTEGER NOT NULL,
                        `expiresAt` INTEGER
                    )
                """)
            }
        }
        
        // Migración de la versión 6 a la 7 - Agregar tabla purchase_history
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crear la tabla purchase_history
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `purchase_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `username` TEXT NOT NULL,
                        `totalAmount` REAL NOT NULL,
                        `itemsCount` INTEGER NOT NULL,
                        `pointsEarned` INTEGER NOT NULL,
                        `bonusPoints` INTEGER NOT NULL DEFAULT 0,
                        `purchaseDate` INTEGER NOT NULL,
                        `orderNumber` TEXT NOT NULL,
                        `itemsSummary` TEXT NOT NULL
                    )
                """)
            }
        }
        
        fun getDatabase(context: Context): ProductoDatabase{
            return INSTANCE?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    ProductoDatabase::class.java,
                    "producto_database"
                )
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                .fallbackToDestructiveMigration() // Solo para desarrollo
                .build() // fin Room
                INSTANCE=instance
                instance

            }//fin return
        }// fin getDatabase

    }// fin companion


}// fin abstract
