package org.saudigitus.climasaude.data.mapper

import org.saudigitus.climasaude.data.local.entity.AreaEntity
import org.saudigitus.climasaude.data.local.entity.ProfileEntity
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.CatchmentArea
import org.saudigitus.climasaude.domain.model.UserProfile

internal fun AreaEntity.toDomain() = CatchmentArea(id, name)

internal fun ProfileEntity.toDomain(areas: List<AreaEntity>) =
    UserProfile(
        id,
        username,
        name,
        areas.map { it.toDomain() },
        demo,
        AppLanguage.entries.firstOrNull { it.name == language } ?: AppLanguage.PORTUGUESE
    )
