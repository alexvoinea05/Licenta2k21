package com.localgrower.domain.custom.AlgPackage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {

    private String id;

    private String numeProdus;

    private Map<Node, BigDecimal> adjacentNodes = new HashMap<>();

    private BigDecimal distance;

    private List<Node> shortestPath = new LinkedList<>();

    public void addDestination(Node destination, BigDecimal distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(String id, String numeProdus) {
        this.id = id;
        this.numeProdus = numeProdus;
    }

    public String getNumeProdus() {
        return numeProdus;
    }

    public void setNumeProdus(String numeProdus) {
        this.numeProdus = numeProdus;
    }

    public String getId() {
        return id;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public Map<Node, BigDecimal> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<Node, BigDecimal> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    @Override
    public String toString() {
        return "Node{" + "id='" + id + '\'' + ", numeProdus='" + numeProdus + '\'' + '}';
    }
}
