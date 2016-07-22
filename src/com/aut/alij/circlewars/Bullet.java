package com.aut.alij.circlewars;

import java.awt.*;

/**
 * Created by Ali J on 3/14/2015.
 */
public class Bullet {

    //Fields
    private double x;
    private double y;
    //we're dealing with radians and we need doubles for them
    private int r;//radius

    private double dx;
    private double dy;
    private double rad;//radian
    private double speed;

    private Color color1;


    //Constructor
    public Bullet(double angle, int x, int y){
        this.x = x;
        this.y = y;
        r = 2;

        rad = Math.toRadians(angle);

        speed = 10;
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;



        color1 = Color.yellow;
    }

    //Functions

    public boolean update(){
        x += dx;
        y += dy;

        if (x < -r || x >GamePanel.WIDTH + r || y < -r || y > GamePanel.HEIGHT + r )
            return true;

        return false;/*this function is going to return a boolean(the purpose of this boolean is weather or not we want to remove this bullet from the screen,so if it returns true u remove it and if false you
        keep doing the shit normally) so by default this function returns false,but if we want to remove the bullet,like if it hits the enemy or if it gets out of bounds then we want to return true*/
    }

    public void draw(Graphics2D g){
        g.setColor(color1);
        g.fillOval((int) (x-r) , (int) (y-r), 2*r,2*r);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getR(){
        return r;
    }
}
