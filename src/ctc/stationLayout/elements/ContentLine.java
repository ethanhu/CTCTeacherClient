package ctc.stationLayout.elements;

import org.eclipse.swt.graphics.Color;

public class ContentLine extends ContentObject {
	
	private Color lineColor;
	private int lineWidth;
	private ContentObject p1;
	private ContentObject p2;
	private String startStationName;//直线起始点车站名称
	private String endstationName;//直线终止点车站名称
	
	public ContentLine(){}
	
	public ContentLine(ContentObject p1, ContentObject p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public String getStartStationName() {
		return startStationName;
	}

	public void setStartStationName(String startStationName) {
		this.startStationName = startStationName;
	}

	public String getEndstationName() {
		return endstationName;
	}

	public void setEndstationName(String endstationName) {
		this.endstationName = endstationName;
	}

	public ContentObject getP1() {
		return p1;
	}

	public void setP1(ContentObject p1) {
		this.p1 = p1;
	}

	public ContentObject getP2() {
		return p2;
	}

	public void setP2(ContentObject p2) {
		this.p2 = p2;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}
	

}