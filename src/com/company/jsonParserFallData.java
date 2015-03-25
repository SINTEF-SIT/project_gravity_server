package com.company;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;


public class jsonParserFallData {

    //read json file - calculate total acc and vertical acc
    static ArrayList<String> totAcc = new ArrayList<String>();
    static ArrayList<String> vertAcc = new ArrayList<String>();
    static double highetstAcc=0;

    public static void main(String[] args) {

        try {
            parseJson(readJson("ID"+"walk"+"NR"+"1"+".json"));
            for (String d : totAcc){
                double temp = Double.parseDouble(d);
                if (temp>highetstAcc){
                    if(temp<25)highetstAcc=temp;
                }
                System.out.println(d);
            }
            //System.out.println("vertacc");

            for (String d : vertAcc){
                //System.out.println(d);
            }
            System.out.println("Peak acceleration: "+highetstAcc);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public static void  parseJson(String jsonString) throws Exception {
        System.out.println("hello");
        JSONObject obj = new JSONObject(jsonString);
        JSONObject sensorData = obj.getJSONObject("sensor_data");
        JSONArray accData = sensorData.getJSONArray("phone:linear_acceleration");
        JSONArray magField = sensorData.getJSONArray("phone:magnetic_field");
        if (magField.length() > accData.length()) {
            System.out.println("acc len: "+accData.length()+", MagField len: "+magField.length());
            for (int i = 0; i < accData.length(); i++) {
                JSONObject temp = accData.getJSONObject(i);
                JSONObject temp2;
                temp2 = magField.getJSONObject(i);
                //this loop is a bit of a workaround. There is much more magnetic field data than acceleration data. This loop synchronizes the data.
                int c = 0;
                while (temp.getInt("time") > temp2.getInt("time")) {
                    c++;
                    temp2 = magField.getJSONObject(i + c);
                }
                double x = temp.getDouble("x");
                double y = temp.getDouble("y");
                double z = temp.getDouble("z");
                double vertAccD;
                double tetaZ;
                double tetaY;
                tetaZ = temp2.getDouble("z");
                tetaY = temp2.getDouble("y");
                double totalAcc = Math.abs((9.81) - (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
                vertAccD = Math.abs(x * Math.sin(tetaZ) + y * Math.sin(tetaY) - z * Math.cos(tetaY) * Math.cos(tetaZ));
                //String totS = Double.toString(totalAcc).replace(".", ",");
                //String vertS = Double.toString(vertAccD).replace(".", ",");
                String totS = Double.toString(totalAcc);
                String vertS = Double.toString(vertAccD);
                totAcc.add(totS);
                vertAcc.add(vertS);
            }
        } else {
            System.out.println("not enough magfield data!");
            System.out.println("acc len: "+accData.length()+", MagField len: "+magField.length());
            for (int i = 0; i < accData.length(); i++) {
                JSONObject temp = accData.getJSONObject(i);
                double x = temp.getDouble("x");
                double y = temp.getDouble("y");
                double z = temp.getDouble("z");
                double totalAcc = Math.abs((9.81) - (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
                String totS = Double.toString(totalAcc).replace(".", ",");
                totAcc.add(totS);
            }
        }
    }

    public static String readJson(String filename) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String everything;
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            br.close();
        }
        return everything;

    }
}
