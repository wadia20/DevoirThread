package com.example.devoirthreads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.devoirthreads.DatabaseConnection.isCustomerExists;

public class OrderProcessor extends Thread {
    private Object mutex;
    public List<ObjectNode> orders;
    public OrderProcessor(Object mutex, List<ObjectNode> orders) {
        this.mutex = mutex;
        this.orders = orders;
    }
    public void run() {
        synchronized (mutex) {
            try {
                // Read the orders.json file
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode rootNode = (ObjectNode) mapper.readTree(new File("C:\\Users\\ahmed\\IdeaProjects\\devoirThreqds\\Files\\input.json"));
                ArrayNode ordersArray = (ArrayNode) rootNode.get("orders");

                // Prepare output lists
                List<ObjectNode> validOrders = new ArrayList<>();
                List<ObjectNode> invalidOrders = new ArrayList<>();

                // Get database connection
                DatabaseConnection dbConnection = DatabaseConnection.getInstance();
                Connection conn = dbConnection.getConnection();

                // Process each order
                for (int i = 0; i < ordersArray.size(); i++) {
                    ObjectNode orderNode = (ObjectNode) ordersArray.get(i);
                    int customerId = orderNode.get("customer_id").asInt();

                    // Check if customer exists
                    if (isCustomerExists(conn, customerId)) {
                        validOrders.add(orderNode);
                        orders.add(orderNode);
                    } else {
                        invalidOrders.add(orderNode);
                    }
                }

                // Create output files
                ObjectNode outputRoot = mapper.createObjectNode();
                outputRoot.set("orders", mapper.valueToTree(validOrders));
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File("C:\\Users\\ahmed\\IdeaProjects\\devoirThreqds\\Files\\output.json"), outputRoot);

                ObjectNode errorRoot = mapper.createObjectNode();
                errorRoot.set("orders", mapper.valueToTree(invalidOrders));
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File("C:\\Users\\ahmed\\IdeaProjects\\devoirThreqds\\Files\\error.json"), errorRoot);
                for (ObjectNode i:validOrders){
                    System.out.println(i);
                }

                System.out.println("Processing completed. See output.json and error.json.");
                System.out.println("off");
                mutex.notify();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(60*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        }
}

