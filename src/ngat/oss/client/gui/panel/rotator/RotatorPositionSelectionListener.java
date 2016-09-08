package ngat.oss.client.gui.panel.rotator;

public interface RotatorPositionSelectionListener {

    /** A rotator position was selected at time.*/
    public void rotatorSelection(long time, double rotator);
    public void receiveLockingEvent(boolean locked);
}
