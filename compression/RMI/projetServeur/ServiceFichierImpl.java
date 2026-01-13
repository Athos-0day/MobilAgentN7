import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ServiceFichierImpl extends UnicastRemoteObject
        implements ServiceFichier {

    public ServiceFichierImpl() throws RemoteException {
    }

    @Override
    public byte[] envoyerFichier(String nomFichier, byte[] contenu)
            throws RemoteException {

        try {
            // sauvegarde du fichier reçu
            File fichier = new File("recu_" + nomFichier);
            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                fos.write(contenu);
            }

            // compression 
            File fichierZip = compresserFichier(fichier);

            // lecture du fichier ZIP en un tableau de bytes
            byte[] zipBytes = lireFichierEnBytes(fichierZip);

            System.out.println("Fichier reçu, compressé et renvoyé : " + fichierZip.getName());

            return zipBytes;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File compresserFichier(File fichier) throws IOException {

        File fichierZip = new File(fichier.getName() + ".zip");

        try (
            // On utilise pour gérer les flux
            // le flux d'entrée du fichier à compresser
            FileInputStream fis = new FileInputStream(fichier);
            // le flux de sortie du fichier ZIP
            FileOutputStream fos = new FileOutputStream(fichierZip);
            // le flux ZIP pour compresser
            ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
           // création de l'entrée ZIP
            ZipEntry entry = new ZipEntry(fichier.getName());
            zos.putNextEntry(entry);

            byte[] buffer = new byte[4096];
            int n;
            // lecture du fichier et écriture dans le ZIP
            while ((n = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, n);
            }

            zos.closeEntry();
        }

        return fichierZip;
    }

    private byte[] lireFichierEnBytes(File fichier) throws IOException {
        // lecture du fichier dans un tableau de bytes
        try (FileInputStream fis = new FileInputStream(fichier);
            // flux en mémoire pour stocker les bytes
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // lecture du fichier 
            byte[] buffer = new byte[4096];
            int n;
            // copie du fichier dans le flux en mémoire
            while ((n = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }

            // retour du tableau de bytes le toByteArray() sert à obtenir le tableau de bytes à partir du flux en mémoire
            return baos.toByteArray();
        }
    }
}
