package ctc.stationLayout.test;

import ctc.constant.Constants;
import ctc.stationLayout.*;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CTCStationLayoutGraphDrawer {
	
	private CTCStationLayoutGraph graph;
	private final static Logger LOGGER = LoggerFactory.getLogger("CTCServer");
	final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/monitor.jpg";
	  
	public static void main(String[] args) {
		new CTCStationLayoutGraphDrawer();
	}
	
	public CTCStationLayoutGraphDrawer() {
		
		//并没有真正用
	 	PropertyConfigurator.configure(Constants.PATH_LOG4J);
	 	  
		Display display = new Display();//.getDefault();
		final Shell shell = new Shell(display,SWT.TITLE|SWT.CLOSE|SWT.MIN|SWT.MAX|SWT.ON_TOP| SWT.NO_REDRAW_RESIZE );
		
		
		Menu menu = new Menu(shell, SWT.BAR);//BAR用于主菜单
		MenuItem item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
		item.setText("文件");
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(dropMenu);

		item = new MenuItem(dropMenu, SWT.NULL);
		item.setText("退出\tAlt+F4");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//  if (minaServer != null)
				//	  minaServer.close();
			  shell.dispose();
			  System.exit(1);//退出主程序
			}
		});
		
		shell.setMenuBar(menu);
		shell.setLayout(new GridLayout(1, false));
		shell.setText("监控中心");
		
		shell.setMaximized(true);//最大化
		
		Image TRAY = new Image(shell.getDisplay(),IMAGE_PATH);
		shell.setImage(TRAY);
	
	    graph = new CTCStationLayoutGraph(shell, SWT.BORDER);
		// public GridData(int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace,
		//boolean grabExcessVerticalSpace, int horizontalSpan, int verticalSpan);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//graph.centerOrigin();
		
		shell.layout();
		shell.open();
		
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();		
	}
	
	
	
}
