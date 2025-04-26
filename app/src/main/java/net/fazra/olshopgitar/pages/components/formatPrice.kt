package net.fazra.olshopgitar.pages.components

fun formatPrice(number: Number, separator: String = "."): String {
    val formattedString = number.toString()
    val integerPart = if (formattedString.contains(".")) formattedString.substringBefore(".") else formattedString
    val decimalPart = if (formattedString.contains(".")) formattedString.substringAfter(".") else ""

    val formattedInteger = integerPart.reversed().chunked(3).joinToString(separator).reversed()

    return if (decimalPart.isNotEmpty()) {
        "$formattedInteger,$decimalPart"
    } else {
        formattedInteger
    }
}