package io.lackstudio.omnifeed.core.domain.usecase

interface UseCase<in Input, out Output> {
    suspend operator fun invoke(input: Input): UseCaseResult<Output>
}
