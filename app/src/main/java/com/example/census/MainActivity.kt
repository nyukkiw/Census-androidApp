package com.example.census

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.census.data.UmatEntity
import com.example.census.databinding.ActivityMainBinding
import com.example.census.ui.UmatAdapter
import com.example.census.utils.CsvHelper
import com.example.census.viewmodel.UmatViewModel
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: UmatViewModel by viewModels()
    private lateinit var adapter: UmatAdapter

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            lifecycleScope.launch {
                val data = viewModel.getAllOnce()
                val result = CsvHelper.export(this@MainActivity, it, data)
                result.fold(
                    onSuccess = { count -> showSnackbar("Berhasil export $count data") },
                    onFailure = { e -> showSnackbar("Gagal export: ${e.message}") }
                )
            }
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            lifecycleScope.launch {
                val result = CsvHelper.import(this@MainActivity, it)
                result.fold(
                    onSuccess = { list ->
                        viewModel.insertAll(list)
                        showSnackbar("Berhasil import ${list.size} data")
                    },
                    onFailure = { e -> showSnackbar("Gagal import: ${e.message}") }
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = UmatAdapter(
            onItemClick = { umat ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("umat_id", umat.id)
                startActivity(intent)
            },
            onItemLongClick = { umat ->
                showDeleteDialog(umat)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener { text ->
            viewModel.setSearchQuery(text.toString())
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.umatList.collect { list ->
                adapter.submitList(list)
                binding.emptyView.visibility =
                    if (list.isEmpty()) android.view.View.VISIBLE
                    else android.view.View.GONE
            }
        }
    }

    private fun showDeleteDialog(umat: UmatEntity) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Hapus data ${umat.namaLengkap}?")
            .setPositiveButton("Hapus") { _, _ -> viewModel.delete(umat) }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export -> {
                exportLauncher.launch("data_umat.csv")
                true
            }
            R.id.action_import -> {
                importLauncher.launch("*/*")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSnackbar(message: String) {
        com.google.android.material.snackbar.Snackbar
            .make(binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
            .show()
    }
}