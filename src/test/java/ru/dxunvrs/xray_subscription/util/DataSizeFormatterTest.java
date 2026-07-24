package ru.dxunvrs.xray_subscription.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DataSizeFormatterTest {
    private final DataSizeFormatter dataSizeFormatter = new DataSizeFormatter();

    @ParameterizedTest(name = "Для {0} байт ожидаем строку: \"{1}\"")
    @CsvSource({
            "0, 0 B",
            "-100, 0 B",
            "512, 512.00 B",
            "1023, 1023.00 B",
            "1024, 1.00 KiB",
            "1536, 1.50 KiB",                  // 1.5 KiB
            "1048576, 1.00 MiB",               // 1024 * 1024
            "104857600, 100.00 MiB",           // 100 MiB
            "1073741824, 1.00 GiB",            // 1 GiB
            "10737418240, 10.00 GiB",          // 10 GiB
            "1181116006400, 1.07 TiB"          // ~1.07 TiB
    })
    @DisplayName("Проверка форматирования различных значений байтов")
    void shouldFormatBytesCorrectly(long inputBytes, String expectedFormat) {
        String result = dataSizeFormatter.formatBytes(inputBytes);

        assertThat(result).isEqualTo(expectedFormat);
    }
}