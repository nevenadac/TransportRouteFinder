package org.example.projekat2025.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa {@code GraphNode} predstavlja jedan čvor u grafu transportne mreže.
 * Svaki čvor sadrži referencu na jednu stanicu {@link Station} i
 * listu izlaznih veza {@link GraphEdge}.
 */
public class GraphNode {

    /**
     * Enum koji opisuje tip stanice u ovom čvoru.
     */
    public enum StationType {
        BUS, TRAIN,
    }
    private Station station;
    private StationType stationType;
    private List<GraphEdge> edges;


    /**
     * Konstruktor koji inicijalizuje čvor sa stanicom i njenim tipom.
     *
     * @param station objekat stanice koji je sadržan u ovom čvoru
     * @param stationType tip stanice
     */
    public GraphNode(Station station, StationType stationType) {
        this.station = station;
        this.stationType = stationType;
        this.edges = new ArrayList<>();
    }

    public StationType getStationType() {
        return stationType;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public void addEdge(GraphEdge edge) {
        edges.add(edge);
    }

    /**
     * @return tekstualni prikaz čvora (npr. A_0_1)
     */
    @Override
    public String toString() {
        return station.getId();
    }

    /**
     * Dva čvora su jednaka ako imaju istu stanicu (po ID-u)
     * @param obj drugi objekat za poređenje
     * @return true ako su čvorovi jednaki po ID-u stanice
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GraphNode other = (GraphNode) obj;
        return station.getId().equals(other.station.getId());
    }

    /**
     * heš-kod zasnovan na ID-u stanice
     * @return
     */
    @Override
    public int hashCode() {
        return station.getId().hashCode();
    }

}
