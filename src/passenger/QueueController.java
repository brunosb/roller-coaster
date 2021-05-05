package passenger;

import config.Rules;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class QueueController implements Runnable {

    private Rules rules;
    private Queue<Passenger> queuePassengers;
    private Queue<GroupPassengers> groupPassengers;
    private int totalPassengersPassedInQueue;
    private int limitPassengers;
    private int lastIdPassenger;
    private boolean isQueueClosed;
    private int lastIdGroup;

    private ReentrantLock queueControllerLock;
    private Condition enterPassenger;


    public QueueController(Rules rules) {
        this.rules = rules;

        this.queuePassengers = new ConcurrentLinkedQueue<>();
        this.groupPassengers = new ConcurrentLinkedQueue<>();
        this.totalPassengersPassedInQueue = 0;
        this.isQueueClosed = false;
        this.limitPassengers = rules.totalPassengers();
        this.lastIdPassenger = 0;
        this.lastIdGroup = 1;

        this.queueControllerLock = new ReentrantLock();
        this.enterPassenger = queueControllerLock.newCondition();
    }

    @Override
    public void run() {
        queueControllerLock.lock();

        System.out.println("Parque aberto, podem entrar na fila da montanha russa!");

        while (limitPassengers != totalPassengersPassedInQueue) {
            try {
                int timeRandom = rules.timeArriveQueue();
                System.out.println("Tempo para o proximo passageiro entrar na fila: " + timeRandom + "s");

                enterPassenger.await(timeRandom, TimeUnit.SECONDS);

                int id = ++lastIdPassenger;
                Passenger newPassenger = new Passenger(id, System.currentTimeMillis());
                queuePassengers.add(newPassenger);
                totalPassengersPassedInQueue++;

                computerGroupPassenger(newPassenger);

                System.out.println("Passageiro(" + id + ") entrou na fila! \tTotal na Fila: " + this.queuePassengers.size() + "\tTotal que chegou: " + this.totalPassengersPassedInQueue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Entradas encerradas, número maximo de passageiros atingido: " + rules.totalPassengers());
        this.isQueueClosed = true;

        queueControllerLock.unlock();
    }

    private void computerGroupPassenger(Passenger passenger) {
        if (groupPassengers.isEmpty()) {
            GroupPassengers groupPassengers = new GroupPassengers(lastIdGroup, rules.totalCarChairs());
            groupPassengers.add(passenger);
            this.groupPassengers.add(groupPassengers);
            lastIdGroup++;
        } else {
            boolean enteredGroup = false;
            for (GroupPassengers group : groupPassengers) {
                if (!group.isAlreadyWalkedRollerCoaster() && !group.isGroupFull()) {
                    group.add(passenger);
                    enteredGroup = true;
                    break;
                }
            }
            if (!enteredGroup) {
                GroupPassengers groupPassengers = new GroupPassengers(lastIdGroup, rules.totalCarChairs());
                groupPassengers.add(passenger);
                this.groupPassengers.add(groupPassengers);
                lastIdGroup++;
            }
        }
    }

    public void setFinalWaitTimeForGroup(int groupId) {
        getGroup(groupId).getPassengers()
                .forEach(passenger -> passenger.setFinalWait(System.currentTimeMillis()));
    }

    public Queue<Passenger> getQueuePassengers() {
        return queuePassengers;
    }

    public boolean isQueueClosed() {
        return isQueueClosed;
    }

    public Queue<GroupPassengers> getGroupPassengers() {
        return groupPassengers;
    }

    public GroupPassengers getGroup(int groupId) {
        for (GroupPassengers groupPassengers : getGroupPassengers()) {
            if (groupPassengers.getGroupId() == groupId) {
                return groupPassengers;
            }
        }
        return null;
    }
}
