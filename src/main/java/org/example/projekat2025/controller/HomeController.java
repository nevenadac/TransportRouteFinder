package org.example.projekat2025.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.projekat2025.model.*;
import org.example.projekat2025.service.*;
import org.example.projekat2025.util.JsonLoader;
import org.example.projekat2025.util.PropertyLoader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Kontroler za glavnu scenu JavaFX aplikacije koja prikazuje mapu sa gradovima, omogućava korisniku da bira početnu i
 * krajnju lokaciju, izabere kriterijum za pretragu, pokrene pronalaženje optimalne rute i prikaže top 5 ruta sa
 * vizualizacijom i tabelarnim prikazom.
 *
 * Ova klasa takođe prikazuje statistiku ukupno prodatih karata i ukupnog prihoda,
 * i omogućava korisniku da izađe iz aplikacije.
 *
 * Funkcionalnosti:
 * - Učitavanje mape iz JSON fajla
 * - Interaktivna selekcija gradova kao dugmadi
 * - Vizualizacija pronađene rute na mapi
 * - Animacija rute korak po korak
 * - Prikaz top 5 ruta
 * - Prikaz statistike karata
 *
 * @author Nevena
 * @version 1.0
 */
public class HomeController {

    @FXML
    private Label lblTotalTickets, lblTotalIncome;
    @FXML
    private GridPane mapGrid;
    @FXML
    private RadioButton criterionTime, criterionPrice, criterionTransfer;

    private Button startButton = null;
    private Button endButton = null;
    private Set<GraphNode> allNodes;
    private Map<String, GraphNode> stationMap;

    private List<PathNode> top5routes;

    private ToggleGroup criteriaGroup;

    /** Zatvara aplikaciju kada korisnik klikne dugme "Done" */
    @FXML
    private void onDoneClicked() {
        Platform.exit();
        System.exit(0);
    }

