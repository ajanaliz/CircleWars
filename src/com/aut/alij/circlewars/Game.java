package com.aut.alij.circlewars;

import javax.swing.*;

/**
 * Created by Ali J on 3/13/2015.
 */
public class Game {

    public static void main(String[] args){
        JFrame window = new JFrame("CircleWars");
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new GamePanel());
        window.pack();//this sets the window size to whatever is inside of it
        window.setVisible(true);
    }
}
