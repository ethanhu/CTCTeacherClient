package ctc.ui.teacher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ctc.constant.Constants;
import ctc.databaseserver.DatabaseAccessHandler;
import ctc.transport.MinaClient;
import ctc.transport.message.CommonMessage;
import ctc.transport.message.ErrorMessage;
import ctc.ui.CTCTeacherMain;
import ctc.util.ErrorLog;
import ctc.util.JsonUtil;
import ctc.util.SystemTray;

public class ErrorMainWindow {

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
	 * 
	 * @param newShell
	 *            the parent shell
	 */

	public ErrorMainWindow(Shell newShell, MinaClient minaClient, String userName, String password, int teamID, String districtName, boolean roleFlag) {

		sysTray = new SystemTray(minaClient);
		this.minaClient = minaClient;
		this.userName = userName;
		this.password = password;

		this.teamID = teamID;

		/** xbm2010-4-20添加 */
		this.roleFlag = roleFlag;
		this.districtName = districtName;

		shell = newShell;
		shell.setText("CTC仿真系统客户端(故障设置)");

		createContents();

		// 监听关闭窗口事件,对应窗口右上角的关闭按钮
		shell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				CTCTeacherMain.getApp().closeWindow();
			}
		});
	}

	/**
	 * Gets the shell (main window) of the application
	 * 
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

	private void createContents() {
		
		final String[] M1 = { "0"};
		final String[] M2 = { "车站一", "车站二", "车站三", "车站四", "车站五" };

		final String[] MENU1 = { "股道", "道岔", "信号机" };

		final String[] MENU2_1 = { "I", "II", "3", "4", "5", "6" };

		final String[] MENU2_2 = { "1/3_5/7", "2/4_6/8", "11", "12", "13", "15", "17" };

		final String[] MENU2_3 = { "S1", "S2", "S3", "S4", "S5", "S6", "X1", "X2", "X3", "X4", "X5", "X6" };

		final String[] MENU3_1 = { "股道断裂", "股道维修" };

		final String[] MENU3_2 = { "挤岔", "断裂" };

		final String[] MENU3_3 = { "灯丝断裂", "线路故障" };

		// Display display = getParent().getDisplay();

		// final Shell shell = new Shell(getParent(), getStyle());

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 15;
		gridLayout.marginHeight = 150;
		
		gridLayout.numColumns = 6;
		gridLayout.horizontalSpacing = 20;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
				
		final Label lab1 = new Label(shell, SWT.NONE);
		final Label lab2 = new Label(shell, SWT.NONE);
		
		final Label lab3 = new Label(shell, SWT.NONE);
		lab3.setText("故障设置与恢复");
		lab3.setFont(new Font(Display.getDefault(), "Times New Roman", 18, SWT.NORMAL));
		
		
		final Label lab4 = new Label(shell, SWT.NONE);		
		final Label lab5 = new Label(shell, SWT.NONE);
		final Label lab6 = new Label(shell, SWT.NONE);
		
		final Label lab21 = new Label(shell, SWT.NONE);
		final Label lab22 = new Label(shell, SWT.NONE);
		final Label lab23 = new Label(shell, SWT.NONE);		
		final Label lab24 = new Label(shell, SWT.NONE);
		final Label lab25 = new Label(shell, SWT.NONE);
		final Label lab26 = new Label(shell, SWT.NONE);
		
		final Label lab31 = new Label(shell, SWT.NONE);
		final Label lab32 = new Label(shell, SWT.NONE);
		final Label lab33 = new Label(shell, SWT.NONE);		
		final Label lab34 = new Label(shell, SWT.NONE);
		final Label lab35 = new Label(shell, SWT.NONE);		
		final Label lab36 = new Label(shell, SWT.NONE);
		
		final Label lab41 = new Label(shell, SWT.NONE);
		final Label lab42 = new Label(shell, SWT.NONE);
		final Label lab43 = new Label(shell, SWT.NONE);		
		final Label lab44 = new Label(shell, SWT.NONE);
		final Label lab45 = new Label(shell, SWT.NONE);		
		final Label lab46 = new Label(shell, SWT.NONE);
		
		final Label lable1 = new Label(shell, SWT.NONE);
		lable1.setText("小组");

		final Label lable2 = new Label(shell, SWT.NONE);
		lable2.setText("车站");
		
		final Label label1 = new Label(shell, SWT.NONE);
		label1.setText("类型");

		final Label label2 = new Label(shell, SWT.NONE);
		label2.setText("名称");

		final Label label3 = new Label(shell, SWT.NONE);
		label3.setText("故障名称");

		final Button button1 = new Button(shell, SWT.NONE);
		button1.setText("设置故障");

		
		final Combo com1 = new Combo(shell, SWT.NONE);
		com1.setBounds(10, 10, 200, 30);
		com1.setItems(M1);
		com1.select(0);
		
		final Combo com2 = new Combo(shell, SWT.NONE);
		com2.setBounds(100, 10, 200, 30);
		com2.setItems(M2);
		com2.select(0);
		
		
		final Combo combo1 = new Combo(shell, SWT.NONE);
		combo1.setBounds(200, 10, 200, 30);
		combo1.setItems(MENU1);
		combo1.select(0);

		final Combo combo2 = new Combo(shell, SWT.NONE);
		combo2.setBounds(300, 10, 200, 30);
		combo2.setItems(MENU2_1);
		combo2.select(0);

		final Combo combo3 = new Combo(shell, SWT.NONE);
		combo3.setBounds(400, 10, 200, 30);
		combo3.setItems(MENU3_1);
		combo3.select(0);

		final Button button2 = new Button(shell, SWT.NONE);
		button2.setText("恢复故障");

		combo1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedItem = combo1.getItem(combo1.getSelectionIndex());

				if (selectedItem.equals("股道")) {
					combo2.removeAll();
					combo2.setItems(MENU2_1);

					combo3.removeAll();
					combo3.setItems(MENU3_1);
				} else if (selectedItem.equals("道岔")) {
					combo2.removeAll();
					combo2.setItems(MENU2_2);

					combo3.removeAll();
					combo3.setItems(MENU3_2);
				} else if (selectedItem.equals("信号机")) {
					combo2.removeAll();
					combo2.setItems(MENU2_3);

					combo3.removeAll();
					combo3.setItems(MENU3_3);
				}
				combo2.select(0);
				combo3.select(0);
			}
		});

		button1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				//设置故障
				SZGZ(0, com2.getText(), combo2.getText());

			}
		});

		button2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				//设置故障
				HFGZ(0, com2.getText(), combo2.getText());
			}
		});
		shell.pack();
		shell.open();

	}
	
	
	//设置故障
	public void SZGZ(int teamID, String stationName, String figName){
		
		ErrorLog.log("设置故障：--开始--");
		
		CommonMessage cMsg = new CommonMessage();
		cMsg.setTeamID(teamID);		
		cMsg.setCommandType(Constants.TYPE_DDZR_TO_SICS_ASYN);
		cMsg.setStationName(stationName);
				
		ErrorMessage sMsg = new ErrorMessage();
		sMsg.setStationName(stationName);
		sMsg.setFigName(figName);
		sMsg.setTeamID(teamID);
		sMsg.setType(false);
		
		cMsg.setMeseageName("ErrorMessage");
		cMsg.setMessage(JsonUtil.bean2json(sMsg));		
		
		DatabaseAccessHandler.getInstance().sendCommonMessageToServer(cMsg);
		
		ErrorLog.log("设置故障：--结束--");
	}

	//设置故障
	public void HFGZ(int teamID, String stationName, String figName){
		
		CommonMessage cMsg = new CommonMessage();
		cMsg.setTeamID(teamID);		
		cMsg.setCommandType(Constants.TYPE_DDZR_TO_SICS_ASYN);
		cMsg.setStationName(stationName);
				
		ErrorMessage sMsg = new ErrorMessage();
		sMsg.setStationName(stationName);
		sMsg.setFigName(figName);
		sMsg.setTeamID(teamID);
		sMsg.setType(true);
		
		cMsg.setMeseageName("ErrorMessage");
		cMsg.setMessage(JsonUtil.bean2json(sMsg));		
		
		DatabaseAccessHandler.getInstance().sendCommonMessageToServer(cMsg);
		
	}
	

}
