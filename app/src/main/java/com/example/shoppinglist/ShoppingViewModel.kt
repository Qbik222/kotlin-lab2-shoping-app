package com.example.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShoppingRepository
import com.example.shoppinglist.domain.ShoppingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class FilterType {
    All,
    Bought,
    NotBought,
}

enum class SortType {
    NameAsc,
    CreatedAtDesc,
    BoughtFirst,
}

data class ShoppingUiState(
    val displayedItems: List<ShoppingItem> = emptyList(),
    val filter: FilterType = FilterType.All,
    val sort: SortType = SortType.CreatedAtDesc,
    val page: Int = 1,
    val hasMore: Boolean = false,
    val isSyncing: Boolean = false,
    val syncError: String? = null,
    val nameInput: String = "",
    val quantityInput: String = "1",
) {
    companion object {
        const val PAGE_SIZE: Int = 10
    }
}

class ShoppingViewModel(
    private val repository: ShoppingRepository,
) : ViewModel() {

    private val _allItems = MutableStateFlow<List<ShoppingItem>>(emptyList())
    private val _filter = MutableStateFlow(FilterType.All)
    private val _sort = MutableStateFlow(SortType.CreatedAtDesc)
    private val _page = MutableStateFlow(1)
    private val _isSyncing = MutableStateFlow(false)
    private val _syncError = MutableStateFlow<String?>(null)
    private val _nameInput = MutableStateFlow("")
    private val _quantityInput = MutableStateFlow("1")

    init {
        viewModelScope.launch {
            repository.observeItems().collect { list -> _allItems.value = list }
        }
    }

    private data class CoreInputs(
        val allItems: List<ShoppingItem>,
        val filter: FilterType,
        val sort: SortType,
        val page: Int,
        val isSyncing: Boolean,
    )

    val uiState: StateFlow<ShoppingUiState> = combine(
        combine(
            _allItems,
            _filter,
            _sort,
            _page,
            _isSyncing,
        ) { items, filter, sort, page, syncing ->
            CoreInputs(items, filter, sort, page, syncing)
        },
        _syncError,
        _nameInput,
        _quantityInput,
    ) { core, err, name, qty ->
        val filtered = when (core.filter) {
            FilterType.All -> core.allItems
            FilterType.Bought -> core.allItems.filter { it.isBought }
            FilterType.NotBought -> core.allItems.filter { !it.isBought }
        }
        val sorted = when (core.sort) {
            SortType.NameAsc -> filtered.sortedBy { it.name.lowercase() }
            SortType.CreatedAtDesc -> filtered.sortedByDescending { it.createdAt }
            SortType.BoughtFirst -> filtered.sortedWith(
                compareByDescending<ShoppingItem> { it.isBought }
                    .thenByDescending { it.createdAt },
            )
        }
        val limit = core.page * ShoppingUiState.PAGE_SIZE
        val displayed = sorted.take(limit)
        ShoppingUiState(
            displayedItems = displayed,
            filter = core.filter,
            sort = core.sort,
            page = core.page,
            hasMore = sorted.size > limit,
            isSyncing = core.isSyncing,
            syncError = err,
            nameInput = name,
            quantityInput = qty,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShoppingUiState(),
    )

    fun onNameChange(value: String) {
        _nameInput.value = value
    }

    fun onQuantityChange(value: String) {
        _quantityInput.value = value.filter { it.isDigit() }.ifEmpty { "" }
    }

    fun addItem() {
        val name = _nameInput.value.trim()
        if (name.isEmpty()) return
        val qty = _quantityInput.value.toIntOrNull()?.coerceAtLeast(1) ?: 1
        viewModelScope.launch {
            repository.upsertLocal(
                ShoppingItem(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    quantity = qty,
                    isBought = false,
                    createdAt = System.currentTimeMillis(),
                ),
            )
            _nameInput.value = ""
            _quantityInput.value = "1"
        }
    }

    fun toggleBought(id: String, bought: Boolean) {
        viewModelScope.launch {
            repository.toggleBought(id, bought)
        }
    }

    fun setFilter(filter: FilterType) {
        _filter.value = filter
        _page.value = 1
    }

    fun setSort(sort: SortType) {
        _sort.value = sort
        _page.value = 1
    }

    fun loadMore() {
        val state = uiState.value
        if (state.hasMore) {
            _page.value = state.page + 1
        }
    }

    fun sync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null
            repository.syncFromRemote().onFailure { e ->
                _syncError.value = e.message ?: e.toString()
            }
            _isSyncing.value = false
        }
    }

    fun clearSyncError() {
        _syncError.value = null
    }

    companion object {
        fun factory(repository: ShoppingRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
                        return ShoppingViewModel(repository) as T
                    }
                    error("Unknown ViewModel class")
                }
            }
    }
}
