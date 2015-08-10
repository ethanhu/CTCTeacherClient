package ctc.ui.teacher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import com.swtdesigner.SWTResourceManager;
import ctc.ui.CTCTeacherMain;

//此类需要进一步完善,目前只是演示代码
/**
 * This class contains the toolbar for the Password application
 */
public class CTCTeacherToolbarFactory {

//private static final String IMAGE_PATH = "/images/";

  private static final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/";
  // These contain the images for the toolbar buttons
  private static Image QUIT,ABOUT;
  
  public CTCTeacherToolbarFactory(){}
  /**
   * Factory create method
   * 
   * @param composite the parent composite
   * @return ToolBar
   */
  public static ToolBar create(Composite composite) {
    createImages(composite);//create images for coolbar buttons
  
     //创建工具栏
    ToolBar toolbar = new ToolBar(composite,/*SWT.VERTICAL*/ SWT.HORIZONTAL);
    toolbar.setBackground(SWTResourceManager.getColor(103, 172, 231));
    //toolbar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    createItems(toolbar);
    return toolbar;
  }

  /**
   * Creates the toolbar items
   * 
   * @param toolbar the parent toolbar
   */
  //用于控制启动/关闭服务器的两个按钮之间的逻辑互联关系
  private static ToolItem quit,about; 
  
  private static void createItems(ToolBar toolbar) {

    quit = createItemHelper(toolbar,QUIT, "退出系统");
    quit.addSelectionListener(new SelectionAdapter() {
    public void widgetSelected(SelectionEvent event) {
    	  CTCTeacherMain.getApp().closeWindow();
      }
    });

    about = createItemHelper(toolbar, ABOUT, "关于");
    about.addSelectionListener(new SelectionAdapter() {
    public void widgetSelected(SelectionEvent event) {
    	  CTCTeacherMain.getApp().about();
      }
    });
  
    //teacherMenuDispatcher(Constants.TEACHER_PASSWORD_UPDATE)
  }

  /**
   * Helper method to create a toolbar item
   * 
   * @param toolbar the parent toolbar
   * @param image the image to use
   * @param text the text to use
   * @return ToolItem
   */
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
    if (QUIT == null) {
      Display display = composite.getDisplay();
      Class c = CTCTeacherMain.getApp().getClass();
    //PARAMSET = new Image(display, c.getResourceAsStream(IMAGE_PATH + "save.png"));
      ABOUT = new Image(display, (IMAGE_PATH + "startserver.gif"));
      QUIT = new Image(display, (IMAGE_PATH + "closeserver.gif"));
    }
  }
}