    public void showRouteTable(List<GraphEdge> path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekat2025/table_view.fxml"));
            Parent root = loader.load();

            TableViewController controller = loader.getController();
            controller.setTop5Routes(top5routes);
            controller.setRoute(path);

            Stage stage = new Stage();
            stage.setTitle("Optimal route - details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Platform.runLater(stage::showAndWait);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Poziva se pri inicijalizaciji kontrolera.
     * Učitava mapu, stanice, postavlja dugmad, statistiku i grupiše kriterijume.
     */
    @FXML
    public void initialize(){

        loadStatistics();

        String[][] countryMap = JsonLoader.loadCountryMap();

        this.allNodes = JsonLoader.loadGraphNodes();
        this.stationMap = allNodes.stream()
                .collect(Collectors.toMap(n -> n.getStation().getId(), n -> n));

        for (int i = 0; i < countryMap.length; i++) {
            for (int j = 0; j < countryMap[i].length; j++) {
                String cityName = countryMap[i][j];
                Button cityButton = new Button(cityName);
                cityButton.setMinSize(50,50);
                cityButton.setStyle("-fx-font-size: 9px; -ft-text-alignment: center;");
                cityButton.setOnAction(e -> handleCityButtonClick(cityButton));
                mapGrid.add(cityButton, j, i);
            }
        }

        criteriaGroup = new ToggleGroup();
        criterionTime.setToggleGroup(criteriaGroup);
        criterionPrice.setToggleGroup(criteriaGroup);
        criterionTransfer.setToggleGroup(criteriaGroup);
    }

    /**
     * Učitava i prikazuje statistiku broja karata i ukupnog prihoda.
     */
    private void loadStatistics() {
        List<Receipt> receipts = ReceiptService.loadAllReceipts();

        int count = receipts.size();
        int totalIncome = receipts.stream()
                .mapToInt(Receipt::getTotalPrice)
                .sum();

        lblTotalTickets.setText("Total number of tickets sold: " + count);
        lblTotalIncome.setText("Total income: " + totalIncome + " KM");
    }

    /**
     * Obradjuje klik na dugme grada - omogućava izbor polaznog i odredišnog grada.
     */
    private void handleCityButtonClick(Button clickedButton) {
        if (startButton == null) {
            startButton = clickedButton;
            startButton.setStyle("-fx-background-color: lightgreen;");
        } else if (endButton == null && clickedButton != startButton) {
            endButton = clickedButton;
            endButton.setStyle("-fx-background-color: lightcoral;");
        } else {
            // resetuj prethodni izbor i ponovo postavi start na novi klik
            if (startButton != null) startButton.setStyle("");
            if (endButton != null) endButton.setStyle("");
            startButton = clickedButton;
            endButton = null;
            startButton.setStyle("-fx-background-color: lightgreen;");
        }
    }

    /**
     * Pokreće generisanje optimalne rute na osnovu izabranog kriterijuma.
     */
    @FXML
    private void onGenerateButton() {

        if (startButton == null || endButton == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please select a departure and destination location.");
            alert.showAndWait();
            return;
        }

        String startCity = startButton.getText();
        String endCity = endButton.getText();

        RouteFinderInterface strategy = null;

        if (criterionTime.isSelected()) {
            strategy = new FastestRouteFinder();
        } else if (criterionPrice.isSelected()) {
            strategy = new CheapestRouteFinder();
        } else if (criterionTransfer.isSelected()) {
            strategy = new LeastTransferRouteFinder();
        }

        if (strategy == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please select a criterion.");
            alert.showAndWait();
            return;
        }

        String startBusId = startCity.replace("G", "A");
        String startTrainId = startCity.replace("G", "Z");
        String endBusId = endCity.replace("G", "A");
        String endTrainId = endCity.replace("G", "Z");

        GraphNode startBusNode = stationMap.get(startBusId);
        GraphNode startTrainNode = stationMap.get(startTrainId);
        GraphNode endBusNode = stationMap.get(endBusId);
        GraphNode endTrainNode = stationMap.get(endTrainId);

        List<PathNode> possibleRoutes = new ArrayList<>();

        if (startBusNode != null && endBusNode != null) {
            possibleRoutes.addAll(strategy.findTopRoutes(startBusNode, endBusNode, allNodes, 5));
        }
        if (startBusNode != null && endTrainNode != null) {
            possibleRoutes.addAll(strategy.findTopRoutes(startBusNode, endTrainNode, allNodes, 5));
        }
        if (startTrainNode != null && endBusNode != null) {
            possibleRoutes.addAll(strategy.findTopRoutes(startTrainNode, endBusNode, allNodes, 5));
        }
        if (startTrainNode != null && endTrainNode != null) {
            possibleRoutes.addAll(strategy.findTopRoutes(startTrainNode, endTrainNode, allNodes, 5));
        }

        if (possibleRoutes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setContentText("No routes were found for that day!");
            alert.showAndWait();
            return;
        }

        PathNode bestRoute = possibleRoutes.stream()
                .min((r1, r2) -> Integer.compare(r1.getTotalCost(), r2.getTotalCost()))
                .orElse(null);

        if (bestRoute == null) {
            System.out.println("No valid routes were found.");
            return;
        }

        System.out.println("Best route found:");
        for (GraphEdge edge : bestRoute.getPath()) {
            Departure d = edge.getDeparture();
            System.out.println("- " + d.getFrom() + " -> " + d.getTo() +
                    " (" + d.getPrice() + " KM, " + d.getType() + ")");
        }

        int maxResults = PropertyLoader.getIntProperty("DEFAULT_TRANSFER_TIME",15);
        this.top5routes = possibleRoutes.size() > maxResults
                ? possibleRoutes.subList(0, maxResults)
                : possibleRoutes;

        highlightRoute(bestRoute.getPath());
        animateRoute(bestRoute.getPath(), () -> {
            resetMapStyles();
            showRouteTable(bestRoute.getPath());
        });
    }

    /**
     * Resetuje sve stilove dugmadi na mapi na podrazumijevani stil.
     * Startna stanica se boji zeleno, a krajnja crveno.
     */
    private void resetMapStyles() {
        for(Node node : mapGrid.getChildren()) {
            if(node instanceof Button) {
                node.setStyle("");
            }
        }
        if(startButton != null)
            startButton.setStyle("-fx-background-color: lightgreen;");
        if(endButton != null)
            endButton.setStyle("-fx-background-color: lightcoral;");
    }

    /**
     * Parsira koordinate (red, kolona) iz ID-a stanice koji je u formatu npr. A_2_3.
     *
     * @param stationId ID stanice u formatu "A_row_col"
     * @return niz dužine 2 gdje je [0] red, a [1] kolona
     */
    private int[] parseCoordinatesFromId(String stationId) {
        String[] parts = stationId.split("_");
        int row = Integer.parseInt(parts[1]);
        int col = Integer.parseInt(parts[2]);
        return new int[]{row, col};
    }

    /**
     * Pronalazi čvor (Node) u {@link GridPane} na osnovu rednog i kolonijalnog indeksa.
     *
     * @param row redni indeks (npr. 2)
     * @param column kolona (npr. 3)
     * @param gridPane GridPane u kome se traži dugme
     * @return čvor na zadatoj poziciji ili null ako nije pronađen
     */
    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for(Node node : gridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);
            if(rowIndex == null)
                rowIndex = 0;
            if(colIndex == null)
                colIndex = 0;

            if(rowIndex == row && colIndex == column)
                return node;
        }
        return null;
    }

