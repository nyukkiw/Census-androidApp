package com.example.census.utils

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
        "ID", "Nama Lengkap", "Nomor KK", "Nomor NIK", "Jenis Kelamin",
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
                val parser = CSVParser(reader, CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build())
                val list = parser.records.mapNotNull { record ->
                    try {
                        UmatEntity(
                            namaLengkap = record.get("Nama Lengkap"),
                            nomorKK = record.get("Nomor KK"),
                            nomorNIK = record.get("Nomor NIK"),
                            jenisKelamin = record.get("Jenis Kelamin"),
                            tempatTanggalLahir = record.get("Tempat Tanggal Lahir"),
                            tempatBaptis = record.get("Tempat Baptis"),
                            tempatKomuni1 = record.get("Tempat Komuni 1"),
                            tempatKrisma = record.get("Tempat Krisma"),
                            tanggalMenikah = record.get("Tanggal Menikah"),
                            tinggalDiApmrTahun = record.get("Tinggal Di APMR Tahun"),
                            berkas = record.get("Berkas")
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