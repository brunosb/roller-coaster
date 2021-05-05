package barbosa.developer.config;

public enum Rules {

    ONE_CAR {
        @Override
        public int totalPassengers() {
            return 52;
        }

        @Override
        public int totalCars() {
            return 1;
        }

        @Override
        public int totalCarChairs() {
            return 4;
        }

        @Override
        public int timeBoardingOrLading() {
            return 1;
        }

        @Override
        public int timeTuor() {
            return 10;
        }

        @Override
        public int timeArriveQueue() {
            return Rules.randomTimeArriveQueue(1, 3);
        }
    },

    TWO_CARS {
        @Override
        public int totalPassengers() {
            return 92;
        }

        @Override
        public int totalCars() {
            return 2;
        }

        @Override
        public int totalCarChairs() {
            return 4;
        }

        @Override
        public int timeBoardingOrLading() {
            return 1;
        }

        @Override
        public int timeTuor() {
            return 10;
        }

        @Override
        public int timeArriveQueue() {
            return Rules.randomTimeArriveQueue(1, 3);
        }
    },

    THREE_CARS {
        @Override
        public int totalPassengers() {
            return 148;
        }

        @Override
        public int totalCars() {
            return 3;
        }

        @Override
        public int totalCarChairs() {
            return 4;
        }

        @Override
        public int timeBoardingOrLading() {
            return 1;
        }

        @Override
        public int timeTuor() {
            return 10;
        }

        @Override
        public int timeArriveQueue() {
            return Rules.randomTimeArriveQueue(1, 3);
        }
    };

    private static int randomTimeArriveQueue(int min, int max) {
        return (int) Math.round(Math.random() * (max - min) + min);
    }

    /*Return in seconds*/
    public abstract int totalPassengers();

    public abstract int totalCars();

    public abstract int totalCarChairs();

    public abstract int timeBoardingOrLading();

    public abstract int timeTuor();

    public abstract int timeArriveQueue();
}
