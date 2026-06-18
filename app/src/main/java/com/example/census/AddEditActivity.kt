package com.example.census

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.census.data.UmatEntity
import com.example.census.databinding.ActivityAddEditBinding
import com.example.census.viewmodel.UmatViewModel
import kotlinx.coroutines.launch


class AddEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
    private val viewModel: UmatViewModel by viewModels()
    private var existingUmat: UmatEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupDropdown()
        loadExistingData()
        setupSaveButton()
    }

    private fun setupDropdown() {
        val options = listOf("Laki-laki", "Perempuan")
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            options
        )
        binding.etJenisKelamin.setAdapter(adapter)
    }

    private fun loadExistingData() {
        val umatId = intent.getIntExtra("umat_id", -1)
        if (umatId != -1) {
            lifecycleScope.launch {
                val umat = viewModel.getAllOnce().find { it.id == umatId }
                umat?.let {
                    existingUmat = it
                    binding.toolbar.title = "Edit Umat"
                    binding.etNama.setText(it.namaLengkap)
                    binding.etNomorKK.setText(it.nomorKK)
                    binding.etNomorNIK.setText(it.nomorNIK)
                    binding.etJenisKelamin.setText(it.jenisKelamin, false)
                    binding.etTempatTanggalLahir.setText(it.tempatTanggalLahir)
                    binding.etTempatBaptis.setText(it.tempatBaptis)
                    binding.etTempatKomuni.setText(it.tempatKomuni1)
                    binding.etTempatKrisma.setText(it.tempatKrisma)
                    binding.etTanggalMenikah.setText(it.tanggalMenikah)
                    binding.etTinggalDiApmr.setText(it.tinggalDiApmrTahun)
                    binding.etBerkas.setText(it.berkas)
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            if (nama.isEmpty()) {
                binding.etNama.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            val umat = UmatEntity(
                id = existingUmat?.id ?: 0,
                nomorKK = binding.etNomorKK.text.toString().trim(),
                namaLengkap = nama,
                nomorNIK = binding.etNomorNIK.text.toString().trim(),
                jenisKelamin = binding.etJenisKelamin.text.toString().trim(),
                tempatTanggalLahir = binding.etTempatTanggalLahir.text.toString().trim(),
                tempatBaptis = binding.etTempatBaptis.text.toString().trim(),
                tempatKomuni1 = binding.etTempatKomuni.text.toString().trim(),
                tempatKrisma = binding.etTempatKrisma.text.toString().trim(),
                tanggalMenikah = binding.etTanggalMenikah.text.toString().trim(),
                tinggalDiApmrTahun = binding.etTinggalDiApmr.text.toString().trim(),
                berkas = binding.etBerkas.text.toString().trim()
            )

            if (existingUmat == null) viewModel.insert(umat)
            else viewModel.update(umat)

            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}