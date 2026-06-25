package com.example.census.utils

import android.R
import android.content.Context
import android.net.Uri
import com.example.census.data.UmatEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object CsvHelper {

    private val HEADERS = arrayOf(
        "ID", "Nama Lengkap Umat", "Nomor KK", "Nomor NIK", "Jenis Kelamin",
        "Tempat Tanggal Lahir", "Tempat Baptis", "Tempat Komuni 1",
        "Tempat Krisma", "Tanggal Menikah", "Tinggal Di APMR Tahun", "Berkas"
    )

    fun export(context: Context, uri: Uri, data: List<UmatEntity>): Result<Int> {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream, Charsets.UTF_8)
                val printer = CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                    .setHeader(*HEADERS)
                    .build())
                data.forEach { u ->
                    printer.printRecord(
                        u.id, u.namaLengkap, u.nomorKK, u.nomorNIK,
                        u.jenisKelamin, u.tempatTanggalLahir, u.tempatBaptis,
                        u.tempatKomuni1, u.tempatKrisma, u.tanggalMenikah,
                        u.tinggalDiApmrTahun, u.berkas
                    )
                }
                printer.flush()
                Result.success(data.size)
            } ?: Result.failure(Exception("Tidak bisa membuka file"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun import(context: Context, uri: Uri): Result<List<UmatEntity>> {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = InputStreamReader(inputStream, Charsets.UTF_8)
                val lines = reader.readLines()

                // cari baris yang mengandung header kolom
                val headerIndex = lines.indexOfFirst { line ->
                    line.contains("NAMA LENGKAP UMAT", ignoreCase = true) ||
                            line.contains("NOMOR KK", ignoreCase = true)
                }

                if (headerIndex == -1) return Result.failure(Exception("Header tidak ditemukan"))

                // ambil dari baris header ke bawah
                val csvContent = lines.drop(headerIndex).joinToString("\n")

                val parser = CSVParser.parse(csvContent, CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)    // tambah ini
                    .setAllowMissingColumnNames(true)  // tambah ini
                    .build())

                val list = parser.records.mapNotNull { record ->
                    try {
                        fun get(vararg keys: String): String {
                            for (key in keys) {
                                try {
                                    val v = record.get(key)
                                    if (v != null) return v
                                } catch (e: Exception) { }
                            }
                            return ""
                        }
                        UmatEntity(
                            namaLengkap = get("NAMA LENGKAP UMAT", "Nama Lengkap"),
                            nomorKK = get("NOMOR KK", "Nomor KK"),
                            nomorNIK = get("NOMOR NIK", "Nomor NIK"),
                            jenisKelamin = get("JENIS KELAMIN", "Jenis Kelamin"),
                            tempatTanggalLahir = get("TEMPAT, TANGGAL LAHIR", "TEMPAT TANGGAL LAHIR", "Tempat Tanggal Lahir"),
                            tempatBaptis = get("TEMPAT BAPTIS", "Tempat Baptis"),
                            tempatKomuni1 = get("TEMPAT KOMUNI 1", "Tempat Komuni 1"),
                            tempatKrisma = get("TEMPAT KRISMA", "Tempat Krisma"),
                            tanggalMenikah = get("TANGGAL MENIKAH", "Tanggal Menikah"),
                            tinggalDiApmrTahun = get("TINGGAL DI APMR TAHUN", "Tinggal Di APMR Tahun"),
                            berkas = get("BERKAS", "Berkas")
                        )
                    } catch (e: Exception) { null }
                }
                Result.success(list)
            } ?: Result.failure(Exception("Tidak bisa membuka file"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}