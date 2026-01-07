import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AgentCompression implements AgentMobile {

    private byte[] fichier;
    private String nom;
    private byte[] resultat;

    public AgentCompression(String nom, byte[] fichier) {
        this.nom = nom;
        this.fichier = fichier;
    }

    @Override
    public void executer() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            ZipEntry entry = new ZipEntry(nom);
            zos.putNextEntry(entry);
            zos.write(fichier);
            zos.closeEntry();
            zos.close();

            resultat = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getResultat() {
        return resultat;
    }
}

