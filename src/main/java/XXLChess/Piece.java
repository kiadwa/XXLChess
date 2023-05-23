package XXLChess;


//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;
import processing.core.PImage;
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

import static XXLChess.App.*;

public abstract class Piece {

    protected double value;
    protected boolean canMove_Knightlike = false;
    protected boolean canMove_Rooklike = false;
    protected boolean canMove_BishopLike = false;
    protected boolean isAlive = true;
    protected boolean isCloseRook = false;
    protected boolean isFarRook = false;
    private boolean white = false;
    private PImage image;
    protected boolean hasMoved = false;
    private Cell isIn;


    protected int x;
    protected int y;





    public Piece(boolean white){
        this.setWhite(white);

    }

    //getter

    public double getRank(){return this.value;}
    public boolean getIsWhite(){return this.white;}
    public boolean getHasMoved(){return this.hasMoved;}
    public Cell getCell(){return this.isIn;}
    public int getX(){return this.x;}
    public int getY(){return this.y;}
    public boolean getIsAlive(){return this.isAlive;}


    //setter

    public void setWhite(boolean isWhite){
        this.white = isWhite;
    }
    public void Dead(){
        this.isAlive = false;
    }
    public void setImage(){
        this.image = piecePImageHashMap.get(this.getType());
    }
    public void setCell(Cell cell){this.isIn = cell;}
    public void setX(int x){this.x = x;}
    public void setY(int y){this.y = y;}

    public void setHasMoved(Boolean bool){this.hasMoved = bool;}

/**Check if the instance of piece is of same side (black/white) with the input piece
 * @param piece to check with
 * @return boolean true for same side, false for otherwise*/
    public boolean isSameSide(Piece piece){
        if (piece == null){
            return false;
        }
        if (this.getIsWhite() == piece.getIsWhite()){
            return true;
        }
        return false;
    }


/**Check if a piece can capture a target by generating its possible moves and Check if target Cell is in range
 * @param board the current game board
 * @param target target piece
 * @return boolean: true if this piece instance in this board can capture target, false otherwise*/
    public boolean canCaptureThis(Board board, Piece target){
        List<Cell> myMoveList = this.getPossibleMoves(board);
        if(myMoveList == null || myMoveList.isEmpty()){
            return false;
        }
        if (myMoveList.contains(target.getCell()) && !this.isSameSide(target)){
            return true;
        }
        return false;
    }










    public abstract String getType();

    public abstract List<Cell> getPossiblesKnightLikeMoves(Board board);
    public abstract List<Cell> getPossibleRookLikeMoves(Board board);
    public abstract List<Cell> getPossibleBishopLikeMove(Board board);

    public abstract List<Cell> getPossibleMoves(Board board);
    public abstract void draw(PApplet parent);
    public abstract boolean canCastleFar(Board board);
    public abstract boolean canCastleClose(Board board);

    public abstract void setCloseRook(boolean bool);
    public abstract void setFarRook(boolean bool);
/**Override equals for further checking and comparison needs
 * @param obj Piece object here*/
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Piece)) return false;
        Piece other = (Piece) obj;
        if (this.getRank() != other.getRank()) return false;
        if (this.getIsWhite() != other.getIsWhite()) return false;
        if (this.getHasMoved() != other.getHasMoved()) return false;
        if (this.getCell() != other.getCell()) return false;
        return true;
    }

}
