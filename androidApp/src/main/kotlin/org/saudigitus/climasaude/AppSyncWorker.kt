package org.saudigitus.climasaude

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.context.GlobalContext
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.error.AppFailure
import org.saudigitus.climasaude.domain.sync.SyncCoordinator

class AppSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val coordinator = GlobalContext.get().get<SyncCoordinator>()
        return runCatching {
            coordinator.sync()
            Result.success()
        }.getOrElse { error ->
            if ((error as? AppFailure)?.code == AppError.SIGN_IN_AGAIN) Result.failure() else Result.retry()
        }
    }
}
