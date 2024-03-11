import pk.CalculatorExecutor;
import pk.Container;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        Container c = new Container();
        c.start();
        CalculatorExecutor calculatorExecutor = (CalculatorExecutor) c.getBean("CalculatorExecutor");
        System.out.println("Suma: " + calculatorExecutor.execute("suma", 5, 4));
    }
}
