import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class Client {
    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8081);

            ServiceFichier service =
                    (ServiceFichier) registry.lookup("ServiceFichier");

            File fichier = new File("document.txt");
            byte[] contenu = Files.readAllBytes(fichier.toPath());

            byte[] zipRecu = service.envoyerFichier(fichier.getName(), contenu);

            try (FileOutputStream fos = new FileOutputStream("resultat.zip")) {
                fos.write(zipRecu);
            }

            System.out.println("ZIP reçu avec succès !");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
