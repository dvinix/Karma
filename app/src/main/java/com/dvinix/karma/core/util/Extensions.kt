package com.dvinix.karma.core.util

// Extension functions can go here
fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { it.uppercase() }
}
