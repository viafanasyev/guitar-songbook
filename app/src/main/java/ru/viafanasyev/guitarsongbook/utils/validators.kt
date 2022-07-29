package ru.viafanasyev.guitarsongbook.utils

fun <T> validateInput(
    input: T,
    isValid: (T) -> Boolean,
    onError: () -> Unit,
    onNoError: () -> Unit
): T? {
    return if (isValid(input)) {
        onNoError()
        input
    } else {
        onError()
        null
    }
}