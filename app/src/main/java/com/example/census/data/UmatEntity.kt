package com.example.census.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "umat")
data class UmatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaLengkap: String = "",
    val nomorKK: String = "",
    val nomorNIK: String = "",
    val jenisKelamin: String = "",
    val tempatTanggalLahir: String = "",
    val tempatBaptis: String = "",
    val tempatKomuni1: String = "",
    val tempatKrisma: String = "",
    val tanggalMenikah: String = "",
    val tinggalDiApmrTahun: String = "",
    val berkas: String = ""
)