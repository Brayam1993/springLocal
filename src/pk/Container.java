package pk;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Container {

    private Map<String, Object> objects;

    public Container() {
        objects = new HashMap<>();
    }

    public void start() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        // Create beans
        // 1 obtener el paquete actual de la clase container

        Class<?> clazz = Class.forName("pk.Container");
        Package actualPackage = clazz.getPackage();
        System.out.println("actualPackage" + actualPackage);

        // 2 una vez obtenido el paquete obtener el listado de las clases que se estan en ese paquete
        List<Class<?>> namePackage = getClassesFromPackage(actualPackage.getName());
        List<Class<?>> arrobaList = new ArrayList<>();

        // 3 filtrar las clases que estan anotadas con @ bean
        System.out.println("namePackage" + namePackage);
        for (Class<?> name : namePackage) {
            if (name.isAnnotationPresent(Bean.class)) {
                arrobaList.add(name);
            }
        }

        // 4 por cada clase que esta anotada por @bean se tiene que generar una nueva instancia
        System.out.println("ArrobaList" + arrobaList);
        for (Class<?> nameInstance : arrobaList){
            Bean beanAnnotation = (Bean) nameInstance.getAnnotation(Bean.class);
                createBean(nameInstance,beanAnnotation.value());
        }
    }

    public Object getBean(String beanName) {
        // Returns a bean from objects
        // return null;

        Object bean = objects.get(beanName);
        if (bean == null) {
            System.out.println("Warning: Bean '" + beanName + "' is null.");
        }
        return bean;
    }

    private void createBean(Class clazz, String beanName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Creates bean if it doesn't exists getting a instance from a constructor

        // Validar si existe o no existe ese bean dentro de los objetos del contenedor de dependencias
        if (objects.containsKey(beanName)) {
            System.out.println("Bean '" + beanName + "' already exists. Skipping creation.");
            return; // El bean ya existe, no es necesario crear otro
        }

        // En caso de que no exista necesitamos crear un nuevo objeto
        System.out.println("Creating bean '" + beanName + "' for class: " + clazz.getName());

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            System.out.println("Warning: No constructors found for class: " + clazz.getName());
            return;
        }

        Constructor<?> constructor = constructors[0]; // Tomamos el primer constructor, puedes adaptarlo según tus necesidades
        System.out.println("Constructor parameters for class '" + clazz.getName() + "': " + constructor.getParameters());

        Object[] paramBeans = getParams(constructor.getParameters());
        Object beanInstance = constructor.newInstance(paramBeans);

        // Almacenar el nuevo objeto en los objetos del contenedor
        objects.put(beanName, beanInstance);

        System.out.println("Bean '" + beanName + "' created successfully.");
    }

    private Object[] getParams(Parameter[] params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Resolve beans params for constructors
        // return paramBeans;

        List<Object> paramBeans = new ArrayList<>();

        for (Parameter param : params) {
            if (param.isAnnotationPresent(Bean.class)) {
                Bean beanAnnotation = param.getAnnotation(Bean.class);
                paramBeans.add(getBean(beanAnnotation.value()));
            }
        }

        return paramBeans.toArray();
    }

    private static List<Class<?>> getClassesFromPackage(String packageName) {
        String path = packageName.replace(".", File.separator);
        List<Class<?>> classes = new ArrayList<>();
        String[] classPathEntries = System.getProperty("java.class.path").split(
                System.getProperty("path.separator")
        );

        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while((entry = is.getNextJarEntry()) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) && name.endsWith(".class")) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[\\|/]", ".");
                                classes.add(Class.forName(classPath));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);
                    for (File file : base.listFiles()) {
                        name = file.getName();
                        if (name.endsWith(".class")) {
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name));
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            }
        }

        return classes;
}
}