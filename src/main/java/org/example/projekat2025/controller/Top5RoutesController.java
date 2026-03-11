package org.example.projekat2025.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.example.projekat2025.service.PathNode;
import org.example.projekat2025.service.ReceiptService;

import java.io.IOException;
import java.util.List;

/**
 * Kontroler koji prikazuje top 5 najboljih ruta prema izabranom kriterijumu.
 * Omogućava korisniku da vidi trajanje, cijenu i broj presjedanja za svaku rutu
 * i da kupi kartu za željenu rutu direktno iz tabele.
 *
 * @author Nevena
 * @version 1.0
 */
public class Top5RoutesController {

    @FXML
    private TableView<PathNode> routesTable;
    @FXML
    private TableColumn<PathNode, Integer> totalTime, totalPrice, totalTransferNumber;
    @FXML
    private TableColumn<PathNode, Void> actionColumn;

    private List<PathNode> routes;

    /**
     * Inicijalizuje kolone i dugme za kupovinu.
     */
    @FXML
    public void initialize() {

        totalTime.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTotalDuration()).asObject());
        totalPrice.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTotalPrice()).asObject());
        totalTransferNumber.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTotalTransfers()).asObject());

        addBuyButtonToTable();
    }

    /**
     * Postavlja listu ruta koje će biti prikazane u tabeli.
     *
     * @param routes lista ruta (objekata klase PathNode)
     */
    public void setRoutes(List<PathNode> routes) {
        this.routes = routes;
        if (routesTable != null) {
            populateTable();
        } else {
            System.out.println("There is not routes");
        }
    }

    /**
     * Popunjava tabelu sa podacima o rutama.
     */
    private void populateTable() {
        if(routes == null)
            return;
        ObservableList<PathNode> data = FXCollections.observableArrayList(routes);
        routesTable.setItems(data);
    }

    /**
     * Dodaje kolonu sa dugmetom "Buy ticket" koje omogućava korisniku da kupi kartu za određenu rutu.
     */
    private void addBuyButtonToTable() {
        Callback<TableColumn<PathNode, Void>, TableCell<PathNode, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PathNode, Void> call(final TableColumn<PathNode, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Buy ticket");

                    {
                        btn.setOnAction(event -> {
                            PathNode selectedRoute = getTableView().getItems().get(getIndex());
                            if(selectedRoute == null || selectedRoute.getPath().isEmpty())
                                return;

                            String from = selectedRoute.getPath().get(0).getDeparture().getFrom();
                            String to = selectedRoute.getPath().get(selectedRoute.getPath().size() - 1).getDeparture().getTo();
                            int totalDuration = selectedRoute.getPath().stream().mapToInt((e->e.getDeparture().getDuration())).sum();
                            int totalPrice = selectedRoute.getPath().stream().mapToInt(e->e.getDeparture().getPrice()).sum();
                            String relation = from + " -> " + to;

                            try {
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
                        });

                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }
}
