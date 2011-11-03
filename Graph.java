package graph;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

/**
 * The Graph class will create a JPanel and draw given points and equations while providing interactive viewing of the graph.
 * 
 * @author Patrick Jameson
 * @version 11.20.2010
 * @version 11.1.2011
 */
@SuppressWarnings("serial")
public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {
    private static final int DEFAULT_COMPRESSION = 1;
    
    private static final int DEFAULT_SCALE = 12;
    private static final int MIN_SCALE = 1;
    private static final int MAX_SCALE = 4000;
    private static final double ZOOM_SPEED = 0.3; //percentage zoomed in every mouse "notch"
    
    private static final Color BACKGROUND_COLOR = Color.white;
    private static final Color GRID_COLOR = new Color(230, 230, 230);//grayish
    private static final Color AXIS_COLOR = Color.black;
    private static final Color LINE_COLOR = Color.green;
    private static final Color EQUATION_COLOR = Color.red;
    
    private static final boolean DEFAULT_SHOW_POINT_DOTS = false;
    private static final boolean DEFAULT_CONNECT_POINTS = true;
    private static final boolean DEFAULT_RESTRICTED_RANGE = false;
    private static final boolean DEFAULT_RESTRICTED_DOMAIN = false;
    
	private int width, height, xAxisLoc, yAxisLoc, preX, preY, mouseX, mouseY;
    private int yStart, yEnd;
    private int scale, compression;
    private double[][] points = {}, equations = {};
    private boolean restrictedRange, restrictedDomain, showPointDots, connectPoints;
    private double range1, range2, domain1, domain2;
    
    /**
     * Creates a new graph of size (_width, _height).
     * @param _width the width of the new graph JPanel.
     * @param _height the height of the new graph JPanel.
     */
    public Graph(int _width, int _height) {
            width = _width;
            height = _height;
            xAxisLoc = width/2;
            yAxisLoc = height/2;
            
            scale = DEFAULT_SCALE;
            compression = DEFAULT_COMPRESSION;
            
            restrictedRange = DEFAULT_RESTRICTED_RANGE;
            restrictedDomain = DEFAULT_RESTRICTED_DOMAIN;
            
            showPointDots = DEFAULT_SHOW_POINT_DOTS;
            connectPoints = DEFAULT_CONNECT_POINTS;
            
            this.setRange(100, 100);
            
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            addComponentListener(this);
    }
    /**
     * Draws the graph.
     */
    public void paintComponent(Graphics g) {
            //clears graph.
            g.setColor(Color.gray);
            g.fillRect(0,0,width,height);
            
            if (restrictedRange) {
                    yStart = (int)(yAxisLoc-(scale*range1));
                    yEnd = (int)(yAxisLoc+(scale*range2));
            } else {
                    yStart = yAxisLoc%scale;//to line up the x axis with the background grid.
                    yEnd = height;
            }
            
            //clears graphing area.
            g.setColor(BACKGROUND_COLOR);
            if (restrictedRange) {
            	g.fillRect(0, yStart, width, yEnd-yStart);
            } else {
            	g.fillRect(0, 0, width, height);
            }
            
            //draws grid
            g.setColor(GRID_COLOR);//greyish
            for (int x = 0;x <= width;x+=scale)
                    g.drawLine(x+xAxisLoc%scale, restrictedRange ? yStart : 0, x+xAxisLoc%scale, yEnd);
            for (int y = yStart;y <= yEnd;y+=scale)
                    g.drawLine(0, y, width, y);
            
            //draws x and y axis.
            g.setColor(AXIS_COLOR);
            g.drawLine(xAxisLoc, 0, xAxisLoc, height);//y axis.
            g.drawLine(0, yAxisLoc, width, yAxisLoc);//x axis.
            
            //draw points
            g.setColor(LINE_COLOR);
            for (int i = 0;i < points.length;i++) {
                    int x = (int)(xAxisLoc+(points[i][0]/compression*scale));
                    int y = (int)(yAxisLoc-(points[i][1]/compression*scale));
                    //g.fillOval(x-2, y-2, 4, 4);//draws a dot at each point.
                    if (i < points.length-1)
                            g.drawLine(x, y, (int)(xAxisLoc+(points[i+1][0]/compression*scale)), (int)(yAxisLoc-(points[i+1][1]/compression*scale)));
            }
            
            //draws equations
            g.setColor(EQUATION_COLOR);
            for (int i = 0;i < equations.length;i++) {
            	g.drawLine((int)(xAxisLoc + equations[i][2]/compression*scale), //x1
            			   (int)(yAxisLoc - ((equations[i][0]*equations[i][2] + equations[i][1])/compression*scale)), //y1
            			   (int)(xAxisLoc + equations[i][3]/compression*scale), //x2
            			   (int)(yAxisLoc - ((equations[i][0]*equations[i][3] + equations[i][1])/compression*scale))); //y2
            }
            
            //draws box next to mouse showing points.
            g.setColor(Color.black);
            double x = -(xAxisLoc-mouseX)*compression/(double)scale;
            double y = (yAxisLoc-mouseY)*compression/(double)scale;
            
            FontMetrics font = new FontMetrics(g.getFont()){};
            String firstDim = "("+round(x, 2) + ", ";
            g.drawString(firstDim, mouseX, mouseY);
            g.drawString(round(y, 2)+")", (int)(mouseX+font.getStringBounds(firstDim, g).getWidth()), mouseY);
    }
    
    /**
     * Rounds a number to a specified number of decimal places. Currently just used for rounding the mouse coordinates.
     * @param preNum the number to round.
     * @param decPlaces the number of decimal places to round preNum to.
     * @return the rounded number.
     */
    private double round(double preNum, int decPlaces) {
        return (double)Math.round((preNum*Math.pow(10, decPlaces)))/Math.pow(10, decPlaces);
    }
        
    /**
     * Sets the points to be graphed.
     * 
     * Format: {{x1, y1}, {x2, y2}, ...}
     * 
     * @param _points the points to be graphed in the format of {{x1, y1}, {x2, y2}, ...}
     */
    public void setPoints(double[][] _points) {
        points = _points;
        repaint();
    }
    
    /**
     * Sets the equations to be graphed.
     * 
     * Format: {{m, b, startY, endY}, ...}
     * 
     * @param _equations the equations to be graphed in the format of {{m, b, startY, endY}, ...}
     */
    public void setEquations(double[][] _equations) {
        equations = _equations;
        repaint();
    }
    
    
    /**
     * Sets the x location at the middle of the JPanel to be pX.
     * @param pX the x location to be at the middle of the JPanel.
     */
    public void setXAxisPosition(double pX) {
        xAxisLoc = (int)(pX*scale)*-1+width/2;
        repaint();
    }
    
    /**
     * Sets the x location at the middle of the JPanel to be pX.
     * @param pY the y location to be at the middle of the JPanel.
     */
    public void setYAxisPosition(double pY) {
        yAxisLoc = (int)(pY*scale)+height/2;
        repaint();
    }
    
    /**
     * TODO: Restricts the graph to the range provided.
     * @param _range1
     * @param _range2
     */
    public void setRange(double _range1, double _range2) {
        restrictedRange = true;
        range1 = _range1;
        range2 = _range1;
    }
    
    /**
     * Sets whether or not to restrict the range of the graph.
     * @param restrict true to restrict the range false to unrestrict the range.
     */
    public void restrictRange(boolean restrict) {
    	restrictedRange = restrict;
    }
    
    /**
     * Sets the height and width of the graph.
     * @param _width Desired width of graph.
     * @param _height Desired height of graph.
     */
    public void setGraphSize(int _width, int _height) {
        width = _width;
        height = _height;
    }
    
    /**
     * Sets the compression value. The compression value is equal to the number of units that are in
     * between two grid lines.
     * @param _compression the compression value
     */
    public void setCompression(int _compression) {
    	compression = _compression;
    }
    
    /**
     * moves the graph by (mX, mY)
     * @param mX move the x axis mX pixels to the right.
     * @param mY move the y axis mY pixels down.
     */
    public void moveGraph(int mX, int mY) {
        xAxisLoc += mX;
        yAxisLoc += mY;
        repaint();
    }
    
    /**
     * Gets the starting point for use in mouseDragged.
     */
    public void mousePressed(MouseEvent e) {
        preX = e.getX();
        preY = e.getY();
    }
    
    /**
     * Moves graph with the dragging of the mouse.
     */
    public void mouseDragged(MouseEvent e) {
        moveGraph(e.getX() - preX, e.getY() - preY);
        preX = e.getX();
        preY = e.getY();
        mouseX = preX;
        mouseY = preY;
    }
    
    /**
     * Records the mouse position for use with showing the current coordinate of the mouse.
     */
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }
    
    /**
     * Zooms the graph in and out relative to the position of the mouse when user rolls the mouse wheel. 
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        double preX = (xAxisLoc-e.getX())/(double)scale;
        double preY = (yAxisLoc-e.getY())/(double)scale;

        scale -= Math.ceil(scale*ZOOM_SPEED)*e.getWheelRotation();
        
        if (scale < MIN_SCALE)
                scale = MIN_SCALE;
        else if (scale >= MAX_SCALE)
                scale = MAX_SCALE;
        
        xAxisLoc = (int)((preX*scale)+e.getX());
        yAxisLoc = (int)((preY*scale)+e.getY());
        repaint();
    }
    
    public void componentResized(ComponentEvent e) {
    	setGraphSize(getWidth(), getHeight());
    	repaint();
    }
    
    //extra stuffs
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}
}