package com.example.shoppinglist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.shoppinglist.ui.UiTestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Інструментовані UI-тести (Jetpack Compose).
 */
@RunWith(AndroidJUnit4::class)
class ShoppingListComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearAppDatabase() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase("shopping.db")
    }

    @Test
    fun addItem_displaysInList() {
        composeRule.onNodeWithTag(UiTestTags.INPUT_NAME).performTextInput("Автотест Молоко")
        composeRule.onNodeWithTag(UiTestTags.BTN_ADD).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Автотест Молоко", substring = true).assertIsDisplayed()
    }

    @Test
    fun deleteItem_removesRow() {
        val name = "TestDeleteItem"
        composeRule.onNodeWithTag(UiTestTags.INPUT_NAME).performTextInput(name)
        composeRule.onNodeWithTag(UiTestTags.BTN_ADD).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(name, substring = true).assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Видалити $name").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(name, substring = true).assertDoesNotExist()
    }
}
