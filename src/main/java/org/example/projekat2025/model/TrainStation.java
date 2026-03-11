package org.example.projekat2025.model;

/**
 * Klasa TrainStation predstavlja željezničku stanicu u našoj aplikaciji.
 * Nasljeđuje apstraktnu klasu Station i implementira njen tip kao "Train".
 *
 * @author Nevena
 * @version 1.0
 */
public class TrainStation extends Station {

    /**
     * Konstruktor klase TrainStation
     * @param trainId jedinstveni identifikator željezničke stanice (npr. Z_0_1)
     */
    public TrainStation(String trainId) {
        super(trainId);
    }

    /**
     * {@inheritDoc}
     *
     * @return tekst "Train"
     */
    @Override
    public String getType() {
        return "Train";
    }
}
