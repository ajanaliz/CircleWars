package com.aut.alij.circlewars;

import java.awt.*;

/**
 * Created by Ali J on 3/14/2015.
 */
public class Player {


    //FIELDS
    private int x;
    private int y;
    private int r;
    //coordinates and radius

    private int dx;//the changed in x
    private int dy;//the changes in y
    private int speed;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    private boolean firing;
    private long firingTimer;
    private long firingDelay;

    private boolean recovering;
    private long recoveryTimer;

    private int lives;
    private Color color1;
    private Color color2;

    private int score;

    /*our powerup system is going to go like this:the player collects power and once the player has gained enough power,he gains a powerlevel and power gets reset*/
    private int powerLevel;
    private int power;
    private int[] requiredPower = {
            1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 , 12 , 13 , 14 , 15 , 16 , 17 , 18 , 19 , 20 , 21 , 22
    };

    public Player(){
        x = GamePanel.WIDTH /2;
        y = GamePanel.HEIGHT /2;
        r = 5;

        dx = 0;
        dy = 0;
        speed = 5;

        lives = 3;
        color1 = Color.white;//our players regular and ordinary color
        color2 = Color.RED;//our players color when hes hit

        firing = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;//5 shots per second

        recovering = false;
        recoveryTimer = 0;
        score = 0;
    }

    //FUNCTIONS
    //every class we make from this point on will have 2 things in common:
    //we're gonna have an update and a draw function

    public void update(){

        if (left){
            dx = -speed;
        }
        if (right){
            dx = speed;
        }
        if (up){
            dy = -speed;
        }
        if (down){
            dy = speed;
        }

        x += dx;
        y += dy;

        if (x < r) x = r;
        if (y < r) y = r;
        if (x >GamePanel.WIDTH - r)x = GamePanel.WIDTH - r;
        if (y >GamePanel.HEIGHT - r)y = GamePanel.HEIGHT - r;

        dx = 0;
        dy = 0;

        //firing
        if (firing){
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;
            if (elapsed > firingDelay){//inside this if statement,we are allowed to fire
                firingTimer = System.nanoTime();
                /*what we do here is before firing,we check our power level*/
                if (powerLevel < 2)
                    GamePanel.bullets.add(new Bullet(270, x , y));
                else if (powerLevel < 4){
                    GamePanel.bullets.add(new Bullet(270, x + 5 , y));
                    GamePanel.bullets.add(new Bullet(270, x - 5, y));
                }
                else{
                    GamePanel.bullets.add(new Bullet(270, x , y));
                    GamePanel.bullets.add(new Bullet(275, x + 5 , y));
                    GamePanel.bullets.add(new Bullet(265, x - 5 , y));
                }
            }
        }

        //recovery timer logic

        if (recovering){
            //first we're going to check how much time has passed since the player has been hit
            long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
            if (elapsed > 2000){//you will be invincible for 2 seconds after being hit,if elapsed goes beyond 2 seconds then u wont be recovering anymore
                recovering = false;
                recoveryTimer = 0;
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public boolean isRecovering(){
        return recovering;
    }

    public int getScore() {
        return score;
    }

    public boolean isDead(){
        return lives <= 0;
    }

    public void loselife(){
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }

    public void gainLife(){
        lives++;
    }

    public void increasePower(int i){
        power += i;
        if (powerLevel == 4){
            if (power > requiredPower[powerLevel]){
                power = requiredPower[powerLevel];
            }
            return;
        }
        if (power >= requiredPower[powerLevel]){
            power -= requiredPower[powerLevel];
            powerLevel++;
        }
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public int getPower() {
        return power;
    }

    public int getRequiredPower() {
        return requiredPower[powerLevel];
    }

    public int getlives(){
        return lives;
    }

    public void draw(Graphics2D g){

        if (recovering){
            g.setColor(color2);
            g.fillOval( x - r , y - r , 2* r , 2*r);//this makes our x and y coordinates at the center of the player
            g.setStroke(new BasicStroke(3));//this makes our lines and such 3 pixels Wide
            g.setColor(color2.darker());//this is gonna be like a grey'ish color
            g.drawOval( x - r , y - r , 2 * r , 2 * r  );//we're gonna draw the border just to make it look nice
            g.setStroke(new BasicStroke(1));
        }
        else {
            g.setColor(color1);
            g.fillOval(x - r, y - r, 2 * r, 2 * r);//this makes our x and y coordinates at the center of the player

            g.setStroke(new BasicStroke(3));//this makes our lines and such 3 pixels Wide
            g.setColor(color1.darker());//this is gonna be like a grey'ish color
            g.drawOval(x - r, y - r, 2 * r, 2 * r);//we're gonna draw the border just to make it look nice
            g.setStroke(new BasicStroke(1));
        }
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
    public void setFiring(boolean Firing){
        this.firing = Firing;
    }

    public void addScore( int i ){
        score += i;
    }
}
