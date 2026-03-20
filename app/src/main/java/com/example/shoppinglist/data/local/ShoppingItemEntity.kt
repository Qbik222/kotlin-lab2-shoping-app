package com.example.shoppinglist.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_items",
    indices = [Index(value = ["isBought"])],
)
data class ShoppingItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val quantity: Int,
    val isBought: Boolean,
    val createdAt: Long,
)
