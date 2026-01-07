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
            // 1. Sauvegarde du fichier reçu
            File fichier = new File("recu_" + nomFichier);
            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                fos.write(contenu);
            }

            // 2. Compression côté serveur
            File fichierZip = compresserFichier(fichier);

            // 3. Lecture du fichier ZIP en byte[]
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

        return fichierZip;
    }

    private byte[] lireFichierEnBytes(File fichier) throws IOException {
        try (FileInputStream fis = new FileInputStream(fichier);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }

            return baos.toByteArray();
        }
    }
}
