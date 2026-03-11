package org.example.projekat2025.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.projekat2025.util.PropertyLoader;
import org.example.projekat2025.util.TransportDataGenerator;

/**
 * Kontroler za početni ekran JavaFX aplikacije.
 *
 * Omogućava korisniku unos dimenzija matrice (n × m) i generisanje transportnih podataka
 * koji se potom snimaju u JSON fajl na osnovu konfiguracije.
 * Nakon generisanja, korisnik se preusmjerava na glavni prikaz (home.fxml).
 *
 * Ograničenja za unos dimenzija su od 1 do 50.
 *
 * @author Nevena
 * @version 1.0
 */
public class HelloController {

    /** Tekstualna polja za unos vrijednosti n i m */
    @FXML
    private TextField nTextField, mTextField;
    @FXML
    /** Dugme za pokretanje generisanja podataka */
    private Button buttonGenerate;

    /**
     * Prikazuje upozorenje u vidu dijaloga.
     *
     * @param title naslov dijaloga
     * @param message poruka koja se prikazuje korisniku
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Obrada pritiska na dugme "Generate".
     *
     * Metoda validira unos korisnika, kreira instancu generatora transportnih podataka,
     * generiše podatke i snima ih u JSON fajl.
     * Nakon toga učitava novu scenu iz <code>home.fxml</code>.
     */
    @FXML
    public void handleGenerate() {
        try {
            int n = Integer.parseInt(nTextField.getText());
            int m = Integer.parseInt(mTextField.getText());

            if (n < 1 || n > 50 || m < 1 || m > 50) {
                showAlert("Incorrent Entry! ", "Numbers must be in the range 1 to 50!");
                return;
            }

            TransportDataGenerator generator = new TransportDataGenerator(n, m);
            var data = generator.generateData();
            generator.saveToJson(data, PropertyLoader.getProperty("TRANSPORT_DATA_PATH"));

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projekat2025/home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) buttonGenerate.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("Home");
            stage.centerOnScreen();
            stage.show();

        } catch (NumberFormatException e) {
            showAlert("Incorrent Entry! ","Please enter n and m correctly. The variables n and m are numbers in the range 1 to 50.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}