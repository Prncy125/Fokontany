package com.example.fokontany.data.remote

class FakeRemoteDataSource : RemoteDataSource {

    var reseauDisponible: Boolean = true

    fun simulerReseauDisponible(disponible: Boolean) {
        reseauDisponible = disponible
    }

    override suspend fun synchroniser(): Result<Unit> {
        return if (reseauDisponible) {
            Result.success(Unit)
        } else {
            Result.failure(
                Exception("Réseau indisponible")
            )
        }
    }
}