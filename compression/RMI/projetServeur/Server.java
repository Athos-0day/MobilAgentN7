import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * Class for the RMI server that connects to the RMI registry and binds the remote object
 *
 * @author you
 */
public final class Server {
  // modifier l'ip et le port si besoin
  public static final String serverURI = "//localhost:8081/ServiceFichier";



  public static void main(String args[]) {
  try {
    // /* Launching the naming service – rmiregistry – within the JVM */
    // LocateRegistry.createRegistry(8081);
    // Naming.bind(serverURI, new ServiceFichierImpl());

    // On enregistre l'objet distant dans le registre existant, il faut avoir lancé le rmi avant
    Naming.rebind(serverURI, new ServiceFichierImpl());
    while(true);   
        } catch (Exception e) {
    e.printStackTrace();
  }
}
}
