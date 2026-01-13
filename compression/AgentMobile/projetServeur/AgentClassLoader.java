import java.util.*;

public class AgentClassLoader extends ClassLoader {
    // Map pour stocker les classes chargées dynamiquement
    private final Map<String, byte[]> classes = new HashMap<>();
    // ajout d'une classe
    public void addClass(String name, byte[] bytes) {
        classes.put(name, bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classes.get(name);
        if (bytes != null) {
            // On définit la classe seulement si on a le bytecode
            return defineClass(name, bytes, 0, bytes.length);
        }
        throw new ClassNotFoundException(name);
    }
}
