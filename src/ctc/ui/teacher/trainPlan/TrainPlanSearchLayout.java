package ctc.ui.teacher.trainPlan;

import java.util.List;
import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import ctc.constant.Constants;
import ctc.pojobean.*;
import ctc.transport.*;
import ctc.transport.message.*;
import ctc.util.*;


public class TrainPlanSearchLayout {

	private static SynClientSupport synClientSupport;
	private Shell shell;
	TrainPlanData dataInstance = new TrainPlanData();

	public TrainPlanSearchLayout(){}
	public TrainPlanSearchLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();
		this.shell = shell;
	}

	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"列车原始计划查询","pos 0.5al 0.2al",null);
		return  comp;
	}

	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}
	
	private void createComp(Composite group) {
		
		getTrainInfo();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		group.setLayout(gridLayout);
		
		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label.setLayoutData(data);
		label.setText("选取列车车次:");
		final Combo comb = new Combo(group,SWT.READ_ONLY);
		comb.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (dataInstance.searchFlag){
					dataInstance.reloadFlag = true;
				}
				if (! dataInstance.reloadFlag)
					return;
				
				getTrainInfo();
				
				if(dataInstance.trainName != null){
					comb.setItems(dataInstance.trainName);
					dataInstance.reloadFlag = false;
					dataInstance.searchFlag = false;
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
		});
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 80;
	    comb.setLayoutData(data);
	  
	    new Label(group, SWT.NONE);
	    Button OKButton = new Button(group, SWT.PUSH);//SWT.DOWN
	    OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
	    OKButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	    OKButton.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(final SelectionEvent e) {
	    		if (comb.getText().length() == 0){
						showMsg("请选取查询条件!");
						return;
				}
					
	    		String selText = comb.getText();
	    		String trainID = selText;
	    	
				if (getPlanInfo(trainID)){
					new TrainPlanBrowserLayout(TrainPlanSearchLayout.this.shell,
													 TrainPlanSearchLayout.this.synClientSupport,
													 trainID).show();
				}
				else
					showMsg("该车次原始计划不存在!");
	    	}
	    });
	    OKButton.setText("查询");
	}
	
	//////////////////////////////////
	
	private void showMsg(String str){
		//Toolkit.getDefaultToolkit().beep();//报警
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");
		mb.setMessage(str);
		mb.open();
	}
	
	private void getTrainInfo() {
    	SQLRequestMessage msg = new SQLRequestMessage();
    	msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
    	msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
    	msg.setDataBean("Train");

    	msg.setSql("select * from Train");
    	String paprams = "null";
    	msg.setParams(paprams);

    	String listString = synClientSupport.sqlMessageSend(msg);
    	if(listString == null){//不可能出现
    		return;		
    	}else{
    		List<Train> list = JsonUtil.getList4Json(listString,Train.class);
    		dataInstance.setTrainList(list);
    	}
    }
	
	private boolean getPlanInfo(String trainID){

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);

		msg.setDataBean("Plan");
		msg.setSql("select * from Plan where Train_name=?");
		Object[] params = new Object[]{trainID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		
		if(listString == null){//此情况不会出现
			return false;		
		}else{
			List<Plan> list = JsonUtil.getList4Json(listString,Plan.class);
			if(list.size() <= 0)
				return false;
			else{
				dataInstance.setData(list);
				return true;
			}
		}
	}


}


