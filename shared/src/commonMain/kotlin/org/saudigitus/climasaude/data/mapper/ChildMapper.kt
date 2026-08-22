package org.saudigitus.climasaude.data.mapper

import org.saudigitus.climasaude.data.local.entity.ChildEntity
import org.saudigitus.climasaude.data.remote.dto.ChildPayload
import org.saudigitus.climasaude.domain.model.Child

internal fun ChildEntity.toDomain() =
    Child(id, name, ageYears, createdAt, demo, sex, caregiver, community, syncedAt, areaId)

internal fun ChildEntity.toPayload() =
    ChildPayload(id, userId, name, ageYears, createdAt, sex, caregiver, community, areaId)

internal fun ChildPayload.toEntity(syncedAt: String) = ChildEntity(
    id, userId, name, ageYears, createdAt, false,
    sex, caregiver, community, syncedAt, areaId
)
