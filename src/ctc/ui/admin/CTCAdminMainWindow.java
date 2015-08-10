package ctc.ui.admin;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import ctc.ui.CTCAdminMain;
import ctc.ui.teacher.personal.StudentCreateLayout;
import ctc.ui.teacher.personal.StudentSearchLayout;
import ctc.constant.*;
import ctc.transport.MinaClient;
import ctc.util.SystemTray;


/**
 * This class represents the main window of the application
 */
public class CTCAdminMainWindow {
  

  private static Shell shell;
  private static SystemTray sysTray;
  private static CTCAdminToolbarFactory ctcServerToolbarFactory = new CTCAdminToolbarFactory();
  private static CTCAdminMenu ctcServerMenu;
  private static MinaClient minaClient;
  private static String userName;
  private static String password;  
  /**
   * Constructs a PasswordMainWindow
   * @param newShell the parent shell
   */
  public CTCAdminMainWindow(Shell newShell,MinaClient minaClient,String userName, String password) {

	sysTray = new SystemTray(minaClient);
	this.minaClient = minaClient;
	this.userName = userName;
	this.password = password;
	  
	shell = newShell;
    //shell.setText("CTC仿真系统客户端(管理员)");
	shell.setText("分散自律调度集中仿真软件-管理员客户端");
	
    createContents();
    

    //监听关闭窗口事件,对应窗口右上角的关闭按钮
	shell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
        public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
         	CTCAdminMain.getApp().closeWindow();    	
        }
    });
  }

  /**
   * Gets the shell (main window) of the application
   * @return Shell
   */
  public Shell getShell() {
    return shell;
  }
  public SystemTray getSystemTray() {
	    return sysTray;
  }
  
  public CTCAdminToolbarFactory getCTCServerToolbarFactory() {
	    return ctcServerToolbarFactory;
  }
  public CTCAdminMenu getCTCServerMenu() {
	    return ctcServerMenu;
  }
   /**
   * Creates the window contents
   */
  
  private StackLayout stackLayout = new StackLayout();
  private Composite parentComp;
  private Composite adminTeacherInfoSearchComp,adminTeacherInfoInputComp,adminPasswordComp;
   
  public void setTopControl(int id){
	  switch(id){
	  	case Constants.ADMIN_PASSWORD_UPDATE: //管理员自身用户名及密码维护
	  		  stackLayout.topControl = adminPasswordComp;
	  		  break;
	  	case Constants.ADMIN_TEACHER_INPUT://教师信息录入
	  		  stackLayout.topControl = adminTeacherInfoInputComp;
	  		  break;	
	  	case Constants.ADMIN_TEACHER_SEARCH://教师信息查询
	  		  stackLayout.topControl = adminTeacherInfoSearchComp;
	  		  break;	  
	  }
	  parentComp.layout();
  }

  

  private void createContents() {
	
	//创建系统托盘
	sysTray.createSysTray(shell);
	    
	//构造菜单 
	ctcServerMenu = new CTCAdminMenu(shell);
	shell.setMenuBar(ctcServerMenu.getMenu());
     	    

   //构造工具栏
    ToolBar toolbar = CTCAdminToolbarFactory.create(shell);
    toolbar.pack();
    
    //设置操作界面
    shell.setLayout(new GridLayout(1, false));

   //复合控件，可以把子控件安排成行或列的布局并使用框格分隔，用户也因此可以改变其大小。
  	SashForm sash = new SashForm(shell, SWT.BORDER);//在面板shell里再嵌一个面板sash
 
   	sash.setLayout(new FillLayout());//默认为水平铺方式
   	sash.setLayoutData(new GridData(GridData.FILL_BOTH));
   	
   //在面板sash里嵌一个面板Composite 
   	parentComp = new Composite(sash,SWT.NONE);
    //parentComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	parentComp.setLayout(stackLayout);
	
	
	AdminTeacherInfoInputLayout adminTeacherInfoInputLayout = new AdminTeacherInfoInputLayout(shell,minaClient);
	adminTeacherInfoInputComp = adminTeacherInfoInputLayout.create(parentComp);
	
	AdminTeacherInfoSearchLayout adminTeacherInfoSearchLayout = new AdminTeacherInfoSearchLayout(shell,minaClient);
	adminTeacherInfoSearchComp = adminTeacherInfoSearchLayout.create(parentComp);
	
	AdminPasswordLayout adminPasswordLayout = new AdminPasswordLayout(shell,minaClient,userName,password);
	adminPasswordComp = adminPasswordLayout.create(parentComp);
	
  }


}
