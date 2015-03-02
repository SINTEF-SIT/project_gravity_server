package com.company;

import java.io.*;
import java.net.*;

import org.json.*;

public class Main {
    public static int i;
    static int fallID;
    static int fallNR;

    public static void main(String args[]) throws Exception
    {
        String clientSentence;
        ServerSocket welcomeSocket = new ServerSocket(8765);
        System.out.println("Server started");
        i = 0;

        while(true)
        {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            clientSentence = inFromClient.readLine();
            System.out.println("Meld mottat");
            decodeJson(clientSentence);
            writeJsonToFile(clientSentence);
            i++;
        }
    }

    private static void decodeJson(String jsonString){
        try {
            JSONObject obj = new JSONObject(jsonString);
            System.out.println(obj.toString());
            fallID = Integer.parseInt(obj.get("fallID").toString());
            fallNR = Integer.parseInt(obj.get("fallNR").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeJsonToFile(String jsonString) throws Exception{
        String filePath;
        boolean done = false;
        while (!done){
            filePath = "ID"+fallID+"NR"+fallNR+","+i+".json";
            File f = new File(filePath);
            if (!(f.exists())){
                byte dataToWrite[] = jsonString.getBytes();
                FileOutputStream out = new FileOutputStream(filePath);
                out.write(dataToWrite);
                out.close();
                done = true;
            }else i++;
        }
    }
}
