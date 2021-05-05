package barbosa.developer.rollercoaster;

import barbosa.developer.config.Rules;
import barbosa.developer.passenger.QueueController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RollerCoasterSingleCar {

    private Rules rules;
    private QueueController queueController;

    private int passengersInCar = 0;
    private boolean carIsWaitingPassengers = false;

    private final ReentrantLock rollerCoasterLock;
    private final Condition carLoad; //Signaled when the car can load passengers
    private final Condition carUnload; //Signaled when the car is unloading its passengers
    private final Condition carIsFull; //Signaled when the car is full
    private final Condition timeWait; //Time wait for next move

    public RollerCoasterSingleCar(Rules rules, QueueController queueController) {
        this.rules = rules;
        this.queueController = queueController;

        rollerCoasterLock = new ReentrantLock();
        carLoad = rollerCoasterLock.newCondition();
        carUnload = rollerCoasterLock.newCondition();
        carIsFull = rollerCoasterLock.newCondition();
        timeWait = rollerCoasterLock.newCondition();
    }

    public void load() {
        rollerCoasterLock.lock();

        /*Espera pelo carro se ele estiver rodando*/
        while (passengersInCar == rules.totalCarChairs()) {
            carUnload.awaitUninterruptibly();
        }
        carIsWaitingPassengers = true;

        /*Notifica os passageiros para entrar no carro*/
        carLoad.signalAll();

        /*Espera ficar full*/
        carIsFull.awaitUninterruptibly();
        carIsWaitingPassengers = false;
        /*Notifica aos passageiros que o carro ta full*/
        carIsFull.signalAll();

        rollerCoasterLock.unlock();
    }

    public void unload() {
        rollerCoasterLock.lock();

        carUnload.signalAll();
        passengersInCar = 0;

        rollerCoasterLock.unlock();
    }

    public void takeWalk(int groupId) throws InterruptedException {
        rollerCoasterLock.lock();

        /*Esperar o carro ficar pronto*/
        while (!carIsWaitingPassengers || passengersInCar == rules.totalCarChairs()) {
            System.out.println("Grupo " + groupId + " está aguardando carro(1) ficar liberado");
            carLoad.awaitUninterruptibly();
        }

        System.out.println("Grupo " + groupId + " está embarcando em " + rules.timeBoardingOrLading() + "s");
        passengersInCar = queueController.getGroup(groupId).getSizeGroup();

        /*Gravar tempo de saida da fila*/
        queueController.setFinalWaitTimeForGroup(groupId);

        /*Remover da fila principal*/
        for (int i = 0; i < rules.totalCarChairs(); i++) {
            queueController.getQueuePassengers().remove();
        }

        /*Espera subir no carro*/
        timeWait.await(rules.timeBoardingOrLading(), TimeUnit.SECONDS);

        /*Avisa que o carro esta cheio*/
        if (passengersInCar == rules.totalCarChairs()) {
            carIsFull.signal();
        }
        carIsFull.awaitUninterruptibly();

        /*Aguarda a volta terminar*/
        carUnload.awaitUninterruptibly();
        System.out.println("Carro(1) terminou a volta, desembarcando o grupo " + groupId + " em " + rules.timeBoardingOrLading() + "s");
        /*Espera todos descerem do carro*/
        timeWait.await(rules.timeBoardingOrLading(), TimeUnit.SECONDS);

        /*Avisa que esse grupo já deu a volta*/
        queueController.getGroup(groupId).setAlreadyWalkedRollerCoaster(true);

        timeWait.await(rules.timeBoardingOrLading(), TimeUnit.SECONDS);

        rollerCoasterLock.unlock();
    }
}
