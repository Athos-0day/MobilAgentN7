package Common;
import java.io.*;
import java.net.Socket;

/**
 * Classe de base abstraite définissant le comportement d'un agent mobile.
 * Un agent est capable de s'exécuter localement et de migrer vers un hôte distant
 * en transportant à la fois son état (données) et son comportement (bytecode).
 */
public abstract class Agent implements Serializable {
    // Identifiant de version pour la sérialisation (évite les conflits de versions de classe)
    private static final long serialVersionUID = 1L;

    /**
     * Référence vers le service local fourni par le serveur d'accueil.
     * transient : ne doit pas être sérialisé car le service est propre à chaque machine.
     */
    protected transient Object localService; 

    // Point d'entrée de la logique de l'agent sur une machine
    public abstract void execute();
    
    // Permet à l'agent de fournir ses propres octets (.class) pour la migration
    public abstract byte[] getByteCode(); 

    /**
     * Injecte le service local (base de données, annuaire, etc.) 
     * pour que l'agent puisse l'utiliser à son arrivée.
     */
    public void setService(Object service) {
        this.localService = service;
    }

    /**
     * Gère la migration de l'agent vers une destination spécifique.
     * Utilise un protocole séquentiel : Classe -> Bytecode -> État (Objet).
     */
    public void move(String destHost, int destPort) {
        System.out.println(">>> Agent : Initialisation de la migration vers " + destHost + ":" + destPort);
        
        try (Socket socket = new Socket(destHost, destPort)) {
            // Utilisation d'un DataOutputStream pour un contrôle précis des types envoyés
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Sérialisation de l'état de l'agent
            // On sérialise l'instance actuelle (this) dans un tampon d'octets en RAM
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            byte[] agentBytes = bos.toByteArray();

            // Transmission via le protocole réseau
            
            // Identification : On envoie le nom complet de la classe 
            dos.writeUTF(this.getClass().getName());
            
            // Comportement : On envoie le fichier binaire (.class)
            byte[] code = this.getByteCode();
            dos.writeInt(code.length); // Taille pour que le récepteur sache quand s'arrêter
            dos.write(code);

            // État : On envoie l'objet sérialisé contenant les variables de l'agent
            dos.writeInt(agentBytes.length);
            dos.write(agentBytes);
            
            dos.flush();
            System.out.println(">>> Agent : Migration réussie vers " + destHost);
            
        } catch (Exception e) {
            System.err.println("CRITICAL : Échec de la migration vers " + destHost + " : " + e.getMessage());
        }
    }
}