package ctc.ui.teacher.experiment;

import java.awt.Toolkit;
import java.util.List;
import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;
import ctc.constant.Constants;
import ctc.pojobean.District;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.*;
import ctc.util.JsonUtil;

public class ExperimentSetLayout{

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
	
	ExperimentData experimentData = new ExperimentData();
	
	org.eclipse.swt.widgets.List swtSelectedList;
	int selectedListIndex = -1;
    
	public ExperimentSetLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"设置实验环境","pos 0.5al 0.2al",null);
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

	//	getDistrictInfo();
		
	    final Label label = new Label(comp, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label.setText("工作模式:");
		final Combo model = new Combo (comp, SWT.READ_ONLY);
		model.setItems (new String [] {"自律模式","人工模式"});
		model.select(0);
		GridData data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
		data.heightHint = 15;
	    data.widthHint = 100;
		model.setLayoutData(data);
	    
	    final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("实验类别:");
		final Combo subject = new Combo (comp, SWT.READ_ONLY);
		//subject.setItems (new String [] {"综合实验","行车调度","车站联锁"});
		subject.setItems (new String [] {"综合实验"});
		subject.select(0);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
		data.heightHint = 15;
	    data.widthHint = 100;
		subject.setLayoutData(data);

		final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("区段名称:");
		final Combo districtName = new Combo (comp, SWT.READ_ONLY);
		districtName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (experimentData.districtFlag){
					experimentData.reloadFlag = true;
				}
				if (! experimentData.reloadFlag)
					return;
				
				getDistrictInfo();//从服务器获取车站信息
				
				if(experimentData.districtName != null){
					//hu 2010-11-2 修改
					//districtName.setItems(experimentData.districtName);
					districtName.setItems(new String [] {"实验区段"});
					experimentData.reloadFlag = false;
					experimentData.districtFlag = false;
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
		    });
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
		data.heightHint = 15;
	    data.widthHint = 100;
	    districtName.setLayoutData(data);
	    
	  new Label(comp, SWT.NONE);
		ToolBar toolBar_2 = new ToolBar(comp, SWT.FLAT);
		ToolItem retrievalToolItem = new ToolItem(toolBar_2, SWT.PUSH);
		retrievalToolItem.setText("确定");
		retrievalToolItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String modelStr = model.getText();
				String subjectStr = subject.getText();
				String districtText = districtName.getText();
				if( (modelStr.length() == 0)||
					(subjectStr.length() == 0)||
					(districtText.length() == 0) )
					{
						showMsg("请对所有选项赋值!");
						return;
					}
				
				String districtID = districtText;
				if(! setExperimentEnv(modelStr,subjectStr,districtID)){
					showMsg("操作失败！");
				}
				else{
					showMsg("操作成功！");
				}
			}
		});
		ToolItem aboutToolItem = new ToolItem(toolBar_2, SWT.PUSH);
		aboutToolItem.setText("帮助");
		aboutToolItem.addListener(SWT.Selection, aboutListener);
	
	}
	
	
	Listener aboutListener = new Listener() {
		public void handleEvent(Event e) {
			final Shell s = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			s.setText("说明");
			GridLayout layout = new GridLayout(1, false);
			layout.verticalSpacing = 20;
			layout.marginHeight = layout.marginWidth = 10;
			s.setLayout(layout);
			Label label = new Label(s, SWT.NONE);
			label.setText("工作模式\n\r" +
						   "1)人工模式：无须学员参与\n\r" +
					       "2)自律模式：需要学员参与");
			Button button = new Button(s, SWT.PUSH);
			button.setText("返回");
			GridData data = new GridData();
			data.horizontalAlignment = GridData.CENTER;
			button.setLayoutData(data);
			button.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					s.dispose();
				}
			});
			s.pack();
			Rectangle parentBounds = shell.getBounds();
			Rectangle bounds = s.getBounds();
			int x = parentBounds.x + (parentBounds.width - bounds.width)/ 2;
			int y = parentBounds.y + (parentBounds.height - bounds.height)/ 2;
			s.setLocation(x, y);
			s.open();
			Display display = shell.getDisplay();
			while (!s.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
	};
	
	private void showMsg(String str){
		Toolkit.getDefaultToolkit().beep();//报警
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");
		mb.setMessage(str);
		mb.open();
	}
	
	private void getDistrictInfo() {
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("District");

		msg.setSql("select * from District");
		String paprams = "null";//
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);
		if(listString == null){
			return;		
		}else{
			List<District> list = JsonUtil.getList4Json(listString,District.class);
			experimentData.setDistrictList(list);
		}
	}
	
	private boolean setExperimentEnv(String modelStr,String subjectStr,String districtID){
		
		ExperimentCommandMessage sMsg = new ExperimentCommandMessage();
		
		sMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//设置用户角色
		sMsg.setCommandType(Constants.TYPE_CLIENT_EXPERIMENT_ENV);
		
		if(modelStr.equalsIgnoreCase("自律模式"))
			sMsg.setRunItem(Constants.RUN_MODE_AUTO);
		else
		if(modelStr.equalsIgnoreCase("人工模式"))
			sMsg.setRunItem(Constants.RUN_MODE_MANUAL);
		
		if(subjectStr.equalsIgnoreCase("行车调度"))
			sMsg.setSubjectItem(Constants.EXPERIMENT_MODE_TDCS);
		else
		if(subjectStr.equalsIgnoreCase("车站联锁"))
			sMsg.setSubjectItem(Constants.EXPERIMENT_MODE_SICS);
		else
		if(subjectStr.equalsIgnoreCase("综合实验"))
			sMsg.setSubjectItem(Constants.EXPERIMENT_MODE_TDSI);
		
		
		sMsg.setDistrictName(districtID);
			
		return synClientSupport.ExperimentCommandMessageSend(sMsg);//同步通信
		
	}
}
