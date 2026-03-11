package org.example.projekat2025.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.projekat2025.model.GraphEdge;
import org.example.projekat2025.service.PathNode;
import org.example.projekat2025.service.ReceiptService;

import java.io.IOException;
import java.util.List;

/**
 * Kontroler zadužen za prikaz detalja rute u tabelarnom obliku i na Canvas-u.
 * Omogućava korisniku da vizuelno pregleda optimalnu rutu,
 * izvrši kupovinu karte i prikaže top 5 pronađenih ruta.
 *
 * @author Nevena
 * @version 1.0
 */
public class TableViewController {

    @FXML
    private TableView<GraphEdge> routeTable;
    @FXML
    private TableColumn<GraphEdge, String> colFrom, colTo, colType, colDepartureTime;
    @FXML
    private TableColumn<GraphEdge, Integer> colArrivalTime, colPrice;
    @FXML
    private Label lblTotalDuration, lblTotalPrice;
    @FXML
    private Canvas routeCanvas;

    private ObservableList<GraphEdge> routeData = FXCollections.observableArrayList();

    private List<PathNode> top5Routes;

    /**
     * Postavlja top 5 pronađenih ruta koje se mogu prikazati u posebnom prozoru.
     * @param routes lista objekata klase PathNode koji predstavljaju top 5 ruta
     */
    public void setTop5Routes(List<PathNode> routes) {
        this.top5Routes = routes;
    }

    /**
     * Inicijalizacija tabele i povezivanje kolona sa odgovarajućim podacima.
     */
    @FXML
    public void initialize() {
        colFrom.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getDeparture().getFrom()));
        colTo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getDeparture().getTo()));
        colType.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getDeparture().getType()));
        colDepartureTime.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getDeparture().getDepartureTime()));
        colArrivalTime.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
             () -> cellData.getValue().getDeparture().getDuration()));
        colPrice.setCellValueFactory(cellData-> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getDeparture().getPrice()));

        routeTable.setItems(routeData);
        routeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> drawRoute(routeData));
    }

    /**
     * Postavlja rutu koja će se prikazati u tabeli i grafički na Canvas-u.
     * @param path lista ivica koje čine optimalnu rutu
     */
    public void setRoute(List<GraphEdge> path) {
        routeData.setAll(path);
        drawRoute(path);
        updateTotals(path);
    }

    /**
     * Crta vizuelni prikaz rute na Canvas-u.
     * @param path lista ivica koje predstavljaju rutu
     */
    private void drawRoute(List<GraphEdge> path) {
        GraphicsContext gc = routeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, routeCanvas.getWidth(), routeCanvas.getHeight());

        if (path == null || path.isEmpty()) return;

        double x = 80;
        double y = routeCanvas.getHeight() / 2;
        double dx = 100;

        for (GraphEdge edge : path) {
            String label = edge.getDeparture().getDepartureTime() + " - " + edge.getDeparture().getDuration();
            gc.strokeLine(x, y, x + dx, y);
            gc.strokeOval(x - 3, y - 3, 6, 6);
            gc.strokeText(edge.getDeparture().getFrom(), x - 15, y - 10);
            gc.strokeText(label, x + dx / 2 - 20, y + 15);
            x += dx;
        }

        GraphEdge last = path.get(path.size() - 1);
        gc.strokeText(last.getDeparture().getTo(), x - 15, y - 10);
        gc.strokeOval(x - 3, y - 3, 6, 6);
    }

    /**
     * Ažurira ukupno trajanje i cijenu rute i prikazuje ih u Label-ama.
     * @param path lista ivica koje predstavljaju rutu
     */
    private void updateTotals(List<GraphEdge> path) {
        int totalDuration = path.stream()
                .mapToInt(e -> e.getDeparture().getDuration())
                .sum();

        int totalPrice = path.stream()
                .mapToInt(e -> e.getDeparture().getPrice())
                .sum();

        lblTotalDuration.setText(totalDuration + " min");
        lblTotalPrice.setText(totalPrice + " KM");
    }

    /**
     * Otvara novi prozor koji prikazuje top 5 pronađenih ruta.
     */
    @FXML
    private void onTop5Clicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projekat2025/top5.fxml"));
            Parent root = loader.load();

            Top5RoutesController topController = loader.getController();
            topController.setRoutes(top5Routes);

            Stage stage = new Stage();
            stage.setTitle("Top 5 routes");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Omogućava korisniku da kupi kartu za prikazanu rutu i generiše račun.
     */
    @FXML
    private void onBuyTicketClicked() {
        System.out.println("Buy ticket");

        if(routeData == null || routeData.isEmpty()) {
            System.out.println("There is nothing to buy here.");
            return;
        }

            String from = routeData.get(0).getDeparture().getFrom();
            String to = routeData.get(routeData.size() - 1).getDeparture().getTo();
            int totalDuration = routeData.stream().mapToInt((e->e.getDeparture().getDuration())).sum();
            int totalPrice = routeData.stream().mapToInt(e->e.getDeparture().getPrice()).sum();
            String relation = from + " -> " + to;

            try{
            ReceiptService.saveReceipt(relation, totalDuration, totalPrice);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Successful purchase!");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while generating the invoice.");
            alert.showAndWait();
        }

    }
}

