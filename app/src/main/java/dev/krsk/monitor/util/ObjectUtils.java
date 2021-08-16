package dev.krsk.monitor.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ObjectUtils {
    public static <T> T defaultWhenNull(@Nullable T object, @NonNull T def) {
        return (object == null) ? def : object;
    }
}
