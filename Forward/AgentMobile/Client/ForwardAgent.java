package Client;
import Common.*;
import java.util.*;

public class ForwardAgent extends Agent {
    private static final long serialVersionUID = 1L;
    private int etape = 0; 
    private String villeCible;
    
    // Résultats
    private List<String> restosTrouves;
    private Map<String, String> annuaire = new HashMap<>();

    // Itinéraire complet (3 machines)
    private String ipA, ipB, ipClient;
    private int portA, portB, portClient;
    
    private byte[] myByteCode;
    private long startTime;

    // Le constructeur accepte maintenant les 8 paramètres envoyés par le client
    public ForwardAgent(String ville, String ipA, int pA, String ipB, int pB, String ipClient, int pC, byte[] code) {
        this.villeCible = ville;
        this.ipA = ipA;
        this.portA = pA;
        this.ipB = ipB;
        this.portB = pB;
        this.ipClient = ipClient;
        this.portClient = pC;
        this.myByteCode = code;
        this.startTime = System.nanoTime();
    }

    @Override
    public byte[] getByteCode() { return myByteCode; }

    @Override
    public void execute() {
        etape++;
        try {
            // Le service est injecté par le serveur d'accueil
            DatabaseServiceRem db = (DatabaseServiceRem) localService;

            if (etape == 1) {
                // étape: SUR SERVEUR 
                System.out.println("Agent: Recherche restaurants à " + villeCible + " sur Serveur A");
                restosTrouves = db.getRestaurantParVille(villeCible);
                
                System.out.println("Agent: " + restosTrouves.size() + " trouvés. Migration vers Serveur B (" + ipB + ")");
                this.move(ipB, portB); // Saut vers l'IP spécifique de B

            } else if (etape == 2) {
                // étape 2 : SUR SERVEUR B
                System.out.println("Agent: Recherche numéros sur Serveur B");
                if (restosTrouves != null) {
                    for (String r : restosTrouves) {
                        annuaire.put(r, db.getNumeroParRestaurant(r));
                    }
                }
                System.out.println("Agent: Numéros récupérés. Retour vers Client (" + ipClient + ")");
                this.move(ipClient, portClient); // Retour vers l'IP du Client

            } else if (etape == 3) {
                //étape 3 : RETOUR SUR LE PC CLIENT
                long duree = (System.nanoTime() - startTime) / 1000000;
                System.out.println("\n" + "=".repeat(40));
                System.out.println("MISSION TERMINÉE EN " + duree + " ms");
                System.out.println("Ville : " + villeCible);
                System.out.println("-".repeat(40));
                annuaire.forEach((r, n) -> System.out.println(" > " + r + " : " + n));
                System.out.println("=".repeat(40));
                
                // On arrête proprement le programme client après le retour
                new Thread(() -> { 
                    try { Thread.sleep(1000); } catch(Exception e) {}
                    System.exit(0); 
                }).start();
            }
        } catch (Exception e) { 
            System.err.println("Erreur durant l'exécution de l'agent : " + e.getMessage());
            e.printStackTrace(); 
        }
    }
}