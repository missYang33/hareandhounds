package com.oose2017.syang91.hareandhounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.ipAddress;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

/**
 * Created by syang91 on 9/17/17.
 */
public class Bootstrap {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 8080;
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception{
        // set up IP address & Port
        ipAddress(IP_ADDRESS);
        port(PORT);

        staticFileLocation("/public");

        //Create the model instance and then configure and start the web service
        try {
            GameService model = new GameService();
            new GameController(model);
        } catch (Exception ex) {
            logger.error("Failed to create a GameService instance. Aborting");
        }
    }
}

