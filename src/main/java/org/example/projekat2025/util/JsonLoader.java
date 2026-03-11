package org.example.projekat2025.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projekat2025.model.*;

import java.io.File;
import java.util.*;

/**
 * Pomoćna klasa za učitavanje podataka iz JSON fajla koji opisuje mapu države, stanice i veze između njih.
 *
 * Klasa koristi Jackson biblioteku za parsiranje JSON strukture, i koristi {@link PropertyLoader}
 * za dobijanje putanje do konfiguracionog fajla.
 *
 * JSON fajl treba da sadrži sljedeće sekcije:
 * - <code>countryMap</code>: 2D matricu stringova koja predstavlja geografsku mapu
 * - <code>stations</code>: listu stanica po gradovima
 * - <code>departures</code>: liste polazaka između stanica
 *
 * Klasa vraća podatke u strukturama pogodnim za izgradnju grafa rute.
 *
 * @author Nevena
 * @version 1.0
 */
public class JsonLoader {

    /**
     * Učitava mapu države (gradove i raspored) iz JSON fajla na osnovu putanje zadate u konfiguraciji.
     *
     * @return 2D niz stringova koji predstavlja mapu države
     */
    public static String[][] loadCountryMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(PropertyLoader.getProperty("TRANSPORT_DATA_PATH")));
            JsonNode mapNode = root.get("countryMap");

            int rows = mapNode.size();
            int cols = mapNode.get(0).size();
            String[][] map = new String[rows][cols];

            for (int i = 0; i < rows; i++) {
                JsonNode row = mapNode.get(i);
                for (int j = 0; j < cols; j++) {
                    map[i][j] = row.get(j).asText();
                }
            }
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new String[0][0];
        }
    }

    /**
     * Učitava sve čvorove grafa (stanice i njihove veze) iz JSON fajla.
     *
     * Čvorovi uključuju autobuske i železničke stanice, a veze uključuju i presjedanja unutar grada.
     * @return skup čvorova grafa sa svim vezama (tipa {@link GraphNode})
     */
    public static Set<GraphNode> loadGraphNodes() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(PropertyLoader.getProperty("TRANSPORT_DATA_PATH")));

            JsonNode stationsNode = root.get("stations");
            if (stationsNode == null || !stationsNode.isArray())
                return Collections.emptySet();

            Map<String, GraphNode> nodeMap = new HashMap<>();
            Map<String, String> cityToBusStationId = new HashMap<>();
            Map<String, String> cityToTrainStationId = new HashMap<>();

            for (JsonNode stationNode : stationsNode) {
                String city = stationNode.get("city").asText();
                String busId = stationNode.get("busStation").asText();
                String trainId = stationNode.get("trainStation").asText();

                cityToBusStationId.put(city, busId);
                cityToTrainStationId.put(city, trainId);

                BusStation busStation = new BusStation(busId);
                GraphNode busNode = new GraphNode(busStation, GraphNode.StationType.BUS);
                nodeMap.put(busId, busNode);

                TrainStation trainStation = new TrainStation(trainId);
                GraphNode trainNode = new GraphNode(trainStation, GraphNode.StationType.TRAIN);
                nodeMap.put(trainId, trainNode);
            }

            for (String city : cityToBusStationId.keySet()) {
                String busId = cityToBusStationId.get(city);
                String trainId = cityToTrainStationId.get(city);

                GraphNode busNode = nodeMap.get(busId);
                GraphNode trainNode = nodeMap.get(trainId);

                if (busNode != null && trainNode != null) {
                    int walkingTransferTime = PropertyLoader.getIntProperty("DEFAULT_MAX_RESULTS", 3);

                    busNode.addEdge(new GraphEdge(trainNode, walkingTransferTime));
                    trainNode.addEdge(new GraphEdge(busNode, walkingTransferTime));
                }
            }

            JsonNode edgesNode = root.get("departures");
            if (edgesNode != null && edgesNode.isArray()) {
                for (JsonNode edgeNode : edgesNode) {
                    String from = edgeNode.get("from").asText();
                    String to = edgeNode.get("to").asText();

                    GraphNode fromNode = nodeMap.get(from);

                    if (fromNode == null) {
                        System.err.println("Nedostaje čvor za polaznu stanicu: " + from);
                        continue;
                    }

                    String cityKey = to;

                    String busStationId = cityToBusStationId.get(cityKey);
                    String trainStationId = cityToTrainStationId.get(cityKey);

                    if (busStationId == null && trainStationId == null) {
                        System.err.println("Nedostaju stanice za grad odredišta: " + to);
                        continue;
                    }

                    String type = edgeNode.get("type").asText();
                    String departureTime = edgeNode.get("departureTime").asText();
                    int duration = edgeNode.get("duration").asInt();
                    int price = edgeNode.get("price").asInt();
                    int minTransferTime = edgeNode.get("minTransferTime").asInt();

                    Departure departure = new Departure(type, from, to, departureTime, duration, price, minTransferTime);

                    if (busStationId != null) {
                        GraphNode busNode = nodeMap.get(busStationId);
                        if (busNode != null) {
                            GraphEdge edge = new GraphEdge(busNode, departure, minTransferTime,
                                    type.equalsIgnoreCase("bus") ? GraphEdge.TransportType.BUS : GraphEdge.TransportType.TRAIN);
                            fromNode.addEdge(edge);
                        }
                    }

                    if (trainStationId != null) {
                        GraphNode trainNode = nodeMap.get(trainStationId);
                        if (trainNode != null) {
                            GraphEdge edge = new GraphEdge(trainNode, departure, minTransferTime,
                                    type.equalsIgnoreCase("bus") ? GraphEdge.TransportType.BUS : GraphEdge.TransportType.TRAIN);
                            fromNode.addEdge(edge);
                        }
                    }
                }
            }
            return new HashSet<>(nodeMap.values());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }
}

