package ctc.ui.admin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import ctc.constant.Constants;
import ctc.ui.CTCAdminMain;


public class CTCAdminToolbarFactory {

  private static final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/";
  private static Image CLOSESERVER;
  
  public CTCAdminToolbarFactory(){}
  
  public static ToolBar create(Composite composite) {
    createImages(composite);//create images for coolbar buttons
  
     //创建工具栏
    ToolBar toolbar = new ToolBar(composite,/*SWT.VERTICAL*/ SWT.HORIZONTAL);
    toolbar.setBackground(SWTResourceManager.getColor(103, 172, 231));
    createItems(toolbar);
    return toolbar;
  }

  private static ToolItem closeServer; 
  
  private static void createItems(ToolBar toolbar) {
	closeServer = createItemHelper(toolbar, CLOSESERVER, "退出系统");
	closeServer.addSelectionListener(new SelectionAdapter() {
    public void widgetSelected(SelectionEvent event) {
    	CTCAdminMain.getApp().closeWindow();
      }
    });
  }

  private static ToolItem createItemHelper(ToolBar toolbar, Image image,String text) {
    ToolItem item = new ToolItem(toolbar, SWT.PUSH);
    if (image == null) {
      item.setText(text);
    } else {
      item.setImage(image);
      item.setToolTipText(text);
    }
    return item;
  }
  
  //create images for coolbar buttons
  private static void createImages(Composite composite) {
    if (CLOSESERVER == null) {
      Display display = composite.getDisplay();
      Class c = CTCAdminMain.getApp().getClass();
    //PARAMSET = new Image(display, c.getResourceAsStream(IMAGE_PATH + "save.png"));
      CLOSESERVER = new Image(display, (IMAGE_PATH + "closeserver.gif"));
    }
  }
}
