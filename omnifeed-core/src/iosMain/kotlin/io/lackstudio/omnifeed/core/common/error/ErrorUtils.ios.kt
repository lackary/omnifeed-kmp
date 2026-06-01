package io.lackstudio.omnifeed.core.common.error

actual fun Throwable.getFriendlyMessage(): String {
    val msg = message ?: return "An unknown error occurred"
    // On iOS, native NSError descriptions are often wrapped in double quotes.
    // Example: Error Domain=FIRAuthErrorDomain Code=17007 "The email address is already in use by another account."
    val regex = Regex("\"([^\"]*)\"")
    val match = regex.find(msg)
    return match?.groupValues?.get(1) ?: msg
}
