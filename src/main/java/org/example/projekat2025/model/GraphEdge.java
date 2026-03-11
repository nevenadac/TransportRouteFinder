package org.example.projekat2025.model;

/**
 * Klasa {@code GraphEdge} predstavlja vezu (granu) između čvorova u grafu prevoza.
 * Svaka veza sadrži ciljnu stanicu {@link GraphNode}, informacije o polasku {@link Departure},
 * vrijeme transfera i tip prevoza.
 *
 * @author Nevena
 * @version 1.0
 */

public class GraphEdge {

    /**
     * Enum koji predstavlja tip transporta za ovu granu
     */
    public enum TransportType {
        /** Autobuski prevoz */
        BUS,
        /** Željeznički prevoz */
        TRAIN,
        /** Pješačenje (transfer između stanica) */
        WALK
    }

    private GraphNode target;
    private Departure departure;
    private int transferTime;
    private TransportType transportType;

    /**
     * Konstruktor GraphEgde klase koji ima sve parametre
     *
     * @param target ciljni čvor (stanica) na koju vodi ova grana
     * @param departure podaci o polasku za ovu granu
     * @param transferTime vrijeme čekanja/transfera u minutama
     * @param transportType tip prevoza
     */
    public GraphEdge(GraphNode target, Departure departure, int transferTime, TransportType transportType) {
        this.target = target;
        this.departure = departure;
        this.transferTime = transferTime;
        this.transportType = transportType;

    }

    /**
     * Konstruktor za granu koja predstavlja transfer hodanjem (WALK),
     * tj. nije definisan polazak.
     * @param target ciljni čvor (stanica) na koju vodi ova grana
     * @param transferTime vrijeme hodanja ili transfera u minutama
     */
    public GraphEdge(GraphNode target, int transferTime) {
        this.target = target;
        this.departure = null;
        this.transferTime = transferTime;
        this.transportType = TransportType.WALK;

    }

    public GraphNode getTarget() {
        return target;
    }

    public void setTarget(GraphNode target) {
        this.target = target;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public int getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(int transferTime) {
        this.transferTime = transferTime;
    }
}
