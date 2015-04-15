package com.company;

import java.io.*;
import java.net.*;
import org.json.*;

public class Main {
    static String fallID;
    static int phoneVertLen =0;
    static int phoneTotLen = 0;
    static int watchFallIndexLen = 0;
    static double timediff;

    static boolean debug = true;

    //This is a server for receiving sensor recordings from the "project gravity" client.

    public static void main(String args[]) throws Exception
    {
        String clientMessage;
        ServerSocket welcomeSocket = new ServerSocket(8765);
        System.out.println("Server started");
        while(true)
        {
            try{
                Socket connectionSocket = welcomeSocket.accept();   //accepting new incoming recordings
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientMessage = inFromClient.readLine();
                decodeJson(clientMessage);  //Extracts meta info from the recording. - also runs the "writeJsonToFile()" method
            }
            catch (Exception e){
                System.out.println("ERROR: failed to connect to sender");
                if (debug) e.printStackTrace();
            }
        }
    }

    /*
    decodeJson extracts meta information from the received recording.
    It retrieves:
        fallID - which is later used as the filename. If no fallID is found, the id will be set "unnamed"
        Lengths of arrays - used to debug incoming recording
        Length of the recording
    */
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

            writeJsonToFile(jsonString,fallID);

        }catch (Exception e){
            System.out.println("ERROR: failed to decode file");
            try{fallID="fail_"+fallID+"_fail";} catch (Exception k){fallID="unnamed";}
            if (debug) e.printStackTrace();
        }
    }

    /* This method checks if the algorithms in the client indicated a fall
    the "id" field is a unique id of a specific set of samples throughout the algorithms.
    The recording contains information about each time an algorithm evaluates a set of data.
    to see if there is an indicated fall. You must follow the ID, and see whether each algorithm returned "true" or "false"
    if all none returned "true" - it was considered a fall. When an algorithm returns "false", no more algorithms are run for that id.
    */
    private static boolean containsFall(JSONArray fall) {
        try {
            double id = 0;
            JSONObject temp;
            boolean wasfall = false;
            for (int i = 0; i < fall.length(); i++) {
                temp = fall.getJSONObject(i);
                if (temp.getDouble("id") == id)
                    if (!temp.getBoolean("isFall")) wasfall = false;
                    else wasfall = true;
                else {
                    if (wasfall) return true;   //this indicates that a new ID is found - and that the last algorithm check was true - therefor it is a fall
                    id = temp.getDouble("id");
                }
            }
        }catch (Exception e) {
            if(debug)e.printStackTrace();
            else System.out.println("ERROR: failed to check for falls");
        }
        return false;
    }

    private static void writeJsonToFile(String jsonString, String fallID){
        try {
            JSONObject temp = new JSONObject(jsonString);
            String filePath;
            int count = 1;
            boolean done = false;
            // This while loop loops through possible filenames to find the lowest available.
            while (!done) {
                filePath = "ID" + fallID + "NR" + count + ".json";
                File f = new File(filePath);
                if (!(f.exists())) {
                    byte dataToWrite[] = jsonString.getBytes();
                    FileOutputStream out = new FileOutputStream(filePath);
                    out.write(dataToWrite);
                    out.close();
                    done = true;
                    System.out.println("Data received:   "+fallID + " - " + count+"    "+containsFall(temp.getJSONArray("fall_detection")) + "     phoneData: "+phoneTotLen+", watchData: "+watchFallIndexLen+"    Time: "+timediff+" sek");
                } else count++;
            }
        }catch (Exception e){
            System.out.println("ERROR: failed to write file to disk");
            if (debug) e.printStackTrace();
        }
    }
}
