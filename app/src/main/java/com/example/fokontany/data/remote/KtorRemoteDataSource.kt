package com.example.fokontany.data.remote

class KtorRemoteDataSource(
    private val apiService: FokontanyApiService
) : RemoteDataSource {

    override suspend fun synchroniser(): Result<Unit> {
        return try {
            apiService.getFoyers()
            apiService.getProgrammes()
            apiService.getDistributions()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
