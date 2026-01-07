import java.io.*;
import java.lang.reflect.*;

public class AgentObjectInputStream extends ObjectInputStream {

    private final ClassLoader loader;

    public AgentObjectInputStream(InputStream in, ClassLoader loader)
            throws IOException {
        super(in);
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {

        String name = desc.getName();
        try {
            return Class.forName(name, false, loader);
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }
}

