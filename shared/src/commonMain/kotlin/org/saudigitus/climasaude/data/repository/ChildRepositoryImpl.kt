package org.saudigitus.climasaude.data.repository

import org.saudigitus.climasaude.data.local.dao.AreaDao
import org.saudigitus.climasaude.data.local.dao.ChildDao
import org.saudigitus.climasaude.data.local.dao.ProfileDao
import org.saudigitus.climasaude.data.local.entity.ChildEntity
import org.saudigitus.climasaude.domain.repository.ChildRepository
import org.saudigitus.climasaude.platform.AutoSyncScheduler
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ChildRepositoryImpl(
    private val profileDao: ProfileDao,
    private val areaDao: AreaDao,
    private val childDao: ChildDao,
    private val autoSyncScheduler: AutoSyncScheduler
) : ChildRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun addChild(
        name: String,
        ageYears: Int?,
        sex: String?,
        caregiver: String?,
        community: String?,
        areaId: String?
    ): String {
        val profile = profileDao.active() ?: error("Sem sessão ativa")
        require(name.isNotBlank() && name.length <= 80)
        require(ageYears == null || ageYears in 0..17)
        require(caregiver == null || caregiver.length <= 80)
        require(community == null || community.length <= 80)
        val assignedAreas = areaDao.forUser(profile.id)
        val selectedArea = areaId ?: assignedAreas.singleOrNull()?.id
        require(selectedArea != null && assignedAreas.any { it.id == selectedArea })
        val id = newId("child")
        childDao.insert(
            ChildEntity(
                id,
                profile.id,
                name.trim(),
                ageYears,
                Clock.System.now().toString(),
                profile.demo,
                sex?.takeIf { it in SEX_OPTIONS },
                caregiver?.trim(),
                community?.trim(),
                areaId = selectedArea
            )
        )
        if (!profile.demo) runCatching { autoSyncScheduler.requestSync() }
        return id
    }

    private companion object {
        val SEX_OPTIONS = listOf("Feminino", "Masculino", "Não informado")
    }
}
