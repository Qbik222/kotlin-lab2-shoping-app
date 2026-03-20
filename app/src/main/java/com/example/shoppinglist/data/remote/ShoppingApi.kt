package com.example.shoppinglist.data.remote

import com.example.shoppinglist.data.remote.dto.ShoppingItemDto
import retrofit2.http.GET

interface ShoppingApi {
    @GET("api/items")
    suspend fun getItems(): List<ShoppingItemDto>
}
