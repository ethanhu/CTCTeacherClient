package ctc.ui.teacher.districttrainrelation;


import java.util.ArrayList;
import java.util.List;
import net.miginfocom.swt.MigLayout;

import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;
import ctc.constant.Constants;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.util.JsonUtil;
import ctc.pojobean.*;

public class DistrictTrainCreateLayout{

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
	
	DistrictTrainData districtTrainData = new DistrictTrainData();
	
	public DistrictTrainCreateLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout( new MigLayout());  
		createGroup(comp, "定义区段与车次关系","pos 0.5al 0.2al",null);
		return comp;
	}
	
	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}

	org.eclipse.swt.widgets.List swtSelectedList;
	int selectedListIndex = -1;
	
	private void createComp(Composite comp) {
		
		getTrainInfo();
		getDistrictInfo();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		comp.setLayout(gridLayout);
        
        final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label_1.setLayoutData(data);
		label_1.setText("区段名称:");
		final Combo districtName = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		districtName.removeAll(); //先清空stationLayout
		districtName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (districtTrainData.districtFlag){
					districtTrainData.reloadFlag = true;
				}
				
				if (! districtTrainData.reloadFlag)
					return;
				
				getDistrictInfo();//从服务器获取车站信息
				
				if(districtTrainData.districtName != null){
					districtName.setItems(districtTrainData.districtName);
					districtTrainData.reloadFlag = false;
					districtTrainData.districtFlag = false;
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
		    });
		//显示已存在车站名
		districtName.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e){
				districtTrainData.stationFlag = true;
				
				swtSelectedList.removeAll();
				String districtID = districtName.getText();
	    		List <TrainDistrictRelation> list = getDistrictTrainInfo(districtID);
	    		if(list == null || list.size() ==0)
	    			return;
				for(int i=0; i<list.size(); i++){
					TrainDistrictRelation data = list.get(i);
					String item = data.getTrain_name();
					swtSelectedList.add(item);
				}
			}
		});
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 126;
	    districtName.setLayoutData(data);
	    
	    final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("车次名称:");
		
		final Combo trainName = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		trainName.removeAll(); //先清空stationLayout
		trainName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (districtTrainData.stationFlag){
					districtTrainData.reloadFlag = true;
				}else
					return;
				
				
				if (! districtTrainData.reloadFlag)
					return;
				
				getTrainInfo();//从服务器获取车站信息
				
				if(districtTrainData.trainName != null){
					trainName.setItems(districtTrainData.trainName);
					districtTrainData.reloadFlag = false;
					districtTrainData.stationFlag = false;
				}
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 126;
	    trainName.setLayoutData(data);
	 
	    new Label(comp, SWT.NONE);
  	    Action actionSelect = new Action("选取") {//清除所显示内容，点击保存后更新库  
			 public void run() {
				 String selText = trainName.getText();
				 if ((selText != null)&& (swtSelectedList.indexOf(selText)  == -1))
				 {
					 swtSelectedList.add(selText);
				 }
			 }  
		 };
		 Action actionDelete = new Action("删除") {//清除所显示内容，点击保存后更新库  
			 public void run() {
				 if ( (swtSelectedList.getItemCount() != 0) && (selectedListIndex != -1))
				 {
					swtSelectedList.remove(selectedListIndex);
				 	selectedListIndex = -1;
				 }
			 }  
		 }; 
		 ToolBar toolBar = new ToolBar(comp, SWT.FLAT|SWT.RIGHT);//|SWT.BORDER
		 ToolBarManager manager = new ToolBarManager(toolBar);
		 manager.add(actionSelect);
		 manager.add(actionDelete);
		 manager.update(true);
		 
	    final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("已选取车次:");
		swtSelectedList = new org.eclipse.swt.widgets.List(comp,SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);//SWT.MULTI
		swtSelectedList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) e.getSource();
				selectedListIndex = list.getSelectionIndex();
			}
		});
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 150;
	    data.widthHint = 130;
	    swtSelectedList.setLayoutData(data);
	    
	    new Label(comp, SWT.NONE);
	    Action actionAppend = new Action("追加") {  
			 public void run() {
				if (districtName.getText().length() == 0)
				{
					showMsg("请选取区段名称!");
					return;
				}
				String []items =  swtSelectedList.getItems();
				if (items.length == 0)
				{
					showMsg("请选取车次!");
					return;
				}
				List<TrainDistrictRelation> infoList =  new ArrayList<TrainDistrictRelation>();
				String districtID = districtName.getText();//区段名称
				for (int i = 0; i < items.length; i++ ){
					TrainDistrictRelation info = new TrainDistrictRelation(districtID,items[i]); 
					infoList.add(info);
				}
				 if(appendDistrictInfo(districtID,infoList))
					showMsg("操作成功!");
				 else
					showMsg("操作失败!");
			 }  
		 };
	    Action actionSave = new Action("更新") {  
			 public void run() {
				if (districtName.getText().length() == 0)
				{
					showMsg("请选取区段名称!");
					return;
				}
				String []items =  swtSelectedList.getItems();
				if (items.length == 0)
				{
					showMsg("请选取车次!");
					return;
				}
				List<TrainDistrictRelation> infoList =  new ArrayList<TrainDistrictRelation>();
				String districtID = districtName.getText();
				
				for (int i = 0; i < items.length; i++ ){
					String trainID = items[i];
					//System.out.println(district+ ":"+station);
					TrainDistrictRelation info = new TrainDistrictRelation(districtID,trainID); 
					infoList.add(info);
				}
				 if(updateDistrictInfo(districtID,infoList))
					showMsg("操作成功!");
				 else
					showMsg("操作失败!");
			 }  
		 };
		 Action actionReset = new Action("复位") {  
			 public void run() {
				 swtSelectedList.removeAll();
			 }  
		 }; 
		 Action actionHelp = new Action("帮助") {  
			 public void run() {
				 String str =  "功能:\n\r" +
				 			   "定义一个区段内的所有车站信息：\n\r" +
			                   "删除：\n\r" +
			                   "从已选取站中选取要删除的车站名，然后点击删除按钮\n\r"+
			                   "复位：\n\r" +
			                   "清空已选取站表中所有内容\n\r" +
				 			   //"追加：\n\r" +
				 			   //"添加新的内容到该区段\n\r"+
				 			   "更新：\n\r" +
				 			   "保存信息到库中";
				 showMsg(str);
			 }  
		 };  
		 ToolBar toolBar_1 = new ToolBar(comp, SWT.FLAT|SWT.RIGHT);//|SWT.BORDER
		 ToolBarManager manager_1 = new ToolBarManager(toolBar_1);
		// manager_1.add(actionAppend);
		 manager_1.add(actionSave);
		// manager_1.add(actionReset);
		 manager_1.add(actionHelp);
		 manager_1.update(true);
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
    		districtTrainData.setTrainList(list);
    	}
    }
	
	private void getDistrictInfo() {
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("District");

		msg.setSql("select * from District");// limit 16
		String paprams = "null";//
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);
		if(listString == null){
			return;		
		}else{
			List<District> list = JsonUtil.getList4Json(listString,District.class);
			districtTrainData.setDistrictList(list);
		}
	}
	
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
	//目前没有用
	private boolean appendDistrictInfo(String districtID, List<TrainDistrictRelation> list){
		int length = list.size();
		if(length <= 0)
			return false;
		String[]sqlStr = new String[length];
		int rowNumber = 0;

		for (int i = 0; i < list.size(); i++) {//获取每行的内容  
			TrainDistrictRelation info = list.get(i);
			
			String inertStr = "insert into TrainDistrictRelation(District_name,Train_name) Values('"+ info.getDistrict_name() + "','"+info.getTrain_name()+"');";
			sqlStr[rowNumber] = inertStr;
			rowNumber++;
		}  
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHAPPEND);//批量追加
		msg.setDataBean("TrainDistrictRelation");
		String sql = JsonUtil.array2json(sqlStr);
		msg.setSql(sql);
		String paprams = "null";
		msg.setParams(paprams);
		
		String result = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(result == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	private boolean updateDistrictInfo(String districtID, List<TrainDistrictRelation> list){
		int length = list.size();
		if(length <= 0)
			return false;
		
		String[]sqlStr = new String[length];
		int rowNumber = 0;

		for (int i = 0; i < list.size(); i++) {//获取每行的内容  
			TrainDistrictRelation info = list.get(i);
			String inertStr = "insert into TrainDistrictRelation(District_name,Train_name) Values('"+ info.getDistrict_name() + "','"+info.getTrain_name()+"');";
			sqlStr[rowNumber] = inertStr;
			rowNumber++;
		}  
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHUPDATE);//批量更新
		msg.setDataBean("TrainDistrictRelation");
		
		//组装插入sql
		String sql = JsonUtil.array2json(sqlStr);
		msg.setSql(sql);
		String paprams = "null";
		msg.setParams(paprams);
		
		//组装删除sql
		String delteSql = "delete from TrainDistrictRelation where District_name=?";
		msg.setSql_1(delteSql);
		Object[] params = new Object[]{districtID};
		paprams = JsonUtil.array2json(params);
		msg.setParams_1(paprams);
		
		String result = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(result == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	private List getDistrictTrainInfo(String districtID) {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		
		msg.setDataBean("TrainDistrictRelation");

		msg.setSql("select * from TrainDistrictRelation where District_name=?");
		Object[] params = new Object[]{districtID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		
		if(listString == null)
			return null;		
		else
			return JsonUtil.getList4Json(listString,TrainDistrictRelation.class);
		}

}
