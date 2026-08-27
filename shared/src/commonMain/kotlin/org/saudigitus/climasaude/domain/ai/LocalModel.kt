package org.saudigitus.climasaude.domain.ai

import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.GuidanceAction

/** On-device model that ranks the protocol actions allowed for an alert. */
interface LocalModel {
    suspend fun selectActions(alert: Alert): List<GuidanceAction>?
}
