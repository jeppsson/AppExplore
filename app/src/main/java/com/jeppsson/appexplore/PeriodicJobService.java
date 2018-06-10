package com.jeppsson.appexplore;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PeriodicJobService extends JobService {

    private static final int JOB_ID_PERIODIC = 1000;
    private static final long PERIOD = 2 * 24 * 60 * 60 * 1000;

    static void enqueueWork(Context context) {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID_PERIODIC,
                new ComponentName(context, PeriodicJobService.class));
        builder.setRequiresCharging(true)
                .setPersisted(true)
                .setPeriodic(PERIOD);

        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        PackageScannerService.enqueueWork(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
