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
import com.example.proyectologin006d_final.data.model.Producto
import com.example.proyectologin006d_final.data.model.LevelUpPoints
import com.example.proyectologin006d_final.data.model.Referral
import com.example.proyectologin006d_final.data.model.User

@Database(
    entities = [Producto::class, LevelUpPoints::class, Referral::class, User::class],
    version=4,
    exportSchema = false // Agregar para evitar el warning
)
abstract class ProductoDatabase: RoomDatabase(){
    abstract fun productoDao(): ProductoDao
    abstract fun levelUpDao(): LevelUpDao
    abstract fun userDao(): UserDao

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
        
        fun getDatabase(context: Context): ProductoDatabase{
            return INSTANCE?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    ProductoDatabase::class.java,
                    "producto_database"
                )
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration() // Solo para desarrollo
                .build() // fin Room
                INSTANCE=instance
                instance

            }//fin return
        }// fin getDatabase

    }// fin companion


}// fin abstract