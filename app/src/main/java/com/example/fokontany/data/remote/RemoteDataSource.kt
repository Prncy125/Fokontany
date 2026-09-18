package com.example.fokontany.data.remote

interface RemoteDataSource {

    suspend fun synchroniser(): Result<Unit>
}