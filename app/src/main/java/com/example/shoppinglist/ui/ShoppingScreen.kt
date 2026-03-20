package com.example.shoppinglist.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shoppinglist.FilterType
import com.example.shoppinglist.ShoppingUiState
import com.example.shoppinglist.ShoppingViewModel
import com.example.shoppinglist.SortType
import com.example.shoppinglist.ui.components.ShoppingItemRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShoppingScreen(viewModel: ShoppingViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.syncError) {
        val err = state.syncError ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(err)
        viewModel.clearSyncError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список покупок") },
                actions = {
                    IconButton(
                        onClick = { viewModel.sync() },
                        enabled = !state.isSyncing,
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Синхронізувати")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            if (state.isSyncing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            RowInputs(
                name = state.nameInput,
                quantity = state.quantityInput,
                onNameChange = viewModel::onNameChange,
                onQuantityChange = viewModel::onQuantityChange,
                onAdd = { viewModel.addItem() },
            )

            Spacer(modifier = Modifier.height(12.dp))

            FilterRow(
                selected = state.filter,
                onSelect = viewModel::setFilter,
            )

            Spacer(modifier = Modifier.height(8.dp))

            SortRow(
                selected = state.sort,
                onSelect = viewModel::setSort,
            )

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedListBlock(
                state = state,
                onToggle = { id, bought -> viewModel.toggleBought(id, bought) },
                onLoadMore = { viewModel.loadMore() },
            )
        }
    }
}

@Composable
private fun RowInputs(
    name: String,
    quantity: String,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Назва товару") },
            singleLine = true,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = quantity,
                onValueChange = onQuantityChange,
                modifier = Modifier.weight(1f),
                label = { Text("Кількість") },
                singleLine = true,
            )
            Button(onClick = onAdd, modifier = Modifier.padding(top = 8.dp)) {
                Text("Додати")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterRow(
    selected: FilterType,
    onSelect: (FilterType) -> Unit,
) {
    Text("Фільтр", style = MaterialTheme.typography.labelLarge)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FilterChip(
            selected = selected == FilterType.All,
            onClick = { onSelect(FilterType.All) },
            label = { Text("Усі") },
        )
        FilterChip(
            selected = selected == FilterType.Bought,
            onClick = { onSelect(FilterType.Bought) },
            label = { Text("Куплені") },
        )
        FilterChip(
            selected = selected == FilterType.NotBought,
            onClick = { onSelect(FilterType.NotBought) },
            label = { Text("Не куплені") },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SortRow(
    selected: SortType,
    onSelect: (SortType) -> Unit,
) {
    Text("Сортування", style = MaterialTheme.typography.labelLarge)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FilterChip(
            selected = selected == SortType.NameAsc,
            onClick = { onSelect(SortType.NameAsc) },
            label = { Text("Назва A–Я") },
        )
        FilterChip(
            selected = selected == SortType.CreatedAtDesc,
            onClick = { onSelect(SortType.CreatedAtDesc) },
            label = { Text("Новіші") },
        )
        FilterChip(
            selected = selected == SortType.BoughtFirst,
            onClick = { onSelect(SortType.BoughtFirst) },
            label = { Text("Куплені спочатку") },
        )
    }
}

@Composable
private fun AnimatedListBlock(
    state: ShoppingUiState,
    onToggle: (String, Boolean) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listKey = "${state.filter}-${state.sort}"

    AnimatedContent(
        targetState = listKey,
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith
                fadeOut(animationSpec = tween(180))
        },
        label = "listAnimatedContent",
    ) { _ ->
        when {
            state.displayedItems.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Список порожній для обраного фільтра.")
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(
                        items = state.displayedItems,
                        key = { item -> item.id },
                    ) { item ->
                        ShoppingItemRow(
                            item = item,
                            onToggle = { checked -> onToggle(item.id, checked) },
                        )
                    }
                    item {
                        AnimatedVisibility(visible = state.hasMore) {
                            Button(
                                onClick = onLoadMore,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                            ) {
                                Text("Завантажити ще")
                            }
                        }
                    }
                }
            }
        }
    }
}
