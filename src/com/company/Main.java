package com.company;

import java.io.*;
import java.net.*;

import org.json.*;

public class Main {
    static String fallID = "1";
    static String fallArr;
    static int accLen =0;
    static int watchLen=0;
    static int hertz=0;
    static boolean debug = false;

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
                System.out.println("ERROR: failed to connect to sender");
                if (debug) e.printStackTrace();
            }
        }
    }

    private static void decodeJson(String jsonString){
        try {
            JSONObject obj = new JSONObject(jsonString);
            fallID = obj.get("test_id").toString().replaceAll("\\s","");
            fallArr = obj.get("fall_detected_at_times").toString();
            JSONObject sensorData = obj.getJSONObject("sensor_data");
            JSONArray linAcc = sensorData.getJSONArray("phone:linear_acceleration");
            JSONArray phonAcc = new JSONArray();
            try{phonAcc = sensorData.getJSONArray("watch:linear_acceleration");}
            catch (Exception e){}
            accLen = linAcc.length();
            watchLen=phonAcc.length();


            //calculating frequency:
            JSONObject firstArr = new JSONObject(linAcc.getJSONObject(0).toString());
            JSONObject lastArr = new JSONObject(linAcc.getJSONObject(accLen - 1).toString());
            double timediff = (lastArr.getInt("time") - firstArr.getInt("time"));
            hertz=(int)(accLen/(timediff/1000));

        }catch (Exception e){
            System.out.println("ERROR: failed to decode file");
            fallID="fail";
            if (debug) e.printStackTrace();
        }
    }

    private static void writeJsonToFile(String jsonString){
        try {
            String filePath;
            boolean done = false;
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
                        System.out.println(fallID + ", " + count + ": Fall Detected!    #phone: " + accLen +", #watch: "+watchLen+",    PhoneFrequency: "+hertz);
                    } else
                        System.out.println(fallID + ", " + count + ": No fall           #phone: " + accLen +", #watch: "+watchLen+",    PhoneFrequency: "+hertz);
                } else count++;
            }
        }catch (Exception e){
            System.out.println("ERROR: failed to write file to disk");
            if (debug) e.printStackTrace();
        }
    }
}
