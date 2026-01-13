import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class Client {

    public static void main(String[] args) {
        // vérification des arguments
        if (args.length != 3) {
            System.out.println("Usage: java Client <ip_serveur> <port> <fichier>");
            System.out.println("Exemple: java Client 192.168.1.10 8081 document.txt");
            return;
        }

        String ipServeur = args[0];
        int port;

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Erreur : le port doit être un nombre.");
            return;
        }

        String nomFichier = args[2];

        try {
            // connexion au registre RMI distant qui tourne sur un port du serveur
            Registry registry = LocateRegistry.getRegistry(ipServeur, port);
            // on récupère le stub du service distant
            ServiceFichier service =
                    (ServiceFichier) registry.lookup("ServiceFichier");

            // lecture du fichier côté client
            File fichier = new File(nomFichier);
            if (!fichier.exists()) {
                System.out.println("Erreur : le fichier " + nomFichier + " n'existe pas.");
                return;
            }
            // lecture du contenu du fichier
            byte[] contenu = Files.readAllBytes(fichier.toPath());

            System.out.println("Envoi du fichier au serveur " + ipServeur + "...");

            // on appelle la méthode distante qui va compresser
            byte[] zipRecu = service.envoyerFichier(fichier.getName(), contenu);

            // Sauvegarde côté client
            String nomZip = fichier.getName() + ".zip";
            try (FileOutputStream fos = new FileOutputStream(nomZip)) {
                fos.write(zipRecu);
            }

            System.out.println("ZIP reçu avec succès depuis " + ipServeur);
            System.out.println("Fichier créé : " + nomZip);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
