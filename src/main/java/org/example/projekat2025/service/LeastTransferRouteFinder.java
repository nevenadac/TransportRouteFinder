package org.example.projekat2025.service;

import org.example.projekat2025.model.*;

import java.util.*;

/**
 * Implementacija interfejsa {@link RouteFinderInterface} koja pronalazi rute
 * između dvije stanice sa najmanjim brojem presjedanja koristeći modifikovani BFS.
 * Presjedanje se broji kao prelazak sa jedne stanice na drugu bez obzira na tip prevoza.
 *
 * Rute se rangiraju prema broju presjedanja (broj segmenata), uz poštovanje
 * vremena polaska i minimalnog vremena za presjedanje.
 *
 * Duplikati ruta se izbjegavaju poređenjem potpisa (niz stanica kroz koje ruta prolazi).
 *
 * @author Nevena
 * @version 1.0
 */
public class LeastTransferRouteFinder implements RouteFinderInterface {

    /**
     * Pomoćna metoda koja parsira vrijeme iz formata HH:mm u minute od ponoći.
     *
     * @param time string koji predstavlja vrijeme (npr. "12:30")
     * @return broj minuta od ponoći
     */
    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * Pronalazi top K ruta sa najmanjim brojem presjedanja između zadate početne i krajnje stanice.
     * Rute se biraju na osnovu broja presjedanja (dužine putanje), uz poštovanje vremena polaska
     * i minimalnog vremena za presjedanje. Rezultati su bez duplikata.
     *
     * @param start početni čvor/stanica
     * @param goal odredišni čvor/stanica
     * @param allNodes skup svih čvorova u mreži
     * @param topK broj najboljih ruta koje treba pronaći
     * @return lista najkraćih ruta po broju presjedanja
     */
    @Override
    public List<PathNode> findTopRoutes(GraphNode start, GraphNode goal, Set<GraphNode> allNodes, int topK) {
        List<PathNode> results = new ArrayList<>();
        PriorityQueue<PathNode> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.getPath().size()));

        int startTime = 0;
        queue.add(new PathNode(start, 0, new ArrayList<>(), startTime));

        Set<String> visitedPaths = new HashSet<>();

        while (!queue.isEmpty() && results.size() < topK) {
            PathNode current = queue.poll();

            if (current.getNode().equals(goal)) {
                String pathSignature = generatePathSignature(current.getPath());
                if (!visitedPaths.contains(pathSignature)) {
                    visitedPaths.add(pathSignature);
                    results.add(current);
                }
                continue;
            }

            for (GraphEdge edge : current.getNode().getEdges()) {
                Departure dep = edge.getDeparture();
                if (dep == null) {
                    continue;
                }
                int departureTime = parseTimeToMinutes(dep.getDepartureTime());

                if (departureTime < current.getArrivalTime() + dep.getMinTransferTime()) {
                    continue;
                }

                int arrivalTime = departureTime + dep.getDuration();
                List<GraphEdge> newPath = new ArrayList<>(current.getPath());
                newPath.add(edge);

                queue.add(new PathNode(edge.getTarget(), 0, newPath, arrivalTime));
            }
        }

        return results;
    }

    // Pomoćna metoda za izbjegavanje duplikata ruta (npr. isti niz stanica)
    private String generatePathSignature(List<GraphEdge> path) {
        StringBuilder sb = new StringBuilder();
        for (GraphEdge edge : path) {
            sb.append(edge.getDeparture().getFrom()).append("->").append(edge.getTarget().getStation()).append("|");
        }
        return sb.toString();
    }
}
