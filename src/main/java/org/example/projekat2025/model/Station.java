package org.example.projekat2025.model;

/**
 * Apstraktna klasa Station predstavlja opštu transportnu stanicu u našoj aplikaciji.
 * Svaka stanica ima jedinstveni ID.
 *
 * <p>Potklase ove klase treba da definišu konkretan tip stanice,
 * u našem slučaju autobusku i željezničku stanicu.</p>
 *
 *@author Nevena
 *@version 1.0
 */
public abstract class Station {

    private final String id;

    /**
     * Konstruktor klase Station kom se prosljedjuje ID
     *
     * @param id jedinstveni identifikator stanice (npr. A_0_1 ili Z_0_1)
     */
    public Station(String id) {
        this.id = id;
    }

    /**
     * Geter koji vraća ID stanice.
     *
     * @return ID stanice
     */
    public String getId() {
        return id;
    }

    /**
     * Apstraktna metoda koja definiše tip stanice.
     *
     * @return tip stanice: "Bus" ili "Train"
     */
    public abstract String getType();

    /**
     * String reprezentacija stanice u formatu:
     * "Bus station: A_0_1".
     *
     * @return tekstualna reprezentacija stanice
     */
    @Override
    public String toString() {
        return getType() + " station: " + id;
    }
}
