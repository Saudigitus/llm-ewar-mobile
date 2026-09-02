package org.saudigitus.climasaude.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import org.saudigitus.climasaude.data.remote.dto.AlertsResponse
import org.saudigitus.climasaude.data.remote.dto.ChildPayload
import org.saudigitus.climasaude.data.remote.dto.ChildrenResponse
import org.saudigitus.climasaude.data.remote.dto.TriagePayload
import org.saudigitus.climasaude.data.remote.dto.TriagesResponse
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.error.AppFailure
import org.saudigitus.climasaude.domain.model.Credentials

/** Dedicated Clima Saúde Community service for risk alerts and follow-up records. */
class ClimaSaudeApi(private val client: HttpClient, private val baseUrl: String?) {
    val configured: Boolean get() = !baseUrl.isNullOrBlank()

    suspend fun alerts(credentials: Credentials): AlertsResponse {
        val response = client.get("${endpoint()}/v1/alerts") {
            basicAuth(credentials.username, credentials.password)
        }
        if (response.status == HttpStatusCode.Unauthorized) throw AppFailure(AppError.SIGN_IN_AGAIN)
        if (response.status != HttpStatusCode.OK) throw AppFailure(AppError.CONNECTION)
        return response.body()
    }

    suspend fun putChild(credentials: Credentials, child: ChildPayload) {
        val response = client.put("${endpoint()}/v1/children/${child.id}") {
            basicAuth(credentials.username, credentials.password)
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(child)
        }
        checkWriteResponse(response.status)
    }

    suspend fun putTriage(credentials: Credentials, triage: TriagePayload) {
        val response = client.put("${endpoint()}/v1/triages/${triage.id}") {
            basicAuth(credentials.username, credentials.password)
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(triage)
        }
        checkWriteResponse(response.status)
    }

    suspend fun children(credentials: Credentials): ChildrenResponse {
        val response = client.get("${endpoint()}/v1/children") {
            basicAuth(credentials.username, credentials.password)
        }
        checkReadResponse(response.status)
        return response.body()
    }

    suspend fun triages(credentials: Credentials): TriagesResponse {
        val response = client.get("${endpoint()}/v1/triages") {
            basicAuth(credentials.username, credentials.password)
        }
        checkReadResponse(response.status)
        return response.body()
    }

    private fun endpoint(): String {
        val endpoint = baseUrl ?: throw AppFailure(AppError.ALERTS_NOT_CONFIGURED)
        if (!endpoint.startsWith("https://")) throw AppFailure(AppError.HTTPS_REQUIRED)
        return endpoint.trimEnd('/')
    }

    private fun checkReadResponse(status: HttpStatusCode) {
        if (status == HttpStatusCode.Unauthorized || status == HttpStatusCode.Forbidden) throw AppFailure(
            AppError.SIGN_IN_AGAIN
        )
        if (status != HttpStatusCode.OK) throw AppFailure(AppError.CONNECTION)
    }

    private fun checkWriteResponse(status: HttpStatusCode) {
        if (status == HttpStatusCode.Unauthorized || status == HttpStatusCode.Forbidden) throw AppFailure(
            AppError.SIGN_IN_AGAIN
        )
        if (status != HttpStatusCode.OK && status != HttpStatusCode.Created && status != HttpStatusCode.NoContent) {
            throw AppFailure(AppError.CONNECTION)
        }
    }
}
