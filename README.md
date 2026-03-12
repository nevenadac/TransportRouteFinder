# eTransport – Transport Route Finder

**Author:** Nevena Dačević  
**Language:** Java  
**Date:** 2026

---

# Project Description

**eTransport** is a GUI application that helps users find the optimal travel route between cities within a country, using a combination of **bus** and **train** transport.

The application allows users to optimize routes according to **three criteria**:

- **Shortest Travel Time**  
- **Lowest Price**  
- **Least Number of Transfers**  

Each criterion uses a specific algorithm to compute the optimal route, taking into account departure times, travel durations, and minimum transfer times.

---

# Features

- Find routes between cities using bus and train connections  
- Optimize routes based on:
  - **Shortest Travel Time**
  - **Lowest Price**
  - **Least Number of Transfers**  
- GUI interface for easy selection of cities and criteria  
- Validation of departure and transfer times  
- Avoids duplicate routes  
- Priority queues and efficient graph search algorithms for performance  

---

# Algorithms and Implementation

## Shortest Travel Time

- Implemented in the class `FastestRouteFinder`  
- Uses **Dijkstra's Algorithm** to find the route with the earliest arrival time  
- Considers:
  - Departure times  
  - Duration of each segment (`duration`)  
  - Minimum transfer time at stations (`minTransferTime`)  
- Uses a **PriorityQueue** to expand the path with the earliest arrival first  
- Checks that the next segment's departure is after the current arrival + transfer time  

> Dijkstra's algorithm finds the shortest path from a starting node to all other nodes in a weighted graph. In this application, the edge weight represents travel duration with transfer constraints.

---

## Lowest Price

- Implemented in the class `CheapestRouteFinder`  
- Uses **Dijkstra's Algorithm**, but edge weights represent travel costs  
- Tracks the lowest cost to each node using `costSoFar` map  
- Uses a **PriorityQueue** to sort nodes by total cost so far  
- Checks departure times and transfer validity  

> Ensures the returned route has the lowest total travel cost while respecting departure and transfer times.

---

## Least Number of Transfers

- Implemented in the class `LeastTransferRouteFinder`  
- Uses **Breadth-First Search (BFS)** to find the route with the fewest transfers  
- Nodes are sorted by number of transfers using a **PriorityQueue**  
- Checks for valid departures and possible transfers  
- Avoids duplicate paths using the `generatePathSignature` method  

> BFS guarantees that the first route found to the destination has the minimum number of steps (transfers). This algorithm is simple, efficient, and easily extendable.

---

# Technical Specifications

- **Programming Language:** Java  
- **Platform:** Desktop GUI (Java Swing or JavaFX)  
- **Algorithms:** Dijkstra (time & price), BFS (transfers)  
- **Data Structures:** PriorityQueue, HashMap  
- **Input:** City selection, departure times, optimization criterion  
- **Output:** Optimal route, travel time, cost, and number of transfers  

---

# How to Run

1. Open the project in a Java IDE (e.g., IntelliJ, Eclipse)  
2. Compile and run the main GUI application  
3. Select starting and destination cities  
4. Choose the optimization criterion: **Shortest Time**, **Lowest Price**, or **Least Transfers**  
5. View the optimal route with details  
