package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.room.Room
import com.example.shoppinglist.data.ShoppingRepository
import com.example.shoppinglist.data.local.AppDatabase
import com.example.shoppinglist.data.remote.MockShoppingInterceptor
import com.example.shoppinglist.data.remote.ShoppingApi
import com.example.shoppinglist.ui.ShoppingScreen
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "shopping.db",
        ).fallbackToDestructiveMigration().build()
    }

    private val repository by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(MockShoppingInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        ShoppingRepository(
            database.shoppingDao(),
            retrofit.create(ShoppingApi::class.java),
        )
    }

    private val viewModel: ShoppingViewModel by viewModels {
        ShoppingViewModel.factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                ShoppingScreen(viewModel = viewModel)
            }
        }
    }
}
