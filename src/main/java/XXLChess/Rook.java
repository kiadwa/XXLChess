package XXLChess;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static XXLChess.App.CELLSIZE;
import static XXLChess.App.piecePImageHashMap;

public class Rook extends Piece {

    public Rook(boolean white) {
        super(white);
        this.value = 5.25;
        this.canMove_Rooklike = true;
    }

    public void setCloseRook(boolean bool){
        this.isCloseRook = bool;
    }

    @Override
    public void setFarRook(boolean bool) {
        this.isFarRook = bool;
    }

    @Override
    public String getType() {
        if (this.getIsWhite()) {
            return "r";
        }
        return "R";
    }

    @Override
    public List<Cell> getPossiblesKnightLikeMoves(Board board) {
        return null;
    }
    /**
     * Calculating possible Rook like moves using coordinates pairs
     *
     * @param board the game board instances to account for the current game board when calculating moves
     */
    @Override
    public List<Cell> getPossibleRookLikeMoves(Board board) {
        List<Cell> possibleMoveswDups = new ArrayList<>();
        int x = this.getCell().getX();
        int y = this.getCell().getY();
        int[][] possibleCoor = {{0,1},{0, -1},{1,0},{-1,0}};
        int coor_x;
        int coor_y;
        for (int[] coor: possibleCoor){
            if (this.getIsWhite()){
                coor_x = x - coor[0];
                coor_y = y - coor[1];}
            else{
                coor_x = x + coor[0];
                coor_y = y + coor[1];
            }
            while((coor_x < 14 && coor_x > -1 && coor_y < 14 && coor_y > -1)){
                Cell cell = board.getCell(coor_x, coor_y);
                if (cell.getPiece() != null){
                    if (!this.isSameSide(cell.getPiece())) {
                        possibleMoveswDups.add(cell);
                        break;
                    }
                    break;
                }
                possibleMoveswDups.add(cell);
                if (this.getIsWhite()) {
                    coor_x -= coor[0];
                    coor_y -= coor[1];
                }else{
                    coor_x += coor[0];
                    coor_y += coor[1];
                }
            }
        }
        Set<Cell> hs = new HashSet<>(possibleMoveswDups);
        List<Cell> possibleMoves = new ArrayList<>(hs);
        return possibleMoves;
    }

    @Override
    public List<Cell> getPossibleBishopLikeMove(Board board) {
        return null;
    }


    @Override
    public List<Cell> getPossibleMoves(Board board) {
        return this.getPossibleRookLikeMoves(board);
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
        if(!this.getHasMoved() && this.isFarRook){
            return true;
        }
        return false;
    }

    @Override
    public boolean canCastleClose(Board board) {
        if(!this.getHasMoved() && this.isCloseRook){
            return true;
        }
        return false;
    }

}


