//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.oose2017.syang91.hareandhounds;

import com.google.gson.Gson;
import com.oose2017.syang91.hareandhounds.pojo.StartGamePOJO;
import com.oose2017.syang91.hareandhounds.pojo.StartGameRequest;
import com.oose2017.syang91.hareandhounds.pojo.MovePieceRequest;
import com.oose2017.syang91.hareandhounds.pojo.StatePOJO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.lang.Exception;

public class GameService {

    private HashMap<String, Game> games = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(GameService.class);

/*    public GameService() throws GameServiceException {
    }*/
    // start a game
    public StartGamePOJO startGame(String body) throws MissArgumentException, BadRequestException {

        if (body.equals("")) {
            throw new MissArgumentException("missing argument or wrong argument!", null);
        }
        //generate a new game_id
        StartGameRequest request;
        try {
            request = new Gson().fromJson(body, StartGameRequest.class);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid request", ex);
        }
        if (!request.pieceType.equals("HOUND") && !request.pieceType.equals("HARE")) {
            throw new BadRequestException("Invalid pieceType", null);
        }

        StartGamePOJO sgPOJO = new StartGamePOJO();
        String uuid = UUID.randomUUID().toString();
        Game currGame = new Game(request.pieceType);
        Board newBoard = new Board();
        currGame.boards.add(newBoard);
        currGame.boardHistory.put(newBoard,1);
        games.put(uuid, currGame);
        sgPOJO.gameId = uuid;
        sgPOJO.setPieceType(request.pieceType);
        sgPOJO.setPlayerId(request.pieceType);
        return sgPOJO;
    }

    // join a game
    public StartGamePOJO joinGame(String gameId) throws InvalidGameException,SecondPlayerAlreadyJoinedException {
        StartGamePOJO sgPOJO = new StartGamePOJO();
        if (!games.containsKey(gameId)) {
            throw new InvalidGameException("this is an invalid game id", null);
        }
        sgPOJO.setGameId(gameId);
        Game game = games.get(gameId);
        //which player to join
        if (game.state.equals("WAITING_FOR_SECOND_PLAYER")) {
            game.state = "TURN_HOUND";
        } else {
            throw new SecondPlayerAlreadyJoinedException("Second player already joined", null);
        }
            if (game.pieceType.equals("HOUND")) {

                sgPOJO.setPieceType("HARE");
                sgPOJO.setPlayerId("HARE");

            } else {
                sgPOJO.setPieceType("HOUND");
                sgPOJO.setPlayerId("HOUND");
            }

            return sgPOJO;
        }



