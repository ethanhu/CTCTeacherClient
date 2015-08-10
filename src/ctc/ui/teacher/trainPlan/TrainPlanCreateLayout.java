package ctc.ui.teacher.trainPlan;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import org.eclipse.jface.action.*;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
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

public class TrainPlanCreateLayout{

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
	
	TrainPlanData dataInstance = new TrainPlanData();
	
	org.eclipse.swt.widgets.List swtSelectedList;
	static Map<String,Plan> planMap;//用于保存 实际计划
	int selectedListIndex = -1;
	
	static Map<String,String> preStationMap = new HashMap<String,String>();//记录前站
	
	static String endStationName = "";//记录终点站 
	static String startStationName = "";//记录始发站 
	static int startStationCount = 0;//记录使用始发站的次数
	
	static String lastStationName = "";//记录用户上次所选车站名称
	
	static String selectedDistrictName = "";//当前区段名
	static String selectedTrainName = "";//当前车次名
	
	public TrainPlanCreateLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout( new MigLayout());  
		createGroup(comp, "定义列车原始运行计划","pos 0.5al 0.2al",null);
		return comp;
	}
	
	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}

	private void createComp(Composite comp) {
		
		getDistrictInfo();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		comp.setLayout(gridLayout);
        
		GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);

		final Label label_6 = new Label(comp, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_6.setText("区段名称:");
		final Combo districtName = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		districtName.removeAll(); //先清空stationLayout
		districtName.addFocusListener(new FocusListener(){//当鼠标放置在该组件上时，就执行
			public void focusGained(FocusEvent e) {
				if (dataInstance.districtFlag){
					dataInstance.reloadFlag = true;
				}
				
				if (! dataInstance.reloadFlag)
					return;
				
				getDistrictInfo();
				
				if(dataInstance.districtName != null){
					districtName.setItems(dataInstance.districtName);
					dataInstance.reloadFlag = false;
					dataInstance.districtFlag = false;

				//	dataInstance.trainFlag = true;//区段改变，车次也会改变
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
		    });
		districtName.addSelectionListener(new SelectionListener(){//当用鼠标选取下拉项，就执行
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e){
				
				selectedDistrictName = districtName.getText();
				dataInstance.trainFlag = true;//区段改变，车次也会改变
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
		trainName.add("");
		trainName.removeAll(); //先清空stationLayout
		trainName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
			
				if (dataInstance.trainFlag){
					dataInstance.reloadFlag = true;
				}
				if (! dataInstance.reloadFlag)
					return;

				getTrainInfo(selectedDistrictName);

				if(dataInstance.trainName != null){
					trainName.setItems(dataInstance.trainName);
					dataInstance.reloadFlag = false;
					dataInstance.trainFlag = false;
					
					//车次改变，前站和本站也会改变
					dataInstance.firstStationFlag = true;
					dataInstance.secondStationFlag = true;
				}
				
				preStationMap = new HashMap<String,String>();
				startStationCount = 0;
				planMap = null;
				lastStationName = "";
				endStationName = "";
				swtSelectedList.removeAll();
				
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
		});
		trainName.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e){
				
				//车次改变，前站和本站也会改变
				dataInstance.firstStationFlag = true;
				dataInstance.secondStationFlag = true;
				
				selectedTrainName = trainName.getText();
				
				if(selectedTrainName.length() == 0)
					return;
				
				//保存已经选取的本站的名称和具体计划
				planMap = new HashMap<String,Plan>();
				//保存已经选取的前站的名称
				preStationMap = new HashMap<String,String>();
				startStationCount = 0;
				startStationName = "";
				lastStationName = "";
				endStationName = "";
				swtSelectedList.removeAll();
				
				if ( (lastStationName.length() == 0 ) ||
					 ((lastStationName.length() != 0 )&&(! lastStationName.equalsIgnoreCase(selectedTrainName))))
				{
					lastStationName = selectedTrainName;
				}
				
				//获取该车次首站的名称
				TrainStartEndStation dataStation = (TrainStartEndStation)dataInstance.trainStartEndStatioIDMap.get(selectedTrainName);
				startStationName = dataStation.getTrain_startstationid();
				endStationName = dataStation.getTrain_endstationid();
				
				//首先从库中读取已定义的信息
				String trainID = selectedTrainName;
				List <Plan> list = getPlanInfo(trainID);
				if(list == null || list.size() ==0)
					return;
				
				//结果集按到站时间由小到大排序
				for(int i = 0; i<list.size(); i++){
					Plan data = list.get(i);
			
					String secondStationName = data.getStation_name();
					
					String firstStationName = data.getPrestation_name();
					
					String selText = firstStationName + "->" + secondStationName;//表示前站->本站
					planMap.put(secondStationName,data);
					
					//System.out.println("firstStationName" +firstStationName);
					
					preStationMap.put(firstStationName,firstStationName);
					
					//是首站
					if(startStationName.length() != 0 && startStationName.equalsIgnoreCase(firstStationName)){
						startStationCount++;
					}
					
					//在控件swtSelectedList中显示  并排序
					selText ="时间 " + data.getPlan_arrivestationtime() +"-"+ data.getPlan_leavestationtime() +" 区间 "+selText;
					swtSelectedList.add(selText);
					
					String[] items = swtSelectedList.getItems();
					java.util.Arrays.sort(items);
					swtSelectedList.setItems(items);
				}
			}
		});
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 126;
	    trainName.setLayoutData(data);

        final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("前站站名:");
		final Combo firstStationName = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		firstStationName.removeAll(); //先清空stationLayout
		firstStationName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (dataInstance.firstStationFlag){
					dataInstance.reloadFlag = true;
				}else
					return;
				
				if (! dataInstance.reloadFlag)
					return;
				
				getStationInfoByTrainID(selectedTrainName);
				
				if(dataInstance.stationName != null){
					firstStationName.setItems(dataInstance.stationName);
					dataInstance.reloadFlag = false;
					dataInstance.firstStationFlag = false;
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
		    });
		
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 126;
	    firstStationName.setLayoutData(data);
	    
	    final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("本站站名:");
		final Combo secondStationName = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		secondStationName.removeAll(); //先清空stationLayout
		secondStationName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (dataInstance.secondStationFlag){
					dataInstance.reloadFlag = true;
				}else
					return;
				
				
				if (! dataInstance.reloadFlag)
					return;
				
				getStationInfoByTrainID(selectedTrainName);
				
				if(dataInstance.stationName != null){
					secondStationName.setItems(dataInstance.stationName);
					dataInstance.reloadFlag = false;
					dataInstance.secondStationFlag = false;
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
		    });
		
		
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 126;
	    secondStationName.setLayoutData(data);
	    // final DateTime date = new DateTime (comp, SWT.DATE | SWT.SHORT);
	    final Label label_4 = new Label(comp, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_4.setText("到站时间:");
		final DateTime arriveTime = new DateTime (comp, SWT.TIME | SWT.SHORT);
		//使用时需要重画页面
		/*final CDateTime arriveTime = new CDateTime(comp,CDT.COMPACT|CDT.BORDER|CDT.DROP_DOWN);
		arriveTime.setLocale(java.util.Locale.CHINA);
		arriveTime.setPattern("HH:mm(yyyy.MM.dd)");
		arriveTime.setPattern("HH:mm");
		arriveTime.setSelection(new Date());
		arriveTime.setLocale(java.util.Locale.CHINA);
		*/
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 20;
	    data.widthHint = 155;
	    arriveTime.setLayoutData(data);
	    
	    final Label label_5 = new Label(comp, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_5.setText("离站时间:");
		final DateTime leaveTime = new DateTime (comp, SWT.TIME | SWT.SHORT);
		/*final CDateTime leaveTime = new CDateTime(comp, CDT.COMPACT|CDT.BORDER|CDT.DROP_DOWN);//TIME_MEDIUM|CDT.TIME_SHORT |CDT.CLOCK_24_HOUR  CDT.COMPACT|
		leaveTime.setFormat(CDT.TIME_SHORT);
		leaveTime.setLocale(java.util.Locale.CHINA);
		leaveTime.setPattern("HH:mm");//:ss
		leaveTime.setSelection(new Date());
		*/
		data = new GridData(SWT.LEFT,SWT.CENTER, false, false);
	    data.heightHint = 15;
	    data.widthHint = 155;
	    leaveTime.setLayoutData(data);
	    
	     new Label(comp, SWT.NONE);
  	    Action actionSelect = new Action("确定") {//清除所显示内容，点击保存后更新库  
			 public void run() {
				String preStationName = firstStationName.getText();
				String stationName = secondStationName.getText();
				//java.util.Date arriveDate = arriveTime.getSelection();
			//	java.util.Date leaveDate = leaveTime.getSelection();

				String leaveTimeStr = leaveTime.getHours () + ":" + leaveTime.getMinutes();
				String arriveTimeStr = arriveTime.getHours () + ":" + arriveTime.getMinutes();
				
				//保证输入所有值
				if( (preStationName.length() == 0)||
					(stationName.length() == 0)||
					//(leaveDate == null)||
					//(arriveDate == null)||
					(leaveTimeStr == null)||
					(arriveTimeStr == null) )
				{
					showMsg("请对所有选项赋值!");
					return;
				}
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");//"yyyy:MM:dd:HH:mm"
				//String arriveTimeStr = sdf.format(arriveDate);
				//String leaveTimeStr = sdf.format(leaveDate);
		        //(0:相等; <0:leaveTimeStr < arriveTimeStr; >0: leaveTimeStr > arriveTimeStr)
				if( preStationName.equalsIgnoreCase(stationName))//如果前站和本站相同，就要判断他是否是始发站
				{
					//判断是不是始发站
					if (! preStationName.equalsIgnoreCase(startStationName))
					{
						showMsg("与车次定义模块中所定义的始发站（" + startStationName + "）不一致!");
						return;	
					}
					//前站是始发站,判断时间是否相等
					if (leaveTimeStr.compareTo(arriveTimeStr) != 0 )
					{
						showMsg("始发站的离站时间与到站时间应该相等!");
						return;	
					}
					//是始发站 时间也相等     判断使用次数
					startStationCount++;
					if(startStationCount > 2){
						showMsg("始发站"+ preStationName + "已使用2次!");
						startStationCount--;
						return;
					}
					//判断本站是否已经使用过
					if(planMap.containsKey(preStationName)){
						showMsg(preStationName + "作为本站，已定义!");
						startStationCount--;
						return;	
					}
					//完全符合要求
					preStationMap.put(preStationName,preStationName);
				}//如果前站和本站相同，就要判断他是否是始发站
				else
				{//前站和本站不同
					if (leaveTimeStr.compareTo(arriveTimeStr) < 0 )
					{
						showMsg("保证:离站时间 >=到站时间!");
						return;	
					}
					//如果前站是始发站
					if(preStationName.equalsIgnoreCase(startStationName)){
						startStationCount++;
						if(startStationCount > 2){
							showMsg("始发站"+ preStationName + "已使用2次!");
							startStationCount--;
							return;
						}
						
						//如果本站已经含有次站,也要退出
						if(planMap.containsKey(stationName)){
							showMsg(stationName + "作为本站，已定义!");
							startStationCount--;
							return;	
						}
						//preStationName是首站，使用次数<=2,  stationName也没有使用过
						preStationMap.put(preStationName,preStationName);
						
				   }//前站是始发站
				   else{//前站不是始发站
						if(preStationMap.containsKey(preStationName)){
							showMsg(preStationName + "作为前站，已使用过1次!");
							return;	
						}
						if(planMap.containsKey(stationName)){
							showMsg(stationName + "作为本站，已定义!");
							return;	
						}
						
						preStationMap.put(preStationName,preStationName);
					}
					
				}//前站和本站不同
				
				//保存到服务器用
				Plan plan = new Plan(arriveTimeStr,leaveTimeStr,selectedDistrictName, preStationName,stationName,selectedTrainName);
				planMap.put(stationName,plan);
				
				String selText = preStationName + "->" + stationName;//表示前站->本站
				
				//在控件swtSelectedList中显示  并排序
				selText ="时间 " + arriveTimeStr +"-"+ leaveTimeStr +" 区间 " + selText;
				swtSelectedList.add(selText);
				String[] items = swtSelectedList.getItems();
				java.util.Arrays.sort(items);
				swtSelectedList.setItems(items);
			 }  
		 };
		 Action actionDelete = new Action("删除") {//清除所显示内容，点击保存后更新库  
			 public void run() {
				 if ( (swtSelectedList.getItemCount() != 0) && (selectedListIndex != -1))
				 {
					String []items = swtSelectedList.getSelection();
					String oldItem = items[0].toString();
					
					//本站处理
					String item1 = oldItem.substring(oldItem.indexOf(">")+1);
					planMap.remove(item1);
					
					//前站处理
					String item2 = oldItem.substring(oldItem.indexOf("区间 ") + 3,oldItem.lastIndexOf("-"));
					
					preStationMap.remove(item2);
					if(item2.equalsIgnoreCase(startStationName)){//始发站
						if(startStationCount > 0)
						   startStationCount--;
					}
					
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
		 
		final Label label_7 = new Label(comp, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_7.setText("已定义信息:");
		swtSelectedList = new org.eclipse.swt.widgets.List(comp, SWT.H_SCROLL | SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);//SWT.MULTI
		swtSelectedList.deselectAll();// 
		swtSelectedList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) e.getSource();
				selectedListIndex = list.getSelectionIndex();
			}
		});
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 150;
	    data.widthHint = 200;
	    swtSelectedList.setLayoutData(data);
	   
	    new Label(comp, SWT.NONE);
	    Action actionSave = new Action("保存") {  
			 public void run() {
				 //没有选取车次，无法保存
				 String trainText = trainName.getText();
				 if (trainText.length() == 0)
					 return;
				 
				 String trainID = trainText;
				 
				 if(swtSelectedList.getItemCount() != 0 ){
					 String item = swtSelectedList.getItem(swtSelectedList.getItemCount()-1);
					 item = item.substring(item.indexOf(">")+1);
					 TrainStartEndStation dataStation = (TrainStartEndStation)dataInstance.trainStartEndStatioIDMap.get(trainText);
					 String endStationID = item;
					 if (! (endStationID.equalsIgnoreCase(dataStation.getTrain_endstationid())))
					 {
						 MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
					     dialog.setText("提示信息");
					     dialog.setMessage("与车次定义模块中所定义的到达站("+ endStationName +")不一致!\n\r\n\r" +
					     					"现在保存吗?");
					     if (dialog.open() != SWT.OK)
					          return;
					 }
				 }else//已经全部删除所定义信息
				 {
					 if(clearPlanInfo(trainID))
					 {
						showMsg("操作成功(清空)！");
						
						pushCommand();
					 }
					 else
						showMsg("操作失败(清空)！");
					 
					 return;
				 }
				
				 if(updatePlanInfo(trainID,planMap))
				 {
					showMsg("操作成功（更新）！");
					
					pushCommand();
				 }
				 else
					showMsg("操作失败（更新）！");
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
				 			   "定义某车次所在区段内的所有车站信息\n\r\n\r" +
			                   "对于不停车站,保证到站时间与离站时间相等\n\r";
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
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
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
			dataInstance.setDistrictList(list);
		}
	}
	
	private void getTrainInfo(String districtName) {
		
    	SQLRequestMessage msg = new SQLRequestMessage();
    	msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
    	msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
    	
    	msg.setDataBean("Train");

    	msg.setSql("select * from Train WHERE Train.Train_name "
    			+ "in (SELECT Train_name FROM TrainDistrictRelation WHERE District_name = ?)");
   	
		Object[] param = new Object[]{districtName};
		String paramStr = JsonUtil.array2json(param);
		msg.setParams(paramStr);
    	
    	String listString = synClientSupport.sqlMessageSend(msg);
    	if(listString == null){//不可能出现
    		return;		
    	}else{
    		List<Train> list = JsonUtil.getList4Json(listString,Train.class);
    		dataInstance.setTrainList(list);
    	}
    }
	
	private void getStationInfo() {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Station");

		msg.setSql("select * from Station");
		String paprams = "null";//
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);
		if(listString == null){
			return;		
		}else{
			List<Station> list = JsonUtil.getList4Json(listString,Station.class);
			dataInstance.setStationList(true,list);
		}
	}
	
	//1.获取Train_id车次所对应的District_id(从TrainDistrictRelation) 
	//2.获取District_id对应的所有Station_id(从StationDistrictRelation中)
	//3.获取Station_id对应的Station_name(从Station中)
	private void getStationInfoByTrainID(String trainID) {
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		
		msg.setDataBean("Station");
		String sql = "SELECT * FROM Station WHERE Station.Station_name " 
			+ "in (SELECT Station_name FROM StationDistrictRelation " 
			+ "WHERE StationDistrictRelation.District_name " 
			+ "in (SELECT District_name FROM TrainDistrictRelation WHERE Train_name = ?))";
		msg.setSql(sql);
		
		Object[] params = new Object[]{trainID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);
  
		String listString = synClientSupport.sqlMessageSend(msg);
		//System.out.println("listString:"+listString);
		
		if(listString == null){
			return;		
		}else{
			List<Station> list = JsonUtil.getList4Json(listString,Station.class);
			dataInstance.setStationList(false,list);
		}
	}
	
	private boolean clearPlanInfo(String trainID){
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLDELETE);
		
		msg.setDataBean("Plan");
		msg.setSql("delete from Plan where Train_name=?");
		
		Object[] params = new Object[]{trainID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);
		
		String list = synClientSupport.sqlMessageSend(msg);//同步通信
		if(list == null){
			return false;		
		}else{
			return true;	 
		} 
	}
	
	private boolean updatePlanInfo(String trainID, Map<String, Plan>planMap){
		
		int length = planMap.size();
		if(length == 0)
			return false;

		Collection<Plan> values = planMap.values(); 
		Iterator<Plan> iter = values.iterator();
		String[]sqlStr = new String[length];
		int rowNumber = 0;
		
		//迭代输出
		while (iter.hasNext()) {                  
			Plan info = iter.next();//获取每行的内容
			
			String inertStr = "insert into Plan(District_name,Train_name,Prestation_name,Station_name,Plan_arrivestationtime,Plan_leavestationtime) Values('"
							  + info.getDistrict_name()+ "','"+ info.getTrain_name()+ "','"+info.getPrestation_name()+"','" 
							  + info.getStation_name()+"','" + info.getPlan_arrivestationtime()+ "','"+info.getPlan_leavestationtime()+ "')";
			
			sqlStr[rowNumber] = inertStr;
			rowNumber++;
		}  
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHUPDATE);//批量更新
		msg.setDataBean("Plan");
		
		//组装插入sql
		String sql = JsonUtil.array2json(sqlStr);
		msg.setSql(sql);
		String paprams = "null";
		msg.setParams(paprams);
		
		//组装删除sql
		String delteSql = "delete from Plan where Train_name=?";
		msg.setSql_1(delteSql);
		Object[] params = new Object[]{trainID};
		paprams = JsonUtil.array2json(params);
		msg.setParams_1(paprams);
		
		String result = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(result == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	private List<Plan> getPlanInfo(String trainID){

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);

		msg.setDataBean("Plan");
		msg.setSql("select * from Plan where Train_name=? order by Plan_arrivestationtime");//结果集按到站时间由小到大排序
		Object[] params = new Object[]{trainID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		
		if(listString == null){//此情况不会出现
			return null;		
		}else{
			return JsonUtil.getList4Json(listString,Plan.class);
		}
	}
	
	private void pushCommand(){
		TrainPlanData.initial();
	}

}
