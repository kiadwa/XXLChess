package XXLChess;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

import static XXLChess.App.CELLSIZE;
import static XXLChess.App.piecePImageHashMap;

public class Pawn extends Piece{
    private List<Cell> possibleCapture = new ArrayList<>();
    public Pawn(boolean white) {
        super(white);
        this.value = 1;
    }
    public boolean canBePromoted(){
        if(this.getIsWhite()){
            if(this.getX() == 0){
                return true;
            }
        }else{
            if(this.getX() == 13){
                return true;
            }
        }
        return false;
    }



    public boolean pawnCapture(Board board){
        int [][] possibleCoorDiagCaptureB = {{1,-1},{1,1}};
        int [][] possibleCoorDiagCaptureW = {{-1,-1},{-1,1}};
        int x = this.getX();
        int y = this.getY();
        if (this.getIsWhite()){
            for(int[] coor: possibleCoorDiagCaptureW) {
                int coor_x = x + coor[0];
                int coor_y = y+ coor[1];
                if (coor_x < 14 && coor_x > -1 && coor_y < 14 && coor_y > -1) {
                    if (board.getCell(coor_x, coor_y).getPiece() != null && !this.isSameSide(board.getCell(coor_x, coor_y).getPiece())) {
                        Cell cell = board.getCell(coor_x, coor_y);
                        this.possibleCapture.add(cell);
                    }
                }
            }
        }else{
            for(int[] coor: possibleCoorDiagCaptureB) {
                int coor_x = x + coor[0];
                int coor_y = y + coor[1];
                if (coor_x < 14 && coor_x > -1 && coor_y < 14 && coor_y > -1) {
                    if (board.getCell(coor_x, coor_y).getPiece() != null && !this.isSameSide(board.getCell(coor_x, coor_y).getPiece())) {
                        Cell cell = board.getCell(coor_x, coor_y);
                        this.possibleCapture.add(cell);
                    }
                }
            }
        }
        if(this.possibleCapture == null || this.possibleCapture.isEmpty()){
            return false; // return false if there is no pieces for it to capture, true when it does
        }
        return true;
    }

    @Override
    public String getType(){
        if (this.getIsWhite()){
            return "p";
        }
        return "P";
    }

    @Override
    public List<Cell> getPossiblesKnightLikeMoves(Board board) {
        return null;
    }

    @Override
    public List<Cell> getPossibleRookLikeMoves(Board board) {
        return null;
    }

    @Override
    public List<Cell> getPossibleBishopLikeMove(Board board) {
        return null;
    }


    @Override
    public List<Cell> getPossibleMoves(Board board)  {
            List<Cell> possibleMoveswDups = new ArrayList<>();
            int x = this.getCell().getX();
            int y = this.getCell().getY();
            Cell cellInFront;
            if(this.getIsWhite()){
                cellInFront = board.getCell(x - 1,y);
            }else{
                cellInFront = board.getCell(x + 1,y);
            }
            int [][] possibleCoor = {{1,0}};
            int [][] possibleCoorFirstMove = {{1,0},{2,0}}; //coordinates for the pawn first move

            if (!this.getHasMoved() && cellInFront != null && cellInFront.getPiece() == null){
                int coor_x;
                int coor_y;
                for (int[] coor : possibleCoorFirstMove) {
                    if (this.getIsWhite()){
                        coor_x = x - coor[0];
                        coor_y = y - coor[1];
                    } else {
                        coor_x = x + coor[0];
                        coor_y = y + coor[1];
                    }
                    Cell cell = board.getCell(coor_x, coor_y);
                    while ((coor_x < 14 && coor_x > -1 && coor_y < 14 && coor_y > -1)) {
                        if (cell.getPiece() != null) {
                            if (cell.getPiece() == null) {
                                possibleMoveswDups.add(cell);
                                break;
                            }
                            break;
                        }
                        possibleMoveswDups.add(cell);
                        if (this.getIsWhite()) {
                            coor_x -= coor[0];
                            coor_y -= coor[1];
                        } else {
                            coor_x += coor[0];
                            coor_y += coor[1];
                        }

                    }
                }
            }else{
                for (int[] coor : possibleCoor) {
                    int coor_x;
                    int coor_y;

                    if (this.getIsWhite()) {
                        coor_x = x - coor[0];
                        coor_y = y - coor[1];
                    } else {
                        coor_x = x + coor[0];
                        coor_y = y + coor[1];
                    }
                    Cell cell = board.getCell(coor_x, coor_y);
                    while ((coor_x < 14 && coor_x > -1 && coor_y < 14 && coor_y > -1)) {
                        if (cell.getPiece() != null) {
                            if (cell.getPiece() == null) {
                                possibleMoveswDups.add(cell);
                                break;
                            }
                            break;
                        }
                        possibleMoveswDups.add(cell);
                        if (this.getIsWhite()) {
                            coor_x -= coor[0];
                            coor_y -= coor[1];
                        } else {
                            coor_x += coor[0];
                            coor_y += coor[1];
                        }

                    }
                }
            }
        // handle capturing of pawn piece
        this.possibleCapture.clear();//remove any possible capture of the previous pawn or when of its previous location
        if(this.pawnCapture(board)){
            for(Cell cell: this.possibleCapture){
                possibleMoveswDups.add(cell);
            }
        }
        Set<Cell> hs = new HashSet<>(possibleMoveswDups);
        List<Cell> possibleMoves = new ArrayList<>(hs);
        return possibleMoves;
    }
    public void tick() {
        if (this.y != this.getCell().getY() || this.x != this.getCell().getX()) {
            int[] coor_diff = {this.getCell().getY() - this.y, this.getCell().getX() - this.x};

            // Check if the piece has reached the target cell
            if (coor_diff[0] == 0 && coor_diff[1] == 0) {
                // The piece has reached the target cell, no need for further movement
                return;
            }

            // Calculate the movement direction
            int directionX = Integer.signum(coor_diff[1]);
            int directionY = Integer.signum(coor_diff[0]);

            // Update the piece's position based on the movement direction
            this.x += directionX;
            this.y += directionY;
        }
    }
    /**
     * Draw the piece on the board base on its x and y value, using its image store in Hashmap with its key as
     * its type
     *
     * @param parent PApplet instances to call draw method
     */
    @Override
    public void draw(PApplet parent){
        PImage image = piecePImageHashMap.get(this.getType());
        if (this.getIsAlive()) {
            tick();
            parent.image(image, this.y * CELLSIZE, this.x * CELLSIZE, CELLSIZE, CELLSIZE);
        }
}

    @Override
    public boolean canCastleFar(Board board) {
        return false;
    }

    @Override
    public boolean canCastleClose(Board board) {
        return false;
    }

    @Override
    public void setCloseRook(boolean bool) {

    }

    @Override
    public void setFarRook(boolean bool) {

    }

}
