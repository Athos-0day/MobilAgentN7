import java.util.*;

public class AgentClassLoader extends ClassLoader {
    private final Map<String, byte[]> classes = new HashMap<>();

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
        // Sinon on laisse le parent (système) chercher
        throw new ClassNotFoundException(name);
    }
}