    /**
     * Ističe sve stanice duž date putanje žutom bojom.
     * Startna i krajnja stanica zadržavaju svoje boje.
     *
     * @param path lista segmenata rute koji se vizuelno označavaju na mapi
     */
    private void highlightRoute(List<GraphEdge> path) {
        for (GraphEdge edge : path) {
            String fromId = edge.getDeparture().getFrom();
            String toId = edge.getDeparture().getTo();

            int[] fromCoords = parseCoordinatesFromId(fromId);
            int[] toCoords = parseCoordinatesFromId(toId);

            Button fromBtn = (Button) getNodeByRowColumnIndex(fromCoords[0], fromCoords[1], mapGrid);
            Button toBtn = (Button) getNodeByRowColumnIndex(toCoords[0], toCoords[1], mapGrid);

            if (fromBtn != null && fromBtn != startButton && fromBtn != endButton) {
                fromBtn.setStyle("-fx-background-color: yellow;");
            }
            if (toBtn != null && toBtn != startButton && toBtn != endButton) {
                toBtn.setStyle("-fx-background-color: yellow;");
            }
        }
    }

    /**
     * Animira prikaz putanje kroz mrežu koristeći vremenski raspored bojenja dugmadi.
     * Dugmad na putanji se postepeno boje crvenom bojom kako bi simulirala kretanje.
     *
     * @param path lista segmenata (grana) koje treba prikazati kao animaciju
     * @param onFinished akcija koja će se izvršiti kada se animacija završi
     */
    private void animateRoute(List<GraphEdge> path, Runnable onFinished) {
        List<Button> buttonsInRoute = new ArrayList<>();

        GraphEdge firstEdge = path.get(0);
        int[] startCoords = parseCoordinatesFromId(firstEdge.getDeparture().getFrom());
        Button startBtn = (Button) getNodeByRowColumnIndex(startCoords[0], startCoords[1], mapGrid);
        buttonsInRoute.add(startBtn);

        for (GraphEdge edge : path) {
            int[] coords = parseCoordinatesFromId(edge.getDeparture().getTo());
            Button btn = (Button) getNodeByRowColumnIndex(coords[0], coords[1], mapGrid);
            buttonsInRoute.add(btn);
        }

        Timeline timeline = new Timeline();
        for (int i = 0; i < buttonsInRoute.size(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 1000), e ->
            {
                for (Button b : buttonsInRoute) {
                    if (b != startButton && b != endButton) b.setStyle("-fx-background-color: yellow;");
                }
                Button current = buttonsInRoute.get(index);
                if (current != startButton && current != endButton) {
                    current.setStyle("-fx-background-color: red;");
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setOnFinished(event -> {
            if(onFinished != null)
                onFinished.run();
        });

        timeline.play();
    }
}
