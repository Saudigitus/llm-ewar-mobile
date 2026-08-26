package org.saudigitus.climasaude.domain.ai

import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.GuidanceAction

class UnavailableLocalModel : LocalModel {
    override suspend fun selectActions(alert: Alert): List<GuidanceAction>? = null
}
