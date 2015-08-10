package ctc.stationLayout.elements;

import org.eclipse.swt.graphics.Color;

public class ContentObject implements Comparable<ContentObject> {
	
	private Float x;
	private Float y;
	private Integer diameter = 4;
	private Color pointColor;
	
	
	public Color getPointColor() {
		return pointColor;
	}

	public void setPointColor(Color pointColor) {
		this.pointColor = pointColor;
	}

	public Integer getDiameter() {
		return diameter;
	}

	public void setDiameter(Integer diameter) {
		this.diameter = diameter;
	}

	public ContentObject(){}
	
	public ContentObject(Float x, Float y) {
		this.x = x;
		this.y = y;
	}

	public ContentObject(Float x, Float y, String label) {
		this(x, y);
	}

	public Float getX() {
		return x;
	}
	
	public void setX(Float x) {
		this.x = x;
	}

	public void setY(Float y) {
		this.y = y;
	}

	public Float getY() {
		return y;
	}

	public int compareTo(ContentObject o) {
		return getX().compareTo(o.getX());
	}
	
	public String toString() {
		return String.format("(%.5f, %.5f)", x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContentObject))
			return false;
		
		ContentObject cp2 = (ContentObject) obj;
		
		return this.getX().equals(cp2.getX()) && this.getY().equals(cp2.getY());
	}
	
}
