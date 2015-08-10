package ctc.stationLayout.elements;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class ContentText extends ContentObject {
	
	private String text;
	private Color textColor;	
	private Font font;
	
	public ContentText(){}
	public ContentText(Float x, Float y) {
		super(x, y);
	}

	public ContentText(Float x, Float y, String text) {
		super(x, y);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

}
