package com.example.census.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.census.data.AppDatabase
import com.example.census.data.UmatEntity
import com.example.census.repository.UmatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UmatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UmatRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val umatList: StateFlow<List<UmatEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAll()
            else repository.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val dao = AppDatabase.getInstance(application).umatDao()
        repository = UmatRepository(dao)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insert(umat: UmatEntity) = viewModelScope.launch {
        repository.insert(umat)
    }

    fun update(umat: UmatEntity) = viewModelScope.launch {
        repository.update(umat)
    }

    fun delete(umat: UmatEntity) = viewModelScope.launch {
        repository.delete(umat)
    }

    suspend fun getAllOnce(): List<UmatEntity> = repository.getAllOnce()

    fun insertAll(list: List<UmatEntity>) = viewModelScope.launch {
        list.forEach { repository.insert(it) }
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}