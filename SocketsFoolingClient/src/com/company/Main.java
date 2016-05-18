package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Client client = new Client("localhost");
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.start();
    }
}
