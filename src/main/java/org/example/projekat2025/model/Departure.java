package org.example.projekat2025.model;

/**
 * Klasa Departure predstavlja jedan polazak između dvije stanice
 * (autobuski ili željeznički), sa svim detaljima
 *
 * @author Nevena
 * @version 1.0
 */
public class Departure {

    private String type;
    private String from;
    private String to;
    private String departureTime;
    private int duration;
    private int price;
    private int minTransferTime;

    /**
     * Konstruktor koji inicijalizuje sve podatke o polasku.
     *
     * @param type tip prevoza ("bus" ili "train")
     * @param from ID početne stanice
     * @param to ID odredišne stanice
     * @param departureTime vrijeme polaska u formatu HH:mm
     * @param duration trajanje putovanja u minutama
     * @param price cijena karte u KM
     * @param minTransferTime minimalno vrijeme za presjedanje u minutama
     */
    public Departure(String type, String from, String to, String departureTime,
                     int duration, int price, int minTransferTime) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.departureTime = departureTime;
        this.duration = duration;
        this.price = price;
        this.minTransferTime = minTransferTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMinTransferTime() {
        return minTransferTime;
    }

    public void setMinTransferTime(int minTransferTime) {
        this.minTransferTime = minTransferTime;
    }

    /**
     * Tekstualna reprezentacija Departure objekta
     * @return detalji o polasku
     */
    @Override
    public String toString() {
        return "Departure details:" +
                "\nType: " + type +
                "\nFrom: " + from +
                "\nTo: " + to +
                "\nDeparture time: " + departureTime +
                "\nDuration: " + duration + " min" +
                "\nPrice: " + price + "KM" +
                "\nMinimum transfer time: " + minTransferTime + " min";
    }
}
