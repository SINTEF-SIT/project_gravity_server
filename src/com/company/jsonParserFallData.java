package com.company;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class jsonParserFallData {

    //read json file - calculate total acc and vertical acc
    static ArrayList<String> totAcc = new ArrayList<String>();
    static ArrayList<String> vertAcc = new ArrayList<String>();
    static ArrayList<String> fallIndexList = new ArrayList<String>();
    static ArrayList<String> movingNumberThing = new ArrayList<String>();
    static ArrayList<String> fallIndexPostList = new ArrayList<String>();

    //TODO: Skal gi ut: TotAccPhone, VertAccPhone, TotAccWatch

    public static void main(String[] args) {

        try {
            String id = "q";
            String nr = "1";
            parseJson(readJson("ID"+id+"NR"+nr+".json"));
           /* for (String d : totAcc){
                System.out.println(d);
            }
            System.out.println("------------Vertical Acceleration----------");
            for (String d : vertAcc){
                System.out.println(d);
            }*/
            System.out.println("------------fallIndex----------");
            for (String d : fallIndexList){
                System.out.println(d);
            }
            System.out.println("------------fallIndexPOST----------");
            for (String d : fallIndexPostList){
                System.out.println(d);
            }/*
            System.out.println("------------movingThing----------");
            for (String d : movingNumberThing){
                System.out.println(d);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    private static double fallIndex(JSONArray sensors, int startList, int endList) throws JSONException {

        List <Double> x = new ArrayList<Double>();
        List <Double> y = new ArrayList<Double>();
        List <Double> z = new ArrayList<Double>();
        int startValue = startList;

        for (int i = 0; i < sensors.length(); i++){
            x.add(sensors.getJSONObject(i).getDouble("x"));
            y.add(sensors.getJSONObject(i).getDouble("y"));
            z.add(sensors.getJSONObject(i).getDouble("z"));
        }

        List <List> sensorData = new ArrayList<List>();
        sensorData.add(x);
        sensorData.add(y);
        sensorData.add(z);

        double directionAcceleration = 0;
        double totAcceleration = 0;

        for (int i = 0; i < sensorData.size(); i++){
            for (int j = startValue; j < endList; j++){
                movingNumberThing.add(String.valueOf(Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2)).replace(".", ","));
                directionAcceleration += Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        return Math.sqrt(totAcceleration);
    }*/

    public static void  parseJson(String jsonString) throws Exception {
        JSONObject obj = new JSONObject(jsonString);
        JSONObject calculations = obj.getJSONObject("calculations");
        JSONArray phoneTotal = calculations.getJSONArray("phone_total_acceleration");
        JSONArray phoneVertical = calculations.getJSONArray("phone_vertical_acceleration");
        JSONArray watchFallIndex = calculations.getJSONArray("watch_fall_index");
        JSONArray watchDirection = calculations.getJSONArray("watch_direction_acceleration");
        JSONArray watchFallPost = calculations.getJSONArray(("watch_after_fall"));

        for (int i = 0; i < phoneTotal.length(); i++) {
            Double totValue = phoneTotal.getJSONObject(i).getDouble("value");
            totAcc.add(String.valueOf(totValue).replace(".", ","));
            Double vertValue = phoneVertical.getJSONObject(i).getDouble("value");
            vertAcc.add(String.valueOf(vertValue).replace(".", ","));
        }
        for (int i = 0; i<watchFallIndex.length(); i++){
            fallIndexList.add(String.valueOf(watchFallIndex.getJSONObject(i).getDouble("value")).replace(".", ","));
        }
        for (int i = 0; i<watchFallPost.length(); i++){
            fallIndexPostList.add(String.valueOf(watchFallPost.getJSONObject(i).getDouble("value")).replace(".", ","));
        }
        for (int i = 0; i<watchDirection.length(); i++){
            movingNumberThing.add(String.valueOf(watchDirection.getJSONObject(i).getDouble("value")).replace(".", ","));
        }


        /*
        boolean done = false;
        int iterations = 0;
        while (!done){
            int startValue = iterations *25+1;
            if (startValue+50 >= watchData.length()){
                fallIndexList.add(String.valueOf(fallIndex(watchData, startValue, watchData.length())).replace(".", ","));
                done = true;
            }
            else{
                fallIndexList.add(String.valueOf(fallIndex(watchData, startValue, startValue + 50)).replace(".", ","));
            }
            iterations++;
        }
        float[] degs = new float[3];
        float[] rotationMatrix = new float[9];
        if (geoRotVecData.length() > accData.length()) {
            //System.out.println("acc len: "+accData.length()+", MagField len: "+geoRotVecData.length()+", rotVec len: "+rotData.length());
            for (int i = 0; i < accData.length(); i++) {
                if(i >=geoRotVecData.length() )break;
                if(i >=rotData.length() )break;
                JSONObject temp = accData.getJSONObject(i);
                JSONObject temp2 = geoRotVecData.getJSONObject(i);
                JSONObject temp3 = rotData.getJSONObject(i);


                //this loop is a bit of a workaround. There is much more magnetic field data than acceleration data. This loop synchronizes the data.
                int c = 0;
                while (temp.getInt("time") > temp2.getInt("time")) {
                    c++;
                    if (c+i >= geoRotVecData.length()){
                        break;
                    }
                    temp2 = geoRotVecData.getJSONObject(i + c);
                }
                float[] rotArr = new float[5];
                float[] geoRotArr = new float[3];
                rotArr[0] = (float)temp3.getDouble("x");
                rotArr[1] = (float)temp3.getDouble("y");
                rotArr[2] = (float)temp3.getDouble("z");
                rotArr[3] = (float)temp3.getDouble("cos");
                rotArr[4] = (float)temp3.getDouble("eha");
                geoRotArr[0] = (float)temp2.getDouble("x");
                geoRotArr[1] = (float)temp2.getDouble("y");
                geoRotArr[2] = (float)temp2.getDouble("z");
                getRotationMatrix(rotationMatrix, null, rotArr, geoRotArr);
                getOrientation(rotationMatrix, degs);
                double tetaY = degs[2];
                double tetaZ = degs[0];
                double x = temp.getDouble("x");
                double y = temp.getDouble("y");
                double z = temp.getDouble("z");
                double vertAccD;
                double totalAcc = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                vertAccD = Math.abs(x*Math.sin(tetaZ) + y*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ));
                String totS = Double.toString(totalAcc).replace(".", ",");
                String vertS = Double.toString(vertAccD).replace(".", ",");
                totAcc.add(totS);
                vertAcc.add(vertS);
            }
        } else {
            System.out.println("acc len: "+accData.length()+", MagField len: "+geoRotVecData.length()+", rotVec len: "+rotData.length());
            for (int i = 0; i < geoRotVecData.length(); i++) {
                JSONObject temp = accData.getJSONObject(i);
                JSONObject temp2 = geoRotVecData.getJSONObject(i);
                JSONObject temp3 = rotData.getJSONObject(i);


                //this loop is a bit of a workaround. There is much more magnetic field data than acceleration data. This loop synchronizes the data.
                int c = 0;
                while (temp.getInt("time") < temp2.getInt("time")) {
                    c++;
                    if (c + i >= accData.length()) break;
                    temp = accData.getJSONObject(i + c);
                }
                float[] rotArr = new float[5];
                float[] geoRotArr = new float[3];
                rotArr[0] = (float)temp3.getDouble("x");
                rotArr[1] = (float)temp3.getDouble("y");
                rotArr[2] = (float)temp3.getDouble("z");
                rotArr[3] = (float)temp3.getDouble("cos");
                rotArr[4] = (float)temp3.getDouble("eha");
                geoRotArr[0] = (float)temp2.getDouble("x");
                geoRotArr[1] = (float)temp2.getDouble("y");
                geoRotArr[2] = (float)temp2.getDouble("z");
                getRotationMatrix(rotationMatrix, null, rotArr, geoRotArr);
                getOrientation(rotationMatrix, degs);
                double tetaY = degs[2];
                double tetaZ = degs[0];
                double x = temp.getDouble("x");
                double y = temp.getDouble("y");
                double z = temp.getDouble("z");
                double vertAccD;
                double totalAcc = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                vertAccD = Math.abs(x*Math.sin(tetaZ) + y*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ));
                String totS = Double.toString(totalAcc).replace(".", ",");
                String vertS = Double.toString(vertAccD).replace(".", ",");
                totAcc.add(totS);
                vertAcc.add(vertS);
            }*/
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

    }/*

    public static boolean getRotationMatrix(float[] R, float[] I, float[] gravity, float[] geomagnetic) {
        float Ax = gravity[0];
        float Ay = gravity[1];
        float Az = gravity[2];
        final float Ex = geomagnetic[0];
        final float Ey = geomagnetic[1];
        final float Ez = geomagnetic[2];
        float Hx = Ey*Az - Ez*Ay;
        float Hy = Ez*Ax - Ex*Az;
        float Hz = Ex*Ay - Ey*Ax;
        final float normH = (float)Math.sqrt(Hx*Hx + Hy*Hy + Hz*Hz);
        if (normH < 0.1f) {
            // device is close to free fall (or in space?), or close to
            // magnetic north pole. Typical values are  > 100.
            return false;
        }
        final float invH = 1.0f / normH;
        Hx *= invH;
        Hy *= invH;
        Hz *= invH;
        final float invA = 1.0f / (float)Math.sqrt(Ax*Ax + Ay*Ay + Az*Az);
        Ax *= invA;
        Ay *= invA;
        Az *= invA;
        final float Mx = Ay*Hz - Az*Hy;
        final float My = Az*Hx - Ax*Hz;
        final float Mz = Ax*Hy - Ay*Hx;
        if (R != null) {
            if (R.length == 9) {
                R[0] = Hx;     R[1] = Hy;     R[2] = Hz;
                R[3] = Mx;     R[4] = My;     R[5] = Mz;
                R[6] = Ax;     R[7] = Ay;     R[8] = Az;
            } else if (R.length == 16) {
                R[0]  = Hx;    R[1]  = Hy;    R[2]  = Hz;   R[3]  = 0;
                R[4]  = Mx;    R[5]  = My;    R[6]  = Mz;   R[7]  = 0;
                R[8]  = Ax;    R[9]  = Ay;    R[10] = Az;   R[11] = 0;
                R[12] = 0;     R[13] = 0;     R[14] = 0;    R[15] = 1;
            }
        }
        if (I != null) {
            // compute the inclination matrix by projecting the geomagnetic
            // vector onto the Z (gravity) and X (horizontal component
            // of geomagnetic vector) axes.
            final float invE = 1.0f / (float)Math.sqrt(Ex*Ex + Ey*Ey + Ez*Ez);
            final float c = (Ex*Mx + Ey*My + Ez*Mz) * invE;
            final float s = (Ex*Ax + Ey*Ay + Ez*Az) * invE;
            if (I.length == 9) {
                I[0] = 1;     I[1] = 0;     I[2] = 0;
                I[3] = 0;     I[4] = c;     I[5] = s;
                I[6] = 0;     I[7] =-s;     I[8] = c;
            } else if (I.length == 16) {
                I[0] = 1;     I[1] = 0;     I[2] = 0;
                I[4] = 0;     I[5] = c;     I[6] = s;
                I[8] = 0;     I[9] =-s;     I[10]= c;
                I[3] = I[7] = I[11] = I[12] = I[13] = I[14] = 0;
                I[15] = 1;
            }
        }
        return true;
    }

    public static float[] getOrientation(float[] R, float values[]) {

        * 4x4 (length=16) case:
        *   /  R[ 0]   R[ 1]   R[ 2]   0  \
        *   |  R[ 4]   R[ 5]   R[ 6]   0  |
        *   |  R[ 8]   R[ 9]   R[10]   0  |
        *   \      0       0       0   1  /
        *
        * 3x3 (length=9) case:
        *   /  R[ 0]   R[ 1]   R[ 2]  \
        *   |  R[ 3]   R[ 4]   R[ 5]  |
        *   \  R[ 6]   R[ 7]   R[ 8]  /
        *

        if (R.length == 9) {
            values[0] = (float)Math.atan2(R[1], R[4]);
            values[1] = (float)Math.asin(-R[7]);
            values[2] = (float)Math.atan2(-R[6], R[8]);
        } else {
            values[0] = (float)Math.atan2(R[1], R[5]);
            values[1] = (float)Math.asin(-R[9]);
            values[2] = (float)Math.atan2(-R[8], R[10]);
        }
        return values;
    }
    */
}
