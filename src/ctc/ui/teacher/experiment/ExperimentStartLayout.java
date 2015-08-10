package ctc.ui.teacher.experiment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.*;
import ctc.constant.Constants;

public class ExperimentStartLayout{

	private static SynClientSupport synClientSupport;
	private static Shell shell;
	
	public ExperimentStartLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"启动实验","pos 0.5al 0.2al",null);
		return  comp;
	}
	
	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}
	
	private void createComp(Composite comp) {
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		comp.setLayout(gridLayout);

		GridData data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
		final Label label_4 = new Label(comp, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_4.setText("虚拟时间:");
		final DateTime time = new DateTime (comp, SWT.TIME | SWT.SHORT);//SWT.CALENDAR SWT.DATE
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
		data.heightHint = 20;
		data.widthHint = 90;
		time.setLayoutData(data);
		
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
		final Label label = new Label(comp, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label.setText("时间步长(分钟):");
		final Text minute = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.widthHint = 85;
	    minute.setLayoutData(data);
	    minute.addVerifyListener(listener);//检查监听器,每输入一个字符都回触发
	    minute.setText("1");//默认值
	    minute.setTextLimit(2);  //最都只能输入3位数字
		
		new Label(comp, SWT.NONE);
		ToolBar toolBar_2 = new ToolBar(comp, SWT.FLAT);
		final ToolItem startItem = new ToolItem(toolBar_2, SWT.PUSH);
		
		/**xbm2010-4-20删除*/
		//final ToolItem beginItem = new ToolItem(toolBar_2, SWT.PUSH);
		
		startItem.setText("启动");
		startItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String timeStr = time.getHours () + ":" + time.getMinutes();
				if(timeStr.length() >0){
					if(! startExperiment(timeStr,minute.getText()))
						showMsg("操作失败！");
					else{
						showMsg("操作成功！");
						//beginItem.setEnabled(true);
					}
			    }else{
					showMsg("请输入虚拟时间！");
				}
			}
		});
		
		/**xbm2010-4-20删除*/
		/*beginItem.setText("运行");
		//beginItem.setEnabled(false);
		beginItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(! runExperiment())
					showMsg("操作失败！");
				else{
					showMsg("操作成功！");
					//beginItem.setEnabled(false);
				}
			}
		});
		*/
		ToolItem helpItem = new ToolItem(toolBar_2, SWT.PUSH);
		helpItem.setText("帮助");
		helpItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				 String str = "启动:\n\r" +
	 			              "使服务器开始接收学员登录 \n\r\n\r";// + "运行：\n\r" +"实验正式开始 ,可以进行实质性操作\n\r";
				 showMsg(str);
			}
		});
	}
	
	

	VerifyListener listener = new VerifyListener() {
		//doit属性如果为true,则字符允许输入,反之不允许
		public void verifyText(VerifyEvent e) {   
			//输入控制键，输入中文，输入字符，输入数字 正整数验证   
			Pattern pattern = Pattern.compile("[0-9]\\d*"); //正则表达式  
			Matcher matcher = pattern.matcher(e.text);   
			if (matcher.matches()) //处理数字   
			{
				e.doit = true;
			}
			else if (e.text.length() > 0) //字符: 包含中文、空格   
				e.doit = false;
			else//控制键  
				e.doit = true;   
		}   
	};
	
	private void showMsg(String str){
		//Toolkit.getDefaultToolkit().beep();//报警
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");
		mb.setMessage(str);
		mb.open();
	}
	
	
	private boolean startExperiment(String timeStr,String timeStep){

		ExperimentCommandMessage sMsg = new ExperimentCommandMessage();

		sMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//设置用户角色
		sMsg.setCommandType(Constants.TYPE_CLIENT_EXPERIMENT_START);
		sMsg.setTime(timeStr);
		if(timeStep.length() == 0)
			timeStep = "2";
		sMsg.setTimeStep(timeStep);
		
		return synClientSupport.ExperimentCommandMessageSend(sMsg);
	}
	//开始按钮发送消息到服务器
	private boolean runExperiment(){

		ExperimentCommandMessage sMsg = new ExperimentCommandMessage();

		sMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//设置用户角色
		sMsg.setCommandType(Constants.TYPE_CLIENT_EXPERIMENT_RUN);
				
		return synClientSupport.ExperimentCommandMessageSend(sMsg);

	}

	
}

