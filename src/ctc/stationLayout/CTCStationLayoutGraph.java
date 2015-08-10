package ctc.stationLayout;

import ctc.stationLayout.elements.*;

import com.swtgraph.layeredcanvas.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class CTCStationLayoutGraph extends LayeredCanvas {

	Shell shell;
	Diagram diagram = new Diagram();

	private Point originOffset = null;
   
	private ScrollBar horizontalBar;//水平滚动条
	private Integer horizSelection = 0;
	private ScrollBar verticalBar;//垂直滚动条
	private Integer vertSelection = 0;
	
    //true设置滚动条
	private void init(Boolean useScrollBars){
		horizontalBar = getHorizontalBar();
		verticalBar = getVerticalBar();
		//shell.addListener (SWT.Resize, getResizeListener());
		
		if (useScrollBars)
		{
			//设置Scale的最大值和最小值
			horizontalBar.setMinimum(1);
			horizontalBar.setMaximum(1000);
			 
			verticalBar.setMaximum(1000);
			//horizontalBar.setSelection(50);
			//verticalBar.setSelection(50);
			//horizSelection = 50;
		//	vertSelection = 50;
			horizontalBar.setIncrement(1);
			verticalBar.setIncrement(1);
			horizontalBar.setPageIncrement(100);//设置Scale增量的增加或递减值，
			verticalBar.setPageIncrement(100);

			// Add scroll listeners
			horizontalBar.addListener(SWT.Selection, getHorizontalScrollListener());
			verticalBar.addListener(SWT.Selection, getVerticalScrollListener());
		}
		setScrollBarEnable(useScrollBars);
	}
	
	private Listener getResizeListener(){
		return new Listener(){
			public void handleEvent (Event e) {
				//shell.redraw();
			}
		};
	}

	private Listener getHorizontalScrollListener(){
		return new Listener(){
			public void handleEvent(Event e){
				if (!isEnabled())
					return;

				int currentSelection = horizontalBar.getSelection();
				int delta = horizSelection - currentSelection;

				if (delta != 0){
					int destX = -currentSelection - originOffset.x;
					//(int destX, int destY, int x, int y, int width, int height, boolean all)
					shell.scroll(destX, 0, 0, 0, gcWidth, gcHeight, false);
					originOffset.x = -currentSelection;
					horizSelection = currentSelection;
					redraw();
				}
			}};
	}

	private Listener getVerticalScrollListener(){
		return new Listener(){
			public void handleEvent(Event e){
				int currentSelection = verticalBar.getSelection();
				int delta = vertSelection - currentSelection;

				if (delta != 0){
					int destY = -currentSelection - originOffset.y;
					shell.scroll (0, destY, 0, 0, gcWidth, gcHeight, false);
					originOffset.y = -currentSelection;
					vertSelection = currentSelection;
					redraw();
				}
			}
		};
	}
	public void setScrollBarEnable(Boolean enabled) {
		horizontalBar.setVisible(enabled);
		verticalBar.setVisible(enabled);
	}
	
	
///////////////////////	
	
	public CTCStationLayoutGraph(Shell shell, int style) {
		super(shell, style | SWT.DOUBLE_BUFFERED |SWT.NO_BACKGROUND |SWT.BORDER| SWT.V_SCROLL | SWT.H_SCROLL);
		setLayout(new FillLayout());
		this.shell = shell;
		init(true);//设置滚动条
		
		//绘制CTC层
		ICanvasLayer graphCTCLayer = new ICanvasLayer() {
			public void paint(GC gc) {
				paintObjects(gc);				
			}
			public void dispose() {
			}
			
		};
		addLayer(graphCTCLayer);
/*
		//绘制火车层
		ICanvasLayer graphCurves = new ICanvasLayer() {
			public void paint(GC gc) {
				paintCurves(gc);
			}
			public void dispose() {
			}
		};
		addLayer(graphCurves);
		*/
		
	}
	

	/////////ICanvasLayer()接口////////////////////////

	private void recalcArea(GC gc) {
		Point gcSize = this.getSize();
  	  	gcWidth = gcSize.x;
  	  	gcHeight = gcSize.y;
  		//System.out.println("gcWidth:"+gcWidth);
  	  	
  	  //	System.out.println("gcHeight:"+gcHeight);
  	  
		//定位 起始点
		//if (originOffset == null)
			//originOffset = new Point(axisMarginWidth, axisMarginHeight);
	
		if (originOffset == null)
			originOffset = screenPoint(0, 0);

		//originPoint = new Point(
		//		graphArea.x + originOffset.x,
		//		graphArea.height + graphArea.y - originOffset.y 
		//	);		

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// private colors
	private Color colorWhite = null;
	private Color colorBlack = null;
	
	
	int gcWidth;
	int gcHeight;
	
	//绘制车站及车站之间的关系
	protected void paintObjects(GC gc) {
		
		recalcArea(gc);

		colorBlack = getDisplay().getSystemColor(SWT.COLOR_BLACK); 
	  	colorWhite = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));

		FontData defaultFontData = getDisplay().getSystemFont().getFontData()[0];
		defaultFontData.height = 7;
		
		Font smallFont = new Font(getDisplay(), defaultFontData);
		
		gc.setFont(smallFont);
		
		
		// grid
		gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		//产生要绘制图形的数据
		Color colorBlack = getDisplay().getSystemColor(SWT.COLOR_BLACK);

		colorBlack = getDisplay().getSystemColor(SWT.COLOR_BLACK); 
	  	colorWhite = getDisplay().getSystemColor(SWT.COLOR_WHITE);
	  	
	    Color cRed = getDisplay().getSystemColor(SWT.COLOR_RED);
        Color cBlue = getDisplay().getSystemColor(SWT.COLOR_BLUE);
        Color cGreen = getDisplay().getSystemColor(SWT.COLOR_GREEN);
        Color cYellow = getDisplay().getSystemColor(SWT.COLOR_YELLOW);
	  	
        diagram.drawLine(0,0,10,10,cRed, 3);
        diagram.drawLine(10,10,102,102);
	    /*   diagram.drawLine(20,20,40,10, cRed, 3);
	       diagram.drawLine(40,10,60,30, cRed, 3);
	       diagram.drawLine(60,30,80,15, cRed, 3);
	       diagram.drawLine(80,15,100,40, cRed, 3);
		diagram.drawLine(130, 10, 90, 80,colorBlack, 4);
			diagram.drawRectangle(1110, 1110, 333, 5);
	
	    */
		for (ContentObject obj : diagram.getContentList()) {
			if (obj instanceof ContentLine) {
				paintLine(gc, (ContentLine)obj);
			} else if (obj instanceof ContentRectangle) {
				paintRectangle(gc, (ContentRectangle)obj);
			} else if (obj instanceof ContentText) {
				paintText(gc, (ContentText)obj);
			}
		}
		
		
		smallFont.dispose(); 
	}
	
	/** Paints a line on the GC */
	private void paintLine(GC gc, ContentLine line) {
		
		ContentObject p1 = line.getP1();
		ContentObject p2 = line.getP2();
				
		gc.setBackground(colorWhite);
		gc.setForeground(line.getLineColor());
		gc.setLineWidth(line.getLineWidth());

		gc.drawLine((int)(originOffset.x + p1.getX()), (int)(originOffset.y + p1.getY()),
				(int)(originOffset.x + p2.getX()), (int)(originOffset.y + p2.getY()));
	}
	
	///////////////////////////////以上为正确的代码//////////////////
	
	
	
	
	/** Paints text on the GC */
	private void paintText(GC gc, ContentText text) {
		Point paintPoint = getPaintCoords(text.getX(), text.getY());

		// virtual and real x coordinates point the same way, -> +xoffset
		// but virtual y coordinates point up, not down -> -yoffset
		int x = paintPoint.x;// + text.xoffset;
		int y = paintPoint.y;// - text.yoffset;

		// draw string as transparent
		gc.setBackground(colorWhite);
		gc.setForeground(colorBlack);
		gc.drawString(text.getText(), x, y, true);
	}
	

	
	/** Paints a rectangle on the GC */
	private void paintRectangle(GC gc, ContentRectangle rect) {
		Point paintPoint1 = getPaintCoords(rect.getP1().getX(), rect.getP1().getY());
		Point paintPoint2 = getPaintCoords(rect.getP2().getX(), rect.getP2().getY());
		int width = Math.abs(paintPoint2.x - paintPoint1.x);
		int height = Math.abs(paintPoint2.y - paintPoint1.y);
		
		gc.setBackground(rect.getFillColor());
		gc.setLineWidth(0);
		
		//System.out.println("rect");
		gc.drawRectangle(Math.min(paintPoint1.x, paintPoint2.x), 
				Math.min(paintPoint1.y, paintPoint2.y), 
				width, height);
	}
	
	public Point screenPoint(float x, float y) {
		Point point = new Point(
				(originOffset == null ? 0 : originOffset.x) + (int) (x - horizSelection),
				(originOffset == null ? 0 : originOffset.y) - (int) (y - vertSelection));
		
		return point;
	}
	
	/** Convert points in our virtual (0->100,0->100) coordinates to physical pixel coordinates */
	private Point getPaintCoords(float x, float y) {
		
		return null;
	}
	
	
	
   /////////////////////绘制曲线//////////////////////////
	//绘制火车的动画效果
	protected void paintCurves(GC gc) {
		
		
	}
	
	//居中绘制所有图形
	public void centerOrigin() {
		originOffset = new Point(getClientArea().width / 2, getClientArea().height / 2);
	  // System.out.println("originOffset:"+originOffset.x + ":" + originOffset.y);	
	}
	public Point getOriginOffset() {
		return originOffset;
	}
	public void setOriginOffset(Point originOffset) {
		this.originOffset = originOffset;
	}
	

	@Override
	public void dispose() {
		super.dispose();
	}

}
