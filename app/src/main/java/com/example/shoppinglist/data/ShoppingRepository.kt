package com.example.shoppinglist.data

import com.example.shoppinglist.data.local.ShoppingDao
import com.example.shoppinglist.data.remote.ShoppingApi
import com.example.shoppinglist.domain.ShoppingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShoppingRepository(
    private val dao: ShoppingDao,
    private val api: ShoppingApi,
) {

    fun observeItems(): Flow<List<ShoppingItem>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun upsertLocal(item: ShoppingItem) {
        dao.upsert(item.toEntity())
    }

    suspend fun toggleBought(id: String, bought: Boolean) {
        dao.updateBought(id, bought)
    }

    suspend fun deleteById(id: String) {
        dao.deleteById(id)
    }

    suspend fun syncFromRemote(): Result<Unit> = runCatching {
        val remote = api.getItems()
        dao.upsertAll(remote.map { it.toEntity() })
    }
}
