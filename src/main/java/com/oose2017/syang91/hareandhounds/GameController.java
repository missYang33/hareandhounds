package com.oose2017.syang91.hareandhounds;

/**
 * Created by syang91 on 9/17/17.
 **/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oose2017.syang91.hareandhounds.pojo.FailReasonResponse;
import java.util.Collections;

import static spark.Spark.*;

public class GameController {
    private static final String API_CONTEXT = "/hareandhounds/api";
    private final GameService gameService;
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(GameService gameService){
        this.gameService = gameService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        // start a game
        post(API_CONTEXT + "/games","application/json",(request, response) -> {
            try{
                response.status(201);
                return gameService.startGame(request.body());
            }catch (GameService.MissArgumentException ex){
                logger.error("wrong argument!");
                response.status(400);
            }
            return Collections.EMPTY_MAP;
        },new JsonTransformer());

        // join a game
        put(API_CONTEXT+"/games/:id","application/json",(request, response) -> {
            try{
                response.status(200);
                return gameService.joinGame(request.params(":id"));
            }catch(GameService.InvalidGameException ex){
                logger.error("invalid game id!");
                response.status(404);
            }catch(GameService.SecondPlayerAlreadyJoinedException ex){
                logger.error("Second player already joined");
                response.status(410);
            }
            return Collections.EMPTY_MAP;
        },new JsonTransformer());

        // play a game
        post(API_CONTEXT+"/games/:id/turns","application/json",(request, response) -> {
            try{
                response.status(200);
                gameService.movePiece(request.params(":id"),request.body());
            }catch(GameService.InvalidGameException ex){
                logger.error("invalid game id!");
                response.status(404);
                FailReasonResponse res = new FailReasonResponse();
                res.reason = "INVALID_GAME_ID";
                return res;
            }catch(GameService.InvalidIdException ex){
                logger.error("invalid player id!");
                response.status(404);
                FailReasonResponse res = new FailReasonResponse();
                res.reason = "INVALID_PLAYER_ID";
                return res;
            }catch(GameService.IncorrectTurnOrIllegalMoveException ex){
                logger.error("incorrect turn!");
                response.status(422);
                FailReasonResponse res = new FailReasonResponse();
                res.reason = "INCORRECT_TURN";
                return res;
            }
            /*catch(GameService.IncorrectTurnException ex){
                logger.error("incorrect turn!");
                response.status(422);
                FailReasonResponse res = new FailReasonResponse();
                res.reason = "INCORRECT_TURN";
                return res;
            }catch(Board.GameIllegalMoveException ex){
                logger.error("illegal move!"+ex.getMessage());
                response.status(422);
                FailReasonResponse res = new FailReasonResponse();
                res.reason = "ILLEGAL_MOVE";
                return res;
            }*/
            return Collections.EMPTY_MAP;
        },new JsonTransformer());

        // describe game board
        get(API_CONTEXT+"/games/:id/board","application/json",(request, response) -> {
            try{
                response.status(200);
                return gameService.describeBoard(request.params(":id"));
            }catch(GameService.InvalidGameException ex){
                logger.error("invalid game id!");
                response.status(404);
            }
            return Collections.EMPTY_MAP;
        },new JsonTransformer());

        // describe game state
        get(API_CONTEXT+"/games/:id/state","application/json",(request, response) -> {
            try{
                response.status(200);
                return gameService.describeState(request.params(":id"));
            }catch (GameService.InvalidGameException ex){
                logger.error("invalid game id!");
                response.status(404);
            }
            return Collections.EMPTY_MAP;

        },new JsonTransformer());

    }
}
