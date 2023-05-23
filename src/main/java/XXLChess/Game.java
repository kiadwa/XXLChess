package XXLChess;

import processing.core.PApplet;

import java.util.*;

import static XXLChess.App.CELLSIZE;

public class Game {
    private PApplet parent;

    private boolean End = false;
    private Player activePlayer;
    private Board board;
    private King BKing;
    private King WKing;
    private boolean turn = true; //true for white, false for black, white start first

    private Piece pieceInAction; //set Piece which currently the player want to move
    private Cell pieceInActionOriginalPos;
    private boolean toHiPieceOrgPos = false;
    private List<Cell> currentPossibleMoves = new ArrayList<>();// possible moves of the current selected piece
    private Player wPlayer;
    private Player bPlayer;
    private boolean checkmateW = false;
    private boolean checkmateB = false;
    private boolean illegalMove = false;
    public boolean stalemate = false;


    public Game(Player wPlayer,Player bPlayer ,Board board, PApplet parent){
        this.wPlayer = wPlayer;
        this.bPlayer = bPlayer;
        this.board = board;
        this.parent = parent;
        this.activePlayer = wPlayer;
    }
    public void setbPlayer(Player player){this.bPlayer = player;}

    public void setStalemate(boolean bool){
        this.stalemate = bool;
    }
    public boolean getisStalemate(){
        return this.stalemate;
    }
    public void EndGame(){
        this.End = true;
    }
    public void StartGame(){
        this.End = false;
    }
    public boolean getEnd(){
        return this.End;
    }

    /**Check if there is pawn reach the end of the board for promotion
     * This will be called every frame to consistenly check for promotion*/
    public void CheckPawnPromotion(){
        HashMap<Cell, Piece> hm = this.getCurrentGameBoard();
        for (Map.Entry<Cell,Piece> entry: hm.entrySet()) {
            Piece piece = entry.getValue();
            Cell cell = entry.getKey();
            if (piece.getIsWhite()){
                if(piece instanceof Pawn && cell.getX() == 0){
                    
                    Queen queenFromPawn = new Queen(true);
                    this.board.getCell(cell.getX(),cell.getY()).setPiece(queenFromPawn);
                    queenFromPawn.setCell(cell);
                    queenFromPawn.setX(cell.getX());
                    queenFromPawn.setY(cell.getY());
                }
            }else{
                if(piece instanceof Pawn && cell.getX() == 13){
                    Queen queenFromPawn = new Queen(false);
                    this.board.getCell(cell.getX(),cell.getY()).setPiece(queenFromPawn);
                    queenFromPawn.setCell(cell);
                    queenFromPawn.setX(cell.getX());
                    queenFromPawn.setY(cell.getY());
                }
            }
        }

    }

    /*game checkmate and check management*/
    public void setBKing(){
        this.BKing = this.board.getB_King();
    }
    public void setWKing(){
        this.WKing = this.board.getW_King();
    }

    public Piece getBKing(){
        return (King) this.BKing;
    }
    public Piece getWKing(){
        return (King) this.WKing;
    }
    public void setIllegalMoveState(boolean bool){
        this.illegalMove = bool;
    }
    public boolean getIllegalMoveState(){
        return this.illegalMove;
    }

    public void setCheckmateW(boolean bool){
        this.checkmateW = bool;
    }
    public void setCheckmateB(boolean bool){
        this.checkmateB = bool;
    }

    public boolean getCheckmateW(){
        return this.checkmateW;
    }
    public boolean getCheckmateB(){
        return this.checkmateB;
    }



