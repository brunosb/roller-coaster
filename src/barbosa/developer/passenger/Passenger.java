package passenger;

public class Passenger {

    private int id;
    private long startWait;
    private long finalWait;
    private long totalWait;

    public Passenger(int id, long startWait) {
        this.id = id;
        this.startWait = startWait;
    }

    public long getFinalWait() {
        return finalWait;
    }

    public void setFinalWait(long finalWait) {
        this.finalWait = finalWait;
        totalWait = Math.subtractExact(finalWait, startWait);
    }

    public long getTotalWait() {
        return totalWait;
    }

    public int getId() {
        return id;
    }

    public long getStartWait() {
        return startWait;
    }
}
