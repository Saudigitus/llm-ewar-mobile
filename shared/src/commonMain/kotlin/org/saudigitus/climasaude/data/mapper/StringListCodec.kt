package org.saudigitus.climasaude.data.mapper

import kotlinx.serialization.json.Json

/** Room stores lists of strings as JSON text columns. */
internal fun encodeStringList(values: List<String>): String = Json.encodeToString(values)

internal fun decodeStringList(value: String?): List<String> =
    value?.let { runCatching { Json.decodeFromString<List<String>>(it) }.getOrNull() }.orEmpty()

internal inline fun <reified T : Enum<T>> enumValueOrNull(value: String): T? =
    enumValues<T>().firstOrNull { it.name == value }
