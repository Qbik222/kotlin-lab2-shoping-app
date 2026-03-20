package com.example.shoppinglist.data.remote.dto

import com.example.shoppinglist.data.local.ShoppingItemEntity

data class ShoppingItemDto(
    val id: String,
    val name: String,
    val quantity: Int,
    val isBought: Boolean,
    val createdAt: Long,
) {
    fun toEntity(): ShoppingItemEntity = ShoppingItemEntity(
        id = id,
        name = name,
        quantity = quantity,
        isBought = isBought,
        createdAt = createdAt,
    )
}
