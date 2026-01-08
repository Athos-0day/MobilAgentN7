import java.util.*;

public class ForwardAgent extends Agent {
    private String ville;
    private Map<String, String> resultats = new HashMap<>();

    public ForwardAgent(String ville) {
        this.ville = ville;
    }

    @Override
    public void execute() {
        try {
            // Ici, l'agent appelle le service EN MÉMOIRE (pas de réseau !)
            // C'est l'interface DatabaseServiceRem qui est utilisée.
            List<String> restos = ((DatabaseServiceRem)localService).getRestaurantParVille(ville);
            
            for (String r : restos) {
                String tel = ((DatabaseServiceRem)localService).getNumeroParRestaurant(r);
                resultats.put(r, tel);
            }
            
            System.out.println("Agent : J'ai trouvé " + resultats.size() + " restaurants.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}