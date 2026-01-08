package Client;
import Common.*;
import java.util.*;

public class ForwardAgent extends Agent {
    private int etape = 0; // État interne
    private String villeCible;
    
    // Résultats
    private List<String> restosTrouves;
    private Map<String, String> annuaire = new HashMap<>();

    // Itinéraire
    private String host;
    private int portA, portB, portClient;
    
    // Le code (Bytecode)
    private byte[] myByteCode;
    private long startTime;

    public ForwardAgent(String ville, String host, int pA, int pB, int pC, byte[] code) {
        this.villeCible = ville;
        this.host = host;
        this.portA = pA; this.portB = pB; this.portClient = pC;
        this.myByteCode = code;
        this.startTime = System.nanoTime();
    }

    @Override
    public byte[] getByteCode() { return myByteCode; }

    @Override
    public void execute() {
        etape++;
        try {
            DatabaseServiceRem db = (DatabaseServiceRem) localService;

            if (etape == 1) {
                // --- SUR SERVEUR A (Restaurants) ---
                System.out.println("Agent: Recherche restaurants à " + villeCible);
                restosTrouves = db.getRestaurantParVille(villeCible);
                System.out.println("Agent: " + restosTrouves.size() + " restaurants trouvés. Départ vers Serveur B.");
                this.move(host, portB); // Saut vers B

            } else if (etape == 2) {
                // --- SUR SERVEUR B (Téléphones) ---
                System.out.println("Agent: Recherche numéros.");
                if (restosTrouves != null) {
                    for (String r : restosTrouves) {
                        annuaire.put(r, db.getNumeroParRestaurant(r));
                    }
                }
                System.out.println("Agent: Numéros récupérés. Retour au client.");
                this.move(host, portClient); // Saut vers Client

            } else if (etape == 3) {
                // --- RETOUR CLIENT ---
                long duree = (System.nanoTime() - startTime) / 1000000;
                System.out.println("\n=== MISSION TERMINÉE (" + duree + " ms) ===");
                System.out.println("Ville : " + villeCible);
                annuaire.forEach((r, n) -> System.out.println(" - " + r + " : " + n));
                System.out.println("=========================================");
                System.exit(0);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}