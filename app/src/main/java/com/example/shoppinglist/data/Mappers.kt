package com.example.shoppinglist.data

import com.example.shoppinglist.data.local.ShoppingItemEntity
import com.example.shoppinglist.domain.ShoppingItem

fun ShoppingItemEntity.toDomain(): ShoppingItem = ShoppingItem(
    id = id,
    name = name,
    quantity = quantity,
    isBought = isBought,
    createdAt = createdAt,
)

fun ShoppingItem.toEntity(): ShoppingItemEntity = ShoppingItemEntity(
    id = id,
    name = name,
    quantity = quantity,
    isBought = isBought,
    createdAt = createdAt,
)
