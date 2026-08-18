package org.saudigitus.climasaude.platform

import org.saudigitus.climasaude.domain.model.Credentials

interface SecureCredentials {
    fun save(credentials: Credentials)
    fun read(): Credentials?
    fun clear()
}
