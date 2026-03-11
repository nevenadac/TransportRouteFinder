package org.example.projekat2025.service;

import org.example.projekat2025.model.Receipt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa Receipt je servisna klasa za rad sa računima.
 * Omogućava čuvanje i učitavanje računa iz tekstualnih i serijalizovanih fajlova
 *
 * Računi se čuvaju u posebnom folderu "receipts".
 *
 * @author Nevena
 * @version 1.0
 */
public class ReceiptService {

    private static final String RECEIPTS_FOLDER = "receipts";

    /**
     * Kuva račun u dva formata:
     * 1) kao tekstualni fajl (.txt)
     * 2) kao serijalizovani objekat (.ser).
     *
     * @param relation relacija rute (npr. A_0_0 -> G_3_4)
     * @param totalDuration ukupno trajanje putovanja u minutama
     * @param totalPrice ukupna cijena putovanja
     * @throws IOException ako dođe do greške prilikom upisa u fajl
     */
    public static void saveReceipt(String relation, int totalDuration, int totalPrice) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss"));

        Receipt receipt = new Receipt(relation, totalDuration, totalPrice, now);

        File receiptDir = new File(RECEIPTS_FOLDER);
        if(!receiptDir.exists()) {
            receiptDir.mkdir();
        }

        File txtFile = new File(receiptDir, "receipts_" + dateTime + ".txt");
        File serFile = new File(receiptDir, "receipts_" + dateTime + ".ser");

        receipt.saveToTextFile(txtFile);
        receipt.saveToSerializedFile(serFile);
    }

    /**
     * Učitava sve serijalizovane račune (.ser fajlove) iz foldera "receipts".
     * Ako folder ne postoji ili je prazan vraća praznu listu
     *
     * @return lista učitanih računa
     */
    public static List<Receipt> loadAllReceipts() {
        File folder = new File(RECEIPTS_FOLDER);
        List<Receipt> receipts = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            return receipts;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null) return receipts;

        for (File file : files) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Receipt receipt = (Receipt) in.readObject();
                receipts.add(receipt);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return receipts;
    }
}
