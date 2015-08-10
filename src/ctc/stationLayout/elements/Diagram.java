package ctc.stationLayout.elements;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class Diagram {
	
	//控制绘制矩形(车站)和直线(连接相邻两个车站)的固定参数
	private int rectWidth = 10;
	private int rectHight = 10;
	private int lineXDistance = 10;
	private int lineYDistance = 10;
	
	private int topMargin = 7;
	private int bottomMargin = 3;	
	private int leftMargin = 4;
	private int rightMargin = 4;
	
	private int  stationNumber = 5 ;//每行绘制车站个数
	private int  rowNumber = 3 ;//绘制车站行数
	private String trainName;//车次名称
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	private List<ContentObject> contentList = new LinkedList<ContentObject>();
	
	//默认值
	final int width = 2;
	final Color color = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	final Font font = Display.getDefault().getSystemFont();
	
	public Diagram(){}
	
    public void drawLine(float x1, float y1, float x2, float y2) {
        drawLine(x1, y1, x2, y2,color,width);
    }

    public void drawLine(float x1, float y1, float x2, float y2, Color lineColor, int lineWidth) {
        
    	ContentLine content = new ContentLine();
    	
        ContentObject p1 = new ContentObject(x1,y1);
        ContentObject p2 = new ContentObject(x2,y2);
        content.setP1(p1);
        content.setP2(p2);
        content.setLineColor(lineColor);
        content.setLineWidth(lineWidth);
        
        contentList.add(content);
        
        return;
    }
    
    public void drawRectangle(float x1, float y1, float x2, float y2) {
        drawRectangle(x1, y1, x2, y2, color);
    }
    public void drawRectangle(float x1, float y1, float x2, float y2, Color fillColor) {
        ContentRectangle content = new ContentRectangle();
    
        ContentObject p1 = new ContentObject(x1,y1);
        ContentObject p2 = new ContentObject(x2,y2);
        content.setP1(p1);
        content.setP2(p2);
        
        content.setFillColor(fillColor);
        
        contentList.add(content);
    
        return;
    }
    
	public void drawText(String text, float x, float y) {
        drawText(text, x, y, font);
    }
    public void drawText(String text, float x, float y, Font font) {
     
    	ContentText content = new ContentText(x,y);
        content.setText(text);
        content.setFont(font);
        
        contentList.add(content);
        
        return;
    }
    //////
    public List<ContentObject> getContentList() {
		return contentList;
	}

	public void setContentList(List<ContentObject> contentList) {
		this.contentList = contentList;
	}

	/** Clears all previously drawn objects in the diagram area. */
    public void clear() {
        contentList.clear();
    }
    ////
    public int getRectWidth() {
		return rectWidth;
	}

	public void setRectWidth(int rectWidth) {
		this.rectWidth = rectWidth;
	}

	public int getRectHight() {
		return rectHight;
	}

	public void setRectHight(int rectHight) {
		this.rectHight = rectHight;
	}

	public int getLineXDistance() {
		return lineXDistance;
	}

	public void setLineXDistance(int lineXDistance) {
		this.lineXDistance = lineXDistance;
	}

	public int getLineYDistance() {
		return lineYDistance;
	}

	public void setLineYDistance(int lineYDistance) {
		this.lineYDistance = lineYDistance;
	}

	public int getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}

	public int getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}

	public int getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}

	public int getStationNumber() {
		return stationNumber;
	}

	public void setStationNumber(int stationNumber) {
		this.stationNumber = stationNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getTrainName() {
		return trainName;
	}

	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
    

	
}