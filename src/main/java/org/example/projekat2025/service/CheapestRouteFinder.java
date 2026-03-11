package org.example.projekat2025.service;

import org.example.projekat2025.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementacija interfejsa {@link RouteFinderInterface} koja pronalazi najjeftinije rute
 * između dvije stanice koristeći Dijkstra algoritam.
 *
 * Cijena putovanja određuje se na osnovu ukupne cijene svih segmenata.
 * Ova klasa takođe uzima u obzir vrijeme polaska i minimalno vrijeme za presijedanje.
 *
 * @author Nevena
 * @version 1.0
 */
public class CheapestRouteFinder implements RouteFinderInterface {

    /**
     * Pomoćna metoda koja parsira vrijeme u formatu HH:mm u minute.
     * @param time string koji predstavlja vrijeme (npr. 12:30)
     * @return broj minuta od ponoći
     */
    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * Pronalazi top K najjeftinijih ruta između početnog i odredišnog čvora.
     * Koristi Dijkstra algoritam uz dodatne uslove za vrijeme polaska i presjedanje.
     *
     * @param start početni čvor/stanica
     * @param goal odredišni čvor/stanica
     * @param allNodes skup svih čvorova u grafu
     * @param topK broj najboljih ruta koje treba pronaći
     * @return lista top K najjeftinijih ruta kao objekti klase {@link PathNode}
     */
    @Override
    public List<PathNode> findTopRoutes(GraphNode start, GraphNode goal, Set<GraphNode> allNodes, int topK) {
        List<PathNode> results = new ArrayList<>();
        Set<String> seenPaths = new HashSet<>();

        PriorityQueue<PathNode> queue = new PriorityQueue<>(Comparator.comparingInt(PathNode::getTotalCost));
        int startTime = 0;

        queue.add(new PathNode(start, 0, new ArrayList<>(), startTime));

        while (!queue.isEmpty() && results.size() < topK) {
            PathNode current = queue.poll();

            if (current.getNode().equals(goal)) {
                String pathSignature = current.getPath().stream()
                        .map(edge -> edge.getTarget().getStation().getId()) // koristi ID stanica
                        .collect(Collectors.joining("->"));

                if (!seenPaths.contains(pathSignature)) {
                    seenPaths.add(pathSignature);
                    results.add(current);
                }
                continue;
            }

            for (GraphEdge edge : current.getNode().getEdges()) {
                Departure dep = edge.getDeparture();
                if (dep == null) continue;

                int departureTime = parseTimeToMinutes(dep.getDepartureTime());

                if (departureTime < current.getArrivalTime() + dep.getMinTransferTime()) continue;

                int arrivalTime = departureTime + dep.getDuration();
                int newCost = current.getTotalCost() + dep.getPrice();

                List<GraphEdge> newPath = new ArrayList<>(current.getPath());
                newPath.add(edge);

                queue.add(new PathNode(edge.getTarget(), newCost, newPath, arrivalTime));
            }
        }

        results.sort(Comparator.comparingInt(PathNode::getTotalCost));
        return results;
    }

}
