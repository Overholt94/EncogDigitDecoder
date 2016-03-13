package com.company;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Skynet on 12.03.2016.
 * Class is for analyzing the {@link DrawPanel}
 */
public class Analyzer {

    private Analyzer(){

    }

    public static double[] analyzeScreen(DrawPanel panel){
        ArrayList<Rectangle> rectangles = panel.dumpData();
        Rectangle drawRec = panel.getDrawRectangle();

        if(rectangles.isEmpty()){
            return null;
        }

        double[] hitArr = new double[DrawPanel.DIM_Y*DrawPanel.DIM_X];

        int recWidth = (int) drawRec.getWidth()/DrawPanel.DIM_X;
        int recHeight = (int)drawRec.getHeight()/DrawPanel.DIM_Y;

        for(int i=0;i<DrawPanel.DIM_X;i++){
            for(int j=0;j<DrawPanel.DIM_Y;j++){
                Rectangle hit = new Rectangle((int)drawRec.getX()+i*recWidth, (int)drawRec.getY()+j*recHeight, recWidth, recHeight);
                for(Rectangle rec : rectangles){
                    if(rec.intersects(hit)){
                        hitArr[i*DrawPanel.DIM_Y+j] = 1.0;
                        break;
                    }
                }
            }
        }
        return hitArr;
    }

    public static int analyzeDigitEncoding(double[] labels){
        for(int i=0;i<labels.length;i++){
            if(labels[i] != 0){
                return i;
            }
        }
        return -1;
    }
    public static int analyzeDigitEncodingWithUncertainty(double[] labels){
        double max = 0;
        int maxIndex = 0;
        for(int i=0;i<labels.length;i++){
            if(labels[i]>max){
                max = labels[i];
                maxIndex = i;
            }
            System.out.println("Probability for " + i + ": " + labels[i]);
        }
        return maxIndex;
    }

    public static double[] createDigitEncoding(int digit){
        if(digit<0 || digit>9){
            return null;
        }
        double[] label = new double[10];
        label[digit]=1.0;
        return label;
    }
}
