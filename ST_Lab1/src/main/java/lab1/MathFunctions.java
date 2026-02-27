package lab1;

public class MathFunctions {

    /**
     * Вычисляет арктангенс через ряд Тейлора.
     * @param x значение (от -1 до 1)
     * @param iterations количество итераций (для точности)
     * @return вычисленное значение arctg(x)
     */
    public static double atan(double x, int iterations) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            throw new ArithmeticException("Invalid input: NaN or Infinite");
        }

        if (Math.abs(x) > 1) {
            throw new IllegalArgumentException("X must be in range [-1, 1] for this series");
        }

        double result = 0.0;
        for (int n = 0; n < iterations; n++) {
            // (-1)^n * x^(2n+1) / (2n+1)
            double term = Math.pow(-1, n) * Math.pow(x, 2 * n + 1) / (2 * n + 1);
            result += term;
        }
        return result;
    }
}