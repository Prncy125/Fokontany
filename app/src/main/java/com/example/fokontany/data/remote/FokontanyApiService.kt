package com.example.fokontany.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class FokontanyApiService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080"
    }

    suspend fun getFoyers(): List<FoyerDTO> {
        return client.get("$BASE_URL/api/foyers").body()
    }

    suspend fun getFoyerDetail(id: Long): FoyerDetailDTO {
        return client.get("$BASE_URL/api/foyers/$id").body()
    }

    suspend fun createFoyer(foyer: FoyerDTO): FoyerDTO {
        return client.post("$BASE_URL/api/foyers") {
            contentType(ContentType.Application.Json)
            setBody(foyer)
        }.body()
    }

    suspend fun updateFoyer(id: Long, foyer: FoyerDTO): FoyerDTO {
        return client.put("$BASE_URL/api/foyers/$id") {
            contentType(ContentType.Application.Json)
            setBody(foyer)
        }.body()
    }

    suspend fun deleteFoyer(id: Long) {
        client.delete("$BASE_URL/api/foyers/$id")
    }

    suspend fun createHabitant(habitant: HabitantDTO): HabitantDTO {
        return client.post("$BASE_URL/api/habitants") {
            contentType(ContentType.Application.Json)
            setBody(habitant)
        }.body()
    }

    suspend fun updateHabitant(id: Long, habitant: HabitantDTO): HabitantDTO {
        return client.put("$BASE_URL/api/habitants/$id") {
            contentType(ContentType.Application.Json)
            setBody(habitant)
        }.body()
    }

    suspend fun deleteHabitant(id: Long) {
        client.delete("$BASE_URL/api/habitants/$id")
    }

    suspend fun getProgrammes(): List<ProgrammeAideDTO> {
        return client.get("$BASE_URL/api/programmes").body()
    }

    suspend fun createProgramme(programme: ProgrammeAideDTO): ProgrammeAideDTO {
        return client.post("$BASE_URL/api/programmes") {
            contentType(ContentType.Application.Json)
            setBody(programme)
        }.body()
    }

    suspend fun updateProgramme(id: Long, programme: ProgrammeAideDTO): ProgrammeAideDTO {
        return client.put("$BASE_URL/api/programmes/$id") {
            contentType(ContentType.Application.Json)
            setBody(programme)
        }.body()
    }

    suspend fun deleteProgramme(id: Long) {
        client.delete("$BASE_URL/api/programmes/$id")
    }

    suspend fun getDistributions(): List<DistributionAideDTO> {
        return client.get("$BASE_URL/api/distributions").body()
    }

    suspend fun getDistributionsByFoyer(foyerId: Long): List<DistributionAideDTO> {
        return client.get("$BASE_URL/api/distributions/foyer/$foyerId").body()
    }

    suspend fun createDistribution(distribution: DistributionAideDTO): DistributionAideDTO {
        return client.post("$BASE_URL/api/distributions") {
            contentType(ContentType.Application.Json)
            setBody(distribution)
        }.body()
    }

    suspend fun updateDistribution(id: Long, distribution: DistributionAideDTO): DistributionAideDTO {
        return client.put("$BASE_URL/api/distributions/$id") {
            contentType(ContentType.Application.Json)
            setBody(distribution)
        }.body()
    }

    suspend fun deleteDistribution(id: Long) {
        client.delete("$BASE_URL/api/distributions/$id")
    }
}
