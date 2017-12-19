package com.tencent.henryye.myapplication;

import android.view.Choreographer;

import java.util.concurrent.TimeUnit;

/**
 * Copyright (C) 2017 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum Metronome implements Choreographer.FrameCallback {
    INSTANCE;

    private Choreographer choreographer;

    private long frameStartTime = 0;
    private int framesRendered = 0;

    private double currentFps = 0;
    private int interval = 500;

    Metronome() {
        choreographer = Choreographer.getInstance();
    }

    public void start() {
        choreographer.postFrameCallback(this);
    }

    public void stop() {
        frameStartTime = 0;
        framesRendered = 0;
        choreographer.removeFrameCallback(this);
    }

    @Override public void doFrame(long frameTimeNanos) {
        long currentTimeMillis = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos);

        if (frameStartTime > 0) {
            // take the span in milliseconds
            final long timeSpan = currentTimeMillis - frameStartTime;
            framesRendered++;

            if (timeSpan > interval) {
                currentFps = framesRendered * 1000 / (double) timeSpan;

                frameStartTime = currentTimeMillis;
                framesRendered = 0;
            }
        } else {
            frameStartTime = currentTimeMillis;
        }

        choreographer.postFrameCallback(this);
    }

    public double getCurrentFps() {
        return currentFps;
    }
}
