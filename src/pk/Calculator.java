package pk;

import pk.Bean;
import pk.Calculation;

@Bean("Calculator")
public class Calculator {
    @Calculation("suma")
    public static double sumar(double num1, double num2) {
        return num1 + num2;
    }
    @Calculation("resta")
    public static double restar(double num1, double num2) {
        return num1 - num2;
    }
    @Calculation("multiplicacion")
    public static double multiplicar(double num1, double num2) {
        return num1 * num2;
    }
    @Calculation("division")
    public static double dividir(double num1, double num2) {
        if (num2 == 0) {
            System.out.println("Error: No se puede dividir por cero");
            return Double.NaN;
        } else {
            return num1 / num2;
        }
    }
}
