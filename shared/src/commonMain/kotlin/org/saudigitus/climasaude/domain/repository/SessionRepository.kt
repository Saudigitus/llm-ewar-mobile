package org.saudigitus.climasaude.domain.repository

import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.CatchmentArea
import org.saudigitus.climasaude.domain.model.UserProfile

/** The signed-in APE account: DHIS2 sign-in, the local presentation account and sign-out. */
interface SessionRepository {
    /** The active profile, or null when nobody is signed in. */
    val profile: Flow<UserProfile?>

    /** Catchment areas assigned to the active profile. */
    val areas: Flow<List<CatchmentArea>>

    suspend fun currentProfile(): UserProfile?

    /**
     * Validates the credentials against DHIS2 and activates that account.
     * Returns false when sign-in succeeded but the first alert refresh did not.
     */
    suspend fun login(username: String, password: String): Boolean

    /** Replaces local data with the offline presentation account. */
    suspend fun enterDemo()

    /** Saves the preferred local language on the active profile. */
    suspend fun setLanguage(language: AppLanguage)

    /** Clears credentials, notifications and every cached record. */
    suspend fun logout()
}
