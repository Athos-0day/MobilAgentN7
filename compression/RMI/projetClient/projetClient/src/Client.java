import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;
import java.io.FileInputStream;

public class Client{
    public static void main(String[] args) throws Exception {

        Registry registry = LocateRegistry.getRegistry("localhost", 8081);
        ServiceFichier service =
            (ServiceFichier) registry.lookup("ServiceFichier");

        File fichier = new File("document.txt");
        byte[] data;

        try (FileInputStream fis = new FileInputStream(fichier)) {
            data = fis.readAllBytes();
        }

        service.envoyerFichier(fichier.getName(), data);
        System.out.println("Fichier envoyé.");
    }
}
