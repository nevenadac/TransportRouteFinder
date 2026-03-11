package org.example.projekat2025.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Klasa Receipt predstavlja račun koji se izdaje korisniku prilikom kupovine karte.
 * Sadrži informacije o relaciji, ukupnom vremenu putovanja, ukupnoj cijeni
 * te datumu i vremenu izdavanja.
 * Može se serijalizovati i sačuvati u tekstualni ili binarni fajl.
 *
 *@author Nevena
 *@version 1.0
 */
public class Receipt implements Serializable {

    private static final long serialVersionUID = 1L;

    private String relation;
    private int totalTime;
    private int totalPrice;
    private LocalDateTime dateTime;

    /**
     * Konstuktor klase Receipt
     *
     * @param relation relacija rute (npr. A_0_0 -> G_3_4)
     * @param totalTime ukupno vrijeme trajanja u minutama
     * @param totalPrice ukupna cijena u KM
     * @param dateTime datum i vrijeme kada je račun izdat
     */
    public Receipt(String relation, int totalTime, int totalPrice, LocalDateTime dateTime) {
        this.relation = relation;
        this.totalTime = totalTime;
        this.totalPrice = totalPrice;
        this.dateTime = dateTime;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Čuva podatke o računu u tekstualni fajl u čitljivom formatu.
     *
     * @param file fajl u koji čuvamo račun
     * @throws IOException ukoliko dođe do greške prilikom pisanja u fajl
     */
    public void saveToTextFile(File file) throws IOException {
        try(PrintWriter pw = new PrintWriter(file)) {
            pw.println("-------------RECEIPTS-------------");
            pw.println("Relation: " + relation);
            pw.println("Total time: " + totalTime + "min");
            pw.println("Total price: " + totalPrice + "KM");
            pw.println("Date and Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            pw.println("__________________________________");
        }
    }

    /**
     * Serijalizuje objekat i snima ga u fajl.
     *
     * @param file fajl u koji se objekat snima
     * @throws IOException ako dođe do greške prilikom serijalizacije
     */
    public void saveToSerializedFile(File file) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        }
    }
}
