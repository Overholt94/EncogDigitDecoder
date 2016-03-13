package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Skynet on 12.03.2016.
 */
public class DrawPanel extends JPanel {
    /**
     * Size of each Rectangle (for the HIT array)
     */
    public final static int DIM_X = 10,
                            DIM_Y = 10;

    /**
     * The rectangle in which to draw the digit
     */
    public static final int REC_WIDTH = 250,
                            REC_HEIGHT = 250;
    public static final int REC_Y_OFFSET = 100;
    public static final int REC_STROKE_WIDTH = 10;
    public static final int REC_STROKE_HEIGHT = 10;

    Rectangle drawDigit;

    /**
     * The parent view
     */
    MainFrame mainFrame;

    /**
     *
     */
    private static String DEFAULT_LABEL = "Draw a digit into the box below..";
    private String label = DEFAULT_LABEL;
    /**
     * All stored strokes
     */
    ArrayList<Rectangle> strokes;

    public DrawPanel(MainFrame mainFrame){
        this.mainFrame = mainFrame;
        strokes = new ArrayList<>();
        drawDigit = new Rectangle((mainFrame.SCREEN_WIDTH-REC_WIDTH)/2,(mainFrame.SCREEN_HEIGHT-REC_HEIGHT)/2-REC_Y_OFFSET,REC_WIDTH,REC_HEIGHT);
    }

    /**
     * Clearing the screen
     */
    public void clearScreen(){
        strokes = new ArrayList<>();
        label = DEFAULT_LABEL;
        repaint();
    }

    /**
     * Adding strokes if they are on the plane
     */
    public void strokeCallback(int x, int y) {
        Rectangle rec = new Rectangle(x,y,REC_STROKE_WIDTH, REC_STROKE_HEIGHT);
        if(drawDigit.contains(rec)){
            strokes.add(rec);
            repaint();
        }
    }

    public Rectangle getDrawRectangle(){
        return new Rectangle(drawDigit);
    }

    public ArrayList<Rectangle> dumpData(){
        return this.strokes;
    }

    public void setData(double[] arr){
        if(arr == null || arr.length<DIM_X*DIM_Y){
            JOptionPane.showMessageDialog(null, "File corrupted, could not load data!" + (arr== null));
            return;
        }
        int recWidth = (int)drawDigit.getWidth()/DIM_X;
        int recHeight = (int)drawDigit.getHeight()/DIM_Y;

        for(int i=0;i<DIM_X;i++){
            for(int j=0;j<DIM_Y;j++){
                if(arr[i*DIM_Y+j] > 0){
                    strokes.add(new Rectangle((int)drawDigit.getX()+recWidth*i,(int)drawDigit.getY()+recHeight*j,recWidth,recHeight));
                }
            }
        }
    }

    public void setLabel(String label){
        this.label = label;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawString(this.label,(int) drawDigit.getX(),(int) drawDigit.getY()-8);
        g.setColor(Color.WHITE);
        g.fillRect((int) drawDigit.getX(),(int) drawDigit.getY(),(int) drawDigit.getWidth(),(int) drawDigit.getHeight());
        g.setColor(Color.BLUE);
        for(Rectangle rec : strokes){
            g.fillRect((int) (rec.getX()),(int) (rec.getY()),(int) rec.getWidth(),(int) rec.getHeight());
        }

    }


}
