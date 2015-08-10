package ctc.tdcs.command;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import ctc.util.ErrorLog;


public class ScheduleCommand extends Dialog {

	private Shell shell;
		
	TeamMemberData teamMemberData = TeamMemberData.getInstance();
	
    public ScheduleCommand(Shell shell) {
		super(shell, SWT.SYSTEM_MODAL | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.shell = shell;
		
	}
	
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}

	// 打开对话框
	public void open() {
		
		Display display = getParent().getDisplay();

		// 创建一个对话框窗口
		Shell shell = new Shell(getParent(), getStyle());

		shell.setText("发送调度命令");
		createContents(shell);
		shell.pack();

		// 使对话框窗口处于屏幕中间
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
		Rectangle shellBounds = shell.getBounds();// 获取屏幕高度和宽度
		int x = displayBounds.x + (displayBounds.width - shellBounds.width) >> 1;
		int y = displayBounds.y + (displayBounds.height - shellBounds.height) >> 1;
		shell.setLocation(x, y);// 定位窗口坐标
		shell.setSize(400,250);
		
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	// 创建对话框内容
	private void createContents(final Shell shell) 
	{

		teamMemberData.getTeamsInfo();//从服务器获取组数据，并保存在有关变量中
		
		// 建立一个默认的GridLayout布局
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;// 列数目
		gridLayout.marginRight = 50;
		
		/*gridLayout.marginRight = 10;
		gridLayout.marginWidth = 20;// 控制左边和右边组件离边缘的距离空间,以像素为单位.
		gridLayout.makeColumnsEqualWidth = true; // 强制全部的列拥有相同的宽度
		gridLayout.horizontalSpacing = 10;// 控制一行中两个网格间组件的宽度,像素为单位
		gridLayout.verticalSpacing = 20;// 一列中两个网络间组件的宽度,像素为单位
		gridLayout.marginHeight = 20;// 控制顶部和底部组件离边缘的距离空间,以像素为单位.
		*/
		// 为Shell设置布局对象
		shell.setLayout(gridLayout);

		Label label = new Label(shell, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		// 创建默认GridData对象.
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		label.setLayoutData(data);
		label.setText("小组编号:");
		final Combo team = new Combo(shell,SWT.READ_ONLY);
		team.setItems(teamMemberData.teamItems);
		team.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		team.addSelectionListener(new SelectionListener(){//当用鼠标选取下拉项，就执行
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			@Override
			public void widgetSelected(SelectionEvent e){
				if (!teamMemberData.teamFlag)
				{
					String selectedTeamID = team.getText();
					//从服务器获取该组内所有成员
					teamMemberData.getTeamMembersInfo(selectedTeamID);
					teamMemberData.teamFlag = true;
				}
			}
		});

		Label label2 = new Label(shell, SWT.NONE);
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(data);
		label2.setText("小组成员:");
		final Combo member = new Combo(shell,SWT.READ_ONLY);
		member.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		member.removeAll();//先清空
		member.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (teamMemberData.teamFlag)
				{
					//hu 2010-11-3
					//member.setItems(teamMemberData.teamMemberItems);
					//String [] teamMemberItems = {"组内:TDCS","组内:CTC","组内:电务维修机","组内:车站一","组内:车站二","组内:车站三","组内:车站四","组内:车站五"};
					String [] teamMemberItems = {"组内:CTC","组内:电务维修机","组内:车站一","组内:车站二","组内:车站三","组内:车站四","组内:车站五"};
					member.setItems(teamMemberItems);
					teamMemberData.teamFlag = false;
				}
			}	
			@Override
			public void focusLost(FocusEvent e) {}
		});
		
		
		final Label label3 = new Label(shell, SWT.NONE);
		label3.setAlignment(SWT.RIGHT);
		label3.setLayoutData(data);
		label3.setText("命令内容:");
		final Text text = new Text(shell, SWT.MULTI |SWT.LEFT |SWT.WRAP |SWT.BORDER | SWT.V_SCROLL);
		text.setTextLimit(5100);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Create the OK button
		final Button ok = new Button(shell, SWT.RIGHT);
		ok.setAlignment(SWT.RIGHT);
		ok.setText("发送");
		ok.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				String teamIDStr = team.getText().trim();
				String memberStr = member.getText().trim();
				String contentStr = text.getText().trim();
				
				if(teamIDStr == null || teamIDStr.length() == 0)
				{
					showMsg("请选取实验小组!");
					return;
				}
				if(memberStr == null || memberStr.length() == 0)
				{
					showMsg("请选取组内成员!");
					return;
				}
				if( contentStr == null || contentStr.length() == 0)
				{
					showMsg("请输入命令!");
					return;
				}
				int teamID = Integer.parseInt(teamIDStr.substring(teamIDStr.indexOf(":") + 1));
				
				//memberStr = memberStr.substring(0,memberStr.indexOf(":"));
				memberStr = memberStr.substring(memberStr.indexOf(":")+1, memberStr.length());
				
				ErrorLog.log("teamID = "+ teamID + "  memberStr = " + memberStr + "  contentStr=" + contentStr);
				teamMemberData.SendScheduleCommand(teamID, memberStr, contentStr);
				
				//showMsg("命令已发出!");
				
				return;
			}
		});

		// Create the Cancel button
		final Button cancel = new Button(shell, SWT.PUSH);
		cancel.setAlignment(SWT.RIGHT);
		cancel.setText("取消");
		cancel.setLayoutData(new GridData());

		// 当主窗口关闭时，会触发DisposeListener。这里用来释放Panel的背景色。
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				//bkColor.dispose();
				shell.close();
			}
		});

		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
			//	bkColor.dispose();
				shell.close();
			}
		});

		// Allow user to press Enter to accept and close
		shell.setDefaultButton(ok);
	}
	
   
}
