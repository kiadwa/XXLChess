//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package XXLChess;

import processing.core.PApplet;

import java.util.HashMap;
import java.util.List;

public class Player {
    private boolean isWhite; //true for white(human) and false for black(AI)
    private int time_have_milisecond = 180000;
    private int time_increment_milisecond = 2000;
    private PApplet parent;
    private boolean isInAction = false;

    private boolean turnStarted = false;
    private int startTime;
    private int endTime;
    private Game game;


    public Player(boolean isWhite, PApplet parent) {
        this.isWhite = isWhite;
        this.parent = parent;

    }

    public int getTime_have_milisecond() {
        return this.time_have_milisecond;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean getIsWhite() {
        return this.isWhite;
    }


    public void setInAction(boolean bool) {
        this.isInAction = bool;
    }

    public boolean getIsInAction() {
        return this.isInAction;
    }


    public void setTime_have_milisecond(int num){
        this.time_have_milisecond = num;
    }
    public void updateTimer() {
        this.time_have_milisecond -= this.endTime - this.startTime;
        this.time_have_milisecond += this.time_increment_milisecond;
    }
    public void setStartTime(int num){
        this.startTime = num;
    }
    public void setEndTime(int num){
        this.endTime = num;
    }

    public void draw() {
        if (!this.isWhite) {
            if (!this.getIsInAction()){
                this.startTime = parent.millis();
            }
            parent.textSize(20);
            parent.fill(180, 180, 180);
            parent.rect(680, 35, 70, 30);
            // Update the timer digits
            parent.fill(225, 225, 225);
            int secondsRemaining = (int) ((this.time_have_milisecond - (parent.millis() - this.startTime)) / 1000);
            if(secondsRemaining > 0){
                //display the timer for the player
                String secondsText = parent.nf(secondsRemaining % 60, 2);
                parent.text((secondsRemaining / 60) + ":" + secondsText, 680, 55);
            }else{
                parent.text("0:00",680,55);
                parent.fill(180, 180, 180);
                parent.rect(672, 300, 120, 100);
                parent.textSize(14);
                parent.fill(139,0,0,175);
                parent.text("White win", 680, 320);
                parent.text("by time!", 680, 350);
            }
        }

    else {
            if (!this.getIsInAction()){
                this.startTime = parent.millis();
            }
                parent.textSize(20);
                parent.fill(180, 180, 180);
                parent.rect(680, 610, 70, 30);
                // Update the timer digits
                parent.fill(225, 225, 225);
                int secondsRemaining = (int) ((this.time_have_milisecond - (parent.millis() - this.startTime)) / 1000);
                if(secondsRemaining > 0){
                    //display the timer for the player
                    String secondsText = parent.nf(secondsRemaining % 60, 2);
                    parent.text((secondsRemaining / 60) + ":" + secondsText, 680, 630);
                }else{
                    parent.text("0:00",680,630);
                    parent.fill(180, 180, 180);
                    parent.rect(672, 300, 120, 100);
                    parent.textSize(14);
                    parent.fill(139,0,0,175);
                    parent.text("Black win", 680, 320);
                    parent.text("by time!", 680, 350);
                }

        }
    }
}

