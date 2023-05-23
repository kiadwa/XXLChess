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

public class App extends PApplet {

    public static final int SPRITESIZE = 480;

    public static final int CELLSIZE = 48;
    public static final int SIDEBAR = 120;
    public static final int BOARD_WIDTH = 14;

    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE;

    public static final int FPS = 60;
    public String configPath = "config.json";
    // game elements
    private static Board chess_board = null;

    private static Game game = null;
    private static Player Wplayer;
    private static Player Bplayer;
    private static AIPlayer AIplayer;
    private static boolean singlePlayer = true;
    private static int mouseX;
    private static int mouseY;


    public static final HashMap<String, PImage> piecePImageHashMap = new HashMap<>();
    private int keycode;
    private boolean escPressed = false;


    public static final boolean playerTurn = true;

    public App() {

        this.configPath = "config.json";

    }

    /**
     * Initialise the setting of the window size.
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
    public void setup() {
        frameRate(FPS);
        this.Wplayer = new Player(true, this);
        this.AIplayer = new AIPlayer(false,this);


        try {
            this.chess_board = new Board(this,"level1.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.chess_board.draw();
        this.game = new Game(Wplayer, AIplayer, this.chess_board, this);
        this.game.setBKing();
        this.game.setWKing();
        this.game.setCheckmateB(false);
        this.game.setCheckmateW(false);
        this.Wplayer.setTime_have_milisecond(180000);
        this.AIplayer.setTime_have_milisecond(180000);
        this.Wplayer.setGame(this.game);
        this.AIplayer.setGame(this.game);
        this.AIplayer.setMyking();
        this.AIplayer.setEnemyKing();
        this.AIplayer.setEnemyPieces();
        this.Bplayer = this.AIplayer;



        // Load images during setup
        // PImage spr = loadImage("src/main/resources/XXLChess/"+...);
        // load config
        JSONObject conf = loadJSONObject(new File(this.configPath));
        //Input Hash Map for piece and its images
        piecePImageHashMap.put("A", loadImage("src/main/resources/XXLChess/b-amazon.png"));
        piecePImageHashMap.put("H", loadImage("src/main/resources/XXLChess/b-archbishop.png"));
        piecePImageHashMap.put("B", loadImage("src/main/resources/XXLChess/b-bishop.png"));
        piecePImageHashMap.put("C", loadImage("src/main/resources/XXLChess/b-camel.png"));
        piecePImageHashMap.put("E", loadImage("src/main/resources/XXLChess/b-chancellor.png"));
        piecePImageHashMap.put("K", loadImage("src/main/resources/XXLChess/b-king.png"));
        piecePImageHashMap.put("N", loadImage("src/main/resources/XXLChess/b-knight.png"));
        piecePImageHashMap.put("G", loadImage("src/main/resources/XXLChess/b-knight-king.png"));
        piecePImageHashMap.put("P", loadImage("src/main/resources/XXLChess/b-pawn.png"));
        piecePImageHashMap.put("Q", loadImage("src/main/resources/XXLChess/b-queen.png"));
        piecePImageHashMap.put("R", loadImage("src/main/resources/XXLChess/b-rook.png"));
        piecePImageHashMap.put("a", loadImage("src/main/resources/XXLChess/w-amazon.png"));
        piecePImageHashMap.put("h", loadImage("src/main/resources/XXLChess/w-archbishop.png"));
        piecePImageHashMap.put("b", loadImage("src/main/resources/XXLChess/w-bishop.png"));
        piecePImageHashMap.put("c", loadImage("src/main/resources/XXLChess/w-camel.png"));
        piecePImageHashMap.put("e", loadImage("src/main/resources/XXLChess/w-chancellor.png"));
        piecePImageHashMap.put("k", loadImage("src/main/resources/XXLChess/w-king.png"));
        piecePImageHashMap.put("n", loadImage("src/main/resources/XXLChess/w-knight.png"));
        piecePImageHashMap.put("g", loadImage("src/main/resources/XXLChess/w-knight-king.png"));
        piecePImageHashMap.put("p", loadImage("src/main/resources/XXLChess/w-pawn.png"));
        piecePImageHashMap.put("q", loadImage("src/main/resources/XXLChess/w-queen.png"));
        piecePImageHashMap.put("r", loadImage("src/main/resources/XXLChess/w-rook.png"));
        this.Wplayer.setStartTime(millis());
        this.Wplayer.setInAction(true);
        this.game.StartGame();
        escPressed = false;
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    public void keyPressed() {
        if(keyCode == 27){
            this.keycode = keyCode;
            escPressed = true;
        }else if(keyCode == 82) {
            setup();
        }
    }

    /**
     * Receive key released signal from the keyboard.
     * as per assignment
     * Additional, s for single player
     * m for multiplayer
     */
    public void keyReleased() {
        if(keyCode == 27){
            this.keycode = keyCode;
            escPressed = true;
        }else if(keyCode == 82) {
            setup();
        }else if(keyCode == 120){
            exit();
        }else if(keyCode == 77){//pressing "M" trigger multiplayer mode, play with other human
                                //else game is default to single player
                                // reset the game to go back to single player
            setup();
            this.singlePlayer = false;
            this.Bplayer = new Player(false, this);
            this.Bplayer.setTime_have_milisecond(180000);
            this.Bplayer.setGame(game);
            this.game.setbPlayer(this.Bplayer);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
    @Override
    public void mousePressed(MouseEvent e){
        if(!singlePlayer) {
            this.mouseX = e.getX();
            this.mouseY = e.getY();
            if (!this.game.getEnd()) {
                mouseEventToAction(mouseX, mouseY);
            }
            if (this.game.getCheckmateB()
                    || this.game.getCheckmateW()
                    || this.game.getisStalemate()) {
                this.game.EndGame();
            }
        }else{
            this.mouseX = e.getX();
            this.mouseY = e.getY();
            if (!this.game.getEnd()) {
                mouseEventToAction(mouseX, mouseY);
                this.AIplayer.myAction();
            }
            if (this.game.getCheckmateB()
                    || this.game.getCheckmateW()
                    || this.game.getisStalemate()) {
                this.game.EndGame();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
    @Override
    public void exit(){

    }


    /**
     * Draw all elements in the game by current frame.
     */

    public void draw() {
        if(!singlePlayer){
            fill(180, 180, 180);
            rect(672, 500, 200, 105);
            fill(139,0,0,175);
            textSize(12);
            text("Mode: Multiplayer", 680, 540);
            text("*Reset game", 680, 560);
            text("to go back to",680,580);
            text("single player", 680,600);
        }else{
            fill(180, 180, 180);
            rect(672, 500, 200, 105);
            fill(139,0,0,175);
            textSize(12);
            text("Mode: Single Player", 680, 540);
            text("*Press key 'M'", 680, 560);
            text("to start",680,580);
            text("multiplayer", 680,600);

        }

        //draw pieces on board
        if(escPressed) { // if player decide to resign show message and stop timer, then end game
            this.game.draw();
            this.Bplayer.draw();
            this.Wplayer.draw();
            this.Wplayer.setStartTime(millis());
            this.Bplayer.setStartTime(millis());
            resign();
        }
        else if(this.game.getEnd()
                || this.game.getisStalemate()
                || this.game.getCheckmateW()
                || this.game.getCheckmateB()){ // if the game is ended, stop the timer and not allow further movement
            this.game.draw();
            this.Bplayer.draw();
            this.Wplayer.draw();
            this.Wplayer.setStartTime(millis());
            this.Bplayer.setStartTime(millis());
        }else { //if there is no alert, continue checking for game state and updating the game
            this.winByTime();
            this.game.draw();
            this.game.CheckPawnPromotion();
            this.Bplayer.draw();
            this.Wplayer.draw();
        }
    }



    /**
     * draw message if the player decide to resign and end the game by prohibited all functionality
     */
    public void resign(){
            fill(180, 180, 180);
            rect(672, 300, 120, 100);
            textSize(14);
            fill(139, 0, 0, 175);
            text("You Resign", 680, 320);
            this.game.EndGame();

    }
    /**Check if either of player has win by time*/
    public void winByTime(){
        if(Bplayer.getTime_have_milisecond() < 0 && Wplayer.getTime_have_milisecond() > 0){
            fill(180, 180, 180);
            rect(672, 300, 120, 100);
            textSize(14);
            fill(139,0,0,175);
            text("White win", 680, 320);
            text("by time!", 680, 350);
        }else if(Bplayer.getTime_have_milisecond() > 0 && Wplayer.getTime_have_milisecond() < 0){
            fill(180, 180, 180);
            rect(672, 300, 120, 100);
            textSize(14);
            fill(139,0,0,175);
            text("Black win", 680, 320);
            text("by time!", 680, 350);
        }

    }
    /**If player attempt to click on castling option on the board, Check if its legal to castling
     * @param mouseX x coordinates of mouse click
     * @param mouseY y coordinates of mouse click
     * @return true if castling was done, false for otherwise*/

    public boolean castlingHandle(int mouseX,int mouseY){
        Cell cellClicked = this.game.getClickedCell(mouseY,mouseX);
        if(this.game.getTurn()){
            Piece wking = this.game.getWKing();
            Piece farRook = ((King) wking).getFarRook();
            Piece closeRook = ((King) wking).getCloseRook();
            if(cellClicked.getY() == wking.getY() + 2 && wking.canCastleClose(chess_board)
                    && closeRook.canCastleClose(chess_board)){
                if(this.game.isCastlingLegal(false)){
                    this.game.castling(false);
                    this.game.setIllegalMoveState(false);
                    this.game.clearPieceInAction();
                    this.game.clearCurrentPossibleMoves();
                    this.game.alternateTurn();
                    return true;
                }else{
                    this.game.setIllegalMoveState(true);
                    return false;
                }
            }else if(cellClicked.getY() == wking.getY() - 2 && wking.canCastleFar(chess_board)
                    && farRook.canCastleFar(chess_board)){
                if(this.game.isCastlingLegal(true)){
                    this.game.setIllegalMoveState(false);
                    this.game.castling(true);
                    this.game.clearPieceInAction();
                    this.game.clearCurrentPossibleMoves();
                    this.game.alternateTurn();
                    return true;
                }else{
                    this.game.setIllegalMoveState(true);
                    return false;
                }
            }
        }else{
            Piece bking = this.game.getBKing();
            Piece farRook = ((King) bking).getFarRook();
            Piece closeRook = ((King) bking).getCloseRook();
            if(cellClicked.getY() == bking.getY() + 2 && bking.canCastleClose(chess_board)
                    && closeRook.canCastleClose(chess_board)){
                if(this.game.isCastlingLegal(false)){
                    this.game.castling(false);
                    this.game.setIllegalMoveState(false);
                    this.game.clearPieceInAction();
                    this.game.clearCurrentPossibleMoves();
                    this.game.alternateTurn();
                    return true;
                }else{
                    this.game.setIllegalMoveState(true);
                    return false;
                }
            }else if(cellClicked.getY() == bking.getY() - 2 && bking.canCastleFar(chess_board)
                    && farRook.canCastleFar(chess_board)){
                if(this.game.isCastlingLegal(true)){
                    this.game.setIllegalMoveState(false);
                    this.game.castling(true);
                    this.game.clearPieceInAction();
                    this.game.clearCurrentPossibleMoves();
                    this.game.alternateTurn();
                    return true;
                }else{
                    this.game.setIllegalMoveState(true);
                    return false;
                }
            }
        }
        return false;
    }
    /**Handle all mouse event during the game
     * @param mouseY y coordinates of mouse clicked
     * @param mouseX x coordinates of mouse clicked*/
    public void mouseEventToAction(int mouseX, int mouseY) {
        //processing mouse movement when click on piece, choose a move, and select and deselecting piece
        //condition to check if a clicked piece is not null and the clicked piece is of the correct player's turn
        Piece pieceClicked = this.game.getClickedPiece(mouseY, mouseX);
        Cell cellClicked = this.game.getClickedCell(mouseY, mouseX);
        if (pieceClicked != null
                && pieceClicked.getIsWhite() == this.game.getTurn()) { //clicked on a piece
        //and that piece is of right color of its turn.
            this.game.setPieceInActionOriginalPos(pieceClicked.getCell());

            if (pieceClicked != this.game.getPieceInAction()) {
                // if clicked on a different piece , switch that piece into action
                this.game.setToHiPieceOrgPos(true);
                this.game.setPieceInAction(pieceClicked);
                //this.game.setPieceInActionOriginalPos(this.game.getPieceInAction().getCell());
                this.game.setCurrentPossibleMoves();

            }
        // if clicked on a valid move in its possible moves, move piece there
        }else if (this.game.getCurrentPossibleMoves() != null
                && this.game.getCurrentPossibleMoves().contains(cellClicked)) {

            if (this.game.isLegalMove(this.game.getPieceInAction(), cellClicked)) {
                //if the move is considered legal, commence
                this.game.setIllegalMoveState(false);
                this.game.movement(this.game.getPieceInAction(), cellClicked);
                this.game.clearPieceInAction();
                this.game.clearCurrentPossibleMoves();

                /*alternate turn to the next player */
                this.game.alternateTurn();
            } else {//if it's not legal to make this move with this piece, set illegal move alert
                this.game.setIllegalMoveState(true);
                return;
            }
        //castling check and commence
        }else if(this.game.getPieceInAction()!= null
                && this.game.getPieceInAction() instanceof King
                && !this.game.getPieceInAction().getHasMoved()){
            castlingHandle(mouseX,mouseY);
        }else{//if clicked on an invalid move or click on an empty cell,
            // clear piece in action instance and its possible moves instance
            //ready for another piece to be select
            this.game.clearPieceInAction();
            this.game.clearCurrentPossibleMoves();
            this.game.clearPieceInActionOriginalPos();
    }
}



	// Add any additional methods or attributes you want. Please put classes in different files.


    public static void main(String[] args) {
        PApplet.main("XXLChess.App");

    }

}
