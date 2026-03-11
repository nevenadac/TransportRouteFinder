package org.example.projekat2025.service;

import org.example.projekat2025.model.*;

import java.util.List;
import java.util.Set;

/**
 * Interfejs koji definiše strategiju za pronalaženje optimalnih ruta između dvije stanice.
 * Implementacije ovog interfejsa mogu se bazirati na različitim kriterijumima pretrage,
 * kao što su najjeftinija, najbrža ruta ili ruta sa najmanje presjedanja.
 *
 * @author Nevena
 * @version 1.0
 */
public interface RouteFinderInterface {

    /**
     * Pronalazi top K najboljih ruta između početnog i odredišnog čvora, na osnovu implementiranog kriterijuma.
     * @param start početni čvor
     * @param goal odredišni čvor
     * @param allNodes skup svih čvorova u grafu
     * @param topK broj najboljih ruta koje treba pronaći
     * @return lista najboljih ruta predstavljena objektima klase {@link PathNode}
     */
    List<PathNode> findTopRoutes(GraphNode start, GraphNode goal, Set<GraphNode> allNodes, int topK);
}
