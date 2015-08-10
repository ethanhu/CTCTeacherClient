package ctc;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctc.constant.*;
import ctc.data.*;
import ctc.databaseserver.CallbackServer;
import ctc.swing.StandardParametersPanel;
import ctc.tdcs.data.BaseParam;
import ctc.transport.*;
import ctc.transport.message.LoginResponseMessage;
import ctc.ui.CTCAdminMain;
import ctc.ui.CTCTeacherMain;
import ctc.util.JsonUtil;

import ctc.pojobean.*;

public class CTCClient_T_GZSZ extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JMenuBar accessDialogMenuBar;
	private JButton loginButton, quitButton;
	
	private ConfigureFile cf = new ConfigureFile();
	private StandardParametersPanel standardParametersPanel;
	
	private String host  = "127.0.0.1";
	private String port  = "9999";
	private String userName  = "test";
	String passwordString  = "";

	public final static Logger LOGGER = LoggerFactory.getLogger("CTCTeacher");
		
	private MinaClient minaClient; 
		
	protected void center()
	{
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension us = getSize();
		int x = (screen.width - us.width)/2;
		int y = (screen.height - us.height)/2;
		setLocation(x,y);
	}
	

	public static void main(String[] args)
	{
		 //并没有真正用
		PropertyConfigurator.configure(Constants.PATH_LOG4J);
		//LOGGER.info("启动");
		
		CTCClient_T_GZSZ loginAccess = new CTCClient_T_GZSZ();
		loginAccess.setSize(335,320);
		loginAccess.setResizable(false);
		loginAccess.center();
		loginAccess.setVisible(true);
	}

	protected CTCClient_T_GZSZ()
	{
		setTitle("登录界面");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String fileSeparator = System.getProperty("file.separator");
		if (fileSeparator == null || fileSeparator.equals(""))
			fileSeparator = "/";
		String IMAGE_PATH = System.getProperty("user.dir")+fileSeparator+"resources"+fileSeparator+"images"+fileSeparator;
		
		// Setting up the look and feel of the frame
		// by accessing the user's system configuration.
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			System.err.println("Can't set look and feel.");
		}


		// Setting up the MenuBar for the access of the
		// ConnectionManager, connection selection & advanced options.

		accessDialogMenuBar = new JMenuBar();
		accessDialogMenuBar.setBorder(BorderFactory.createEtchedBorder());
		accessDialogMenuBar.setMargin(new Insets(0,0,0,0));


		quitButton = new JButton(new ImageIcon(IMAGE_PATH +"quit.png"));
		quitButton.setFocusable(false);
		quitButton.setMargin(new Insets(0,0,0,0));
		quitButton.setToolTipText("退出登录");
		quitButton.addActionListener(this);
		accessDialogMenuBar.add(quitButton);

		setJMenuBar(accessDialogMenuBar);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(null);

		// MyJSQLView Icon Logo & Components
		JPanel iconPanel = new JPanel();
		iconPanel.add(new JLabel(new ImageIcon(IMAGE_PATH +"login_logo.png"), JLabel.LEFT));
		iconPanel.setBounds(0,0,180,210);//int x, int y, int width, int height
		centerPanel.add(iconPanel);
		
		cf.init();
		
		// Standard Parameters Panel & Components
		standardParametersPanel = new StandardParametersPanel(cf.getCtcServerIP(),cf.getCtcServerPort(),cf.getUserName());
		standardParametersPanel.setBounds(190, 0,130, 200);
		centerPanel.add(standardParametersPanel);


		mainPanel.add(centerPanel, BorderLayout.CENTER);

		// Action Button Panel
		JPanel actionPanel = new JPanel();
		actionPanel.setBorder(BorderFactory.createRaisedBevelBorder());


		loginButton = new JButton("登录");
		loginButton.addActionListener(this);
		actionPanel.add(loginButton);

		mainPanel.add(actionPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		(this.getRootPane()).setDefaultButton(loginButton);

	}
	
	protected static void displayErrors(String error)
	{
		JOptionPane.showMessageDialog(null, error,"提示信息", JOptionPane.ERROR_MESSAGE);
	}
	
	
	//CTCClient_T Loginhandle,String ipaddress ,String port, String username, String password
	protected void accessCheck()
	{
		// Check for some kind of valid input.
		host = standardParametersPanel.getHost();
		port = standardParametersPanel.getPort();
		userName = standardParametersPanel.getUserName();
		char[] passwordCharacters = standardParametersPanel.getPassword();
		
		if (host.equals("") ||port.equals("") ||userName.equals("") ||passwordCharacters.length == 0)
		{
			displayErrors("请输入完整的登录信息！");
			return;
		}
		passwordString  = "";
		// Obtaining the password & clearing.
		for (int i=0; i<passwordCharacters.length; i++)
		{
			passwordString += passwordCharacters[i];
			passwordCharacters[i] = '0';
		}
		
		//注册回调方法，供异步通信用，相关方法在CallbackServer中进行实现
		CallbackServer callbackServer = new CallbackServer();
		minaClient = new MinaClient(callbackServer);
		
		//connect(String ipaddress ,String port, String username, String password, int userrole)
		if( ! minaClient.connect(host,port,userName,passwordString,Constants.USER_ROLE_TUTOR)){//登录失败
			displayErrors("服务器连接出错！");
			if (minaClient != null)
				minaClient.closeConnection(Constants.CLIENT_CLOSE_NORMAL);
		}
		else{//成功连接
			LoginResponseMessage rMsg = minaClient.login();//验证用户身份
			
		    //解析private String trainPlan; 
		//	getStationplanList(rMsg);
			
			if (rMsg == null || rMsg.getResult() == Constants.SERVER_RESULT_ERROR){
				 displayErrors("用户名或密码有错！");
				 if (minaClient != null)
					minaClient.closeConnection(Constants.CLIENT_CLOSE_NORMAL);
				 return;
			 }else
			 if (rMsg.getResult() == Constants.SERVER_RESULT_RLOGIN){
				 displayErrors("你已经登录到系统！");
				 if (minaClient != null)
					minaClient.closeConnection(Constants.CLIENT_CLOSE_RLOGIN);//服务器不进行任何处理
				 return;
			 }else
			 if (rMsg.getResult() == Constants.CLIENT_CLOSE_ONEMORE){
				 displayErrors("已有教师或管理员登录！");
				 if (minaClient != null)
					minaClient.closeConnection(Constants.CLIENT_CLOSE_NORMAL);//服务器会进行有关处理
				 return;
			 }
						
			//LoginEntry(String newUserName,String newPassword,String newCtcServerIP,String newCtcServerPort)
			LoginEntry entry = new LoginEntry(userName,"",host,port);
			cf.save(entry);//Getting and saving the current selected site parameters before heading off to main window.
			setVisible(false);

			//根据情况不同,转向不同的系统
			/** 对于教师来讲，rMsg.getTeamID(),rMsg.getDistrictName()参数仅仅是为了调试程序用，roleFlag = true参数主要是为了屏蔽掉TDCS上的一些菜单项
			 * 对于组内TDCS来讲，rMsg.getTeamID(),rMsg.getDistrictName()是必须的，roleFlag应该设为：false
			*/
			if(rMsg.getUserRole() == Constants.USER_ROLE_TEACHER){//启动教师管理界面
				boolean roleFlag = true;//true表示教师启动TDCS
				new CTCTeacherMain(minaClient,userName,passwordString,rMsg.getTeamID(),rMsg.getDistrictName(),roleFlag).start();//xbm2010-4-20修订
			}
			else
			if(rMsg.getUserRole() == Constants.USER_ROLE_ADMIN){//启动管理员界面
				new CTCAdminMain(minaClient,userName,passwordString).start();
			}
		}
	}
	//String stationName = rMsg.getStationName();//分配给某学员的车站名称
	//实验代码   返回不同车站的车次计划Map<String,List<Plan>>
	private Map<String,List<Plan>> getStationplanList(LoginResponseMessage rMsg){
		
		if (rMsg == null)
			return null;
		String planJsonStr = rMsg.getTrainPlan();//所有车次信息 
		if ((planJsonStr == null)||(planJsonStr.isEmpty()))
			return null;
 
		//String 表示车站名 List<Plan>表示经过该车站的所有车次的信息 
		Map<String,List<Plan>> sationsPlanMap = new HashMap<String,List<Plan>>();
		
		//所有车次信息
		List<Plan> planList = new ArrayList<Plan>();
		planList = JsonUtil.getList4Json(planJsonStr,Plan.class);
		if ((planList == null)||(planList.isEmpty()))
			return null;
		int planSize = planList.size();
		System.out.println("planSize:"+planSize);
		
		//所有车站名称信息
		String stationsJsonStr = rMsg.getStationsList();
		List<String> stationsList = JsonUtil.getList4Json(stationsJsonStr,String.class);
		if ((stationsList == null)||(stationsList.isEmpty()))
			return null;
		int stationsSize = stationsList.size();
		
		System.out.println("stationsSize:"+stationsSize);
		
		for (int i = 0; i < stationsSize; i++)
		{
			String stationName = stationsList.get(i);
			List<Plan> listValue = new ArrayList<Plan>();
			//获取经过某一车站stationName的所有车次信息
			for (int j = 0; j < planSize; j++){
				Plan data = new Plan();
				data = (Plan)planList.get(i);
				if(data.getStation_name().equalsIgnoreCase(stationName))
					listValue.add(data);
			}
			System.out.println("listValueSize:"+listValue.size());	
			sationsPlanMap.put(stationName, listValue);

		}
		System.out.println("sationsPlanMap:"+sationsPlanMap.size());

		return sationsPlanMap;
	}
	
	//planList是指定车站stationName的所有车次信息列表
	private void getRecSendTask(String stationName, List<Plan> planList){
		
		if ((stationName == null)||(stationName.isEmpty()))
			return;
		
		if ((planList == null)||(planList.isEmpty()))
			return;
 
		/**可以定义在方法外供其他代码调用*/
		//接车车次信息  到站时间
		List<Plan> recList = new ArrayList<Plan>();
		//发车车次信息 离站时间
		List<Plan> sendList = new ArrayList<Plan>();
		int planSize = planList.size();
		
		List<Plan> listValue = new ArrayList<Plan>();
		//获取经过某一车站stationName的所有车次信息
		for (int i = 0; i < planSize; i++){
			Plan data = new Plan();
			data = (Plan)planList.get(i);
			if(data.getStation_name().equalsIgnoreCase(stationName)){
				recList.add(data);
				sendList.add(data);
			}
		}
	}
	
	
	public void actionPerformed(ActionEvent evt)
	{	
		Object panelSource = evt.getSource();
		// Button Actions
		if (panelSource instanceof JButton){	
			JButton actionButton = (JButton)panelSource;
			// Login Attempt
			if (actionButton == loginButton){
				accessCheck();//连接服务器
			}
			else if (actionButton == quitButton ){
				//同步通信处理
				 if (minaClient != null)
					 minaClient.closeConnection(Constants.CLIENT_CLOSE_NORMAL);
				System.exit(1);
			}
		}
	}

}
