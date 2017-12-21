package com.tencent.henryye.imgcompare.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.tencent.henryye.imgcompare.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by henryye on 2017/12/14.
 *
 * The util to monitor
 */
@SuppressLint("LongLogTag")
public enum StatsMonitor {
    INSTANCE;

    private static final String TAG = "MicroMsg.StatsMonitor";

    private static final long FIXED_RATE = 5; // 3ms 检查一次

    private WeakReference<Context> mCurrentContext = null;

    private boolean isRunning = false;

    private List<IRecord> mRecords = new ArrayList<>(3);

    private final Object handleLock = new Object();

    private Timer mTimer = null;

    public void setContext(Context context) {
        mCurrentContext = new WeakReference<>(context);
    }

    public void requestStartMonitor() {
        synchronized (handleLock) {
            if(isRunning) {
                Log.w(TAG, "hy: already running. stop last and run new");
                isRunning = false;
            }
            if(mTimer != null) {
                mTimer.cancel();
            }
            System.gc();

            mTimer = new Timer("Handle_Monitor_mem_cpu", true);

            mRecords.clear();

            mRecords.add(new MemoryRecord());
            mRecords.add(new CpuRecord());
            mRecords.add(new FpsRecord());

            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
//                    Log.v(TAG, "hy: called to calculate");
                    long ticksBefore = System.currentTimeMillis();
                    synchronized (handleLock) {
                        if(isRunning) {
                            for(IRecord recorder : mRecords) {
                                recorder.trigger();
                            }
                        } else {
                            Log.w(TAG, "hy: already stopped");
                            cancel();
                        }
                    }
//                    Log.v(TAG, "hy: frame check using " + (System.currentTimeMillis() - ticksBefore));
                }
            }, 0, FIXED_RATE);
            isRunning = true;
        }
    }

    public CharSequence dumpInfo() {
        synchronized (handleLock) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            if(mRecords != null && mRecords.size() != 0) {
                for(IRecord record : mRecords) {
                    record.filling();
                    Spannable spannable = new SpannableString(record.toString());
                    spannable.setSpan(new ForegroundColorSpan(record.getColor()), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(spannable).append("\n");
                }
            }
            return builder;
        }
    }

    public void requestStopMonitor() {
        synchronized (handleLock) {
            if(!isRunning) {
                return;
            }
            if(mTimer != null) {
                mTimer.cancel();
            }
            isRunning = false;
        }
    }

    private interface IRecord {
        void filling();
        void trigger();
        int getColor();
    }

    private class MemoryRecord implements IRecord{
        private double memoryUsedWhenInit = 0;

        public MemoryRecord() {
            memoryUsedWhenInit = Utils.getCurrentTotalMemoryInMB(mCurrentContext.get());
            Log.v(TAG, "hy: init mem to " + memoryUsedWhenInit);
        }

        private List<Double> totalMemRecord = new ArrayList<>(100);

        private double averUsed = 0;
        private int checkFrames = 0;
        private double currentMaxUsedMemory = 0;
        private double currentVarianceSum = 0;

        @Override
        public void filling() {
            checkFrames = totalMemRecord.size();
            if(checkFrames == 0) {
                Log.w(TAG, "hy: check frames 0!");
                return;
            }
            Double totalMem = Double.valueOf(0);
            for(Double item : totalMemRecord) {
                totalMem += item;
                currentMaxUsedMemory = Math.max(currentMaxUsedMemory, item);
            }
            Double averageTotalMem = totalMem / checkFrames;
            currentMaxUsedMemory = currentMaxUsedMemory - memoryUsedWhenInit;
            averUsed = averageTotalMem - memoryUsedWhenInit;
            // 计算标准差
            long sum = 0;
            for(Double item : totalMemRecord) {
                sum += Math.pow(item - averageTotalMem, 2);
            }
            currentVarianceSum = Math.sqrt(sum / checkFrames);
        }

        @Override
        public void trigger() {
            // 由于存在一定概率图片加载过程中申请了大量的内存，导致系统进行全面gc以满足需求，因此需要将最低值内存设置为初始内存。
            double currentMem = Utils.getCurrentTotalMemoryInMB(mCurrentContext.get());
            memoryUsedWhenInit = Math.min(currentMem, memoryUsedWhenInit);
            if(memoryUsedWhenInit == currentMem) {
                Log.v(TAG, "hy: updating init mem to " + memoryUsedWhenInit);
            }
            totalMemRecord.add(currentMem);
        }

        @Override
        public int getColor() {
            return Color.RED;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(getString(R.string.stats_monitor_mem_ave, averUsed))
                    .append(getString(R.string.stats_monitor_mem_max, currentMaxUsedMemory))
                    .append(getString(R.string.stats_monitor_mem_variance, currentVarianceSum)).toString();
        }
    }

    private String getString(int id, Object... format) {
        return mCurrentContext.get().getString(id, (Object[]) format);
    }

    private class CpuRecord implements IRecord {

        @Override
        public void filling() {

        }

        @Override
        public void trigger() {

        }

        @Override
        public int getColor() {
            return Color.BLUE;
        }

        @Override
        public String toString() {
            return "Undefined";
        }
    }

    private class FpsRecord implements IRecord {

        private List<Double> fpss = new ArrayList<>(100);
        private double averageFps = 0;
        private double minFps = Double.MAX_VALUE;

        @Override
        public void filling() {
            if(fpss == null) {
                return;
            }
            if(fpss != null && fpss.size() != 0) {
                double fpsSum = 0;
                for(double item : fpss) {
                    fpsSum += item;
                    minFps = Math.min(minFps, item);
                }
                averageFps = fpsSum / fpss.size();
            }
        }

        @Override
        public void trigger() {
            fpss.add(Metronome.INSTANCE.getCurrentFps());
        }

        @Override
        public int getColor() {
            return Color.GREEN;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(getString(R.string.stats_monitor_fps_min, minFps)).append("\n")
                    .append(getString(R.string.stats_monitor_fps_ave, averageFps)).toString();
        }
    }


}
