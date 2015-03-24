package com.company;

import java.io.*;
import java.net.*;

import org.json.*;

public class Main {
    static String fallID = "1";
    static String fallArr;
    static int fallNR = 1;
    static int magLen = 0;
    static int linAccLen =0;

    public static void main(String args[]) throws Exception
    {
        String clientSentence;
        ServerSocket welcomeSocket = new ServerSocket(8765);
        System.out.println("Server started");
        while(true)
        {
            try{
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            clientSentence = inFromClient.readLine();
            decodeJson(clientSentence);
            writeJsonToFile(clientSentence);}
            catch (Exception e){
                System.out.println("ERROR: failed to connect to sender");}
        }
    }

    private static void decodeJson(String jsonString){
        try {
            JSONObject obj = new JSONObject(jsonString);
            fallID = obj.get("test_id").toString().replaceAll("\\s","");
            fallArr = obj.get("fall_detected_at_times").toString();
            JSONObject sensorData = obj.getJSONObject("sensor_data");
            JSONArray linAcc = sensorData.getJSONArray("linear_acceleration");
            JSONArray magField = sensorData.getJSONArray("magnetic_field_data");
            linAccLen = linAcc.length();
            magLen = magField.length();
        }catch (Exception e){
            System.out.println("ERROR: failed to decode file");
        }
    }

    private static void writeJsonToFile(String jsonString){
        try {
            String filePath;
            boolean done = false;
            fallNR = 0;
            int count = 1;
            while (!done) {
                filePath = "ID" + fallID + "NR" + count + ".json";
                File f = new File(filePath);
                if (!(f.exists())) {
                    byte dataToWrite[] = jsonString.getBytes();
                    FileOutputStream out = new FileOutputStream(filePath);
                    out.write(dataToWrite);
                    out.close();
                    done = true;
                    if (fallArr.length() >= 5) {
                        System.out.println(fallID + ", " + count + ": Fall Detected!    linAccLen: " + linAccLen + ", magLen: " + magLen);
                    } else
                        System.out.println(fallID + ", " + count + ": No fall          linAccLen: " + linAccLen + ", magLen: " + magLen);
                    //if (accLen<100) System.out.println("WARNING: too few acceleration data");
                } else count++;
            }
        }catch (Exception e){
            System.out.println("ERROR: failed to write file to disk");}
    }
}
