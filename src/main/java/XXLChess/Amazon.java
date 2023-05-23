package XXLChess;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static XXLChess.App.CELLSIZE;
import static XXLChess.App.piecePImageHashMap;

public class Amazon extends Piece {
    public Amazon(boolean white) {
        super(white);
        this.value = 12;
        this.canMove_Knightlike = true;
        this.canMove_BishopLike = true;
        this.canMove_Rooklike = true;
    }


    public String getType(){
        if (this.getIsWhite()){
            return "a";
        }
        return "A";
    }
    /**
     * Calculating possible Knight like moves using coordinates pairs
     *
     * @param board the game board instances to account for the current game board when calculating moves
     */
    @Override
    public List<Cell> getPossiblesKnightLikeMoves(Board board) {
        List<Cell> possibleMoveswDups = new ArrayList<>();
        int x = this.getCell().getX();
        int y = this.getCell().getY();
        int[][] possibleCoorKnight = {{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {-2, 1}, {2, -1}, {-2, -1}};
        /*Knight-like Movement*/
        for(int[] coor: possibleCoorKnight){
            Cell cell = board.getCell(x+coor[0],y+coor[1]);
            if ((x + coor[0] < 14 && x + coor[0] > -1 && y + coor[1] < 14 && y+coor[1] > -1) && !this.isSameSide(cell.getPiece())){
                possibleMoveswDups.add(cell);
            }
        }
        /*Remove possible duplicates*/
        Set<Cell> hs = new HashSet<>(possibleMoveswDups);
        List<Cell> possibleMoves = new ArrayList<>(hs);
        return possibleMoves;
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
    /**
     * Calculating possible Bishop like moves using coordinates pairs
     *
     * @param board the game board instances to account for the current game board when calculating moves
     */
    public List<Cell> getPossibleBishopLikeMove(Board board) {
        List<Cell> possibleMoveswDups = new ArrayList<>();
        int x = this.getCell().getX();
        int y = this.getCell().getY();
        int[][] possibleCoor = {{1,1},{1,-1},{-1,1},{-1,-1}};
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

    /**Get Possible Knight-like Move*/



    @Override
    public List<Cell> getPossibleMoves(Board board) {
        List<Cell> possibleMoveswDups = new ArrayList<>();
        /*Bishop-like Movement*/
        possibleMoveswDups.addAll(this.getPossibleBishopLikeMove(board));
        /*Rook-like Movement*/
        possibleMoveswDups.addAll(this.getPossibleRookLikeMoves(board));
        /*Knight-like Movement*/
        possibleMoveswDups.addAll(this.getPossiblesKnightLikeMoves(board));
        /*remove possible duplicates*/

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
