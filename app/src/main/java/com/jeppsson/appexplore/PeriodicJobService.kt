package com.jeppsson.appexplore

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class PeriodicJobService : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        PackageScannerService.enqueueWork(this)
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }

    companion object {

        private const val JOB_ID_PERIODIC = 1000
        private const val PERIOD = (2 * 24 * 60 * 60 * 1000).toLong()

        internal fun enqueueWork(context: Context) {
            val builder = JobInfo.Builder(JOB_ID_PERIODIC,
                    ComponentName(context, PeriodicJobService::class.java))
            builder.setRequiresCharging(true)
                    .setPersisted(true)
                    .setPeriodic(PERIOD)

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(builder.build())
        }
    }
}
