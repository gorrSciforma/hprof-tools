package com.badoo.hprof.cruncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.badoo.hprof.cruncher.library.CruncherService;
import com.badoo.hprof.cruncher.library.HprofCatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CruncherService.ACTION_CRUNCHING_STARTED.equals(intent.getAction())) {
                mStatus.setText("Crunching");
                long inputSize = intent.getLongExtra(CruncherService.EXTRA_HPROF_SIZE, 0) / (1024 * 1024);
                mInput.setText(intent.getStringExtra(CruncherService.EXTRA_HPROF_FILE) + " (" + inputSize + "MB)");
                mOutput.setText(intent.getStringExtra(CruncherService.EXTRA_BMD_FILE));
            }
            else if (CruncherService.ACTION_CRUNCHING_FINISHED.equals(intent.getAction())) {
                final boolean success = intent.getBooleanExtra(CruncherService.EXTRA_SUCCESS, false);
                mStatus.setText("Finished (" + (success? "SUCCESS)" : "FAILED)"));
                long outputSize = intent.getLongExtra(CruncherService.EXTRA_BMD_SIZE, 0) / (1024 * 1024);
                mOutput.setText(intent.getStringExtra(CruncherService.EXTRA_BMD_FILE) + " (" + outputSize + "MB)");
            }
        }
    };

    private TextView mStatus;
    private TextView mInput;
    private TextView mOutput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize HprofCatcher so that any uncaught exception is intercepted
        HprofCatcher.init(this);

        setContentView(R.layout.activity_main);
        mStatus = (TextView) findViewById(R.id.status);
        mInput = (TextView) findViewById(R.id.input);
        mOutput = (TextView) findViewById(R.id.output);
        findViewById(R.id.fillMemory).setOnClickListener(this);
        findViewById(R.id.npe).setOnClickListener(this);

        // Register a receiver to listen for updates of the crunching status
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CruncherService.ACTION_CRUNCHING_STARTED);
        filter.addAction(CruncherService.ACTION_CRUNCHING_FINISHED);
        bm.registerReceiver(mStatusReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStatusReceiver);
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "InfiniteLoopStatement"})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fillMemory) {
            List<Bitmap> data = new ArrayList<>();
            while (true) {
                // Allocate bitmaps until we run out of memory
                data.add(generateBitmap());
            }
        }
        else if (v.getId() == R.id.npe) {
            throw new NullPointerException("Let me explain you... crashing!");
        }
    }

    private Bitmap generateBitmap() {
        Random rand = new Random();
        int[] pixels = new int[1024 * 1024];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = rand.nextInt();
        }
        return Bitmap.createBitmap(pixels, 1024, 1024, Bitmap.Config.ARGB_8888);
    }

}
