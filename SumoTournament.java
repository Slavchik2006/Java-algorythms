package sk.upjs.paz;

import java.util.*;

public class SumoTournament {
    public static class Ticket{
        private int startDay;
        private int finalDay;
        private int matches;

        public Ticket(int startDay, int finalDay, int matches) {
            this.startDay = startDay;
            this.finalDay = finalDay;
            this.matches = matches;
        }

        public int getFinalDay() {
            return finalDay;
        }

        public int getStartDay() {
            return startDay;
        }

        public int getMatches() {
            return matches;
        }
    }

    public static class TournamentSolver{
        private List<Ticket> tickets;
        private int maxMatches;

        public TournamentSolver(List<Ticket> tickets, int maxMatches) {
            this.tickets = tickets;
            this.maxMatches = maxMatches;
        }

        public List<Ticket> getTickets() {
            return tickets;
        }

        public int getMaxMatches() {
            return maxMatches;
        }

        public static TournamentSolver findMaxMatches(List<Ticket> tickets){
            if (tickets == null || tickets.isEmpty()){
                return new TournamentSolver(new ArrayList<>(), 0);
            }

            tickets.sort(Comparator.comparingInt(Ticket::getFinalDay));
            int n = tickets.size();

            int[] lastNonOverlapping = new int[n];

            for (int i = 0; i < n; i++) {
                lastNonOverlapping[i] = -1;
                for (int j = i - 1; j >= 0; j--) {
                    if (tickets.get(j).getFinalDay() < tickets.get(i).getStartDay()) {
                        lastNonOverlapping[i] = j;
                        break;
                    }
                }
            }
            // dp[i] — максимум матчей, который можно получить, рассматривая билеты от 0 по i (после сортировки по finalDay)
            int[] dp = new int[n];

            // take[i] — брали ли билет i в оптимальном решении для dp[i]
            boolean[] take = new boolean[n];

            for (int i = 0; i < n; i++) {
                // Вариант 1: билет i не берём — тогда максимум как у dp[i-1]
                int notTake = (i == 0) ? 0 : dp[i - 1];

                // Вариант 2: билет i берём — количество матчей + лучший результат до совместимого lastNonOverlapping[i]
                int takeVal = tickets.get(i).getMatches();
                if (lastNonOverlapping[i] != -1) {
                    takeVal += dp[lastNonOverlapping[i]];
                }

                // Выбираем лучший из двух вариантов и помечаем, взяли ли i
                if (takeVal > notTake) {
                    dp[i] = takeVal;
                    take[i] = true;
                } else {
                    dp[i] = notTake;
                    take[i] = false;
                }
            }

            // Восстановление выбранных билетов
            List<Ticket> chosen = new ArrayList<>();
            int i = n - 1;
            while (i >= 0) {
                if (take[i]) {
                    chosen.add(tickets.get(i));
                    i = lastNonOverlapping[i];
                } else {
                    i--;
                }
            }
            Collections.reverse(chosen);

            return new TournamentSolver(chosen, dp[n - 1]);
        }
    }

    public static void main(String[] args) {
        List<Ticket> input = Arrays.asList(
                new Ticket(2, 4, 10),
                new Ticket(5, 6, 6),
                new Ticket(1, 2, 3),
                new Ticket(3, 5, 5)
        );

        TournamentSolver result = TournamentSolver.findMaxMatches(input);
        System.out.println("Максимум матчей: " + result.getMaxMatches());
    }
}

