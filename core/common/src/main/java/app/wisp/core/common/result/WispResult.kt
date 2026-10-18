package app.wisp.core.common.result

sealed interface WispResult<out T> {
    data class Success<T>(val data: T) : WispResult<T>
    data class Error(val throwable: Throwable) : WispResult<Nothing>
    data object Loading : WispResult<Nothing>
}
