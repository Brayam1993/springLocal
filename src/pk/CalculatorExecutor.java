package pk;

import pk.Bean;
import pk.Calculation;
import pk.Calculator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Bean("CalculatorExecutor")
public class CalculatorExecutor {

    private final Calculator calculator;

    public CalculatorExecutor(@Bean("Calculator") Calculator calculator) {
        this.calculator = calculator;
    }

    public double execute(String calculationName, double num1, double num2) {
        try {
            //Class calculator = Class.forName("com.example.dependency.injection.Calculator");
            Class<?> calculator = Class.forName("pk.Calculator");
            Method[] methods = calculator.getMethods();
            System.out.println("Methods :" + Arrays.toString(methods));
            for(Method method : methods) {
                Calculation calculationAnnotation = method.getAnnotation(Calculation.class);
                if(calculationAnnotation != null && calculationAnnotation.value().equals(calculationName)) {
                    return (double) method.invoke(calculationName, num1, num2);
                }
            }
            throw new RuntimeException("Calculation not supported");
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}