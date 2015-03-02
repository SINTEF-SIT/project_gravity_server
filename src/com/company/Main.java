package com.company;

import java.io.*;
import java.net.*;
import org.json.*;

public class Main {

    public static void main(String args[]) throws Exception
    {
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);
        JSONObject obj = new JSONObject();

        while(true)
        {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            decodeJson(clientSentence);
        }


    }

    private static void decodeJson(String jsonString){
        try {
            JSONObject obj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
