package org.saudigitus.climasaude.platform

interface NudgeScheduler {
    fun schedule(id: String, areaName: String, startsAt: String, message: String)
    fun clear()
}
