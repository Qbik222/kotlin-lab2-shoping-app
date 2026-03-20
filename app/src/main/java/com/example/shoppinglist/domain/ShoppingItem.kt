package com.example.shoppinglist.domain

data class ShoppingItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val isBought: Boolean,
    val createdAt: Long,
)
