package com.example.farooqi.notificationscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int JOB_ID = 0;

    private JobScheduler scheduler;
    private JobInfo info;

    private Switch mDeviceIdle;
    private Switch mDeviceCharging;
    private SeekBar mSeekbar;
    private Switch mPeriodicSwitch;

    public static boolean constraintSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceIdle = (Switch) findViewById(R.id.sw_idle);
        mDeviceCharging = (Switch) findViewById(R.id.sw_char);
        mSeekbar = (SeekBar) findViewById(R.id.sk_bar);
        mPeriodicSwitch = (Switch) findViewById(R.id.sw_periodic);

        final TextView label = (TextView) findViewById(R.id.lbl_sk_bar_label);
        final TextView progress = (TextView) findViewById(R.id.lbl_sk_bar_progress);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int currentValue, boolean b) {
                if (currentValue > 0) {
                    progress.setText(String.valueOf(currentValue) + " s");
                } else {
                    progress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPeriodicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    label.setText(R.string.periodic_interval);
                } else {
                    label.setText(R.string.override_deadline);
                }
            }
        });

    }

    public void scheduleJob(View view) {
        int seekBarInteger = mSeekbar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;

        RadioGroup networkOptions = (RadioGroup) findViewById(R.id.rag_network);
        int selectedNetworkID = networkOptions.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        switch (selectedNetworkID) {
            case R.id.rab_no_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;

            case R.id.rab_any_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;

            case R.id.rab_wifi_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobServices.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(mDeviceIdle.isChecked())
                .setRequiresCharging(mDeviceCharging.isChecked());
        if (mPeriodicSwitch.isChecked()) {
            if (seekBarSet) {
                builder.setPeriodic(seekBarInteger * 1000);
            } else {
                Toast.makeText(this, "Please set a Periodic Interval",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (seekBarSet) {
                builder.setOverrideDeadline(seekBarInteger * 1000);
            }
        }

        constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE)
                || mDeviceIdle.isChecked() || mDeviceCharging.isChecked() || seekBarSet;

        if (constraintSet) {
            info = builder.build();
            scheduler.schedule(info);
            Toast.makeText(this, "Job was Schedule", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Set at least on Constraint", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelJobs(View view) {
        if (scheduler != null) {
            scheduler.cancelAll();
            scheduler = null;
            Toast.makeText(this, "Jobs Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "Scheduler is null");
        }
    }

}
