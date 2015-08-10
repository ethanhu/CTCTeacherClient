package ctc.ui;

//每个SWT应用程序至少需要一个Display和大于等于1个的Shell实例
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import ctc.transport.MinaClient;
import ctc.ui.admin.CTCAdminMainWindow;
import ctc.ui.common.AboutDialog;
import ctc.constant.Constants;
import ctc.data.*;

public class CTCAdminMain {

  final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/tray.png";
  private static  CTCAdminMain app;// = new CTCTeacherMain();
  private static final ConfigureFile configureFile = new ConfigureFile();
  
 // Retain a reference to the main window of the application
  private CTCAdminMainWindow mainWindow;  
  private Shell shell;
  public Rectangle displayBounds; 
  private static MinaClient minClient;
  private static String userName;
  private static String password;
  
  public CTCAdminMain(){ }
  
  //异步通信用
  public CTCAdminMain(MinaClient minClient,String userName,String password){
	  this.minClient = minClient;
	  this.userName = userName;
	  this.password = password;
  }
  
  public static void start(){
	  app = new CTCAdminMain();
	  app.run();
  }
  
  
  public static CTCAdminMain getApp() {
    return app;
  }
  
  public void run() {
	Display display = new Display();
    
    shell = new Shell(display,SWT.SHELL_TRIM|SWT.ON_TOP);//shell是程序的主窗口
    
    shell.setBackground(SWTResourceManager.getColor(103, 172, 231));
    
    shell.setSize(500, 500);
     
    Image TRAY = new Image(shell.getDisplay(),IMAGE_PATH);
    shell.setImage(TRAY); 
   
  //使窗口处于屏幕中间
	displayBounds = display.getPrimaryMonitor().getBounds();//获取屏幕高度和宽度
	Rectangle shellBounds = shell.getBounds();//获取SHell的大小
    int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
    int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
    shell.setLocation(x, y);//定位窗口坐标
    
    mainWindow = new CTCAdminMainWindow(shell,minClient,userName,password);//应用主窗口
    
    shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch())
			display.sleep();//If no more entries in event queue
	}
	display.dispose();
  }
  
  /**
   * Gets the main window
   * @return PasswordMainWindow
   */
  public CTCAdminMainWindow getMainWindow() {
    return mainWindow;
  }
  
  public void about() {
	  AboutDialog dialog = new AboutDialog(shell);
	  dialog.open();
  }  
  
  public void closeWindow(){
	  if (minClient != null)
		  minClient.closeConnection(Constants.CLIENT_CLOSE_NORMAL);
	  mainWindow.getSystemTray().trayDispose();//释放托盘及其相关资源
	  shell.dispose();
	  //shell.close();
	  System.exit(1);//退出主程序  
  }
  
  
  /////////菜单处理代码//////////////
  public void menuDispatcher(int id){
	  mainWindow.setTopControl(id);
  }
  
  public static void main(String[] args) {
	  app = new CTCAdminMain();
	  app.run();
  }
  
}


