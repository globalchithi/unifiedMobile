package com.vaxcare.unifiedhub.feature.pinin.ui

import com.vaxcare.unifiedhub.core.data.repository.UserRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.User
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class PinInViewModelTest : BaseViewModelTest<PinInState, PinInEvent, PinInIntent>() {
    override lateinit var viewModel: PinInViewModel

    private val userRepository: UserRepository = mockk(relaxUnitFun = true)
    private val analyticsRepository: AnalyticsRepository = mockk(relaxUnitFun = true)
    private val dispatcherProvider = TestDispatcherProvider()
    private val userSessionPreferenceDataSource: UserSessionPreferenceDataSource = mockk(relaxUnitFun = true) {
        coEvery { userRepository.getUser("1234") } returns User(
            firstName = "Test",
            lastName = "User",
            pin = "1234",
            userId = 1,
            userName = "test.user"
        )
    }

    @Before
    fun setUp() {
        viewModel = PinInViewModel(
            userRepository = userRepository,
            dispatcherProvider = dispatcherProvider,
            userSessionPreferenceDataSource = userSessionPreferenceDataSource,
            analyticsRepository = analyticsRepository
        )
    }

    @Test
    fun `CloseScreen should fire NavigateBack event`() {
        runTest {
            whenEvent(
                actions = { viewModel.handleIntent(PinInIntent.CloseScreen) },
                assertions = {
                    Assert.assertEquals(
                        PinInEvent.NavigateBack,
                        awaitItem()
                    )
                }
            )
        }
    }

    @Test
    fun `DigitClicked should add to PIN value`() {
        runTest {
            whenState {
                setInitialTestState(pinValue = "", invalidPin = false, isKeyPadEnabled = true)

                enterPIN("1")
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "1",
                        invalidPin = false,
                        isKeypadEnabled = true
                    )
                )
            }
        }
    }

    @Test
    fun `DigitClicked should add to PIN value and disable keypad if current value has a length of 3`() {
        runTest {
            whenState {
                setInitialTestState(pinValue = "123", invalidPin = false, isKeyPadEnabled = true)

                enterPIN("4")
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "1234",
                        invalidPin = false,
                        isKeypadEnabled = false
                    )
                )
            }
        }
    }

    @Test
    fun `DigitClicked should not add to PIN value if current value has a length of 4`() {
        runTest {
            whenState {
                setInitialTestState(pinValue = "123", invalidPin = false, isKeyPadEnabled = true)

                enterPIN("4")
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "1234",
                        invalidPin = false,
                        isKeypadEnabled = false
                    )
                )

                enterPIN("5")
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "1234",
                        invalidPin = false,
                        isKeypadEnabled = false
                    )
                )
            }
        }
    }

    @Test
    fun `DeleteDigit should remove last char from PIN value`() {
        runTest {
            whenState {
                setInitialTestState(pinValue = "1234", invalidPin = false, isKeyPadEnabled = false)

                viewModel.handleIntent(PinInIntent.DeleteDigit)
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "123",
                        invalidPin = false,
                        isKeypadEnabled = true
                    )
                )
            }
        }
    }

    @Test
    fun `ClearPin should clear the PIN value`() {
        runTest {
            whenState {
                setInitialTestState(pinValue = "1234", invalidPin = false, isKeyPadEnabled = false)

                viewModel.handleIntent(PinInIntent.ClearPin)
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "",
                        invalidPin = false,
                        isKeypadEnabled = true
                    )
                )
            }
        }
    }

    @Test
    fun `PIN In should fail`() {
        runTest {
            viewModel.start()
            advanceUntilIdle()

            whenState {
                setInitialTestState(pinValue = "9876", invalidPin = false, isKeyPadEnabled = false)
                viewModel.handleIntent(PinInIntent.AttemptPinIn)
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "",
                        invalidPin = true,
                        isKeypadEnabled = true
                    )
                )
            }
        }
    }

    @Test
    fun `Invalid PIN state should clear when new digit is entered`() {
        runTest {
            viewModel.start()
            advanceUntilIdle()

            whenState {
                setInitialTestState(pinValue = "9876", invalidPin = false, isKeyPadEnabled = false)
                viewModel.handleIntent(PinInIntent.AttemptPinIn)
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "",
                        invalidPin = true,
                        isKeypadEnabled = true
                    )
                )

                enterPIN("1")
                advanceUntilIdle()

                thenStateShouldBe(
                    PinInState(
                        pinValue = "1",
                        invalidPin = false,
                        isKeypadEnabled = true
                    )
                )
            }
        }
    }

    @Test
    fun `PIN In should succeed`() {
        runTest {
            viewModel.start()
            advanceUntilIdle()

            whenEvent(
                actions = {
                    setInitialTestState(pinValue = "1234", invalidPin = false, isKeyPadEnabled = false)
                    viewModel.handleIntent(PinInIntent.AttemptPinIn)
                    advanceUntilIdle()
                },
                assertions = {
                    Assert.assertEquals(
                        PinInEvent.OnSuccess,
                        awaitItem()
                    )
                }
            )
        }
    }

    private fun enterPIN(pin: String) {
        pin.forEach { digit ->
            viewModel.handleIntent(PinInIntent.DigitClicked(digit))
        }
    }

    private fun TestScope.setInitialTestState(
        pinValue: String,
        invalidPin: Boolean,
        isKeyPadEnabled: Boolean
    ) {
        viewModel.setState {
            copy(
                pinValue = pinValue,
                invalidPin = invalidPin,
                isKeypadEnabled = isKeyPadEnabled
            )
        }
        advanceUntilIdle()
        thenStateShouldBe(
            PinInState(
                pinValue = pinValue,
                invalidPin = invalidPin,
                isKeypadEnabled = isKeyPadEnabled
            )
        )
    }
}
