package org.example.projekat2025.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Pomoćna klasa za učitavanje konfiguracionih vrijednosti iz fajla
 * <b>config.properties</b>.
 *
 * Ova klasa automatski učitava konfiguraciju prilikom prvog pristupa i omogućava
 * dohvatanje vrijednosti po ključu kao string ili kao cijeli broj (sa podrazumijevanom vrijednošću).
 *
 * @author Nevena
 * @version 1.0
 */
public class PropertyLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("config.properties nije pronađen u resources folderu!");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Dohvata vrijednosti iz properties fajla na osnovu proslijeđenog ključa
     * @param key ključ koji se koristi za traženje vrijednosti
     * @return vrijednost kao string, ili <code>null</code> ako ključ nije pronađen
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Dohvata vrijednosti iz properties fajla kao cijeli broj.
     * Ako vrijednost nije validan broj ili ne postoji, vraća se podrazumijevana vrijednost.
     *
     * @param key ključ koji se koristi za traženje vrijednosti
     * @param defaultValue vrijednost koja se vraća ako ključ nije pronađen ili nije broj
     * @return cijeli broj koji odgovara vrijednosti iz properties fajla, ili podrazumijevana vrijednost
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}

