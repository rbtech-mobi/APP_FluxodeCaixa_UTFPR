package com.caixaapp.util

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caixaapp.repository.AppDatabase

object DatabaseProvider {
    @Volatile
    private var instance: AppDatabase? = null

    // Migração de String para Long (Timestamp) para o campo data
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Room não permite mudar tipo de coluna facilmente, 
            // então recriamos a tabela ou usamos fallback para propósitos acadêmicos.
            db.execSQL("DROP TABLE IF EXISTS transactions")
            db.execSQL("""
                CREATE TABLE transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    valor REAL NOT NULL,
                    descricao TEXT NOT NULL,
                    data INTEGER NOT NULL,
                    tipo TEXT NOT NULL,
                    pessoaId TEXT NOT NULL
                )
            """)
        }
    }

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "caixaapp.db"
            )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration() // Facilita desenvolvimento se a migração falhar
            .build().also { instance = it }
        }
    }
}