    /**Evaluate if a move is legal by simulating if a piece move there and get the state of the board
     * If a piece of x color attempt this move to/capture destination and result in x in check
     * it is considered an illegal move
     * @param piece piece about to move
     * @param destination cell it's about to move to
     * @return boolean true if the move is legal and false otherwise*/
    public boolean isLegalMove(Piece piece, Cell destination){
        List<Cell> myPossibleMoves = piece.getPossibleMoves(this.board);
            if (myPossibleMoves == null || myPossibleMoves.isEmpty()) {
                return false;
            } else if (myPossibleMoves.contains(destination)
                    && destination.getPiece() == null) {
                this.board.getCell(destination.getX(), destination.getY()).setPiece(piece);
                Cell myCell = piece.getCell();
                piece.setCell(destination);
                this.board.getCell(myCell.getX(), myCell.getY()).setPiece(null);
                if (this.board.isInCheck() == 1 && !piece.getIsWhite()) {
                    this.board.getCell(myCell.getX(), myCell.getY()).setPiece(piece);
                    this.board.getCell(destination.getX(), destination.getY()).setPiece(null);
                    piece.setCell(myCell);
                    return false;
                }
                if (this.board.isInCheck()==2 && piece.getIsWhite()){
                    this.board.getCell(myCell.getX(), myCell.getY()).setPiece(piece);
                    this.board.getCell(destination.getX(), destination.getY()).setPiece(null);
                    piece.setCell(myCell);
                    return false;
                }
                this.board.getCell(myCell.getX(), myCell.getY()).setPiece(piece);
                this.board.getCell(destination.getX(), destination.getY()).setPiece(null);
                piece.setCell(myCell);
            } else if (myPossibleMoves.contains(destination)
                    && destination.getPiece() != null) {
                Cell myCell = piece.getCell();
                this.board.getCell(myCell.getX(), myCell.getY()).setPiece(null);
                piece.setCell(destination);
                Piece orginalPieceatDestination = this.board.getCell(destination.getX(), destination.getY()).getPiece();
                orginalPieceatDestination.setCell(null);
                this.board.getCell(destination.getX(), destination.getY()).setPiece(piece);
                if (this.board.isInCheck()==1 && !piece.getIsWhite()) {
                    piece.setCell(myCell);
                    orginalPieceatDestination.setCell(destination);
                    this.board.getCell(myCell.getX(), myCell.getY()).setPiece(piece);
                    this.board.getCell(destination.getX(), destination.getY()).setPiece(orginalPieceatDestination);
                    return false;
                }

                if (this.board.isInCheck() == 2 && piece.getIsWhite()) {
                    piece.setCell(myCell);
                    orginalPieceatDestination.setCell(destination);
                    this.board.getCell(myCell.getX(), myCell.getY()).setPiece(piece);
                    this.board.getCell(destination.getX(), destination.getY()).setPiece(orginalPieceatDestination);
                    return false;
                }
                piece.setCell(myCell);
                orginalPieceatDestination.setCell(destination);
                this.board.getCell(myCell.getX(), myCell.getY()).setPiece(piece);
                this.board.getCell(destination.getX(), destination.getY()).setPiece(orginalPieceatDestination);
            }
            return true;
        }
    public boolean isInStaleMate(King king){
        if(king.getIsWhite() && this.board.isInCheck() == 0 && this.getTurn()){
            List<Piece> allWPieces = this.board.getAvailableWPiece();
            for(Piece piece: allWPieces){
                List<Cell> myPossibleMoves = piece.getPossibleMoves(board);
                for(Cell cell:myPossibleMoves){
                    if(isLegalMove(piece,cell)){
                        this.setStalemate(false);
                        return false;
                    }
                }
            }
            if(king.canCastleClose(board) && king.canCastleFar(board)){
                if(!this.isCastlingLegal(false) && !this.isCastlingLegal(true)){
                    this.setStalemate(true);
                    return true;
                }
            }
            else if(king.canCastleClose(board) && !king.canCastleFar(board)){
                if(!this.isCastlingLegal(false)) {
                    this.setStalemate(true);
                    return true;
                }
            }else if(!king.canCastleClose(board) && king.canCastleFar(board)){
                if(!this.isCastlingLegal(true)){
                    this.setStalemate(true);
                    return true;
                }
            }else if(!king.canCastleFar(board) && !king.canCastleClose(board)){
                this.setStalemate(true);
                return true;
            }
            this.setStalemate(false);
            return false;
        }else if (!king.getIsWhite() && this.board.isInCheck() == 0 && !this.getTurn()){
            List<Piece> allBPieces = this.board.getAvailableBPiece();
            for(Piece piece: allBPieces){
                List<Cell> myPossibleMoves = piece.getPossibleMoves(board);
                for(Cell cell:myPossibleMoves){
                    if(isLegalMove(piece,cell)){
                        this.setStalemate(false);
                        return false;
                    }
                }
            }
            if(king.canCastleClose(board) && king.canCastleFar(board)){
                if(!this.isCastlingLegal(false) && !this.isCastlingLegal(true)){
                    this.setStalemate(false);
                    return true;
                }
            }
            else if(king.canCastleClose(board) && !king.canCastleFar(board)){
                if(!this.isCastlingLegal(false)) {
                    this.setStalemate(true);
                    return true;
                }
            }else if(!king.canCastleClose(board) && king.canCastleFar(board)){
                if(!this.isCastlingLegal(true)){
                    this.setStalemate(true);
                    return true;
                }
            }else if(!king.canCastleFar(board) && !king.canCastleClose(board)){
                this.setStalemate(true);
                return true;
            }
        }
        this.setStalemate(false);
        return false;

    }


