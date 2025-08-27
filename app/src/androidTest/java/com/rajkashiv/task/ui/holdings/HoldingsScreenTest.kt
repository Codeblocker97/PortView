package com.rajkashiv.task.ui.holdings

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rajkashiv.task.ui.theme.PortViewTheme // Assuming your theme name
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HoldingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun holdingsScreen_initialLoadingState_showsProgressIndicator() {
        // Given
        val initialState = HoldingsUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            PortViewTheme { // Apply your app's theme
                HoldingsScreen(
                    state = initialState,
                    onRetry = { },
                    onToggleExpand = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
    }

    @Test
    fun holdingsScreen_errorState_showsErrorMessage() {
        // Given
        val errorState = HoldingsUiState(error = "Failed to load", holdings = emptyList())

        // When
        composeTestRule.setContent {
            PortViewTheme {
                HoldingsScreen(
                    state = errorState,
                    onRetry = { },
                    onToggleExpand = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to load holdings. Failed to load").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pull down to retry.").assertIsDisplayed() // Part of the error message
    }

    @Test
    fun holdingsScreen_displaysHoldingsAndSummary_whenDataLoaded() {
        // Given
        val holdings = listOf(
            HoldingUiModel("SBI", 10, 500.0, 200.0, null),
            HoldingUiModel("TCS", 5, 3000.0, 500.0, "T1 Holding")
        )
        val summary = PortfolioSummaryUiModel(
            currentValue = 20000.0,
            totalInvestment = 19300.0,
            todaysPnl = 150.0,
            totalPnl = 700.0,
            totalPnlPercentage = 3.62
        )
        val dataLoadedState = HoldingsUiState(
            holdings = holdings,
            summary = summary,
            isLoading = false,
            error = null,
            expanded = false // Initially collapsed
        )

        // When
        composeTestRule.setContent {
            PortViewTheme {
                HoldingsScreen(
                    state = dataLoadedState,
                    onRetry = { },
                    onToggleExpand = { }
                )
            }
        }

        // Then
        // Check holdings
        composeTestRule.onNodeWithText("SBI").assertIsDisplayed()
        composeTestRule.onNodeWithText("NET QTY: 10").assertIsDisplayed()
        composeTestRule.onNodeWithText("LTP: ₹500.00").assertIsDisplayed()
        composeTestRule.onNodeWithText("P&L: ₹200.00").assertIsDisplayed()

        composeTestRule.onNodeWithText("TCS").assertIsDisplayed()
        composeTestRule.onNodeWithText("T1 Holding").assertIsDisplayed() // Check tag

        // Check summary (collapsed state)
        composeTestRule.onNodeWithText("Profit & Loss*").assertIsDisplayed()
        composeTestRule.onNodeWithText("₹700.00").assertIsDisplayed() // Total P&L value
        composeTestRule.onNodeWithText("(3.62%)").assertIsDisplayed()

        // Check that expanded details are not visible
        composeTestRule.onNodeWithText("Current value*").assertDoesNotExist()
    }

    @Test
    fun holdingsScreen_summaryExpandsAndCollapses_onToggle() {
        // Given
        var isExpanded = false
        val summary = PortfolioSummaryUiModel(
            currentValue = 20000.0,
            totalInvestment = 19300.0,
            todaysPnl = 150.0,
            totalPnl = 700.0,
            totalPnlPercentage = 3.62
        )
        val initialState = HoldingsUiState(
            summary = summary,
            expanded = isExpanded
        )

        composeTestRule.setContent {
            PortViewTheme {
                HoldingsScreen(
                    state = initialState.copy(expanded = isExpanded), // Use the mutable isExpanded
                    onRetry = { },
                    onToggleExpand = { isExpanded = !isExpanded } // Simulate ViewModel toggle
                )
            }
        }

        // Initially collapsed, details should not exist
        composeTestRule.onNodeWithText("Current value*").assertDoesNotExist()
        composeTestRule.onNodeWithText("Total investment*").assertDoesNotExist()
        composeTestRule.onNodeWithText("Today's Profit & Loss*").assertDoesNotExist()

        // When: Click to expand (Find the clickable Profit & Loss summary header)
        // You might need a test tag on the clickable Row in PortfolioSummaryCard
        composeTestRule.onNodeWithText("Profit & Loss*").performClick()
        // composeTestRule.onNodeWithTag("portfolioSummaryCardHeader").performClick() // If you add a testTag

        // Then: Details should be visible
        // Recomposition might take a moment, add waitForIdle if needed, though composeTestRule handles it mostly
        composeTestRule.waitForIdle() // Ensure UI updates
        composeTestRule.onNodeWithText("Current value*").assertIsDisplayed()
        composeTestRule.onNodeWithText("₹20000.00").assertIsDisplayed() // Check current value
        composeTestRule.onNodeWithText("Total investment*").assertIsDisplayed()
        composeTestRule.onNodeWithText("Today's Profit & Loss*").assertIsDisplayed()


        // When: Click to collapse
        composeTestRule.onNodeWithText("Profit & Loss*").performClick()
        // composeTestRule.onNodeWithTag("portfolioSummaryCardHeader").performClick()

        // Then: Details should be hidden again
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Current value*").assertDoesNotExist()
    }

    @Test
    fun retryButton_whenErrorAndDataExists_isDisplayedAndClickable() {
        // Given
        val holdings = listOf(HoldingUiModel("SBI", 10, 500.0, 200.0, null))
        var retryClicked = false
        val errorWithDataState = HoldingsUiState(
            holdings = holdings,
            error = "Network issue",
            isLoading = false
        )

        composeTestRule.setContent {
            PortViewTheme {
                HoldingsScreen(
                    state = errorWithDataState,
                    onRetry = { retryClicked = true },
                    onToggleExpand = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Retry: Network issue").assertIsDisplayed().performClick()
        Assert.assertTrue(retryClicked)
    }

}
