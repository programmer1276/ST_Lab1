package lab1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MathFunctionsTest {

    @ParameterizedTest
    @DisplayName("Проверка по табличным значениям (без использования java.lang.Math)")
    @CsvSource({
            "0.0, 0.0",
            "0.2, 0.1973955",
            "0.5, 0.4636476",
            "0.7, 0.6107259",
            "1.0, 0.7853981",
            "-0.5, -0.4636476",
            "-1.0, -0.7853981"
    })
    void testAtanWithTableValues(double x, double expected) {
        double actual = MathFunctions.atan(x, 10000);
        assertEquals(expected, actual, 0.0001, "Ошибка: значение для x=" + x + " не соответствует табличному");
    }

    @ParameterizedTest
    @DisplayName("Покрытие веток исключений (для 100% coverage)")
    @ValueSource(doubles = {1.1, -1.1, 5.0})
    void testIllegalArgumentException(double x) {
        assertThrows(IllegalArgumentException.class, () -> MathFunctions.atan(x, 10));
    }

    @ParameterizedTest
    @DisplayName("Покрытие ветки специальных чисел (NaN/Infinity)")
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY})
    void testArithmeticException(double x) {
        assertThrows(ArithmeticException.class, () -> MathFunctions.atan(x, 10));
    }

//    @ParameterizedTest
//    @DisplayName("Проверка поведения вне области сходимости (|x| > 1)")
//    @ValueSource(doubles = {1.0000001, -1.0000001, 100.0, -500.5})
//    void testOutsideConvergenceRange(double x) {
//        // Ряд Тейлора для arctg(x) расходится при |x| > 1.
//        // Метод должен стабильно выбрасывать исключение, чтобы не выдать неверный результат.
//        assertThrows(IllegalArgumentException.class, () -> MathFunctions.atan(x, 100),
//                "Ожидалось исключение для x=" + x + ", так как значение вне диапазона [-1, 1]");
//    }
}