import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.rmi.Naming;           

public class ClientRMI {
    public static void main(String[] args) {
        // Valeurs par défaut
        String ip = "localhost";
        String ville = null;
        String restaurantUnique = null;

        // Analyse des arguments de la ligne de commande
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--ip":
                    if (i + 1 < args.length) ip = args[++i];
                    break;
                case "--ville":
                    if (i + 1 < args.length) ville = args[++i];
                    break;
                case "--restaurant":
                    if (i + 1 < args.length) restaurantUnique = args[++i];
                    break;
            }
        }

        if (ville == null && restaurantUnique == null) {
            System.out.println("Usage: java ForwardClient --ip <IP> --ville <Ville> OU --restaurant <Nom>");
            return;
        }

        try {
            // Connexion au registre (On utilise le port 8081 celui du serveur donc)
            DatabaseServiceRem stub = (DatabaseServiceRem) Naming.lookup("//" + ip + ":8081/DatabaseService");

            // Mesure de la performance
            long startTime = System.nanoTime();

            if (ville != null) {
                System.out.println("Recherche des restaurants à " + ville + "...");
                
                // Un appel distant pour avoir la liste
                List<String> restos = stub.getRestaurantParVille(ville);
                
                if (restos == null || restos.isEmpty()) {
                    System.out.println("Aucun restaurant trouvé.");
                } else {
                    // N appels distants (un par restaurant)
                    for (String r : restos) {
                        String tel = stub.getNumeroParRestaurant(r);
                        System.out.println(" - " + r + " : " + tel);
                    }
                }
            } else {
                // Recherche directe d'un seul restaurant
                String tel = stub.getNumeroParRestaurant(restaurantUnique);
                System.out.println(restaurantUnique + " : " + tel);
            }

            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1000000;
            System.out.println("\nTemps total d'exécution (RMI): " + durationMs + " ms");

        } catch (Exception e) {
            System.err.println("Erreur Client: " + e.toString());
            e.printStackTrace();
        }
    }
}
