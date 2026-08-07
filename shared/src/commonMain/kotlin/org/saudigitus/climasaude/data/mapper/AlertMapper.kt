package org.saudigitus.climasaude.data.mapper

import org.saudigitus.climasaude.data.local.entity.AlertEntity
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.Disease
import org.saudigitus.climasaude.domain.model.RiskLevel

internal fun AlertEntity.toDomain(): Alert? {
    val disease = enumValueOrNull<Disease>(this.disease) ?: return null
    val risk = enumValueOrNull<RiskLevel>(level) ?: return null
    return Alert(
        id,
        areaId,
        areaName,
        disease,
        risk,
        probability,
        startsAt,
        endsAt,
        rainfallNote,
        updatedAt,
        demo
    )
}