    public boolean isInCheckMate(King king){
        if(king.getIsWhite() && this.board.isInCheck() == 2){
                List<Piece> allWPieces = this.board.getAvailableWPiece();
                for(Piece piece: allWPieces){
                    List<Cell> myPossibleMoves = piece.getPossibleMoves(board);
                    for(Cell cell:myPossibleMoves){
                        if(isLegalMove(piece,cell)){
                            this.setCheckmateW(false);
                            return false;
                        }
                    }
                }
                if(king.canCastleClose(board) && king.canCastleFar(board)){
                    if(!this.isCastlingLegal(false) && !this.isCastlingLegal(true)){
                        this.setCheckmateW(true);
                        return true;
                    }
                }
                else if(king.canCastleClose(board) && !king.canCastleFar(board)){
                    if(!this.isCastlingLegal(false)) {
                        this.setCheckmateW(true);
                        return true;
                    }
                }else if(!king.canCastleClose(board) && king.canCastleFar(board)){
                    if(!this.isCastlingLegal(true)){
                        this.setCheckmateW(true);
                        return true;
                    }
                }else if(!king.canCastleFar(board) && !king.canCastleClose(board)){
                    this.setCheckmateW(true);
                    return true;
                }
            this.setCheckmateW(false);
            return false;
        }else if (!king.getIsWhite() && this.board.isInCheck() == 1){
                List<Piece> allBPieces = this.board.getAvailableBPiece();
                for(Piece piece: allBPieces){
                    List<Cell> myPossibleMoves = piece.getPossibleMoves(board);
                    for(Cell cell:myPossibleMoves){
                        if(isLegalMove(piece,cell)){
                            this.setCheckmateB(false);
                            return false;
                        }
                    }
                }
                if(king.canCastleClose(board) && king.canCastleFar(board)){
                    if(!this.isCastlingLegal(false) && !this.isCastlingLegal(true)){
                        this.setCheckmateB(true);
                        return true;
                    }
                }
                else if(king.canCastleClose(board) && !king.canCastleFar(board)){
                    if(!this.isCastlingLegal(false)) {
                        this.setCheckmateB(true);
                        return true;
                    }
                }else if(!king.canCastleClose(board) && king.canCastleFar(board)){
                    if(!this.isCastlingLegal(true)){
                        this.setCheckmateB(true);
                        return true;
                    }
                }else if(!king.canCastleFar(board) && !king.canCastleClose(board)){
                        this.setCheckmateB(true);
                        return true;
                }
            }
        this.setCheckmateB(false);
        return false;
    }

/** --Game movement mechanics and logic--
   Moving, set piece in action and calculate possible moves for a piece in action
    Only one piece in action is allowed in a turn
 **/

/**
 * set the piece choose by mouse click as the piece in action in the turn
 * @param piece input piece to be the piece in action*/
    public void setPieceInAction(Piece piece){
        if (this.getTurn() == piece.getIsWhite()){
            this.pieceInAction = piece;
        }
    }
    public void clearPieceInAction(){
        this.pieceInAction = null;
    }
    public HashMap<Cell,Piece> getCurrentGameBoard(){
        HashMap<Cell,Piece> currentBoardPiece = new HashMap<>();
        Cell[][] gameBoard = this.board.getGrid();
        for(Cell[] rows: gameBoard){
            for(Cell cell: rows){
                if (cell.getPiece()!=null){
                    currentBoardPiece.put(cell,cell.getPiece());
                }
                if(cell.getPiece() != null && cell.getPiece().getType().equals("k")){
                    this.WKing = (King) cell.getPiece();
                }
                else if(cell.getPiece() != null && cell.getPiece().getType().equals("K")){
                    this.BKing = (King) cell.getPiece();
                }
            }
        }
        return currentBoardPiece;
    }
    public void setPieceInActionOriginalPos(Cell cell){
        this.pieceInActionOriginalPos = cell;
    }
    public void setToHiPieceOrgPos(boolean bool){
        this.toHiPieceOrgPos = bool;
    }
    public void clearPieceInActionOriginalPos(){
        this.pieceInActionOriginalPos = null;
    }
    public boolean capture(Piece killer , Piece victim){ /**method for piece to capture opponents piece**/
        if(killer == null || victim == null || victim.getType().equals("K") || victim.getType().equals("k")){
            return false;
        }
        Cell k_cell = killer.getCell();
        Cell v_cell = victim.getCell();
        victim.Dead();
        this.board.getCell(v_cell.getX(),v_cell.getY()).setPiece(null);
        this.board.getCell(victim.getX(), victim.getY()).setPiece(killer);
        this.board.getCell(k_cell.getX(),k_cell.getY()).setPiece(null);
        killer.setCell(v_cell);
        return true;
    }
    /**Method for piece movement, if it is a capture, call capture then change position of piece and kill the target
     * @param piece Piece to move
     * @param destination Cell to move to
     * @return true if movement success and false otherwise*/
    public boolean movement(Piece piece, Cell destination){
        if (piece != null && piece.getPossibleMoves(this.board).contains(destination)
                && destination != null){
            this.pieceInActionOriginalPos = piece.getCell();
            this.board.getCell(piece.getX(),piece.getY()).setPiece(null);
            this.board.getCell(destination.getX(),destination.getY()).setPiece(piece);
            piece.setCell(destination);
            piece.setHasMoved(true);
            return true;
        }else if (piece!= null && destination.getPiece()!=null
                && !(destination.getPiece() instanceof King)
                && piece.canCaptureThis(this.board,destination.getPiece())){
            this.capture(piece,destination.getPiece());
        }
        return false;
    }
    /**Check if castling is legal by simulating if the castling is done to see if the game in check or not
     * @param bool true for castling to the Rook on the far side of the King, false for the Rook on the close side
     * @return boolean: true if it is legal to castling as per input, false for otherwise*/
    public boolean isCastlingLegal(boolean bool){
        if(this.getTurn()){
            Piece farRook = this.WKing.getFarRook();
            Piece closeRook = this.WKing.getCloseRook();
            int coorx_king = this.WKing.getX();
            int coory_king = this.WKing.getY();
            if (bool) {
                int coorx_rook = farRook.getX();
                int coory_rook = farRook.getY();
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king - 2).setPiece(this.WKing);
                this.WKing.setCell(this.board.getCell(coorx_king,coory_king - 2));
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king - 1).setPiece(farRook);
                farRook.setCell(this.board.getCell(coorx_rook,coory_king - 1));
                if(this.board.isInCheck() == 2){
                    this.WKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king,coory_king).setPiece(this.WKing);
                    this.board.getCell(coorx_king,coory_king - 2).setPiece(null);
                    this.board.getCell(coorx_rook,coory_rook).setPiece(farRook);
                    farRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    this.board.getCell(coorx_rook,coory_king - 1).setPiece(null);
                    return false;
                }else{
                    this.WKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king,coory_king).setPiece(this.WKing);
                    this.board.getCell(coorx_king,coory_king - 2).setPiece(null);
                    this.board.getCell(coorx_rook,coory_rook).setPiece(farRook);
                    farRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    this.board.getCell(coorx_rook,coory_king - 1).setPiece(null);
                    return true;
                }
            }else{
                int coorx_rook = closeRook.getX();
                int coory_rook = closeRook.getY();
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king + 2).setPiece(this.WKing);
                this.WKing.setCell(this.board.getCell(coorx_king,coory_king + 2));
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king + 1).setPiece(closeRook);
                closeRook.setCell(this.board.getCell(coorx_rook,coory_king + 1));
                if(this.board.isInCheck() == 2){
                    this.WKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king,coory_king).setPiece(this.WKing);
                    this.board.getCell(coorx_king,coory_king + 2).setPiece(null);
                    this.board.getCell(coorx_rook,coory_rook).setPiece(closeRook);
                    closeRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    this.board.getCell(coorx_rook,coory_king + 1).setPiece(null);
                    return false;
                }else {
                    this.WKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king,coory_king).setPiece(this.WKing);
                    this.board.getCell(coorx_king,coory_king + 2).setPiece(null);
                    this.board.getCell(coorx_rook,coory_rook).setPiece(closeRook);
                    closeRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    this.board.getCell(coorx_rook,coory_king + 1).setPiece(null);
                    return true;
                }
            }
        }else{
            Piece farRook = this.BKing.getFarRook();
            Piece closeRook = this.BKing.getCloseRook();
            int coorx_king = this.BKing.getX();
            int coory_king = this.BKing.getY();
            if (bool) {
                int coorx_rook = farRook.getX();
                int coory_rook = farRook.getY();
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king - 2).setPiece(this.BKing);
                this.BKing.setCell(this.board.getCell(coorx_king,coory_king - 2));
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king - 1).setPiece(farRook);
                farRook.setCell(this.board.getCell(coorx_rook,coory_king - 1));
                if (this.board.isInCheck() == 1) {
                    this.BKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king, coory_king).setPiece(this.BKing);
                    this.board.getCell(coorx_king, coory_king - 2).setPiece(null);
                    this.board.getCell(coorx_rook, coory_rook).setPiece(farRook);
                    this.board.getCell(coorx_rook, coory_king - 1).setPiece(null);
                    farRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    return false;
                } else {
                    this.BKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king, coory_king).setPiece(this.BKing);
                    this.board.getCell(coorx_king, coory_king - 2).setPiece(null);
                    this.board.getCell(coorx_rook, coory_rook).setPiece(farRook);
                    this.board.getCell(coorx_rook, coory_king - 1).setPiece(null);
                    farRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    return true;
                }
            }else{
                int coorx_rook = closeRook.getX();
                int coory_rook = closeRook.getY();
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king + 2).setPiece(this.BKing);
                this.BKing.setCell(this.board.getCell(coorx_king,coory_king + 2));
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king + 1).setPiece(closeRook);
                closeRook.setCell(this.board.getCell(coorx_rook,coory_king + 1));
                if(this.board.isInCheck() == 1){
                    this.BKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king,coory_king).setPiece(this.BKing);
                    this.board.getCell(coorx_king,coory_king + 2).setPiece(null);
                    this.board.getCell(coorx_rook,coory_rook).setPiece(closeRook);
                    closeRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    this.board.getCell(coorx_rook,coory_king + 1).setPiece(null);
                    return false;
                }else {
                    this.BKing.setCell(this.board.getCell(coorx_king,coory_king));
                    this.board.getCell(coorx_king,coory_king).setPiece(this.BKing);
                    this.board.getCell(coorx_king,coory_king + 2).setPiece(null);
                    this.board.getCell(coorx_rook,coory_rook).setPiece(closeRook);
                    closeRook.setCell(this.board.getCell(coorx_rook,coory_rook));
                    this.board.getCell(coorx_rook,coory_king + 1).setPiece(null);
                    return true;
                }
            }
        }
    }
    /**Method for commence the castling
     * @param bool for castling to the Rook at the far side of the King, false for the closer side Rook
     * @return boolean: true if castling as per input has been done, false otherwise*/
    public boolean castling(boolean bool){
        if(this.getTurn()){
            Piece farRook = this.WKing.getFarRook();
            Piece closeRook = this.WKing.getCloseRook();
            int coorx_king = this.WKing.getX();
            int coory_king = this.WKing.getY();
            if (bool){
                int coorx_rook = farRook.getX();
                int coory_rook = farRook.getY();
                //move king
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king - 2).setPiece(this.WKing);
                this.WKing.setY(coory_king - 2);
                this.WKing.setCell(this.board.getCell(coorx_king,coory_king - 2));
                this.WKing.setHasMoved(true);
                //move far rook
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king - 1).setPiece(farRook);
                farRook.setY(coory_king - 1);
                farRook.setCell(this.board.getCell(coorx_rook,coory_king - 1));
                farRook.setHasMoved(true);
                return true;
            }else{
                int coorx_rook = closeRook.getX();
                int coory_rook = closeRook.getY();

                //move king
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king + 2).setPiece(this.WKing);
                this.WKing.setY(coory_king + 2);
                this.WKing.setCell(this.board.getCell(coorx_king,coory_king + 2));
                this.WKing.setHasMoved(true);
                //move rook
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king + 1).setPiece(closeRook);
                closeRook.setY(coory_king + 1);
                closeRook.setCell(this.board.getCell(coorx_rook,coory_king + 1));
                closeRook.setHasMoved(true);
                return true;
            }
        }else{
            Piece farRook = this.BKing.getFarRook();
            Piece closeRook = this.BKing.getCloseRook();
            int coorx_king = this.BKing.getX();
            int coory_king = this.BKing.getY();
            if (bool){
                int coorx_rook = farRook.getX();
                int coory_rook = farRook.getY();
                //move king
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king - 2).setPiece(this.BKing);
                this.BKing.setY(coory_king - 2);
                this.BKing.setCell(this.board.getCell(coorx_king,coory_king - 2));
                this.BKing.setHasMoved(true);
                //move far rook
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king - 1).setPiece(farRook);
                farRook.setY(coory_king - 1);
                farRook.setCell(this.board.getCell(coorx_rook,coory_king - 1));
                farRook.setHasMoved(true);
                return true;
            }else{
                int coorx_rook = closeRook.getX();
                int coory_rook = closeRook.getY();
                //move king
                this.board.getCell(coorx_king,coory_king).setPiece(null);
                this.board.getCell(coorx_king,coory_king + 2).setPiece(this.BKing);
                this.BKing.setY(coory_king + 2);
                this.BKing.setCell(this.board.getCell(coorx_king,coory_king + 2));
                this.BKing.setHasMoved(true);
                //move rook
                this.board.getCell(coorx_rook,coory_rook).setPiece(null);
                this.board.getCell(coorx_rook,coory_king + 1).setPiece(closeRook);
                closeRook.setY(coory_king + 1);
                closeRook.setCell(this.board.getCell(coorx_rook,coory_king + 1));
                closeRook.setHasMoved(true);
                return true;
            }
        }
    }
    /**Get the clicked Piece from App.MousePressed
     * @param mouseX mouse X coordinates
     * @param mouseY mouse Y coordinates
     * @return Piece: return a piece that player clicked*/
    public Piece getClickedPiece(int mouseX, int mouseY){
        int row = mouseX / CELLSIZE;
        int col = mouseY / CELLSIZE;
        if (row >= 0 && row < 14 && col >= 0 && col < 14) {
            return this.board.getCell(row,col).getPiece();
        } else {
            return null; // Return null if clicked outside of board bounds
        }
    }
    /**Get the clicked Cell from App.MousePressed
     * @param mouseX mouse X coordinates
     * @param mouseY mouse Y coordinates
     * @return return a Cell that was clicked*/
    public Cell getClickedCell(int mouseX, int mouseY) {
        int row = mouseX / CELLSIZE;
        int col = mouseY / CELLSIZE;

        // Check if the coordinates are within the board bounds
        if (row >= 0 && row < 14 && col >= 0 && col < 14) {
            return this.board.getCell(row,col);
        } else {
            return null; // Return null if clicked outside of board bounds
        }
    }
    public void clearCurrentPossibleMoves(){
        this.currentPossibleMoves = null;}
    public void setCurrentPossibleMoves(){
        this.currentPossibleMoves = this.pieceInAction.getPossibleMoves(this.board);

    }
    /**draw the game**/
    public void draw() {
        HashMap<Cell, Piece> hm = this.getCurrentGameBoard();
        this.board.draw();
        /** box for game state text**/
        parent.fill(180, 180, 180);
        parent.rect(672, 300, 120, 100);



        //highlight original position of the piece just make the move
        if(pieceInActionOriginalPos!= null) {
            parent.fill(230, 250, 0, 150);
            int x_test = pieceInActionOriginalPos.getX();
            int y_test = pieceInActionOriginalPos.getY();
            parent.noStroke();
            parent.rect(y_test * CELLSIZE, x_test * CELLSIZE, CELLSIZE, CELLSIZE);
        }

        if(pieceInAction != null && !this.End ) {
            //highlight player's selected piece
            int x = pieceInAction.getX();
            int y = pieceInAction.getY();
            parent.fill(144, 238, 144, 100); //green
            parent.noStroke();
            parent.rect(y * CELLSIZE, x * CELLSIZE, CELLSIZE, CELLSIZE);
            //drawpiece
            if (currentPossibleMoves != null && !this.End) {
                //highlight possible moves after click on a piece and initialize piece in action & possible capture
                for (Cell cell : currentPossibleMoves) {
                    if (cell.getPiece() != null
                            && !cell.getPiece().isSameSide(this.pieceInAction)
                            && this.isLegalMove(pieceInAction,cell)) {
                        parent.fill(255, 0, 0, 150); //red for highlight possible capture
                        int x_cell = cell.getX();
                        int y_cell = cell.getY();
                        parent.noStroke();
                        parent.rect(y_cell * CELLSIZE, x_cell * CELLSIZE, CELLSIZE, CELLSIZE);
                    } else if(this.isLegalMove(pieceInAction,cell)) {
                        parent.fill(135, 206, 250, 100); //blue for possible moves and not a capture;
                        int x_cell = cell.getX();
                        int y_cell = cell.getY();
                        parent.noStroke();
                        parent.rect(y_cell * CELLSIZE, x_cell * CELLSIZE, CELLSIZE, CELLSIZE);
                    }
                }
            }
            //highlight original position of the piece who just make a move
            //Castling highlight
                if(this.getTurn()
                        && this.pieceInAction.equals(this.WKing)){

                    if(this.WKing.canCastleClose(board)){
                        int yking_close_castling = this.WKing.getY() + 2;
                        int xking_close_castling = this.WKing.getX();
                        parent.fill(135, 206, 250, 100); //blue for possible moves and not a capture;
                        parent.noStroke();
                        parent.rect(yking_close_castling * CELLSIZE, xking_close_castling * CELLSIZE, CELLSIZE, CELLSIZE);
                    }
                    if(this.WKing.canCastleFar(board)){
                        int yking_far_castling = this.WKing.getY() - 2;
                        int xking_far_castling = this.WKing.getX();
                        parent.fill(135, 206, 250, 100); //blue for possible moves and not a capture;
                        parent.noStroke();
                        parent.rect(yking_far_castling * CELLSIZE, xking_far_castling * CELLSIZE, CELLSIZE, CELLSIZE);
                    }

                }else if(!this.getTurn()
                        && this.pieceInAction.equals(this.BKing)){
                    Piece closeRook = this.BKing.getCloseRook();
                    Piece farRook = this.BKing.getFarRook();
                    if(this.BKing.canCastleClose(board)){
                        int yking_close_castling = this.BKing.getY() + 2;
                        int xking_close_castling = this.BKing.getX();
                        parent.fill(135, 206, 250, 100); //blue for possible moves and not a capture;
                        parent.noStroke();
                        parent.rect(yking_close_castling * CELLSIZE, xking_close_castling * CELLSIZE, CELLSIZE, CELLSIZE);
                    }
                    if(this.BKing.canCastleFar(board)){
                        int yking_far_castling = this.BKing.getY() - 2;
                        int xking_far_castling = this.BKing.getX();
                        parent.fill(135, 206, 250, 100); //blue for possible moves and not a capture;
                        parent.noStroke();
                        parent.rect(yking_far_castling * CELLSIZE, xking_far_castling * CELLSIZE, CELLSIZE, CELLSIZE);
                    }
                }
        }
        /*this part highlight the king when being checked, checkmate & stalemate*/
        if (this.board.isInCheck() == 2){
            parent.fill(150,0,0,175); //dark red, for king in check
            parent.textSize(14);
            parent.text("White in check!", 680, 320);
            parent.noStroke();
            parent.rect(this.WKing.getY() * CELLSIZE, this.WKing.getX() * CELLSIZE, CELLSIZE,CELLSIZE);
            if(this.getIllegalMoveState()){ //display message when player attempt illegal move
                parent.fill(180, 180, 180);
                parent.rect(672, 300, 120, 100);
                parent.textSize(14);
                parent.fill(139,0,0,175);
                parent.text("You must", 680, 320);
                parent.text("defend", 680, 350);
                parent.text("your king!", 680, 380);
            }
            if(this.isInCheckMate(this.WKing)){
                parent.fill(180, 180, 180);
                parent.rect(672, 300, 120, 100);
                parent.textSize(14);
                parent.fill(139,0,0,175);
                parent.text("Black win", 680, 320);
                parent.text("by", 680, 350);
                parent.text("Checkmate!", 680, 380);

            }
        }else if (this.board.isInCheck() == 1){
            parent.fill(150,0,0,175); // dark red, for king in check
            parent.textSize(14);
            parent.text("Black in check!", 680, 320);
            parent.noStroke();
            parent.rect(this.BKing.getY() * CELLSIZE, this.BKing.getX() * CELLSIZE, CELLSIZE,CELLSIZE);
            if(this.getIllegalMoveState()){//display message when player attempt illegal move
                parent.fill(180, 180, 180);
                parent.rect(672, 300, 120, 100);
                parent.textSize(14);
                parent.fill(139,0,0,175);
                parent.text("You must", 680, 320);
                parent.text("defend", 680, 350);
                parent.text("your king!", 680, 380);
            }
            if(this.isInCheckMate(this.BKing)){ //display message when checkmate
                parent.fill(180, 180, 180);
                parent.rect(672, 300, 120, 100);
                parent.textSize(14);
                parent.fill(139,0,0,175);
                parent.text("White win", 680, 320);
                parent.text("by", 680, 350);
                parent.text("Checkmate!", 680, 380);
            }
        }else{
            if(this.isInStaleMate(this.WKing) || this.isInStaleMate(this.BKing)){ //display message when stalemate
                parent.fill(180, 180, 180);
                parent.rect(672, 300, 120, 100);
                parent.textSize(14);
                parent.fill(139,0,0,175);
                parent.text("Stalemate!", 680, 320);
                parent.text("Draw", 680, 350);
            }
        }



        //this part draw ever frame the normal state of the game after movement
        //  with no highlighting

        for (Map.Entry<Cell,Piece> entry: hm.entrySet()){
            Piece piece = entry.getValue();
            Cell cell = entry.getKey();

            piece.draw(this.parent);

        }
    }

 /**Player management**/

 /**Using boolean value to alternate player turn efficiently
  * @param bool for white turn and false for black turn
  **/
    public void setTurn(boolean bool){this.turn = bool;}
    public boolean getTurn(){
        return this.turn;
    }

