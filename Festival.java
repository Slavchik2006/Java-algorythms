package sk.upjs.paz;

import java.util.*;

public class Festival {
    //параметры фестиваля
    private int startDay;
    private int finalDay;
    private int startTime;
    private int finalTime;

    public Festival(int startDay, int finalDay, int startTime, int finalTime) {
        this.startDay = startDay;
        this.finalDay = finalDay;
        this.startTime = startTime;
        this.finalTime = finalTime;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getFinalDay() {
        return finalDay;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getFinalTime() {
        return finalTime;
    }

    public int absoluteStartTime(){
        return startDay*24 + startTime;
    }

    public int absoluteFinalTime(){
        return finalDay*24 + finalTime;
    }

    //класс для канала
    public static class Channel{
        private int id;
        private int freeTime;

        public Channel(int id, int freeTime) {
            this.id = id;
            this.freeTime = freeTime;
        }

        public int getId() {
            return id;
        }

        public int getFreeTime() {
            return freeTime;
        }
    }

    //класс для расписания
    public static class Scheduler{
        private int pricePerDay;
        private int channels;
        private Map<Integer, List<Festival>> schedule;

        public Scheduler(int pricePerDay, int channels, Map<Integer, List<Festival>> schedule) {
            this.pricePerDay = pricePerDay;
            this.channels = channels;
            this.schedule = schedule;
        }

        public int getPricePerDay() {
            return pricePerDay;
        }

        public int getChannels() {
            return channels;
        }

        public Map<Integer, List<Festival>> getSchedule() {
            return schedule;
        }

        public Scheduler solve(List<Festival> festivals, int cost){
            if (festivals == null || festivals.isEmpty()){
                return new Scheduler(0, 0, new HashMap<>());
            }
            Map<Integer, List<Festival>> schedule = new HashMap<>();
            //сортировка по времени начала
            festivals.sort(Comparator.comparingInt(Festival::absoluteStartTime));

            //сортировка каналов по времени освобождения
            PriorityQueue<Channel> available = new PriorityQueue<>(new Comparator<Channel>() {
                @Override
                public int compare(Channel c1, Channel c2) {
                    return Integer.compare(c1.freeTime, c2.freeTime);
                }
            });

            int channelCounter = 0;

            for(Festival festival : festivals){
                if (!available.isEmpty() && available.peek().freeTime <= festival.absoluteStartTime()){
                    //есть свободные каналы
                    Channel reused = available.poll();

                    schedule.get(reused.getId()).add(festival);

                    available.add(new Channel(reused.getId(), festival.absoluteFinalTime()));
                }else {
                    //нет свободных каналов
                    channelCounter++;
                    int newChannel = channelCounter;

                    schedule.put(newChannel, new ArrayList<>());
                    schedule.get(newChannel).add(festival);

                    available.add(new Channel(newChannel, festival.absoluteFinalTime()));
                }
            }
            int totalPrice = calculateCost(schedule, cost);

            return new Scheduler(totalPrice, channelCounter, schedule);
        }

        //метод для подсчета общей стоимости
        public int calculateCost(Map<Integer, List<Festival>> schedule, int cost){
            int channelDays = 0;
            for (List<Festival> festivals : schedule.values()){
                Set<Integer> daysForChannel = new HashSet<>();
                for (Festival festival : festivals){
                    for (int i = festival.getStartDay(); i <= festival.getFinalDay(); i++) {
                        daysForChannel.add(i);
                    }
                }
                channelDays += daysForChannel.size();
            }
            return channelDays*cost;
        }
    }

    public static class Main{
        public static void main(String[] args) {
            int cost = 1000;
            List<Festival> festivals = Arrays.asList(
                    new Festival(12, 12, 6, 9),
                    new Festival(12, 12, 8, 10), //пересекается с первым
                    new Festival(12, 12, 10, 12), //не пересекается с первым
                    new Festival(13, 13, 0, 2),
                    new Festival(13, 13, 1, 3) //пересекается с фестивалем выше
            );
            Festival.Scheduler scheduler = new Festival.Scheduler(0, 0, new HashMap<>());
            Festival.Scheduler result = scheduler.solve(festivals, cost);
            System.out.println("Минимальное количество каналов: " + result.getChannels());
            System.out.println("Общая стоимость: " + result.getPricePerDay() + " €");
            System.out.println("Распиание: ");

            for (Map.Entry<Integer, List<Festival>> entry : result.getSchedule().entrySet()) {
                System.out.println("Канал " + entry.getKey() + ":");
                for (Festival f : entry.getValue()) {
                    System.out.println(
                            "  день " + f.getStartDay() + " " + f.getStartTime() + ":00"
                                    + " - день " + f.getFinalDay() + " " + f.getFinalTime() + ":00"
                    );
                }
            }
        }
    }
}

