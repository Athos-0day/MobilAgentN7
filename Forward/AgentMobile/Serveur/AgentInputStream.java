package Serveur;
import java.io.*;

public class AgentInputStream extends ObjectInputStream {
    private Loader loader;

    public AgentInputStream(InputStream in, Loader loader) throws IOException {
        super(in);
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        // Force Java à utiliser notre Loader qui contient le bytecode reçu
        try {
            return loader.loadClass(desc.getName());
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }
}