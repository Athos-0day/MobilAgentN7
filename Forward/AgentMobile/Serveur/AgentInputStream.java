package Serveur;
import java.io.*;

/**
 * AgentInputStream est une extension spécialisée d'ObjectInputStream.
 * Elle est conçue pour résoudre le problème de désérialisation de classes 
 * dynamiques reçues via le réseau.
 */
public class AgentInputStream extends ObjectInputStream {
    // Référence vers notre ClassLoader personnalisé qui détient le bytecode
    private Loader loader;

    /**
     * Constructeur injectant le flux d'entrée et le chargeur de classe.
     */
    public AgentInputStream(InputStream in, Loader loader) throws IOException {
        super(in); // Initialise la mécanique standard de désérialisation
        this.loader = loader;
    }

    /**
     *Cette méthode est appelée par Java chaque fois qu'il 
     * rencontre un objet dans le flux et doit identifier sa classe.
     */
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            // On demande explicitement à notre Loader de trouver la classe.
            // Si l'agent a envoyé son bytecode juste avant, le Loader le trouvera.
            return loader.loadClass(desc.getName());
        } catch (ClassNotFoundException e) {
            // on laisse le mécanisme par défaut de Java prendre le relais.
            return super.resolveClass(desc);
        }
    }
}