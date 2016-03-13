package com.company;

import com.company.DrawPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by Skynet on 12.03.2016.
 */
public class DrawViewMouseMotionListener implements MouseMotionListener {

    DrawPanel panel;

    public DrawViewMouseMotionListener(DrawPanel panel){
        this.panel = panel;
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        panel.strokeCallback(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
