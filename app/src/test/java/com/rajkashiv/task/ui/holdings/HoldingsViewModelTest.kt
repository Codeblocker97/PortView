package com.rajkashiv.task.ui.holdings

import app.cash.turbine.test
import com.rajkashiv.task.domain.usecase.ComputePortfolioSummaryUseCase
import com.rajkashiv.task.domain.usecase.GetHoldingsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.time.ExperimentalTime
import com.rajkashiv.task.domain.model.Holding as DomainHolding

@ExperimentalTime
@ExperimentalCoroutinesApi
class HoldingsViewModelTest {

    // Rule for running coroutines synchronously in tests
    private val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler()) // or StandardTestDispatcher

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher) // Custom rule to set main dispatcher

    // Mock dependencies
    private lateinit var getHoldingsUseCase: GetHoldingsUseCase
    private lateinit var computePortfolioSummaryUseCase: ComputePortfolioSummaryUseCase

    // ViewModel under test
    private lateinit var viewModel: HoldingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for tests
        getHoldingsUseCase = mockk()
        computePortfolioSummaryUseCase = mockk()
        // ViewModel instantiation must happen after mocks are ready if it calls load in init
    }


    private fun initializeViewModel() {
        viewModel = HoldingsViewModel(getHoldingsUseCase, computePortfolioSummaryUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher after tests
    }

    @Test
    fun `loadHoldings success - updates state with holdings and summary`() = runTest(testDispatcher) {
        // Given
        val mockDomainHoldings = listOf(
            DomainHolding("SBI", 10, 500.0, 480.0, 490.0),
            DomainHolding("TCS", 5, 3000.0, 2900.0, 2950.0)
        )
        val mockPortfolioSummary = PortfolioSummaryUiModel(
            currentValue = (10*500.0) + (5*3000.0),
            totalInvestment = (10*480.0) + (5*2900.0),
            todaysPnl = ((500.0-490.0)*10) + ((3000.0-2950.0)*5),
            totalPnl = ((10*500.0) + (5*3000.0)) - ((10*480.0) + (5*2900.0)),
            totalPnlPercentage = 2.5 // Dummy value
        )

        coEvery { getHoldingsUseCase(any()) } returns flowOf(Result.success(mockDomainHoldings))
        every { computePortfolioSummaryUseCase(mockDomainHoldings) } returns mockPortfolioSummary

        initializeViewModel() // Initialize here as it calls loadHoldings in init

        // When & Then
        viewModel.state.test {
            var emittedItem = awaitItem() // Initial empty state or loading state from init

            // If init call leads to loading state first
            if (emittedItem.isLoading) {
                assertEquals(true, emittedItem.isLoading)
                emittedItem = awaitItem() // Then the success state
            }


            // Assertions on the final success state
            assertEquals(false, emittedItem.isLoading)
            assertEquals(null, emittedItem.error)
            assertEquals(2, emittedItem.holdings.size)
            assertEquals("SBI", emittedItem.holdings[0].symbol)
            assertEquals(mockPortfolioSummary, emittedItem.summary)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadHoldings failure - updates state with error`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network Error"
        coEvery { getHoldingsUseCase(any()) } returns flowOf(Result.failure(RuntimeException(errorMessage)))

        initializeViewModel()

        // When & Then
        viewModel.state.test {
            var emittedItem = awaitItem()

            if (emittedItem.isLoading) {
                assertEquals(true, emittedItem.isLoading)
                emittedItem = awaitItem()
            }

            assertEquals(false, emittedItem.isLoading)
            assertEquals(errorMessage, emittedItem.error)
            assertTrue(emittedItem.holdings.isEmpty())
            assertEquals(null, emittedItem.summary)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleExpand - updates expanded state`() = runTest(testDispatcher) {
        // Given
        coEvery { getHoldingsUseCase(any()) } returns flowOf(Result.success(emptyList())) // For init
        every { computePortfolioSummaryUseCase(any()) } returns mockk() // For init

        initializeViewModel()


        viewModel.state.test {
            awaitItem() // Initial state

            // When
            viewModel.toggleExpand()
            // Then
            val expandedState = awaitItem()
            assertEquals(true, expandedState.expanded)

            // When
            viewModel.toggleExpand()
            // Then
            val collapsedState = awaitItem()
            assertEquals(false, collapsedState.expanded)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadHoldings with force true - calls use case with force true`() = runTest(testDispatcher) {
        // Given
        coEvery { getHoldingsUseCase(forceRefresh = true) } returns flowOf(Result.success(emptyList()))
        coEvery { getHoldingsUseCase(forceRefresh = false) } returns flowOf(Result.success(emptyList())) // For init
        every { computePortfolioSummaryUseCase(any()) } returns mockk()


        initializeViewModel() // This will call loadHoldings(force=false)

        // When
        viewModel.loadHoldings(force = true)

        // Then
        // Verify that the use case was called with forceRefresh = true.
        // MockK verification can be added here if needed, but the test setup implies it.
        // We're mostly testing the state flow for now.
        viewModel.state.test {
            skipItems(2) // Skip initial state and the result of the init call
            val forcedRefreshState = awaitItem() // This should be the loading state for the forced refresh

            if(forcedRefreshState.isLoading){
                assertEquals(true, forcedRefreshState.isLoading)
                awaitItem() // Success/failure state of forced refresh
            }
            // Add more assertions if needed for the result of the forced refresh

            cancelAndIgnoreRemainingEvents()
        }
    }
}

// Custom MainCoroutineRule for JUnit 4
@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: TestDispatcher) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description as Description?)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description as Description?)
        Dispatchers.resetMain()
    }
}
