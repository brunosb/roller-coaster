package barbosa.developer.passenger;

import java.util.LinkedList;
import java.util.List;

public class GroupPassengers {

    private int groupId;
    private List<Passenger> passengers;
    private boolean alreadyWalkedRollerCoaster;
    private int limitPassengersInGroup;
    private boolean startThread;

    public GroupPassengers(int groupId, int limitPassengersInGroup) {
        this.groupId = groupId;
        this.passengers = new LinkedList<>();
        this.alreadyWalkedRollerCoaster = false;
        this.limitPassengersInGroup = limitPassengersInGroup;
        this.startThread = false;
    }

    public void add(Passenger passenger) {
        passengers.add(passenger);
    }

    public boolean isGroupFull() {
        return passengers.size() == limitPassengersInGroup;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isAlreadyWalkedRollerCoaster() {
        return alreadyWalkedRollerCoaster;
    }

    public void setAlreadyWalkedRollerCoaster(boolean alreadyWalkedRollerCoaster) {
        this.alreadyWalkedRollerCoaster = alreadyWalkedRollerCoaster;
    }

    public int getSizeGroup() {
        return passengers.size();
    }

    public void setStartThread(boolean startThread) {
        this.startThread = startThread;
    }

    public boolean isStartThread() {
        return startThread;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }
}
