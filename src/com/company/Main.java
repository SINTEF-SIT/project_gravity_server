package com.company;

import java.io.*;
import java.net.*;
import org.json.*;

public class Main {
    static String fallID = "1";
    static String fallArr;
    static int phoneVertLen =0;
    static int phoneTotLen = 0;
    static int watchFallIndexLen = 0;
    static int phoneHz=0;
    static int watchHz=0;
    static double timediff;

    static boolean debug = true;

    public static void main(String args[]) throws Exception
    {
        String clientMessage;
        ServerSocket welcomeSocket = new ServerSocket(8765);
        System.out.println("Server started");
        while(true)
        {
            try{
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientMessage = inFromClient.readLine();
                decodeJson(clientMessage);
                writeJsonToFile(clientMessage);}
            catch (Exception e){
                System.out.println("ERROR: failed to connect to sender");
                if (debug) e.printStackTrace();
            }
        }
    }

    private static void decodeJson(String jsonString){
        try {
            JSONObject ob = new JSONObject(jsonString);
            fallID = ob.get("test_id").toString().replaceAll("\\s","");
            JSONObject obj = ob.getJSONObject("calculations");
            JSONArray phoneTot = obj.getJSONArray("phone_total_acceleration");
            JSONArray phoneVert = obj.getJSONArray("phone_vertical_acceleration");
            JSONArray watchIndex = obj.getJSONArray("watch_fall_index");

            phoneTotLen = phoneTot.length();
            phoneVertLen = phoneVert.length();
            watchFallIndexLen = watchIndex.length();

            JSONObject firstArr = new JSONObject(phoneTot.getJSONObject(0).toString());
            JSONObject lastArr = new JSONObject(phoneTot.getJSONObject(phoneTotLen - 1).toString());
            timediff = (lastArr.getInt("time") - firstArr.getInt("time"));
            timediff = timediff / 1000;


            /*
            fallArr = obj.get("fall_detected_at_times").toString();
            JSONObject sensorData = obj.getJSONObject("sensor_data");
            JSONArray linAcc = sensorData.getJSONArray("phone:linear_acceleration");
            JSONArray magf = sensorData.getJSONArray("phone:magnetic_field");
            JSONArray rota = sensorData.getJSONArray("phone:rotation_vector");
            JSONArray phonAcc = sensorData.getJSONArray("watch:linear_acceleration");
            rotLen = rota.length();
            magLen=magf.length();
            accLen = linAcc.length();
            watchLen=phonAcc.length();
            */

        }catch (Exception e){
            System.out.println("ERROR: failed to decode file");
            fallID="fail_"+fallID+"_fail";
            if (debug) e.printStackTrace();
        }
    }

    private static boolean containsFall(JSONArray fall) throws Exception{
        double id = 0;
        JSONObject temp;
        boolean wasfall = false;
        for (int i =0; i<fall.length(); i++){
            temp = fall.getJSONObject(i);
            if (temp.getDouble("id") == id){
                if (!temp.getBoolean("isFall")){
                    wasfall = false;
                }
                else wasfall = true;
            }
            else {
                if (wasfall){
                    return true;
                }
                id = temp.getDouble("id");
            }
        }
        return false;
    }

    private static void writeJsonToFile(String jsonString){
        try {
            JSONObject temp = new JSONObject(jsonString);

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
                    System.out.println("Data received:   "+fallID + " - " + count+"    "+containsFall(temp.getJSONArray("fall_detection")) + "     phoneData: "+phoneTotLen+", watchData: "+wat+"    Time: "+timediff+" sek");
                } else count++;
            }
        }catch (Exception e){
            System.out.println("ERROR: failed to write file to disk");
            if (debug) e.printStackTrace();
        }
       /* accLen=0;
        magLen=0;
        rotLen=0;*/
    }
}
