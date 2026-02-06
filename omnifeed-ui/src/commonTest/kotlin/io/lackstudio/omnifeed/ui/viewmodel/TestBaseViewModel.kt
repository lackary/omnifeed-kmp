package io.lackstudio.omnifeed.ui.viewmodel

import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.ui.state.AppUiState
import kotlinx.coroutines.flow.MutableStateFlow

// A helper class created for testing purposes to expose protected methods
class TestBaseViewModel : BaseViewModel() {

    // Expose handleUseCaseCall for testing
    fun <T> callHandleUseCaseCall(
        name: String = "",
        flow: MutableStateFlow<AppUiState<T>>,
        useCase: suspend () -> UseCaseResult<T>,
    ) {
        super.handleUseCaseCall(name = name, flow = flow, useCase = useCase)
    }

    // Expose getAppErrorMessage for testing
    fun getAppErrorMessageForTest(exception: Exception): String {
        return super.getAppErrorMessage(exception)
    }
}
