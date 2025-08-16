package sk.upjs.paz;

import java.util.HashSet;
import java.util.Set;

public class TrainTravel {

    private static double[] minTimes;
    private static double[] maxDistances;

    public static void dijkstra(double[][] timeGraph, double[][] distGraph, int start) {
        int n = timeGraph.length; //количество городов
        minTimes = new double[n]; // мин время до каждого города
        maxDistances = new double[n]; // макс расстояние до каждого города
        Set<Integer> unvisited = new HashSet<>(); //множество непосещенных городов

        //изначальный случай
        for (int i = 0; i < n; i++) {
            minTimes[i] = Double.POSITIVE_INFINITY;
            maxDistances[i] = 0;
            unvisited.add(i);
        }

        minTimes[start] = 0;

        while (!unvisited.isEmpty()) {
            // Находим город с минимальным временем из непосещённых
            int v = findSmallestUnvisited(unvisited);
            if (v == -1) break;
            unvisited.remove(v);

            // Обходим всех соседей текущего города
            for (int i = 0; i < n; i++) {
                if (timeGraph[v][i] > 0) {
                    // Рассчитываем новое время и расстояние
                    double newTime = minTimes[v] + timeGraph[v][i];
                    double newDist = maxDistances[v] + distGraph[v][i];

                    // Если нашли более короткое время
                    if (newTime < minTimes[i]) {
                        minTimes[i] = newTime;
                        maxDistances[i] = newDist;
                    }
                    // Если время такое же, но расстояние больше
                    else if (newTime == minTimes[i] && newDist > maxDistances[i]) {
                        maxDistances[i] = newDist;
                    }
                }
            }
        }
    }

    // Поиск непосещённого города с минимальным временем
    private static int findSmallestUnvisited(Set<Integer> unvisited) {
        double smallestValue = Double.POSITIVE_INFINITY;
        int smallestVertex = -1;
        for (int v : unvisited) {
            if (minTimes[v] < smallestValue) {
                smallestValue = minTimes[v];
                smallestVertex = v;
            }
        }
        return smallestVertex;
    }

    public static void main(String[] args) {
        // Города
        String[] cities = {"Токио", "Киото", "Осака", "Нагоя", "Саппоро"};
        int n = cities.length;

        double[][] distGraph = new double[n][n];
        double[][] timeGraph = new double[n][n];

        // Токио - Киото
        distGraph[0][1] = 500; timeGraph[0][1] = 140;
        distGraph[1][0] = 500; timeGraph[1][0] = 150;

        // Токио - Нагоя
        distGraph[0][3] = 350; timeGraph[0][3] = 100;
        distGraph[3][0] = 350; timeGraph[3][0] = 95;

        // Киото - Осака
        distGraph[1][2] = 45; timeGraph[1][2] = 30;
        distGraph[2][1] = 45; timeGraph[2][1] = 35;

        // Нагоя - Киото
        distGraph[3][1] = 150; timeGraph[3][1] = 50;
        distGraph[1][3] = 150; timeGraph[1][3] = 55;

        // Токио - Саппоро
        distGraph[0][4] = 1100; timeGraph[0][4] = 250;
        distGraph[4][0] = 1100; timeGraph[4][0] = 260;

        int startCity = 0; // Токио
        int maxTime = 240;

        dijkstra(timeGraph, distGraph, startCity);

        int farthestCity = -1;
        double maxDist = -1;

        for (int i = 0; i < n; i++) {
            if (i == startCity) continue;
            // Если город достижим за время и расстояние больше текущего максимума
            if (minTimes[i] <= maxTime && maxDistances[i] > maxDist) {
                maxDist = maxDistances[i];
                farthestCity = i;
            }
        }

        if (farthestCity != -1) {
            System.out.println("Самый дальний город, достижимый из " + cities[startCity] +
                    " за " + maxTime + " минут: " + cities[farthestCity]);
            System.out.println("Время: " + minTimes[farthestCity] +
                    " мин, Расстояние: " + maxDistances[farthestCity] + " км");
        } else {
            System.out.println("Нельзя доехать ни в один город за " + maxTime + " минут.");
        }
    }
}
