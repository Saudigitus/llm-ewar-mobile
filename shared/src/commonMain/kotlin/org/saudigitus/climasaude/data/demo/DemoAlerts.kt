package org.saudigitus.climasaude.data.demo

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.saudigitus.climasaude.data.local.entity.AlertEntity
import org.saudigitus.climasaude.domain.model.CatchmentArea
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object DemoAlerts {
    @OptIn(ExperimentalTime::class)
    fun forArea(area: CatchmentArea): List<AlertEntity> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val updated = Clock.System.now().toString()
        fun item(level: String, offset: Int, duration: Int, probability: Double) = AlertEntity(
            id = "demo-" + level.lowercase() + "-" + area.id,
            areaId = area.id,
            areaName = area.name,
            disease = "MALARIA",
            level = level,
            probability = probability,
            startsAt = today.plus(offset, DateTimeUnit.DAY).toString(),
            endsAt = today.plus(offset + duration, DateTimeUnit.DAY).toString(),
            rainfallNote = when (level) {
                "RED" -> "Chuva intensa prevista. Reforçar visitas às famílias com crianças menores de 5 anos."
                "YELLOW" -> "Acompanhar a evolução da chuva e verificar materiais para as próximas visitas."
                else -> "Manter a vigilância regular na comunidade."
            },
            updatedAt = updated,
            demo = true
        )
        return listOf(
            item("RED", -4, 8, 0.82),
            item("YELLOW", 5, 6, 0.57),
            item("GREEN", 9, 8, 0.18)
        )
    }
}
