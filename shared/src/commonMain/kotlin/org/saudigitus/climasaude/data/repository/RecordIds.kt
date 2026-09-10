package org.saudigitus.climasaude.data.repository

import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun newId(prefix: String): String =
    "$prefix-${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt(1_000_000)}"