/**set the Player input as the active player and can act in this turn
 * @param player Player to be active in this turn
 * */
    public void setActivePlayer(Player player){
        this.activePlayer = player;
    }

/**Alternate turn between player, set active player based on turn and initiate player's clock
 * add time of 2 second to the clock when player finish a move
 * Unable the current player of its turn and enable the player of next turn as the active player
 * Stop the timer for player whose turn isn't in the current turn.
 */
    public void alternateTurn(){
        /*called when a player finished a move, stop the timer and switch turn to other player*/
        if (this.getTurn()){
            this.activePlayer.setEndTime(parent.millis());
            this.activePlayer.updateTimer();
            this.activePlayer.setInAction(false);
            this.setTurn(false);
            this.setActivePlayer(bPlayer);
            this.activePlayer.setInAction(true);
            this.activePlayer.setStartTime(parent.millis());
           // this.clearPieceInActionOriginalPos();
        }else{
            this.activePlayer.setEndTime(parent.millis());
            this.activePlayer.updateTimer();
            this.activePlayer.setInAction(false);
            this.setTurn(true);
            this.setActivePlayer(wPlayer);
            this.activePlayer.setInAction(true);
            this.activePlayer.setStartTime(parent.millis());
           // this.clearPieceInActionOriginalPos();
        }
    }

//getter
    public Board getBoard(){return this.board;}
    public Piece getPieceInAction(){return this.pieceInAction;}

    public List<Cell> getCurrentPossibleMoves(){return this.currentPossibleMoves;}



}
