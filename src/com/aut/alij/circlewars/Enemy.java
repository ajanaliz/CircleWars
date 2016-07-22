package com.aut.alij.circlewars;

import java.awt.*;

/**
 * Created by Ali J on 3/15/2015.
 */
public class Enemy {

    //Fields
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private int health;
    private int type;
    private int rank;

    private Color color1;

    private boolean ready;//see if the enemy is ready,which means weather it is inside the gamescreen or not
    private boolean dead;//health is <= 0

    private boolean hit;
    private long hitTimer;

    private boolean slow;



    //Constructor
    public Enemy(int type,int rank){
        this.type = type;
        this.rank = rank;

        //Default enemy
        if (type == 1){
//            color1 = Color.BLUE;
            color1 = new Color(0 , 0 , 255 , 128);
            if (rank == 1){
                speed = 2;
                r = 5;
                health = 1;
            }
            if (rank == 2){
                speed = 2;
                r = 10;
                health = 2;
            }
            if (rank == 3){
                speed = 1.5;
                r = 20;
                health = 3;
            }
            if (rank == 4){
                speed = 1.5;
                r = 30;
                health = 4;
            }
        }

        //Stronger, faster default
        if (type == 2){
//            color1 = Color.RED;
            color1 = new Color(255,0,0,128);
            if (rank == 1){
                speed = 3;
                r = 5;
                health = 2;
            }
            if (rank == 2){
                speed = 3;
                r = 10;
                health = 3;
            }
            if (rank == 3){
                speed = 2.5;
                r = 20;
                health = 3;
            }
            if (rank == 4){
                speed = 2.5;
                r = 30;
                health = 4;
            }
        }

        //slow but hard to kill enemy
        if (type == 3){
//            color1 = Color.GREEN;
            color1 = new Color( 0, 255,0,128);
            if (rank == 1){
                speed = 1.5;
                r = 5;
                health = 3;
            }
            if (rank == 2){
                speed = 1.5;
                r = 10;
                health = 4;
            }
            if (rank == 3){
                speed = 1.5;
                r = 25;
                health = 5;
            }
            if (rank == 4){
                speed = 1.5;
                r = 45;
                health = 6;
            }
        }

        x = Math.random() * GamePanel.WIDTH /2 + GamePanel.WIDTH / 4;
        y = -r;//so the enemy spawns at the top of the screen

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);

        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        ready = false;
        dead = false;

        hit = false;
        hitTimer = 0;
    }

    //Functions

    public void hit(){
        health--;
        if (health <= 0)
            dead = true;
        hit = true;
        hitTimer = System.nanoTime();
    }

    public void explode(){
        if (rank > 1){
            int amount = 0;
            if (type == 1)
                amount = 3;
            if (type == 2)
                amount = 3;
            if (type == 3)
                amount = 4;
            for (int i = 0; i < amount; i++){
                Enemy e = new Enemy(getType(), getRank() - 1);
                e.setSlow(slow);
                e.x = this.x;
                e.y = this.y;
                double angle = 0;
                if (!ready){
                    angle = Math.random() * 140 + 20;
                }
                else {
                    angle = Math.random() * 360;
                }
                e.rad = Math.toRadians(angle);
                GamePanel.enemies.add(e);
            }
        }
    }

    public boolean isDead(){
        return dead;
    }

    public void update(){
        if (slow){
            x += dx * 0.3;
            y += dy * 0.3;
        }else {
            x += dx;
            y += dy;
        }
        if (!ready){
            if (x > r && x < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT - r)
                ready = true;
        }

        if ( x < r && dx < 0 )/*the enemy is going left*/ dx = -dx;//change it to right
        if ( y < r && dy < 0 )/*the enemy is going up*/ dy = -dy;//change it to down
        if ( x > GamePanel.WIDTH - r && dx > 0)/*the enemy is going right*/ dx = -dx;//change it to left
        if ( y > GamePanel.HEIGHT - r && dy > 0)/*the enemy is going down*/  dy = -dy;//change it to up


        if (hit){
            long elapsed = (System.nanoTime() - hitTimer)/ 1000000;
            if (elapsed > 50){
                hit = false;
                hitTimer = 0;
            }
        }
    }


    public void draw(Graphics2D g){

        if (hit){//we're gonna change the color used
            g.setColor(Color.WHITE);
            g.fillOval((int) (x - r), (int) (y - r), 2 * r , 2 * r );

            g.setStroke(new BasicStroke(3));
            g.setColor(Color.WHITE.darker());//for the border around the shape
            g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));
        }
        else {//otherwise we're gonna use the default color
            g.setColor(color1);
            g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);

            g.setStroke(new BasicStroke(3));
            g.setColor(color1.darker());//for the border around the shape
            g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));
        }
    }
    public void setSlow(boolean slow) {
        this.slow = slow;
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

    public int getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }
}
