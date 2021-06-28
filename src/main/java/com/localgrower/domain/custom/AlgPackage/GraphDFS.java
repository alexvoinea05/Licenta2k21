package com.localgrower.domain.custom.AlgPackage;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphDFS {

    private Set<Node> nodes = new HashSet<>();

    private BigDecimal cost = BigDecimal.valueOf(Integer.MAX_VALUE);
    private Set<Node> correctPath = new HashSet<>();

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Set<Node> getCorrectPath() {
        return correctPath;
    }

    public void setCorrectPath(Set<Node> correctPath) {
        this.correctPath = correctPath;
    }

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public int getGraphSize() {
        return nodes.size();
    }

    Boolean CheckIfExists(Node son, Set<Node> nodes) {
        for (Node node : nodes) {
            if (son.getNumeProdus().equals(node.getNumeProdus())) {
                return true;
            }
        }

        return false;
    }

    Boolean GotAllProducts(int numarProduseRamase) {
        return (numarProduseRamase == 0);
    }

    boolean[] create_new_visited(boolean[] visited) {
        boolean new_visited[] = new boolean[visited.length];

        for (int i = 0; i < visited.length; i++) {
            new_visited[i] = visited[i];
        }

        return new_visited;
    }

    public void DFSUtil(Node v, BigDecimal totalCost, boolean visited[], Set<Node> currentPath, int numarProduseRamase) {
        visited[Integer.valueOf(v.getId())] = true;
        currentPath.add(v);
        numarProduseRamase = numarProduseRamase - 1;

        if (GotAllProducts(numarProduseRamase)) {
            if (totalCost.compareTo(cost) == -1) {
                cost = totalCost;
                correctPath = new HashSet<>(currentPath);
            }
        } else {
            for (Map.Entry<Node, BigDecimal> adjacencyPair : v.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                BigDecimal costNodAdiacent = adjacencyPair.getValue();

                if (CheckIfExists(adjacentNode, currentPath) == false && (!visited[Integer.valueOf(adjacentNode.getId())])) {
                    DFSUtil(
                        adjacentNode,
                        totalCost.add(costNodAdiacent),
                        create_new_visited(visited),
                        new HashSet<Node>(currentPath),
                        numarProduseRamase
                    );
                }
            }
        }
    }
}
