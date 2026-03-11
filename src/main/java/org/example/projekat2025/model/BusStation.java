package org.example.projekat2025.model;

/**
 * Klasa BusStation predstavlja autobusku stanicu u našoj aplikaciji.
 * Nasljeđuje apstraktnu klasu Station i implementira njen tip kao "Bus".
 *
 *@author Nevena
 *@version 1.0
 */
public class BusStation extends Station {

    /**
     * Konstruktor klase BusStation
     * @param busId jedinstveni identifikator autobuske stanice (npr. A_0_1)
     */
    public BusStation(String busId) {
        super(busId);
    }

    /**
     * {@inheritDoc}
     *
     * @return tekst "Bus"
     */
    @Override
    public String getType() {
        return "Bus";
    }
}