        // play a game
    public void movePiece(String gameId,String body)throws InvalidGameException, BadRequestException,
            InvalidIdException,IncorrectTurnOrIllegalMoveException {


        if (!games.containsKey(gameId)) {
            throw new InvalidGameException("this is an invalid game id", null);
        }
        Game game = games.get(gameId);
        MovePieceRequest request;

        if(game.state != "TURN_HARE" && game.state != "TURN_HOUND"){
            throw new IncorrectTurnOrIllegalMoveException("IncorrectTurn",null);
        }

        try {
            request = new Gson().fromJson(body, MovePieceRequest.class);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid request", ex);
        }
        if (request == null) {
            throw new BadRequestException("Invalid request", null);
        }

        if (!request.playerId.equals("HOUND") && !request.playerId.equals("HARE")) {
            throw new InvalidIdException("INVALID_PLAYER_ID", null);
        }

                if (!game.state.startsWith("TURN_")) {
            throw new IncorrectTurnOrIllegalMoveException("INCORRECT_TURN", null);
        }
        if (!game.state.equals("TURN_" + request.playerId)) {
            throw new IncorrectTurnOrIllegalMoveException("INCORRECT_TURN", null);
        }


        try {

            Board boardBeforeMov;
            boardBeforeMov = game.boards.get(game.boards.size() - 1);
            // game.boards.add(boardBeforeMov);
            Board boardAfterMov;
            boardAfterMov = boardBeforeMov.move(request.fromX, request.fromY, request.toX, request.toY, request.playerId);
            game.boards.add(boardAfterMov);
            Map currHistory = game.boardHistory;
            Iterator iter = currHistory.entrySet().iterator();
            Boolean isInserted = false;
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                Board tempBoard = (Board)entry.getKey();
                if(boardAfterMov.isEqual(tempBoard)){
                    currHistory.put(tempBoard,(Integer)entry.getValue()+1);
                    isInserted = true;
                }
            }
            if(!isInserted){
                currHistory.put(boardAfterMov,1);
            }
            // hound win
            if (((boardAfterMov.boardGrid[3][0] == 2) && (boardAfterMov.boardGrid[3][1] == 2) && (boardAfterMov.boardGrid[3][2] == 2) && (boardAfterMov.boardGrid[4][1] == 1)) ||
                ((boardAfterMov.boardGrid[1][0] == 2) && (boardAfterMov.boardGrid[2][1] == 2) && (boardAfterMov.boardGrid[3][0] == 2) && (boardAfterMov.boardGrid[2][0] == 1)) ||
                    ((boardAfterMov.boardGrid[1][2] == 2) && (boardAfterMov.boardGrid[2][1] == 2) && (boardAfterMov.boardGrid[3][2] == 2) && (boardAfterMov.boardGrid[2][2] == 1))) {
                game.state = "WIN_HOUND";
                return;
                // hare win by escape
            } else if ((boardAfterMov.hound1.getX() - boardAfterMov.hare.getX() >= 0) && (boardAfterMov.hound2.getX() - boardAfterMov.hare.getX() >= 0) && (boardAfterMov.hound3.getX() - boardAfterMov.hare.getX() >= 0)){
                game.state = "WIN_HARE_BY_ESCAPE";
                return;
            }

            // check for three times stalling
            Map newHistory = game.boardHistory;
            Iterator iter2 = currHistory.entrySet().iterator();
            while(iter2.hasNext()){
                Map.Entry entry = (Map.Entry)iter2.next();
                int count = (int)entry.getValue();
                if(count >= 3){
                    game.state = "WIN_HARE_BY_STALLING" ;
                    return;
                }
            }



            if (request.playerId.equals("HOUND")) {
                game.state = "TURN_HARE";
                return;
            } else if (request.playerId.equals("HARE")) {
                game.state = "TURN_HOUND";
                return;
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            //throw new IncorrectTurnOrIllegalMoveException("exception",null);
        }
    }
    // describe game board
    public List<Piece> describeBoard(String gameId) throws InvalidGameException{
        // int game_id = 0;

        List<Piece> returnList = new ArrayList<Piece>();
        if (!games.containsKey(gameId)) {
            throw new InvalidGameException("this is an invalid game id", null);
        }



        Game game = games.get(gameId);
        Board board = game.boards.get(game.boards.size() - 1);
        returnList.add(board.hound1);
        returnList.add(board.hound2);
        returnList.add(board.hound3);
        returnList.add(board.hare);

        return returnList;
    }

    //describe game state
    public StatePOJO describeState(String gameId) throws InvalidGameException {
        if(!games.containsKey(gameId)){
            throw new InvalidGameException("invalid game id!",null);
        }
        Game game = games.get(gameId);
        StatePOJO stPOJO = new StatePOJO();
        stPOJO.setState(game.state);
        return stPOJO;
    }



    public static class BadRequestException extends Exception {
        public BadRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class GameServiceException extends Exception {
        public GameServiceException(String message, Throwable cause) { super(message, cause); }
    }

    public static class InvalidIdException extends Exception {
        public InvalidIdException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SecondPlayerAlreadyJoinedException extends Exception {
        public SecondPlayerAlreadyJoinedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class IncorrectTurnOrIllegalMoveException extends Exception {
        public IncorrectTurnOrIllegalMoveException(String message, Throwable cause) {
            super(message, cause);
        }
    }




    public static class MissArgumentException extends Exception{
        public MissArgumentException(String message, Throwable cause){
            super(message,cause);
        }
    }
    public static class InvalidGameException extends Exception{
        public InvalidGameException(String message, Throwable cause){
            super(message, cause);
        }
    }
    public static class PlayerAlreadyJoinedException extends Exception{
        public PlayerAlreadyJoinedException(String message,Throwable cause){
            super(message, cause);
        }
    }
    public static class IncorrectTurnException extends Exception{
        public IncorrectTurnException(String message, Throwable cause){
            super(message, cause);
        }
    }
    public static class InvalidPlayerException extends Exception{
        public InvalidPlayerException(String message,Throwable cause){
            super(message, cause);
        }
    }

}

