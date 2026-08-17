package org.saudigitus.climasaude.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.saudigitus.climasaude.data.remote.dto.Dhis2User
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.error.AppFailure
import org.saudigitus.climasaude.domain.model.Credentials

/** DHIS2 instance used to authenticate the APE and read their organisation units. */
class Dhis2Api(private val client: HttpClient, private val baseUrl: String) {
    suspend fun authenticate(credentials: Credentials): Dhis2User {
        if (!baseUrl.startsWith("https://")) throw AppFailure(AppError.HTTPS_REQUIRED)
        val response = client.get("${baseUrl.trimEnd('/')}/api/me") {
            basicAuth(credentials.username, credentials.password)
            url.parameters.append(
                "fields",
                "id,username,displayName,organisationUnits[id,displayName]"
            )
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw AppFailure(AppError.INVALID_LOGIN)
        }
        if (response.status != HttpStatusCode.OK) throw AppFailure(AppError.CONNECTION)
        return response.body()
    }
}
