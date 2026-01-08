import java.io.Serializable;

public abstract class Agent implements Serializable {
    protected transient Object localService; // transient pour ne pas sérialiser la DB
    public abstract void execute();
    public void setService(Object service) { this.localService = service; }
}