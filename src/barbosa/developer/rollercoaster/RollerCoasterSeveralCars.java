package rollercoaster;

import config.Rules;
import passenger.QueueController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RollerCoasterSeveralCars {
    private Rules rules;
    private QueueController queueController;

    private final int[] passengersInCar; //Array whose values are the number of passengers in the car[i]
    private int nextCarRequestLoad = 0; //Index of the next car that will request load
    private int nextCarToUnload = 0; //Index of the next car that will be unloaded
    private int nextCarToLoad = 0; //Index of the next car that will load
    private boolean nextCarToLoadIsWaitingPassengers = false; //True if the next car that will load is waiting for passengers

    private final ReentrantLock rollerCoasterLock;
    private final Condition carLoad; //Signaled when a car can load passengers
    private final Condition carUnload; //Signaled when a car is unloading its passengers
    private final Condition carIsFull; //Signaled when a car is full
    private final Condition nextCarCanLoad; //Signaled when the next car to load can load passengers
    private final Condition timeWait; //Time wait for next move

    public RollerCoasterSeveralCars(Rules rules, QueueController queueController) {
        this.rules = rules;
        this.queueController = queueController;

        this.passengersInCar = new int[rules.totalCars()];

        rollerCoasterLock = new ReentrantLock();
        carLoad = rollerCoasterLock.newCondition();
        carUnload = rollerCoasterLock.newCondition();
        carIsFull = rollerCoasterLock.newCondition();
        nextCarCanLoad = rollerCoasterLock.newCondition();
        timeWait = rollerCoasterLock.newCondition();
    }

    public void load() {
        rollerCoasterLock.lock();

        final int thisCar = nextCarRequestLoad;
        /*Atualiza o indice do proximo carro*/
        nextCarRequestLoad = (nextCarRequestLoad + 1) % rules.totalCars();
        /*Se este carro não for o próximo carro a dar a volta, espere que o carro sucessor saia*/
        if (thisCar != nextCarToLoad) {
            nextCarCanLoad.awaitUninterruptibly();
        }
        nextCarToLoadIsWaitingPassengers = true;

        /*Notifica os passageiros para entrar no carro*/
        carLoad.signalAll();
        /*Espera ficar full*/
        carIsFull.awaitUninterruptibly();
        nextCarToLoadIsWaitingPassengers = false;
        /*Notifica aos passageiros que o carro ta full*/
        carIsFull.signalAll();

        /*Acorda o carro predecessor*/
        nextCarToLoad = (nextCarToLoad + 1) % rules.totalCars();
        nextCarCanLoad.signal();

        rollerCoasterLock.unlock();
    }

    public void unload() {
        rollerCoasterLock.lock();

        carUnload.signal();
        passengersInCar[nextCarToUnload] = 0;

        nextCarToUnload = (nextCarToUnload + 1) % rules.totalCars();
        rollerCoasterLock.unlock();
    }

    public void takeWalk(int groupId) throws InterruptedException {
        rollerCoasterLock.lock();

        /*Esperar o carro ficar pronto*/
        while (!nextCarToLoadIsWaitingPassengers || passengersInCar[nextCarToLoad] == rules.totalCarChairs()) {
            System.out.println("Grupo " + groupId + " está aguardando algum carro ficar liberado");
            carLoad.awaitUninterruptibly();
        }

        final int ridingCar = nextCarToLoad;
        passengersInCar[ridingCar] = queueController.getGroup(groupId).getSizeGroup();

        /*Gravar tempo de saida da fila*/
        queueController.setFinalWaitTimeForGroup(groupId);

        /*Remover da fila principal*/
        for (int i = 0; i < rules.totalCarChairs(); i++) {
            queueController.getQueuePassengers().remove();
        }

        System.out.println("Grupo " + groupId + " está embarcando no carro(" + ridingCar + ") em " + rules.timeBoardingOrLading() + "s");
        /*Espera subir no carro*/
        timeWait.await(rules.timeBoardingOrLading(), TimeUnit.SECONDS);

        /*Avisa que o carro esta cheio*/
        if (passengersInCar[ridingCar] == rules.totalCarChairs()) {
            carIsFull.signal();
        }
        carIsFull.awaitUninterruptibly();

        /*Aguarda a volta terminar*/
        carUnload.awaitUninterruptibly();
        System.out.println("Carro(" + ridingCar + ") terminou a volta, desembarcando o grupo " + groupId + " em " + rules.timeBoardingOrLading() + "s");
        /*Espera todos descerem do carro*/
        timeWait.await(rules.timeBoardingOrLading(), TimeUnit.SECONDS);

        /*Avisa que esse grupo já deu a volta*/
        queueController.getGroup(groupId).setAlreadyWalkedRollerCoaster(true);

        rollerCoasterLock.unlock();
    }
}
