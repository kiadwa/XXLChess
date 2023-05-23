package XXLChess;
import java.util.*;
import java.io.*;
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

import static XXLChess.App.CELLSIZE;


public class Board {
    private Cell[][] grid = new Cell[14][14];
    private PApplet parent;
    private King B_King;
    private King W_King;


    private Piece farBlackRook = null;
    private Piece closeBlackRook = null;
    private Piece farWhiteRook = null;
    private Piece closeWhiteRook = null;


    public Board(PApplet parent, String filePath) throws IOException {
        this.grid = this.setOriginalBoard(filePath);
        this.parent = parent;
    }

    public King getB_King(){
        return this.B_King;
    }
    public King getW_King(){
        return this.W_King;
    }
    public Cell getCell(int x, int y)  {
        if (x < 0 || x > 13 || y < 0 || y > 13) {
        return null;
        }
        return grid[x][y];
    }
    public Cell[][] getGrid(){
        return this.grid;
    }

    public int isInCheck(){
        List<Piece> allWhitePiece = this.getAvailableWPiece();
        List<Piece> allBlackPiece = this.getAvailableBPiece();
        Piece BKing = this.B_King;
        Piece WKing = this.W_King;
        for(Piece piece: allWhitePiece){
            if(piece.canCaptureThis(this,BKing)){
                return 1;
            }
        }
        for(Piece piece: allBlackPiece){
            if(piece.canCaptureThis(this, WKing)){
                return 2;
            }
        }
        return 0;
    }
/**Get all available living piece for black
 * @return return ArrayList of all alive Black pieces on the board*/
    public List<Piece> getAvailableBPiece(){
        List<Piece> allBPieces = new ArrayList<>();
        for (int i = 0; i < 14; i++){
            for (int j = 0; j < 14; j++){
                if(grid[i][j].getPiece() != null
                        && !grid[i][j].getPiece().getIsWhite()
                        && grid[i][j].getPiece().getIsAlive()){
                    allBPieces.add(grid[i][j].getPiece());
                }
            }
        }

        return allBPieces;
    }

    /**Get all available living piece for white
     * @return return all alive White piece on the board*/
    public List<Piece> getAvailableWPiece(){
        List<Piece> allWPieces = new ArrayList<>();
        for (int i = 0; i < 14; i++){
            for (int j = 0; j < 14; j++){
                if(grid[i][j].getPiece() != null
                        && grid[i][j].getPiece().getIsWhite()
                        && grid[i][j].getPiece().getIsAlive()){
                    allWPieces.add(grid[i][j].getPiece());
                }
            }
        }
        return allWPieces;
    }
    /**Setting original board at the beginning of the game according to the set-up
     * @param filePath String of file path of the level file
     * @return Cell[][] return 2D Array of the original board with original set up from config
     * @throws IOException throw if file is not found*/
    public Cell[][] setOriginalBoard(String filePath) throws IOException {
        Cell[][] grid = new Cell[14][14];

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < 14) {
                line = line.trim();
                if (!line.isEmpty() && line.length() <= 14) {
                    for (int col = 0; col < line.length(); col++) {
                        char symbol = line.charAt(col);
                        Piece piece = createPieceFromSymbol(symbol);
                        grid[row][col] = new Cell(row, col, piece);
                        if (piece != null) {
                            piece.setCell(grid[row][col]);
                            piece.setX(row);
                            piece.setY(col);
                            if (piece instanceof King) {
                                if (piece.getIsWhite()) {
                                    this.W_King = (King) piece;
                                } else {
                                    this.B_King = (King) piece;
                                }
                            } else if (piece instanceof Rook && piece.getIsWhite()) {
                                if (col == 0) {
                                    farWhiteRook = piece;
                                    piece.setFarRook(true);
                                } else if (col == 13) {
                                    closeWhiteRook = piece;
                                    piece.setCloseRook(true);
                                }
                            } else if (piece instanceof Rook && !piece.getIsWhite()) {
                                if (col == 0) {
                                    farBlackRook = piece;
                                    piece.setFarRook(true);
                                } else if (col == 13) {
                                    closeBlackRook = piece;
                                    piece.setCloseRook(true);
                                }
                            }
                        }
                    }
                }
                row++;
            }
        }

        // Set far and close rook for white and black kings for castling
        if (W_King != null) {
            W_King.setCloseRook(closeWhiteRook);
            W_King.setFarRook(farWhiteRook);
        }
        if (B_King != null) {
            B_King.setCloseRook(closeBlackRook);
            B_King.setFarRook(farBlackRook);
        }

        // Set up the rest of the board with cells containing no piece
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                if (grid[i][j] == null) {
                    grid[i][j] = new Cell(i, j, null);
                }
            }
        }

        return grid;
    }
    /**Instantiate a piece based on the character in level file
     * @param symbol input symbol to instantiate correct piece with its signature symbol*/
    private Piece createPieceFromSymbol(char symbol) {
        switch (symbol) {
            case 'P':
                return new Pawn(false);
            case 'Q':
                return new Queen(false);
            case 'R':
                return new Rook(false);
            case 'N':
                return new Knight(false);
            case 'B':
                return new Bishop(false);
            case 'H':
                return new A_Bishop(false);
            case 'E':
                return new Chancellor(false);
            case 'C':
                return new Camel(false);
            case 'G':
                return new Knight_King(false);
            case 'A':
                return new Amazon(false);
            case 'K':
                return new King(false);
            case 'r':
                return new Rook(true);
            case 'n':
                return new Knight(true);
            case 'b':
                return new Bishop(true);
            case 'h':
                return new A_Bishop(true);
            case 'e':
                return new Chancellor(true);
            case 'c':
                return new Camel(true);
            case 'g':
                return new Knight_King(true);
            case 'a':
                return new Amazon(true);
            case 'k':
                return new King(true);
            case 'p':
                return new Pawn(true);
            case 'q':
                return new Queen(true);
            default:
                return null; // Empty cell
        }
    }

    /**Draw the checkerboard for the game*/
    public void draw(){
        int x ;
        int y ;
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                x = j * CELLSIZE;
                y = i * CELLSIZE;

                if ((i + j) % 2 == 0) {
                    parent.fill(139, 69, 19);
                } else {
                    parent.fill(210, 180, 140);
                }

                parent.noStroke();
                parent.rect(x,y,CELLSIZE,CELLSIZE);
            }
        }
    }
}
