package com.example.fokontany.data.sync

import com.example.fokontany.data.local.repository.DistributionAideRepository
import com.example.fokontany.data.local.repository.FoyerRepository
import com.example.fokontany.data.local.repository.ProgrammeAideRepository
import com.example.fokontany.data.remote.RemoteDataSource

class SyncManager(
    private val remoteDataSource: RemoteDataSource,
    private val foyerRepository: FoyerRepository,
    private val programmeAideRepository: ProgrammeAideRepository,
    private val distributionAideRepository: DistributionAideRepository
) {

    suspend fun synchroniser(): Result<Unit> {

        val resultat = remoteDataSource.synchroniser()

        if (resultat.isSuccess) {
            foyerRepository.synchroniserFoyersEtHabitants()
            programmeAideRepository.synchroniserProgrammes()
            distributionAideRepository.synchroniserDistributions()
        } else {
            foyerRepository.marquerSynchronisationEnErreur()
            programmeAideRepository.marquerSynchronisationEnErreur()
            distributionAideRepository.marquerSynchronisationEnErreur()
        }

        return resultat
    }
}