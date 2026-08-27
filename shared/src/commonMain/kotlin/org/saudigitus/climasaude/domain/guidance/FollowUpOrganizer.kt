package org.saudigitus.climasaude.domain.guidance

import org.saudigitus.climasaude.domain.model.Child
import org.saudigitus.climasaude.domain.model.ChildFollowUp
import org.saudigitus.climasaude.domain.model.Triage

fun organizeFollowUps(children: List<Child>, triages: List<Triage>): List<ChildFollowUp> {
    val visits = triages.groupBy { it.childId }
    return children.map { child ->
        ChildFollowUp(child, visits[child.id].orEmpty().sortedByDescending { it.recordedAt })
    }.sortedWith(compareByDescending<ChildFollowUp> { it.lastVisit ?: it.child.createdAt }
        .thenBy { it.child.name })
}
