package ctc.ui;

//每个SWT应用程序至少需要一个Display和大于等于1个的Shell实例
import com.swtdesigner.SWTResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import ctc.tdcs.TdcsMain;
import ctc.transport.MinaClient;
import ctc.ui.common.AboutDialog;
import ctc.ui.teacher.CTCTeacherMainWindow;
import ctc.ui.teacher.ErrorMainWindow;
import ctc.constant.Constants;
import ctc.data.*;

public class CTCTeacherMain {

  final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/tray.png";
  private static  CTCTeacherMain app;// = new CTCTeacherMain();
  private static final ConfigureFile configureFile = new ConfigureFile();
  
 // Retain a reference to the main window of the application
  private CTCTeacherMainWindow mainWindow;  
  private ErrorMainWindow errorMainWindow;  
  private Shell shell;
  public Rectangle displayBounds; 
  public static MinaClient minaClient;
  private static String userName;
  private static String password;
  private static int teamID;
  private static boolean roleFlag;
  private static String districtName;
  
  public CTCTeacherMain(){ }
  

//异步通信用
  public CTCTeacherMain(MinaClient minaClient,String userName,String password,int teamID,String districtName,boolean roleFlag){
	  this.minaClient = minaClient;
	  this.userName = userName;
	  this.password = password;
	  this.teamID = teamID;//设置该用户终端所在组编号
	  
	  /**xbm2010-4-20添加*/
	  this.roleFlag = roleFlag;
	  this.districtName = districtName;	  
  }
  
  public static void start(){
	  app = new CTCTeacherMain();
	  app.run();
  }
  
  public static CTCTeacherMain getApp() {
	  return app;
  }
  public static String getUserName() {
	  return userName;
  }

  public static void setUserName(String userName) {
	  CTCTeacherMain.userName = userName;
  }

  /**
   * 获取配制文件信息
   */
  public static ConfigureFile getConfigureFile() {
	  return configureFile;
  }

  /**
   * Runs the application
   */
  public void run() {
	//Display负责管理事件循环和控制UI线程和其他线程之间的通讯
	  
	//创建一个新的Display对象，设置当前线程为用户接口线程。程序中经常使用Display.getDefault()
    Display display = new Display();
    
   // shell = new Shell(display,SWT.H_SCROLL|SWT.V_SCROLL|SWT.SHELL_TRIM|SWT.ON_TOP);
    //shell是应用程序中被操作系统窗口管理器管理的窗口
    //SWT.SHELL_TRIM ——是TITLE，CLOSE，MIN，MAX，RESIZE的组合。
    shell = new Shell(display,SWT.CLOSE | SWT.MIN |SWT.ON_TOP);//shell是程序的主窗口
    
   // shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    shell.setBackground(SWTResourceManager.getColor(103, 172, 231));
    
    shell.setSize(500, 500);
   // shell.setMinimized(true);//极小化
    //shell.setMaximized(true);//极大化
     
    //设置左上角的图标
    Image TRAY = new Image(shell.getDisplay(),IMAGE_PATH);
    shell.setImage(TRAY); 
   
  //使窗口处于屏幕中间
	displayBounds = display.getPrimaryMonitor().getBounds();//获取屏幕高度和宽度
	Rectangle shellBounds = shell.getBounds();//获取SHell的大小
    int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
    int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
    shell.setLocation(x, y);//定位窗口坐标
    shell.setText("故障设置");
    
    /**xbm2010-4-20添加*/
    
    // hu 2010-11-4 修改
    mainWindow = new CTCTeacherMainWindow(shell,minaClient,userName,password,teamID,districtName,roleFlag);//教师机主窗口
    //errorMainWindow = new ErrorMainWindow(shell,minaClient,userName,password,teamID,districtName,roleFlag);//故障机主窗口

    //同时启动其他程序
    //new TdcsMain(minaClient).run(); //调度主任主窗口

    /*Listener exitListener = new Listener() {
	      public void handleEvent(Event e) {
	       return;
	      }
	    };
	shell.addListener(SWT.MouseMove, exitListener);//处理移动窗口的事件
    */
  //显示主窗口
    shell.open();
  //当窗体未被关闭时执行循环体内的代码
	while (!shell.isDisposed()) {
		 //如果未发生事件，通过sleep方法进行监视事件队列
		/*readAndDispatch() 方法从平台的事件队列中读取事件，并分配他们到合适的处理程序(接收者)。
		只要队列中一直有事件可以处理，这个方法一直返回true，当事件队列为空时，则返回false(因此
		允许用户界面UI线程出于sleep状态直到事件队列不为空)。*/
		if (!display.readAndDispatch())
			display.sleep();//If no more entries in event queue
	}
	display.dispose();
  }
  
  /**
   * Gets the main window
   * @return PasswordMainWindow
   */
  public CTCTeacherMainWindow getMainWindow() {
    return mainWindow;
  }
  /////////菜单处理代码//////////////
  public void teacherMenuDispatcher(int id){
	  switch(id){
	  	case Constants.CTC_STATION_LAYOUT://绘制站场图,界面比较复杂
	  		
	  		///System.out.println("单独处理");
	  		//CTCStationLayoutPlotter.getInstance().run();
	  		break;
	  	case Constants.CTC_TRAIN_PLAN_LAYOUT://行车计划TDCS
	  		new TdcsMain(minaClient).run();
	  		break;
	  	default://由CTCTeacherMainWindow处理
	  		mainWindow.setTopControl(id);		
	  }
  }

  public void about() {
	  // Display the message box
	  /*MessageBox mb = new MessageBox(mainWindow.getShell(), SWT.ABORT |  SWT.ICON_INFORMATION);
	  mb.setText("关于CTC仿真系统");//消息框的标题
	  mb.setMessage("CTC仿真系统V1.0\n\r" + //消息框的提示文字
	  "发行时间:2009-12-1");
	  mb.open();*/
	  AboutDialog dialog = new AboutDialog(shell);
	  dialog.open();
  }
  

  public void closeWindow(){
	  if (minaClient != null)
		  minaClient.closeConnection(Constants.CLIENT_CLOSE_NORMAL);
	  mainWindow.getSystemTray().trayDispose();//释放托盘及其相关资源
	  
	  shell.dispose();//2009.10.1添加,解决在SWT中调用AWT后无法正常退出的问题
	  //shell.close();
	  System.exit(1);//退出主程序  
  }
  
  public static void main(String[] args) {
	  app = new CTCTeacherMain();
	  app.run();
  }
  
}


