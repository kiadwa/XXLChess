package XXLChess;

import processing.core.PApplet;
import java.util.*;
public class AIPlayer extends Player {
    private Game game = null;
    List<Piece> myPieces;
    List<Piece> enemyPieces;
    Piece piecePicked;
    Cell pieceMoveTo;

    Piece enemyKing;
    Piece myking;

    public AIPlayer(boolean type, PApplet parent) {
        super(type = false, parent = parent);
    }

    public void setGame(Game game){
        this.game = game;
    }
    public void setMyking(){
        myking= game.getBKing();
    }
    public void setEnemyKing(){
        enemyKing = game.getWKing();
    }
    public void setMyPieces(){
        this.myPieces = this.game.getBoard().getAvailableBPiece();
    }
    public void setEnemyPieces(){
        this.enemyPieces = this.game.getBoard().getAvailableWPiece();
    }
    public List<Piece> getMyPieces() {
        return this.myPieces;
    }

    public Piece getPiecePicked() {
        return this.piecePicked;
    }
    public void clearPieceMoveTo(){
        this.pieceMoveTo = null;
    }
    public void clearPickedPiece(){
        this.piecePicked = null;
    }

    public boolean myAction() {
        this.game.isInCheckMate((King)myking);
        this.game.isInStaleMate((King)myking);
        this.game.getBoard().isInCheck();
        if (!this.game.getTurn()
                && !this.game.getCheckmateB()
                && !this.game.getCheckmateW()
                && !this.game.getisStalemate()) {
            this.choosingPiece();
            this.game.movement(piecePicked,pieceMoveTo);
            this.game.alternateTurn();
            clearPickedPiece();
            clearPieceMoveTo();
            return true;
        } else if(this.game.getCheckmateB() || this.game.getCheckmateW() || this.game.getisStalemate()){
            this.game.EndGame();
            return false;
        }else{
            return false;
        }
    }

    public void choosingPiece() {
        myPieces = this.game.getBoard().getAvailableBPiece();
        Piece bestPiece = null;
        Cell bestCelltoMoveto = null;
        for (Piece piece : myPieces) {
            List<Cell> thisPiecePossibleMoves = piece.getPossibleMoves(this.game.getBoard());
            for (Cell cell : thisPiecePossibleMoves) {
                if (this.game.isLegalMove(piece, cell)){
                    bestPiece = piece;
                    bestCelltoMoveto = cell;
                }
            }
        }
        this.piecePicked = bestPiece;
        this.pieceMoveTo = bestCelltoMoveto;
        }
    }

