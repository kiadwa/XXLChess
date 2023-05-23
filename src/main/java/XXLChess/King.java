package XXLChess;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static XXLChess.App.CELLSIZE;
import static XXLChess.App.piecePImageHashMap;

public class King extends Piece{
    private Piece closeRook = null;
    private Piece farRook = null;

    public King(boolean white) {

        super(white);
        this.value = 999;
        this.isAlive = true;

    }
    //getter


    public void setCloseRook(Piece piece){
        this.closeRook = piece;
    }
    public void setFarRook(Piece piece){
        this.farRook = piece;
    }

    public Piece getCloseRook(){
        return this.closeRook;
    }
    public Piece getFarRook(){
        return this.farRook;
    }

    @Override
    public String getType(){
        if (this.getIsWhite()){
            return "k";
        }
        return "K";
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
    public boolean canCastleFar(Board board) {
        Cell[][] grid = board.getGrid();
        if (this.getIsWhite()) {
            if (!this.getHasMoved()
                    && this.farRook != null
                    && !this.farRook.getHasMoved()) {
                int king_y = this.getY();
                for (int i = 1; i < king_y; i++) {
                    if(grid[13][i].getPiece() !=null)
                        return false;
                }
                return true;
            }else{
                return false;
            }
        }else{
            if (!this.getHasMoved()
                    && this.farRook != null
                    && !this.farRook.getHasMoved()) {
                for (int i = 1; i < this.getY(); i++) {
                    if(grid[0][i].getPiece() !=null)
                        return false;
                }
                return true;
            }else{
                return false;
            }
        }
    }
    @Override
    public boolean canCastleClose(Board board){
        Cell[][] grid = board.getGrid();
        if(this.getIsWhite()){
            if(!this.getHasMoved()
                    && this.closeRook!=null
                    && !this.closeRook.getHasMoved()){
                int king_y = this.getY();
                for(int i = 1; i < 6; i++){
                    if(grid[13][king_y+i].getPiece() != null){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        }else{
            if(!this.getHasMoved()
                    &&this.closeRook!=null
                    &&!this.closeRook.getHasMoved()){
                int king_y = this.getY();
                for(int i = 1; i < 6; i++){
                    if(grid[0][king_y+i].getPiece() != null){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        }
    }

    @Override
    public void setCloseRook(boolean bool) {

    }

    @Override
    public void setFarRook(boolean bool) {

    }


    @Override
    public List<Cell> getPossibleMoves(Board board) {
        List<Cell> possibleMoveswDups = new ArrayList<>();
        int x = this.getCell().getX();
        int y = this.getCell().getY();
        int[][] possibleCoor = {{1 ,0}, {0 ,1}, {-1 ,0},{0 ,-1},{1 ,1},{1 ,-1},{-1 ,-1},{-1 ,1}};
        int coor_x;
        int coor_y;
        for(int[] coor : possibleCoor) {
            if (this.getIsWhite()){
                coor_x = x - coor[0];
                coor_y = y - coor[1];}
            else{
                coor_x = x + coor[0];
                coor_y = y + coor[1];
            }
            Cell cell = board.getCell(coor_x, coor_y);
            while((coor_x < 14 && coor_x > -1 && coor_y < 14 && coor_y > -1)){

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




}
