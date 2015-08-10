package ctc.ui.teacher;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import ctc.constant.*;
import ctc.tdcs.data.BaseParam;
import ctc.tdcs.ui.TdcsToolbarFactory;
import ctc.transport.MinaClient;
import ctc.ui.CTCTeacherMain;
import ctc.ui.teacher.district.*;
import ctc.ui.teacher.districtstation.*;
import ctc.ui.teacher.districttrainrelation.*;
import ctc.ui.teacher.experiment.*;
import ctc.ui.teacher.help.*;
import ctc.ui.teacher.personal.StudentCreateLayout;
import ctc.ui.teacher.personal.StudentSearchLayout;
import ctc.ui.teacher.personal.TeacherPasswordLayout;
import ctc.ui.teacher.station.*;
import ctc.ui.teacher.train.*;
import ctc.ui.teacher.trainPlan.*;
import ctc.util.*;


/**
 * This class represents the main window of the application
 */
public class CTCTeacherMainWindow {
  

  private static Shell shell;
  private static SystemTray sysTray;
  private static CTCTeacherToolbarFactory ctcServerToolbarFactory = new CTCTeacherToolbarFactory();
  private static CTCTeacherMenu ctcServerMenu;
  private static MinaClient minaClient;
  private static String userName;
  private static String password;
  private static int teamID;
  private static boolean roleFlag;
  private static String districtName;
  /**
   * Constructs a PasswordMainWindow
   * @param newShell the parent shell
   */
 
