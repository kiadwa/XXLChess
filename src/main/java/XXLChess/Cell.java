package XXLChess;
//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;
import org.checkerframework.checker.units.qual.C;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.awt.Font;
import java.io.*;
import java.util.*;
import java.io.File;
public class Cell {
    private Piece piece;
    private int x;
    private int y;

    public Cell(int x, int y, Piece piece){
        this.setPiece(piece);
        this.setX(x);
        this.setY(y);
    }
    //getter
    public Piece getPiece(){
        return this.piece;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }

    //setter
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setPiece(Piece piece){
        this.piece = piece;
    }
    /**Override equals for further checking purpose
     * @param obj Cell object here*/
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Cell)) {
            return false;
        }
        Cell other = (Cell) obj;
        return this.x == other.x && this.y == other.y && Objects.equals(this.piece, other.piece);
    }
}
