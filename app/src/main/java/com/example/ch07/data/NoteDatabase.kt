
package com.example.ch07.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters

// *TODO [T4.3] Buat `MIGRATION_1_2 = object : Migration(1, 2)` yang menjalankan*
//   *ALTER TABLE notes ADD COLUMN tags TEXT NOT NULL DEFAULT '[]'*
//   *('[]' = daftar tag kosong, supaya catatan lama tetap terbaca).*
// *TODO [T3.2] Buat Migration berikutnya untuk kolom `updatedAt`*
//   *(INTEGER NOT NULL DEFAULT 0), lalu isi catatan lama dengan*
//   *UPDATE notes SET updatedAt = createdAt  agar tidak tampil "puluhan tahun lalu".*
//   *Nomor versi mengikuti URUTAN kamu mengerjakan Tugas 3 dan 4: selalu naik +1*
//   *dan `Migration(awal, akhir)` harus cocok. (Solusi ch07 mengerjakan tag dulu.)*

@Database(
    entities = [NoteEntity::class],
    version = 3,                 // *TODO [T3.2][T4.3] naikkan setiap skema berubah*
    exportSchema = true          // JSON skema -> app/schemas/ (lihat build.gradle.kts)
)
@TypeConverters(Converters::class)
// *TODO [T4.2] Daftarkan converter: @TypeConverters(Converters::class)*
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        // TODO [T3.2]
        // Migration dari versi 1 ke versi 2 untuk menambahkan updatedAt
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE notes ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "UPDATE notes SET updatedAt = createdAt"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE notes ADD COLUMN tags TEXT NOT NULL DEFAULT '[]'"
                )
            }
        }

        // @Volatile: INSTANCE selalu dibaca dari memori utama, bukan cache thread
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        // synchronized: mencegah dua thread membuat instance bersamaan
        fun getInstance(context: Context): NoteDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    // *TODO [T3.2][T4.3] Ganti baris di bawah dengan*
                    //   *.addMigrations(MIGRATION_1_2, ...). fallbackToDestructiveMigration*
                    //   *MENGHAPUS data pengguna saat versi naik: hanya boleh saat*
                    //   *pengembangan awal, jangan dipakai di produksi.*
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
    }
}