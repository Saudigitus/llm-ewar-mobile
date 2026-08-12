package org.saudigitus.climasaude.utils.text

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.RiskLevel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal fun yesNo(value: Boolean) = if (value) "Sim" else "Não"

@OptIn(ExperimentalTime::class)
internal fun shortDate(instant: String): String {
    val localDate = runCatching {
        Instant.parse(instant).toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    }
        .getOrDefault(instant.take(10))
    val parts = localDate.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else localDate
}

/** "12/09 – 19/09/2026", or both full dates when the years differ. */
internal fun dateRange(start: String, end: String): String {
    val from = shortDate(start)
    val to = shortDate(end)
    return if (from.length == 10 && to.length == 10 && from.takeLast(4) == to.takeLast(4))
        "${from.take(5)} – $to" else "$from – $to"
}

fun riskLabel(level: RiskLevel): String = when (level) {
    RiskLevel.GREEN -> UiText.low
    RiskLevel.YELLOW -> UiText.medium
    RiskLevel.RED -> UiText.high
}

fun languageLabel(language: AppLanguage): String = when (language) {
    AppLanguage.PORTUGUESE -> UiText.portuguese
    AppLanguage.XICHANGANA -> UiText.xichangana
    AppLanguage.EMAKHUWA -> UiText.emakhuwa
}
