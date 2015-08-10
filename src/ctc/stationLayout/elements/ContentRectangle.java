package ctc.stationLayout.elements;

import org.eclipse.swt.graphics.Color;

//表示车站
public class ContentRectangle extends ContentObject {
	
	private Color fillColor;
	private ContentObject p1;
	private ContentObject p2;
	private String stationName;//车站名称
	
	
	public ContentRectangle(){}
	
	public ContentRectangle(ContentObject p1,ContentObject p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
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
	
	
	
	
	
}