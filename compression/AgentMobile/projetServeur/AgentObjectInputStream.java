import java.io.*;
import java.io.ObjectStreamClass;
// cette classe permet de désérialiser des objets en utilisant un ClassLoader spécifique pour les classes d'agents
public class AgentObjectInputStream extends ObjectInputStream {

    private final ClassLoader loader;
// constructeur qui prend un InputStream et un ClassLoader
    public AgentObjectInputStream(InputStream in, ClassLoader loader)
            throws IOException {
        super(in);
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {

        String name = desc.getName();

        // ⚡ Si c'est une classe d'agent, charger via le ClassLoader dynamique
        if (name.equals("AgentCompression")) {
            return loader.loadClass(name);
        }

        // ⚡ Sinon (interface commune ou java.*), déléguer au parent
        return super.resolveClass(desc);
    }
}

