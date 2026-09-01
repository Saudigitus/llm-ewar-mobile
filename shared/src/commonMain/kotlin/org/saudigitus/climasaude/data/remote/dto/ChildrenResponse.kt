package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChildrenResponse(val children: List<ChildPayload> = emptyList())
