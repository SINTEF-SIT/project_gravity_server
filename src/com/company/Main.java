package com.company;

import java.io.*;
import java.net.*;

import org.json.*;

public class Main {
    static String fallID = "1";
    static String fallArr;
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
        String temp ="";
        //System.out.println(obj.toString());
        try {
            temp = obj.get("test_id").toString().replaceAll("\\s","");
            fallID = temp;
            fallArr = obj.get("fall_detected_at_times").toString();
            //fallNR = Integer.parseInt(obj.get("fall_nr").toString());
            //System.out.println(fallID + ", " + obj.get("fall_detected_at_times"));
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
            filePath = "ID"+fallID+"NR"+count+".json";
            File f = new File(filePath);
            if (!(f.exists())){
                byte dataToWrite[] = jsonString.getBytes();
                FileOutputStream out = new FileOutputStream(filePath);
                out.write(dataToWrite);
                out.close();
                done = true;

                if (fallArr.length()>=5){
                    System.out.println(fallID + ", " +count+": Fall Detected!");
                }else System.out.println(fallID + ", " +count+": No fall");
            }else count++;
        }
    }
}
