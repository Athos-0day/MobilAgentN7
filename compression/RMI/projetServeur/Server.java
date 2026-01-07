import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * Class for the RMI server that starts the registry and create the pads
 *
 * @author you
 */
public final class Server {

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