  public CTCTeacherMainWindow(Shell newShell,MinaClient minaClient,String userName, String password,int teamID,String districtName,boolean roleFlag) {

	sysTray = new SystemTray(minaClient);
	this.minaClient = minaClient;
	this.userName = userName;
	this.password = password;
	
	this.teamID = teamID;
	
	/**xbm2010-4-20添加*/
	this.roleFlag = roleFlag;
	this.districtName = districtName;
	  
	shell = newShell;
    //shell.setText("CTC仿真系统客户端(故障设置)");
	shell.setText("分散自律调度集中仿真软件-教师客户端");
    createContents();
    

    //监听关闭窗口事件,对应窗口右上角的关闭按钮
	shell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
        public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
         	CTCTeacherMain.getApp().closeWindow();    	
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
  
  public CTCTeacherToolbarFactory getCTCServerToolbarFactory() {
	    return ctcServerToolbarFactory;
  }
  public CTCTeacherMenu getCTCServerMenu() {
	    return ctcServerMenu;
  }
   /**
   * Creates the window contents
   */
  
  private StackLayout stackLayout = new StackLayout();
  private Composite parentComp;
  private Composite stationCreateComp,stationSearchComp,stationBrowserComp;
  private Composite districtCreateComp,districtSearchComp,districtBrowserComp;
  private Composite districtStationCreateComp,districtStationSearchComp;
  private Composite trainCreateComp,trainSearchComp,trainBrowserComp;
  private Composite districtTrainCreateComp,districtTrainSearchComp;
  private Composite trainPlanCreateComp,trainPlanSearchComp;
  private Composite teacherPasswordComp,studentInfoInputComp,studentInfoSearchComp;
  private Composite experimentSetComp,experimentStartComp,experimentCloseComp;
  
  
  
  public void setTopControl(int id){
	  switch(id){
	  	case Constants.TEACHER_STATION_CREATE://车站概况创建
	  		  stackLayout.topControl = stationCreateComp;
	  		  break;
	  	case Constants.TEACHER_STATION_SEARCH://车站概况查询
	  		  stackLayout.topControl = stationSearchComp;
	  		  break;
	  	case Constants.TEACHER_STATION_BROWSER://车站概况浏览
	  		  stackLayout.topControl = stationBrowserComp;
	  		  break;
	     
	  	case Constants.TEACHER_DISTRICT_CREATE://区段创建
	  		  stackLayout.topControl = districtCreateComp;
	  		  break;
	  	case Constants.TEACHER_DISTRICT_SEARCH://区段查询
	  		  stackLayout.topControl = districtSearchComp;
	  		  break;
	  	case Constants.TEACHER_DISTRICT_BROWSER://区段浏览
	  		  stackLayout.topControl = districtBrowserComp;
	  		  break;
	  		  
	  	case Constants.TEACHER_DISTRICT_STATION_CREATE://区段与车站关系创建
	  		  stackLayout.topControl = districtStationCreateComp;
	  		  break;
	  	case Constants.TEACHER_DISTRICT_STATION_SEARCH://区段与车站关系查询
	  		  stackLayout.topControl = districtStationSearchComp;
	  		  break;
	  		  
	  	case Constants.TEACHER_DISTRICT_TRAIN_CREATE://区段与车次关系创建
	  		  stackLayout.topControl = districtTrainCreateComp;
	  		  break;
	  	case Constants.TEACHER_DISTRICT_TRAIN_SEARCH://区段与车次关系查询
	  		  stackLayout.topControl = districtTrainSearchComp;
	  		  break;
	  		  
	  		  
	  	case Constants.TEACHER_TRAIN_CREATE://车次创建
	  		  stackLayout.topControl = trainCreateComp;
	  		  break;
	  	case Constants.TEACHER_TRAIN_SEARCH://车次查询
	  		  stackLayout.topControl = trainSearchComp;
	  		  break;
	  	case Constants.TEACHER_TRAIN_BROWSER://车次浏览
	  		  stackLayout.topControl = trainBrowserComp;
	  		  break;
	  		  
	  	case Constants.TEACHER_TRAIN_PLAN_CREATE://列车原始计划创建
	  		  stackLayout.topControl = trainPlanCreateComp;
	  		  break;
	  	case Constants.TEACHER_TRAIN_PLAN_SEARCH://列车原始计划查询
	  		  stackLayout.topControl = trainPlanSearchComp;
	  		  break;	  
	  		 
	  	case Constants.EXPERIMENT_SET://设置实验
	  		  stackLayout.topControl = experimentSetComp;
	  		  break;	
	  	case Constants.EXPERIMENT_START://启动实验
	  		  stackLayout.topControl = experimentStartComp;
	  		  break;
	  	case Constants.EXPERIMENT_CLOSE://关闭实验
	  		  stackLayout.topControl = experimentCloseComp;
	  		  break;
	  
	  	case Constants.TEACHER_PASSWORD_UPDATE://教师密码维护
	  		  stackLayout.topControl = teacherPasswordComp;
	  		  break;	
	  	case Constants.STUDENT_INFO_INPUT://学员信息录入
	  		  stackLayout.topControl = studentInfoInputComp;
	  		  break;
	  	case Constants.STUDENT_INFO_SEARCH://学员信息查询
	  		  stackLayout.topControl = studentInfoSearchComp;
	  		  break;
	  	case Constants.TEACHER_COUNT_DOWN://倒计时
	  		  new TimeCountDown(Display.getCurrent().getActiveShell()).open();
  		     break;
	  	case Constants.TEACHER_COUNT_UP://计时
	  		  new TimeCountUp(Display.getCurrent().getActiveShell()).open();
		     break;  
	  		  
	  }
	  parentComp.layout();
  }

  

  private void createContents() {
	//设置组号，功能演示代码
	BaseParam.setTeamID(teamID);
	
	/**xbm2010-4-20添加*/
	TdcsToolbarFactory tdcsToolbarFactory = TdcsToolbarFactory.getInstance();
	tdcsToolbarFactory.setRoleFlag(roleFlag);
	tdcsToolbarFactory.setDistrictName(districtName);
		
	//创建系统托盘
	sysTray.createSysTray(shell);
	    
	//构造菜单 
	ctcServerMenu = new CTCTeacherMenu(shell);
	shell.setMenuBar(ctcServerMenu.getMenu());
     	    
   //构造工具栏
    ToolBar toolbar = CTCTeacherToolbarFactory.create(shell);
    toolbar.pack();
    
    //设置操作界面
    shell.setLayout(new GridLayout(1, false));

   //复合控件，可以把子控件安排成行或列的布局并使用框格分隔，用户也因此可以改变其大小。
  	SashForm sash = new SashForm(shell, SWT.BORDER/*SWT.HORIZONTAL SWT.BOTH  VERTICAL*/);//在面板shell里再嵌一个面板sash
 
   	sash.setLayout(new FillLayout());//默认为水平铺方式
   	sash.setLayoutData(new GridData(GridData.FILL_BOTH));
   	
   //在面板sash里嵌一个面板Composite 
   	parentComp = new Composite(sash,SWT.NONE);
    //parentComp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	parentComp.setLayout(stackLayout);

	//车站管理菜单的处理
	StationCreateLayout stationCreateLayout = new StationCreateLayout(shell,minaClient);
	stationCreateComp = stationCreateLayout.create(parentComp);
	StationSearchLayout stationSearchLayout = new StationSearchLayout(shell,minaClient);
	stationSearchComp = stationSearchLayout.create(parentComp);
	StationBrowserLayout stationBrowserLayout = new StationBrowserLayout(shell,minaClient);
	stationBrowserComp = stationBrowserLayout.create(parentComp);
	
	//车站区段菜单的处理
	DistrictCreateLayout districtCreateLayout = new DistrictCreateLayout(shell,minaClient);
	districtCreateComp = districtCreateLayout.create(parentComp);
	DistrictSearchLayout districtSearchLayout = new DistrictSearchLayout(shell,minaClient);
	districtSearchComp = districtSearchLayout.create(parentComp);
	DistrictBrowserLayout districtBrowserLayout = new DistrictBrowserLayout(shell,minaClient);
	districtBrowserComp = districtBrowserLayout.create(parentComp);
	
	DistrictStationCreateLayout districtStationCreateLayout = new DistrictStationCreateLayout(shell,minaClient);
	districtStationCreateComp = districtStationCreateLayout.create(parentComp);
	DistrictStationSearchLayout districtStationSearchLayout = new DistrictStationSearchLayout(shell,minaClient);
	districtStationSearchComp = districtStationSearchLayout.create(parentComp);

	
	//车次菜单的处理
	TrainCreateLayout trainCreateLayout = new TrainCreateLayout(shell,minaClient);
	trainCreateComp = trainCreateLayout.create(parentComp);
	TrainSearchLayout trainSearchLayout = new TrainSearchLayout(shell,minaClient);
	trainSearchComp = trainSearchLayout.create(parentComp);
	TrainBrowserLayout trainBrowserLayout = new TrainBrowserLayout(shell,minaClient);
	trainBrowserComp = trainBrowserLayout.create(parentComp);
	
	DistrictTrainCreateLayout districtTrainCreateLayout = new DistrictTrainCreateLayout(shell,minaClient);
	districtTrainCreateComp = districtTrainCreateLayout.create(parentComp);
	DistrictTrainSearchLayout districtTrainSearchLayout = new DistrictTrainSearchLayout(shell,minaClient);
	districtTrainSearchComp = districtTrainSearchLayout.create(parentComp);
	
	TrainPlanCreateLayout trainPlanCreateLayout = new TrainPlanCreateLayout(shell,minaClient);
	trainPlanCreateComp = trainPlanCreateLayout.create(parentComp);
	TrainPlanSearchLayout trainPlanSearchLayout = new TrainPlanSearchLayout(shell,minaClient);
	trainPlanSearchComp = trainPlanSearchLayout.create(parentComp);
		
	///////////////////////////////
		
	TeacherPasswordLayout teacherPasswordLayout = new TeacherPasswordLayout(shell,minaClient,userName,password);
	teacherPasswordComp = teacherPasswordLayout.create(parentComp);
	
	StudentCreateLayout studentInfoInputLayout = new StudentCreateLayout(shell,minaClient);
	studentInfoInputComp = studentInfoInputLayout.create(parentComp);
	
	StudentSearchLayout studentInfoSearchLayout = new StudentSearchLayout(shell,minaClient);
	studentInfoSearchComp = studentInfoSearchLayout.create(parentComp);
	
	//实验
	ExperimentSetLayout experimentSetLayout = new ExperimentSetLayout(shell,minaClient);
	experimentSetComp = experimentSetLayout.create(parentComp);
	ExperimentStartLayout experimentStartLayout = new ExperimentStartLayout(shell,minaClient);
	experimentStartComp = experimentStartLayout.create(parentComp);
	ExperimentCloseLayout experimentCloseLayout = new ExperimentCloseLayout(shell,minaClient);
	experimentCloseComp = experimentCloseLayout.create(parentComp);
	
	//parentComp.layout();

  }

}
