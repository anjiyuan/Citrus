/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.qut.citrus.include;
import java.awt.Component;  
import java.awt.Dimension;  
import java.awt.Point;  
import java.awt.event.MouseEvent;  
import java.awt.event.MouseListener;  
import java.awt.event.MouseMotionListener;  
import javax.swing.JViewport;  
/**
 *
 * @author an
 */
public class JDragableViewport  extends JViewport {  
    
  public JDragableViewport() {  
      
    MouseDragHandler handler = this.new MouseDragHandler();  
    addMouseListener(handler);  
    addMouseMotionListener(handler);  
  }  
    
  @Override  
  public void setViewPosition(Point p) {  
      
    p.x = Math.max(0, p.x);  
    p.y = Math.max(0, p.y);  
      
    Component v = getView();  
    if( v != null ) {  
        
      Dimension d = v.getPreferredSize();  
      Dimension size = getSize();  
      p.x = Math.min(d.width - size.width, p.x);  
      p.y = Math.min(d.height - size.height, p.y);  
    }  
      
    super.setViewPosition(p);  
  }  
    
  private class MouseDragHandler implements MouseListener, MouseMotionListener {  
      
    private Point cursor = new Point();  
    private Point view = new Point();  
  
    @Override  
    public void mouseClicked(MouseEvent e) {}  
  
    @Override  
    public void mouseReleased(MouseEvent e) {}  
  
    @Override  
    public void mouseEntered(MouseEvent e) {}  
  
    @Override  
    public void mouseExited(MouseEvent e) {}  
  
    @Override  
    public void mouseMoved(MouseEvent e) {}  
  
    @Override  
    public void mouseDragged(MouseEvent e) {  
        
      Point p = e.getPoint();  
      int dx = cursor.x - p.x;  
      int dy = cursor.y - p.y;  
      view.x += dx;  
      view.y += dy;  
        
      setViewPosition(view);  
        
      cursor = p;  
      view = getViewPosition();  
    }  
      
    @Override  
    public void mousePressed(MouseEvent e) {  
        
      cursor = e.getPoint();  
      view = getViewPosition();  
    }  
  }  
}  