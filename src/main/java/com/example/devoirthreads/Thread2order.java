package com.example.devoirthreads;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class Thread2order extends Thread {
    private Object mutex;
    public List<ObjectNode> orders;
    public Thread2order(Object mutex, List<ObjectNode> orders) {
        this.mutex = mutex;
        this.orders = orders;
    }
    public void run() {
        synchronized (mutex) {
            while (orders.isEmpty()) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            for(ObjectNode order : orders) {
                System.out.println(order.get("id"));
            }
        }
    }
}
