package org.example.projekat2025.service;

import org.example.projekat2025.model.GraphEdge;
import org.example.projekat2025.model.GraphNode;

import java.util.List;

/**
 * Klasa PathNode predstavlja čvor u procesu pronalaženja putanje.
 * Sadrži informacije o trenutnoj stanici (čvoru), ukupnom trošku, putanji i vremenu dolaska.
 * Koristi se za praćenje stanja algoritma za pretragu ruta.
 *
 * @author Nevena
 * @version 1.0
 */
public class PathNode {

    private GraphNode node;
    private int totalCost;
    private List<GraphEdge> path;
    private int arrivalTime;

    /**
     * Konstruktor klase PathNode
     *
     * @param node trenutni čvor tj. stanica
     * @param totalCost ukupan cijena putanje do trenutnog čvora
     * @param path putanja koja vodi do trenutnog čvora
     * @param arrivalTime vrijeme dolaska u minutama od početka
     */
    public PathNode(GraphNode node, int totalCost, List<GraphEdge> path, int arrivalTime) {
        this.node = node;
        this.totalCost = totalCost;
        this.path = path;
        this.arrivalTime = arrivalTime;
    }

    /**
     * Ukupno trajanje putovanja sabiranjem trajanja svih segmenata
     * @return ukupno trajanje puta u minutama
     */
    public int getTotalDuration() {
        return path.stream()
                .mapToInt(edge -> edge.getDeparture().getDuration())
                .sum();
    }

    /**
     * Ukupna cijena putovanja sabiranjem cijena svih segmenata
     * @return ukupna cijena putanje
     */
    public int getTotalPrice() {
        return path.stream()
                .mapToInt(edge -> edge.getDeparture().getPrice())
                .sum();
    }

    /**
     * Računa broj presjedanja
     * @return broj presijedanja na putanji
     */
    public int getTotalTransfers() {
        if (path == null || path.size() < 2) return 0;

        int transfers = 0;
        GraphEdge prevEdge = path.get(0);

        for (int i = 1; i < path.size(); i++) {
            GraphEdge currEdge = path.get(i);

            boolean transportChanged = prevEdge.getTransportType() != currEdge.getTransportType();
            boolean stationChanged = !prevEdge.getTarget().equals(currEdge.getTarget());

            if (transportChanged || stationChanged) {
                transfers++;
            }
            prevEdge = currEdge;
        }
        return transfers;
    }


    public GraphNode getNode() {
        return node;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public List<GraphEdge> getPath() {
        return path;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

}
