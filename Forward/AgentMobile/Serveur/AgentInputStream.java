import java.io.*;

public class AgentInputStream extends ObjectInputStream {
    private Loader loader;

    public AgentInputStream(InputStream in, Loader loader) throws IOException {
        super(in);
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        // On demande au loader de trouver la classe reçue par le réseau
        return loader.loadClass(desc.getName());
    }
}