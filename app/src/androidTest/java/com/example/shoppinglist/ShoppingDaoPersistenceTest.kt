package com.example.shoppinglist

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppinglist.data.local.AppDatabase
import com.example.shoppinglist.data.local.ShoppingItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Перевірка персистентності Room: запис лишається після close() і повторного відкриття файлу БД.
 */
@RunWith(AndroidJUnit4::class)
class ShoppingDaoPersistenceTest {

    @Test
    fun dataSurvivesCloseAndReopen() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val dbName = "dao_persist_test.db"
            context.deleteDatabase(dbName)

            val db1 = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                .fallbackToDestructiveMigration()
                .build()
            val dao1 = db1.shoppingDao()
            val entity = ShoppingItemEntity(
                id = "persist-1",
                name = "Хліб",
                quantity = 1,
                isBought = false,
                createdAt = 123L,
            )
            dao1.upsert(entity)
            db1.close()

            val db2 = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                .fallbackToDestructiveMigration()
                .build()
            val dao2 = db2.shoppingDao()
            val list = dao2.observeAll().first()
            assertEquals(1, list.size)
            assertEquals("Хліб", list[0].name)
            assertEquals("persist-1", list[0].id)
            db2.close()
            context.deleteDatabase(dbName)
        }
    }
}
