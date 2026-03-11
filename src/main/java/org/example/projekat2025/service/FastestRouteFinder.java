package org.example.projekat2025.service;

import org.example.projekat2025.model.*;

import java.util.*;

/**
 * Implementacija interfejsa {@link RouteFinderInterface} koja pronalazi najbrže rute
 * između dvije stanice koristeći Dijkstra algoritam.
 * Prioritet u ovom slučaju je ukupno vrijeme putovanja.
 * Ova klasa takođe uzima u obzir vrijeme polaska i minimalno vrijeme za presijedanje.
 *
 * @author Nevena
 * @version 1.0
 */
public class FastestRouteFinder implements RouteFinderInterface {

    /**
     * Pomoćna metoda koja parsira vrijeme iz formata HH:mm u minute.
     * @param time string koji predstavlja vrijeme (npr. "12:30")
     * @return broj minuta od ponoći
     */
    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * Pronalazi top K najbržih ruta između početnog i odredišnog čvora.
     * Rute se rangiraju po ukupnom vremenu dolaska na odredište, uz poštovanje vremena presjedanja.
     *
     * @param start početni čvor/stanica
     * @param goal odredišni čvor/stanica
     * @param allNodes skup svih čvorova u mreži
     * @param topK broj najboljih ruta koje treba pronaći
     * @return lista najbržih ruta kao objekti klase {@link PathNode}
     */
    @Override
    public List<PathNode> findTopRoutes(GraphNode start, GraphNode goal, Set<GraphNode> allNodes, int topK) {
        List<PathNode> results = new ArrayList<>();
        PriorityQueue<PathNode> queue = new PriorityQueue<>(Comparator.comparingInt(PathNode::getArrivalTime));
        int startTime = 0;

        queue.add(new PathNode(start, 0, new ArrayList<>(), startTime));

        while (!queue.isEmpty() && results.size() < topK) {
            PathNode current = queue.poll();

            if (current.getNode().equals(goal)) {
                results.add(current);
                continue;
            }

            for (GraphEdge edge : current.getNode().getEdges()) {
                Departure dep = edge.getDeparture();
                if (dep == null) {
                    continue;
                }
                int departureTime = parseTimeToMinutes(dep.getDepartureTime());

                if (departureTime < current.getArrivalTime() + dep.getMinTransferTime())
                    continue;

                int arrivalTime = departureTime + dep.getDuration();

                List<GraphEdge> newPath = new ArrayList<>(current.getPath());
                newPath.add(edge);

                queue.add(new PathNode(edge.getTarget(), 0, newPath, arrivalTime));
            }
        }

        return results;
    }

}
