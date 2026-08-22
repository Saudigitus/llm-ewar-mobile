package org.saudigitus.climasaude.data.mapper

import org.saudigitus.climasaude.data.local.entity.TriageEntity
import org.saudigitus.climasaude.data.local.entity.TriageTranslationEntity
import org.saudigitus.climasaude.data.remote.dto.TriagePayload
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.Triage
import org.saudigitus.climasaude.domain.model.TriageTranslation

internal fun TriageEntity.toDomain(translations: List<TriageTranslationEntity>): Triage {
    return Triage(
        id, childId, recordedAt, fever, dangerSigns, malariaTest, referred, notes, demo,
        recommendationTitle, recommendationBody, recommendationSource, syncedAt,
        decodeStringList(recommendationSteps), decodeStringList(recommendationAlertIds),
        translations.mapNotNull { item ->
            val language = AppLanguage.entries.firstOrNull { it.name == item.language }
                ?: return@mapNotNull null
            language to TriageTranslation(language, item.title, decodeStringList(item.steps))
        }.toMap()
    )
}

internal fun TriageEntity.toPayload() = TriagePayload(
    id, userId, childId, recordedAt, fever, dangerSigns,
    malariaTest, referred, notes, recommendationTitle, recommendationBody, recommendationSource,
    recommendationSteps?.let { decodeStringList(it) },
    recommendationAlertIds?.let { decodeStringList(it) }
)

internal fun TriagePayload.toEntity(syncedAt: String) = TriageEntity(
    id,
    userId,
    childId,
    recordedAt,
    fever,
    dangerSigns,
    malariaTest,
    referred,
    notes,
    false,
    recommendationTitle,
    recommendationBody,
    recommendationSource,
    syncedAt,
    recommendationSteps?.let { encodeStringList(it) },
    recommendationAlertIds?.let { encodeStringList(it) }
)
