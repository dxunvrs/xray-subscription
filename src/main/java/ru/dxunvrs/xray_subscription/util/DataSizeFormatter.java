package ru.dxunvrs.xray_subscription.util;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DataSizeFormatter {
    private final String[] UNITS = {
            "B", "KiB", "MiB", "GiB", "TiB", "PiB"
    };

    public String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";

        int digitGroup = (int) (Math.log10(bytes) / Math.log10(1024));
        digitGroup = Math.min(digitGroup, UNITS.length);

        double value = bytes / Math.pow(1024, digitGroup);

        return String.format(Locale.US, "%.2f %s", value, UNITS[digitGroup]);
    }
}
