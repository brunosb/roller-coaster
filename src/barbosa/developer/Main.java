package barbosa.developer;

import barbosa.developer.config.Rules;
import barbosa.developer.passenger.GroupPassengers;
import barbosa.developer.passenger.QueueController;
import barbosa.developer.rollercoaster.RollerCoasterSeveralCars;
import barbosa.developer.rollercoaster.RollerCoasterSingleCar;
import barbosa.developer.util.Resume;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static List<Thread> allThreads = new CopyOnWriteArrayList<>();

    private static QueueController queueController;

    private static final ReentrantLock mainLock = new ReentrantLock();
    private static final Condition rolleCoasterTuor = mainLock.newCondition();

    private static Map<Integer, List<Long>> timesPerCar = new HashMap<>();
    private static Long initialProgramTime;
    private static Long totalProgramTime;

    public static void main(String[] args) {
        int option = 0;
        Rules rules = null;

        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Escolha a opcao: 1-Caso um carro\t2-Caso dois carros\t3-Caso tres carros");
            option = scanner.nextInt();
        } while (option == 0);

        switch (option) {
            case 1:
                rules = Rules.ONE_CAR;
                break;
            case 2:
                rules = Rules.TWO_CARS;
                break;
            case 3:
                rules = Rules.THREE_CARS;
                break;
        }

        if (rules != null) {
            initialProgramTime = System.currentTimeMillis();

            queueController = new QueueController(rules);
            Thread queueThread = new Thread(queueController);
            allThreads.add(queueThread);
            queueThread.start();

            if (option == 1) {
                rollerCoastSingleCar(rules);
            } else {
                rollerCoastSeveralCars(rules);
            }
        }

    }

    private static void rollerCoastSingleCar(Rules rules) {

        final RollerCoasterSingleCar rollerCoasterSingleCar = new RollerCoasterSingleCar(rules, queueController);

        Runnable carRun = () -> {
            mainLock.lock();
            while (checkConditionsToRun(queueController)) {
                rollerCoasterSingleCar.load();

                try {
                    System.out.println("Carro iniciou a volta. com duração de " + rules.timeTuor() + "s");
                    long initTimeCarTuor = System.currentTimeMillis();

                    rolleCoasterTuor.await(rules.timeTuor(), TimeUnit.SECONDS);

                    registerTimeCar(1, initTimeCarTuor);
                } catch (InterruptedException e) {
                }

                rollerCoasterSingleCar.unload();
            }
            mainLock.unlock();
        };

        Thread carThread = new Thread(carRun, "Car");
        allThreads.add(carThread);
        carThread.start();

        while (checkConditionsToRun(queueController)) {
            for (GroupPassengers groupPassengers : queueController.getGroupPassengers()) {
                if (!groupPassengers.isAlreadyWalkedRollerCoaster() && groupPassengers.isGroupFull() && !groupPassengers.isStartThread()) {
                    Runnable runnable = () -> {
                        while (!groupPassengers.isAlreadyWalkedRollerCoaster()) {
                            try {
                                rollerCoasterSingleCar.takeWalk(groupPassengers.getGroupId());
                            } catch (InterruptedException e) {
                            }
                        }
                    };
                    Thread groupPassengerThread = new Thread(runnable, "GroupPassenger " + groupPassengers.getGroupId());
                    allThreads.add(groupPassengerThread);
                    groupPassengerThread.start();
                    groupPassengers.setStartThread(true);
                }
            }
        }

        waitFinishedAllThreads();
    }

    private static void rollerCoastSeveralCars(Rules rules) {
        final RollerCoasterSeveralCars rollerCoasterSingleCar = new RollerCoasterSeveralCars(rules, queueController);
        for (int i = 0; i < rules.totalCars(); i++) {
            int finalI = i;
            Runnable carRun = () -> {
                mainLock.lock();
                while (checkConditionsToRun(queueController)) {
                    rollerCoasterSingleCar.load();

                    try {
                        System.out.println("Carro iniciou a volta. com duração de " + rules.timeTuor() + "s");
                        long initTimeCarTuor = System.currentTimeMillis();

                        rolleCoasterTuor.await(rules.timeTuor(), TimeUnit.SECONDS);

                        registerTimeCar(finalI, initTimeCarTuor);
                    } catch (InterruptedException e) {
                    }

                    rollerCoasterSingleCar.unload();
                }
                mainLock.unlock();
            };

            Thread carThread = new Thread(carRun, "Car " + i);
            allThreads.add(carThread);
            carThread.start();
        }


        while (checkConditionsToRun(queueController)) {
            for (GroupPassengers groupPassengers : queueController.getGroupPassengers()) {
                if (!groupPassengers.isAlreadyWalkedRollerCoaster() && groupPassengers.isGroupFull() && !groupPassengers.isStartThread()) {
                    Runnable runnable = () -> {
                        while (!groupPassengers.isAlreadyWalkedRollerCoaster()) {
                            try {
                                rollerCoasterSingleCar.takeWalk(groupPassengers.getGroupId());
                            } catch (InterruptedException e) {
                            }
                        }
                    };
                    Thread groupPassengerThread = new Thread(runnable, "GroupPassenger " + groupPassengers.getGroupId());
                    allThreads.add(groupPassengerThread);
                    groupPassengerThread.start();
                    groupPassengers.setStartThread(true);
                }
            }
        }

        waitFinishedAllThreads();
    }

    private static boolean checkConditionsToRun(QueueController queueController) {
        return !queueController.isQueueClosed() || !queueController.getQueuePassengers().isEmpty();
    }

    private static void registerTimeCar(int numberCar, Long initTimeCar) {
        if (!timesPerCar.containsKey(numberCar)) {
            timesPerCar.put(numberCar, new ArrayList<>());
            timesPerCar.get(numberCar).add((System.currentTimeMillis() - initTimeCar));
        } else {
            timesPerCar.get(numberCar).add((System.currentTimeMillis() - initTimeCar));
        }
    }

    private static void waitFinishedAllThreads() {
        while (!allThreads.isEmpty()) {
            allThreads.removeIf(thread -> !thread.isAlive());
        }
        totalProgramTime = System.currentTimeMillis() - initialProgramTime;

        new Resume(queueController, timesPerCar, totalProgramTime);
    }

}
