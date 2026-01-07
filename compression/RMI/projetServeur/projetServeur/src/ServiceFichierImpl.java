import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ServiceFichierImpl extends UnicastRemoteObject
        implements ServiceFichier {

    public ServiceFichierImpl() throws RemoteException {
    }

    public void envoyerFichier(String nomFichier, byte[] contenu)
            throws RemoteException {

        try {
            // 1. Sauvegarde du fichier reçu
            File fichier = new File("recu_" + nomFichier);
            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                fos.write(contenu);
            }

            // 2. Compression côté serveur
            compresserFichier(fichier);

            System.out.println("Fichier reçu et compressé : " + nomFichier);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compresserFichier(File fichier) throws IOException {

        File fichierZip = new File(fichier.getName() + ".zip");

        try (
            FileInputStream fis = new FileInputStream(fichier);
            FileOutputStream fos = new FileOutputStream(fichierZip);
            ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            ZipEntry entry = new ZipEntry(fichier.getName());
            zos.putNextEntry(entry);

            byte[] buffer = new byte[4096];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, n);
            }

            zos.closeEntry();
        }
    }

    public static final String serverURI = "//localhost:8081/ServiceFichier";

    public static void main(String args[]) {
    try {
      /* Launching the naming service – rmiregistry – within the JVM */
      LocateRegistry.createRegistry(8081);
      Naming.bind(serverURI, new ServiceFichierImpl());

      while(true);   
 //     Naming.bind(serverURI + "/Pad/" + new PadImpl);
 
          } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
