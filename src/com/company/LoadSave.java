package com.company;

import com.google.gson.*;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

import javax.swing.*;
import java.io.*;

/**
 * Created by Skynet on 12.03.2016.
 * Loads and saves created models
 * using Gson
 * Todo: put in array (then you can stop counting data)
 */
public class LoadSave {

    /**
     * All objects for serialize/deserialize
     */
    Gson gson;
    JsonObject jsonObj;
    JsonArray jsonArray;

    /**
     * All static String variables
     */
    public static final String path = "Savefile_Neural_Network.json";
    public static final String LABEL_GLOBAL_DATA = "EncogDataSet";
    public static final String LABEL_DATA = "data";
    public static final String LABEL_LABELS = "labels";

    /**
     * Tracks which data element was retained most recently
     */
    private int selectedIndex=0;

    public LoadSave(){
        init();
    }

    public void init(){
        gson = new Gson();
        jsonObj = new JsonObject();
        jsonArray = new JsonArray();
        jsonObj.add(LABEL_GLOBAL_DATA, jsonArray);
    }

    /**
     * Adds data and label to the JSON Array
     * @param data
     * @param labels
     */
    public void append(double[] data, double[] labels){
        JsonObject entry = new JsonObject();
        entry.add(LABEL_DATA, new JsonPrimitive(gson.toJson(data)));
        entry.add(LABEL_LABELS, new JsonPrimitive(gson.toJson(labels)));
        jsonArray.add(entry);
    }

    /**
     * Gets the amount of added data elements
     * @return
     */
    public int getSize(){
        return jsonArray.size();
    }

    /**
     * Removes data at position index
     * @param index
     */
    public void removeDataAt(int index){
        jsonArray.remove(index);
    }

    /**
     * Removes data that was retained
     * most-recent
     */
    public void removeLastData(){
        removeDataAt(selectedIndex);
    }

    private double[] retainData(String key, int index){
        // Memorize this position
        selectedIndex = index;
        JsonElement entry = ((JsonObject)jsonArray.get(index)).get(key).getAsJsonPrimitive();
        return gson.fromJson(entry.getAsString(), double[].class);
    }

    public double[] getDataAt(int i){
        return retainData(LABEL_DATA, i);
    }
    public double[] getLabelAt(int i) {
        return retainData(LABEL_LABELS, i);
    }

    /**
     * Deletes the savefile
     */
    public boolean reset(){
        int res =JOptionPane.showConfirmDialog(null, "Are you sure you want to delete \"" + path + "\"?");
        final int YES = 0;
        if(res == YES){
            init();
            save();
            JOptionPane.showMessageDialog(null, "\"" + path + "\" successfully deleted!");
            return true;
        }else{
            return false;
        }
    }

    /**
     * Saves all data into a file
     * @return
     */
    public boolean save(){
        try {
            Writer writer = new FileWriter(path);
            gson.toJson(jsonObj, writer);
            writer.close();
        }catch(Exception e){
            return false;
        }
        return true;
    }

    /**
     * Helper method for reading a files input
     * @param path
     * @return
     * @throws IOException
     */
    private String readFile(String path) throws IOException{
        StringBuffer jsonString = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        while (line != null) {
            jsonString.append(line);
            line = reader.readLine();
        }
        reader.close();
        return jsonString.toString();
    }

    /**
     * Loads data from a file
     * @return
     */
    public boolean load(){
        try{
            String fileData = readFile(this.path);
            this.jsonObj = gson.fromJson(fileData, JsonObject.class);
            this.jsonArray = this.jsonObj.getAsJsonArray(LABEL_GLOBAL_DATA);
            JOptionPane.showMessageDialog(null, getSize() + " Elements were restored!");
        }catch(IOException e) {
            JOptionPane.showMessageDialog(null, "File not found!");
            reset();
            return false;
        }catch(NullPointerException e){
            JOptionPane.showMessageDialog(null, "File corrupted! Resetting file!");
            reset();
            return false;
        }
        return true;
    }

}
