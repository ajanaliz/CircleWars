package com.aut.alij.circlewars;

import java.awt.*;

/**
 * Created by Ali J on 3/17/2015.
 */
public class PowerUp {

    //Fields
    private double x;
    private double y;
    private int r;
    private int type;
    private Color color;

    //the different types of power ups we're implementing:
    //1 ----- +1 to life ----> this should be the rarest powerup
    //2 ----- +1 to power
    //3 ----- +2 to power

    //Constructor
    public PowerUp( int type , double x , double y){
        this.type = type;
        this.x = x;
        this.y = y;

        if(type == 1){//life powerup
            color = Color.PINK;
            r = 3;
        }
        if (type == 2 ){
            color = Color.YELLOW;
            r = 3;
        }
        if ( type == 3 ){
            color = Color.YELLOW;
            r = 5;
        }
        if (type == 4){
            color = Color.WHITE;
            r = 3;
        }
    }

    public boolean update(){//this is going to be similar to the bullet classes update,except this one is going downwards, at a slower speed and its going to return a boolean,if we need to remove it from the screen(when it gets offscreen)
        y += 2;

        if (y > GamePanel.HEIGHT + r ){//we remove it if it goes offscreen
            return true;
        }

        return false;
    }

    public void  draw(Graphics2D g){

        g.setColor(color);
        g.fillRect((int) ( x - r ) , (int) (y-r) ,  2 * r , 2 * r );

        g.setStroke(new BasicStroke(3));
        g.setColor(color.darker());
        g.drawRect((int) ( x - r ) , (int) ( y - r ) , 2 * r , 2 * r );
        g.setStroke(new BasicStroke(1));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public int getType() {
        return type;
    }
}
