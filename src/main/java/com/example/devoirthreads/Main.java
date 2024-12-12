package com.example.devoirthreads;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Object mutex=new Object();
        List<ObjectNode> list=new ArrayList<>();
        OrderProcessor Thread1=new OrderProcessor(mutex,list);

        Thread2order Thread2=new Thread2order(mutex,list);
        Thread1.start();

        Thread2.start();
    }
}
