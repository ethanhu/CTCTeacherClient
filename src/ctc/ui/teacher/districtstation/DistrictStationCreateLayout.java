package ctc.ui.teacher.districtstation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class DistrictStationCreateLayout{

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
	
	DistrictStationData districtStationData = new DistrictStationData();
	
	org.eclipse.swt.widgets.List swtSelectedList;
	static Map<String,StationDistrictRelation> districtMap;//用于保存 实际计划
	int selectedListIndex = -1;
	
	static Map<String,String> preStationMap = new HashMap<String,String>();//记录前站
	static String startStationName = "";//记录始发站
	static int startStationCount = 0;//记录使用始发站的次数

	static String selectedDistrictName = "";//当前区段名
		
	public DistrictStationCreateLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout( new MigLayout());  
		createGroup(comp, "定义区段与车站关系","pos 0.5al 0.2al",null);
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

		final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("区段名称:");
		final Combo districtName = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		districtName.removeAll(); //先清空stationLayout
		districtName.addFocusListener(new FocusListener(){//当鼠标放置在该组件上时，就执行
			public void focusGained(FocusEvent e) {
				if (districtStationData.districtFlag){
					districtStationData.reloadFlag = true;
				}
				
				if (! districtStationData.reloadFlag)
					return;
				
				getDistrictInfo();
				
				if(districtStationData.districtName != null){
					districtName.setItems(districtStationData.districtName);
					districtStationData.reloadFlag = false;
					districtStationData.districtFlag = false;
				}
				
				preStationMap = new HashMap<String,String>();
				startStationCount = 0;
				districtMap = null;
				startStationName = "";
				swtSelectedList.removeAll();
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
				
				districtStationData.firstStationFlag = true;//区段改变，车站信息也会改变
				districtStationData.secondStationFlag = true;//区段改变，车站信息也会改变
				
				//保存已经选取的本站的名称和具体计划
				districtMap = new HashMap<String,StationDistrictRelation>();
				//保存已经选取的前站的名称
				preStationMap = new HashMap<String,String>();
				startStationCount = 0;
				startStationName = "";
				startStationName = "";
				swtSelectedList.removeAll();
				
				//首先从库中读取已定义的信息
				List <StationDistrictRelation> list = getDistrictStationInfo(selectedDistrictName);
				if(list == null || list.size() ==0)
					return;
				
				
				for(int i = 0; i<list.size(); i++){
					StationDistrictRelation data = list.get(i);
					String secondStationName = data.getStation_name();
					String firstStationName = data.getPrestation_name();
					
					String selText = firstStationName + "->" + secondStationName;//表示前站->本站
					districtMap.put(secondStationName,data);
					preStationMap.put(firstStationName,firstStationName);
					
					//是首站
					if(secondStationName.equalsIgnoreCase(firstStationName)){
						startStationName = firstStationName;
						startStationCount++;
					}
					
					//在控件swtSelectedList中显示  并排序
					selText ="距离:" + data.getPredistance() +" 区间 "+selText;
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
	    districtName.setLayoutData(data);
	    
	    final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("前站站名:");
		final Combo firstStationName = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		firstStationName.removeAll(); //先清空stationLayout
		firstStationName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				if (districtStationData.firstStationFlag){
					districtStationData.reloadFlag = true;
				}else
					return;
				
				if (! districtStationData.reloadFlag)
					return;
				
				getStationInfo();
				
				if(districtStationData.stationName != null){
					firstStationName.setItems(districtStationData.stationName);
					districtStationData.reloadFlag = false;
					
					districtStationData.firstStationFlag = false;
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
				
				if (districtStationData.secondStationFlag){
					districtStationData.reloadFlag = true;
				}else
					return;
				
				if (! districtStationData.reloadFlag)
					return;
				
				getStationInfo();
				
				if(districtStationData.stationName != null){
					secondStationName.setItems(districtStationData.stationName);
					districtStationData.reloadFlag = false;
					
					districtStationData.secondStationFlag = false;
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
	    
		final Label label_4 = new Label(comp, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_4.setText("本站距前站距离:");
		final Text distanceText = new Text(comp, SWT.BORDER | SWT.LEFT);
		distanceText.setText("200");
		distanceText.setTextLimit(3);
		distanceText.addVerifyListener(new VerifyListener() {   
			     public void verifyText(VerifyEvent e) {   
			       Pattern pattern = Pattern.compile("[0-9]\\d*");   
			       Matcher matcher = pattern.matcher(e.text);
			       if (matcher.matches())   
			        e.doit = true;   
			       else if (e.text.length() > 0)   
			        e.doit = false;   
			       else  
			        e.doit = true;   
			     }   
			     }); 
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    distanceText.setLayoutData(data);
		
	    
	     new Label(comp, SWT.NONE);
  	    Action actionSelect = new Action("确定") {//清除所显示内容，点击保存后更新库  
			 public void run() {
				String preStationName = firstStationName.getText();
				String stationName = secondStationName.getText();
		        String distance = distanceText.getText();
		        int distanceValue = 0;
					
				//保证输入所有值
				if( (preStationName.length() == 0)||
					(stationName.length() == 0) )
				{
					showMsg("请对所有选项赋值!");
					return;
				}
			
				if( preStationName.equalsIgnoreCase(stationName))//如果前站和本站相同，判断是否已经定义过始发站
				{
					if( startStationName.length() == 0)//起始站
						startStationName = preStationName;
					else{
						if (! preStationName.equalsIgnoreCase(startStationName))//判断是不是始发站
						{
							showMsg("始发站已经定义过!");
							return;	
						}
						
						System.out.println("startStationCount::"+startStationCount);
						if(startStationCount >= 2){
							showMsg("始发站"+ preStationName + "已使用2次!");
							startStationCount--;
							return;
						}
						//判断本站是否已经使用过
						if(districtMap.containsKey(preStationName)){
							showMsg(preStationName + "作为本站，已定义!");
							return;	
						}
					}
					
					//完全符合要求
					startStationCount++;
					distance = "0";
					preStationMap.put(preStationName,preStationName);

				}//如果前站和本站相同，就要判断他是否是始发站
				else
				{//前站和本站不同
				
					//如果前站是始发站
					if(preStationName.equalsIgnoreCase(startStationName)){
						
						if(startStationCount >= 2){
							showMsg("始发站"+ preStationName + "已使用2次!");
							return;
						}
						
						//如果本站已经含有次站,也要退出
						if(districtMap.containsKey(stationName)){
							showMsg(stationName + "作为本站，已定义!");
							return;	
						}
						
						//preStationName是首站，使用次数<=2,  stationName也没有使用过
						startStationCount++;
						preStationMap.put(preStationName,preStationName);
						
				   }//前站是始发站
				   else{//前站不是始发站
						if(preStationMap.containsKey(preStationName)){
							showMsg(preStationName + "作为前站，已使用过1次!");
							return;	
						}
						if(districtMap.containsKey(stationName)){
							showMsg(stationName + "作为本站，已定义!");
							return;	
						}
						
						preStationMap.put(preStationName,preStationName);
					}
					
				}//前站和本站不同
				
				distanceValue = Integer.parseInt(distance);
				
				//保存到服务器用
				//(String prestation_name, String station_name,String district_name, String predistance)
				StationDistrictRelation plan = new StationDistrictRelation(preStationName,stationName,selectedDistrictName,distanceValue);
				districtMap.put(stationName,plan);
				
				String selText = preStationName + "->" + stationName;//表示前站->本站
				
				//在控件swtSelectedList中显示  并排序
				selText ="距离:" + distanceValue +" 区间 " + selText;
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
					districtMap.remove(item1);
					
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
	    data.widthHint = 150;
	    swtSelectedList.setLayoutData(data);
	   
	    new Label(comp, SWT.NONE);
	    Action actionSave = new Action("更新") {  
			 public void run() {
				 
				 if(updateDistrictInfo(selectedDistrictName,districtMap)){
					 
					 pushCommand();
					 
					 showMsg("操作成功（更新）！");
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

		 ToolBar toolBar_1 = new ToolBar(comp, SWT.FLAT|SWT.RIGHT);//|SWT.BORDER
		 ToolBarManager manager_1 = new ToolBarManager(toolBar_1);
		 manager_1.add(actionSave);
		// manager_1.add(actionReset);
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
			districtStationData.setDistrictList(list);
		}
	}
	
	private List getDistrictStationInfo(String districtID) {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		
		msg.setDataBean("StationDistrictRelation");

		msg.setSql("select * from StationDistrictRelation where District_name=?");
		Object[] params = new Object[]{districtID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		
		if(listString == null)
			return null;		
		else
			return JsonUtil.getList4Json(listString,StationDistrictRelation.class);
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
			districtStationData.setStationList(list);
		}
	}
	
	private boolean updateDistrictInfo(String selectedDistrictName, Map<String, StationDistrictRelation>districtMap){
		
		int length = districtMap.size();
		if(length == 0)
			return false;

		Collection< StationDistrictRelation> values = districtMap.values(); 
		Iterator< StationDistrictRelation> iter = values.iterator();
		String[]sqlStr = new String[length];
		int rowNumber = 0;
		

		//迭代输出
		while (iter.hasNext()) {                  
			StationDistrictRelation info = iter.next();//获取每行的内容
			
			String inertStr = "insert into StationDistrictRelation(Station_name,Prestation_name,District_name,Predistance) Values('"
				              + info.getStation_name() + "','"+info.getPrestation_name()+ "','"
				              + info.getDistrict_name() + "','"+info.getPredistance() + "');";
			sqlStr[rowNumber] = inertStr;
			rowNumber++;
		}  
		

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHUPDATE);//批量更新
		msg.setDataBean("StationDistrictRelation");
		
		//组装插入sql
		String sql = JsonUtil.array2json(sqlStr);
		msg.setSql(sql);
		String paprams = "null";
		msg.setParams(paprams);
		
		//组装删除sql
		String delteSql = "delete from StationDistrictRelation where District_name=?";
		msg.setSql_1(delteSql);
		Object[] params = new Object[]{selectedDistrictName};
		paprams = JsonUtil.array2json(params);
		msg.setParams_1(paprams);
		
		String result = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(result == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	private void pushCommand(){
		DistrictStationData.initial();
	}


}
