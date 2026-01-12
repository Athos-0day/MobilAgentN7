package Serveur;
import java.util.*;

/**
 * Le Loader est un ClassLoader personnalisé.
 * Son rôle est de permettre au JVM du serveur de "comprendre" et d'instancier 
 * une classe (l'Agent) dont il ne possède pas le fichier .class localement.
 */
public class Loader extends ClassLoader {
    
    // stocke le bytecode reçu du réseau
    // Clé : Nom de la classe (ex: "Client.ForwardAgent")
    // Valeur : Tableau d'octets du fichier .class
    private final Map<String, byte[]> classes = new HashMap<>();

    /**
     * Enregistre dynamiquement le bytecode d'une classe en mémoire.
     */
    public void addClass(String name, byte[] bytes) {
        classes.put(name, bytes);
    }

    /**
     * Elle est appelée par la JVM quand elle rencontre un nom de classe inconnu.
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // On vérifie si on a reçu le bytecode par le réseau
        byte[] bytes = classes.get(name);
        
        // Si on ne l'a pas, on délègue au ClassLoader standard (système)
        if (bytes == null) {
            return super.findClass(name);
        }
        
        // On transforme le tableau d'octets en une véritable "Classe" Java
        // C'est l'étape qui rend l'agent exécutable.
        return defineClass(name, bytes, 0, bytes.length);
    }
}