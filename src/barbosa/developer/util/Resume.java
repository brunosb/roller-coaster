package barbosa.developer.util;

import barbosa.developer.passenger.GroupPassengers;
import barbosa.developer.passenger.Passenger;
import barbosa.developer.passenger.QueueController;

import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Resume {

    private QueueController queueController;
    private Map<Integer, List<Long>> timesPerCar = new HashMap<>();
    private Long totalProgramTime;


    public Resume(QueueController queueController, Map<Integer, List<Long>> timesPerCar, Long totalProgramTime) {
        this.queueController = queueController;
        this.timesPerCar = timesPerCar;
        this.totalProgramTime = totalProgramTime;

        init();
    }

    private void init() {
        /*=========== Calc time passengers ============*/
        LongSummaryStatistics statusPassengers = queueController.getGroupPassengers()
                .stream()
                .map(GroupPassengers::getPassengers)
                .map(passengers -> passengers.stream()
                        .map(Passenger::getTotalWait)
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .mapToLong(value -> value)
                .summaryStatistics();

        long waitMin = statusPassengers.getMin();
        long waitMax = statusPassengers.getMax();
        long waitAverage = (long) statusPassengers.getAverage();

        String minValue = "Tempo: minimo -> " + convertLongInTimeString(waitMin);
        String maxValue = "Tempo: maximo -> " + convertLongInTimeString(waitMax);
        String averageValue = "Tempo: medio -> " + convertLongInTimeString(waitAverage);

        /*=========== Calc time cars ============*/
        AtomicReference<String> utilizationCars = new AtomicReference<>("");
        timesPerCar.forEach((carId, times) -> {
            long sumTimes = times.stream().mapToLong(Long::longValue).sum();
            utilizationCars.getAndSet(utilizationCars.get() +"Tempo movimentacao carro("+carId+"): "+ convertLongInTimeString(sumTimes)+"\n");
        });

        /*=========== Print results ============*/
        System.out.println("==================== Resumo da espera ===================");
        System.out.println(minValue);
        System.out.println(maxValue);
        System.out.println(averageValue);

        System.out.println("==================== Resumo de utilização ===================");
        System.out.println(utilizationCars.get());
        System.out.println("Tempo total da monta russa: " + convertLongInTimeString(totalProgramTime));


        /*queueController.getGroupPassengers()
                .forEach(groupPassengers -> groupPassengers.getPassengers()
                        .forEach(barbosa.developer.passenger -> {
                            System.out.println("Passenger: " + barbosa.developer.passenger.getId() +
                                    "\nStart: " + barbosa.developer.passenger.getStartWait() +
                                    "\nFinished: " +barbosa.developer.passenger.getFinalWait() +
                                    "\nDiff: "+(barbosa.developer.passenger.getFinalWait() - barbosa.developer.passenger.getStartWait())+
                                    "\nTotal: " + convertLongInTimeString(barbosa.developer.passenger.getTotalWait()) +"\n");
                        }));*/
    }

    private String convertLongInTimeString(Long time) {
        return ((time / 1000) / 60) + "m " + ((int) ((time / 1000) % 60)) + "s";
    }
}
