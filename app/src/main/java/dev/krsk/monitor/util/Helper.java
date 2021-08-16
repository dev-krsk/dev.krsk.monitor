package dev.krsk.monitor.util;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.source.BehindLiveWindowException;

public class Helper {
    public static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
