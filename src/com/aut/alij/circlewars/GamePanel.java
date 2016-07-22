package com.aut.alij.circlewars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Ali J on 3/13/2015.
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    //Fields
    public static int WIDTH = 400;
    public static int HEIGHT = 400;

    private boolean running;
    private Thread thread;

    private BufferedImage image;
    private Graphics2D g;//this is our paintbrush

    private int FPS = 30;
    private double avarageFPS;// should be around 30

    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<PowerUp> powerUps;
    public static ArrayList<Explosion> explosions;
    public static ArrayList<Text> texts;

    private long waveStartTimer;
    private long getWaveStartTimerDiff;//this is for keeping track of how much time has passed by
    private int waveNumber;
    private boolean waveStart;//tells us weather to start creating enemies or not
    private int waveDelay;

    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownLength;

    //Constructor
    public GamePanel(){
        super();
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    //Functions
    public void addNotify(){
        super.addNotify();//this basically says that the Jpanels done loading and that we can start doing whatever we want to do
        if (thread == null){
            thread = new Thread(this);
            thread.start();
        }
        addKeyListener(this);
    }

    @Override
    public void run() {//this is what the thread is going to run

        running = true;

        image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);//this is just for the graphics,i want antialias for the text aswell
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        powerUps = new ArrayList<PowerUp>();
        explosions = new ArrayList<Explosion>();
        texts = new ArrayList<Text>();

        waveStartTimer = 0;
        getWaveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;
        waveDelay = 2000;
        slowDownLength = 6000;


        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;

        int FrameCount = 0;
        int maxFrameCount = 30;

        long targetTime = 1000/FPS;//this is gonna give us the amount of time that it takes for one loop to run in order to maintain 30 FPS --> i beleive its around 33 milliseconds per loop at 30 FPS
        //GAME LOOP
        while (running){

            startTime = System.nanoTime();//a built in java function that gets the current time in nanoseconds

            gameUpdate();
            gameRender();
            gameDraw();

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000; // divided by a million,so its in miliseconds
            waitTime = targetTime - URDTimeMillis;//waitTime is going to be the amount of Extra time that we need to wait, so for example we need each loop to be 33 milliseconds,if our update/render/draw functions only take 20milliseconds then we still need to wait an extra 13 milliseconds

            try{
                Thread.sleep(waitTime);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }catch (IllegalArgumentException e){
//                System.out.println(":D");
            }

            totalTime += System.nanoTime() - startTime;// this is going to give us the total loop time
            FrameCount++;
            if (FrameCount == maxFrameCount){
                avarageFPS = 1000.0 / ((totalTime / FrameCount) / 1000000 );
                FrameCount = 0;
                totalTime = 0;
            }
        }

        g.setColor(new Color(0 , 100 , 255));
        g.fillRect(0,0,WIDTH,HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic" , Font.PLAIN, 16));
        String s = "G A M E     O V E R";
        int length = (int) g.getFontMetrics().getStringBounds(s , g).getWidth();
        g.drawString(s , (WIDTH - length) / 2 , HEIGHT / 2 );
        s = "Final Score: " + player.getScore();
        length = (int) g.getFontMetrics().getStringBounds(s , g).getWidth();
        g.drawString(s , (WIDTH - length) / 2 , HEIGHT / 2 + 30 );
        gameDraw();
    }

    private void gameUpdate(){
        /*this function is basically for updating everything in the game(such as player position,enemy position,projectiles,collision)basically everything the game needs to do...all of the game logic*/

        //new Wave
        if (waveStartTimer == 0 && enemies.size() == 0){
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
        }
        else {
            getWaveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if (getWaveStartTimerDiff > waveDelay ){
                waveStart = true;
                waveStartTimer = 0;
                getWaveStartTimerDiff = 0;
            }
        }

        //Create Enemies
        if (waveStart && enemies.size() == 0){
            creatNewEnemies();
        }

        //player update
        player.update();

        //bullet update
        for (int i =0; i < bullets.size();i++){
            boolean remove = bullets.get(i).update();//this is for checking weather the bullet has reached the border, in which case we must remove it from the game(by removing it from the Arraylist)
            if (remove){
                bullets.remove(i);
                i--;
            }
        }

        //enemy update
        for (int i = 0; i < enemies.size(); i++)
            enemies.get(i).update();

        //Powerup update
        for (int i = 0; i < powerUps.size(); i++){
            boolean remove = powerUps.get(i).update();
            if (remove){
                powerUps.remove(i);
                i--;
            }
        }

        //explosion Update
        for (int i = 0; i <explosions.size();i++){
            boolean remove = explosions.get(i).update();
            if (remove){
                explosions.remove(i);
                i--;
            }
        }

        //Text Update
        for (int i = 0; i < texts.size(); i++){
            boolean remove = texts.get(i).update();
            if (remove){
                texts.remove(i);
                i--;
            }
        }

        //bullet-enemy Collision --> since bullet and enemy are both circles,this is going to be a circle-circle collision
        for (int i = 0; i < bullets.size();i++){
            Bullet b = bullets.get(i);
            //for a circle - circle collision,we need the x,the y and the Radius
            //we get the x,the y and the radius of the two circles then we calculate the distance between the two points on our page--->in this instance if the sum of the two circles radius's is
            //less than the distance of the two points on the page,we
            double bx = b.getX();
            double by = b.getY();
            double br = b.getR();
            for (int j = 0; j < enemies.size(); j++){
                Enemy e = enemies.get(j);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();

                double dx = bx - ex;//change in x
                double dy = by - ey;//change in x
                double dist = Math.sqrt( dx * dx + dy * dy );//the distance of two points in a page

                if (dist < br + er ){//if the distance of the enemy and the bullet is less than the sum of their radius,then they have collided
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }
        //check dead enemies
        for (int i = 0; i < enemies.size(); i++){
            if (enemies.get(i).isDead()){
                Enemy e = enemies.get(i);

                //Chance for powerup
                double rand = Math.random();//gives us a random value between 0 and 1
                if (rand < 0.001) powerUps.add(new PowerUp(1 , e.getX(),e.getY()));
                else if (rand < 0.020) powerUps.add(new PowerUp(3 , e.getX(),e.getY()));
                else if (rand < 0.120) powerUps.add(new PowerUp(2 , e.getX(),e.getY()));
                else if (rand < 0.130) powerUps.add(new PowerUp(4 , e.getX(),e.getY()));
                //add to the score
                player.addScore( e.getType() + e.getRank() );
                enemies.remove(i);
                i--;

                e.explode();
                explosions.add(new Explosion(e.getX(),e.getY(),e.getR(),e.getR() + 30));
            }
        }//after checking for dead enemies,we need to add to our total score

        //Check Dead Player
        if (player.isDead()){
            running = false;
        }

        //Player - Enemy Collision
        //here we're going to check if the player is not recovering meaning the player is allowed to be hit again
        if (!player.isRecovering()){
            int px = player.getX();
            int py = player.getY();
            int pr = player.getR();
            for (int i = 0; i < enemies.size(); i++ ){
                Enemy e = enemies.get(i);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();

                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt( dx * dx + dy * dy );

                if (dist < pr + er ){
                    player.loselife();
                }
            }
        }

        //Player - Powerup Collision
        int px = player.getX();
        int py = player.getY();
        int pr = player.getR();
        for (int i = 0; i < powerUps.size();i++){
            PowerUp p = powerUps.get(i);
            double x = p.getX();
            double y = p.getY();
            double r = p.getR();
            double dx = px - x;
            double dy = py - y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            //Collected Powerup
            if (dist < pr + r ){//player and powerup have collided
                int type = p.getType();

                if (type == 1){//extra life
                    player.gainLife();
                    texts.add(new Text(player.getX() , player.getY() , 2000, "Extra Life!!"));
                }
                if (type == 2){//increase by 1
                    player.increasePower(1);
                    texts.add(new Text(player.getX() , player.getY() , 2000, "Power!!"));
                }
                if (type == 3){//increase by 2
                    player.increasePower(2);
                    texts.add(new Text(player.getX() , player.getY() , 2000, "Double Power!!"));
                }
                if (type == 4){
                    slowDownTimer = System.nanoTime();
                    for (int j = 0; j < enemies.size(); j++){
                        enemies.get(j).setSlow(true);
                    }
                    texts.add(new Text(player.getX() , player.getY() , 2000, "Slow Down!!"));
                }
                powerUps.remove(i);
                i--;
            }
        }

        //Slowdown Update
        if (slowDownTimer != 0) {//we are in slowdown mode
            slowDownTimerDiff = ( System.nanoTime() - slowDownTimer ) / 1000000;
            if (slowDownTimerDiff > slowDownLength){
                slowDownTimer = 0;
                for (int j = 0; j < enemies.size(); j++){
                    enemies.get(j).setSlow(false);
                }
            }
        }
    }

    private void gameRender(){
        //draw Background
        g.setColor(new Color(0,100,255));
        g.fillRect(0,0,WIDTH,HEIGHT);

        //draw Slowdown Screen
        if ( slowDownTimer != 0 ){
        g.setColor(new Color(255,255,255,64));
        g.fillRect(0,0,WIDTH,HEIGHT);
        }

        //draw player
        player.draw(g);


        //draw Bullet
        for (int i = 0; i <bullets.size();i++){
            bullets.get(i).draw(g);
        }

        //draw enemy
        for (int i = 0; i < enemies.size(); i++)
            enemies.get(i).draw(g);

        //draw Powerups
        for (int i = 0; i < powerUps.size(); i++){
            powerUps.get(i).draw(g);
        }

        //draw explosions
        for (int i = 0; i < explosions.size();i++){
            explosions.get(i).draw(g);
        }

        // draw text
        for (int i = 0; i < texts.size(); i++){
            texts.get(i).draw(g);
        }

        //draw Wave Number
        if (waveStartTimer != 0){//we are in the process of creating enemies
            g.setFont(new Font("Century Gothic" , Font.PLAIN , 18));
            String s = " -  W A V E    " + waveNumber + "  -";
            //we dont want the messege t just appear on the screen,we want it to pulse,in and out
            int length = (int) g.getFontMetrics().getStringBounds(s , g).getWidth();
            int alpha = (int)(255 * Math.sin(3.14 * getWaveStartTimerDiff / waveDelay));
            if (alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
        }

        //draw Player lives
        for (int i = 0; i < player.getlives(); i++){
            g.setColor(Color.white);
            g.fillOval(20 + (20 * i), 20, player.getR() * 2, player.getR() * 2);
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.white.darker());
            g.drawOval(20 + (20 * i), 20, player.getR() * 2, player.getR() * 2);
            g.setStroke(new BasicStroke(1));
        }

        //draw player power
        g.setColor(Color.yellow);
        g.fillRect( 20 , 40 , player.getPower() * 8 , 8);
        g.setColor(Color.yellow.darker());
        g.setStroke(new BasicStroke(2));
        for (int i = 0; i < player.getRequiredPower(); i++ ){
            g.drawRect( 20 + 8 * i , 40 , 8 , 8 );
        }
        g.setStroke(new BasicStroke(1));
        //draw Player Score
        g.setColor(Color.white);
        g.setFont(new Font("Century Gothic", Font.PLAIN , 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);

        //draw slowdown meter
        if (slowDownTimer != 0){
            g.setColor(Color.white);
            g.drawRect( 20 , 60 , 100 , 8);
            g.fillRect(20 , 60 ,(int) (100 - ((100.0 * slowDownTimerDiff) / slowDownLength)) , 8);
        }

    }/*this function is going to draw everything which is currently active onto an offscreen image-->this includes the players the enemies the background,the projectiles amd everything*/


    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        g2.drawImage(image,0,0,null);
        g2.dispose();
    }//drawing everything on the mainscreen
    //to draw the game on an offscreen image then draw it on the gamescreen image is called double buffering

    private void creatNewEnemies(){
        enemies.clear();
        Enemy e;

        if (waveNumber == 1){
            for (int i = 0; i < 4; i++)
                enemies.add(new Enemy(1, 1));
        }
        if (waveNumber == 2){
            for (int i = 0; i < 8; i++)
                enemies.add(new Enemy(1,1));
            }
        if (waveNumber == 3){
            for (int i = 0; i < 4; i++)
                enemies.add(new Enemy(1, 1));
            enemies.add(new Enemy(1, 2));
            enemies.add(new Enemy(1, 2));
        }
        if (waveNumber == 4){
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(1, 4));
            for (int i = 0; i < 4; i++)
                enemies.add(new Enemy(2, 1));
        }
        if (waveNumber == 5){
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
        }
        if (waveNumber == 6){
            enemies.add(new Enemy(1, 3));
            for (int i = 0; i < 4; i++){
                enemies.add(new Enemy(2, 1));
                enemies.add(new Enemy(3, 1));
            }
        }
        if (waveNumber == 7){
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
            enemies.add(new Enemy(3, 3));
        }
        if (waveNumber == 8){
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(2, 4));
            enemies.add(new Enemy(3, 4));
        }
        if (waveNumber == 9){
            running = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent key) {

    }

    @Override
    public void keyPressed(KeyEvent key) {
        int KeyCode = key.getKeyCode();
        if (KeyCode == KeyEvent.VK_LEFT){
            player.setLeft(true);
        }
        if (KeyCode == KeyEvent.VK_RIGHT){
            player.setRight(true);
        }
        if (KeyCode == KeyEvent.VK_DOWN){
            player.setDown(true);
        }
        if (KeyCode == KeyEvent.VK_UP){
            player.setUp(true);
        }
        if (KeyCode == KeyEvent.VK_Z){
            player.setFiring(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent key) {
        int KeyCode = key.getKeyCode();
        if (KeyCode == KeyEvent.VK_LEFT){
            player.setLeft(false);
        }
        if (KeyCode == KeyEvent.VK_RIGHT){
            player.setRight(false);
        }
        if (KeyCode == KeyEvent.VK_DOWN){
            player.setDown(false);
        }
        if (KeyCode == KeyEvent.VK_UP){
            player.setUp(false);
        }
        if (KeyCode == KeyEvent.VK_Z){
            player.setFiring(false);
        }
    }
}
