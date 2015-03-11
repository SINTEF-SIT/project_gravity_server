package com.company;

import java.io.*;
import java.net.*;

import org.json.*;

public class Main {
    static int fallID = 1;
    static int fallNR = 1;

    public static void main(String args[]) throws Exception
    {
        String clientSentence;
        ServerSocket welcomeSocket = new ServerSocket(8765);
        System.out.println("Server started");

        while(true)
        {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            clientSentence = inFromClient.readLine();
            decodeJson(clientSentence);
            writeJsonToFile(clientSentence);
        }
    }

    private static void decodeJson(String jsonString) throws Exception{
        JSONObject obj = new JSONObject(jsonString);
        //System.out.println(obj.toString());
        try {
            fallID = Integer.parseInt(obj.get("test_id").toString());
            //fallNR = Integer.parseInt(obj.get("fall_nr").toString());
            System.out.println(fallID + "," + fallNR + ", " + obj.get("fall_detected_at_times"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void writeJsonToFile(String jsonString) throws Exception{
        String filePath;
        boolean done = false;
        fallNR=0;
        int count = 0;
        while (!done){
            filePath = "ID"+fallID+"NR"+fallNR+"."+count+".json";
            File f = new File(filePath);
            if (!(f.exists())){
                byte dataToWrite[] = jsonString.getBytes();
                FileOutputStream out = new FileOutputStream(filePath);
                out.write(dataToWrite);
                out.close();
                done = true;
            }else count++;
        }
    }
}
