package ctc.ui.teacher.station;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.miginfocom.swt.MigLayout;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


import ctc.constant.*;
import ctc.pojobean.*;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.ui.teacher.district.DistrictData;
import ctc.ui.teacher.districtstation.DistrictStationData;
import ctc.ui.teacher.train.TrainData;
import ctc.util.JsonUtil;

//tableviewer是扩展Table

public class StationBrowserLayout{

	
	final Color red =  Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	final Color yellow =  Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	final Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
	
	public Table table;
	public TableViewer tableViewer;
    StationData stationData = StationData.getInstance();
	static int pageSize; //表格的每页所显示的记录条数
	static int row = 1;//当前要显示的页
	
    public StationBrowserLayout() {
		super();
	}
	public StationBrowserLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
		getStationInfo();
		pageSize = stationData.PAGE_SIZE;
	}
	
	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"车站信息浏览","pos 0.5al 0.2al",null);
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
		
	    GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        group.setLayout(layout);
        
        //TableViewer是通过Table来布局的
        //制作表格   MULTI可多选  H_SCROLL有水平 滚动条、V_SCROLL有垂直滚动条、BORDER有边框、FULL_SELECTION整行选择 
		table = new Table(group, SWT.SINGLE | SWT.FULL_SELECTION |SWT.BORDER | SWT.VIRTUAL);//注意此处的设置
        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);
        
        //指定Table单元格的宽度和高度
        table.addListener(SWT.MeasureItem, new Listener() {//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
       	     public void handleEvent(Event event) { 
       	         event.width = table.getGridLineWidth();    //设置宽度 
       	         event.height = (int) Math.floor(event.gc.getFontMetrics().getHeight() * 1.5); //设置高度为字体高度的1.5倍 
         }});
        
        
        //表格的视图
        tableViewer = new TableViewer(table);
        //标题和网格线可见
        tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		 //设置填充  
		 //GridData data = new GridData(SWT.LEFT,SWT.CENTER, true, false);//SWT.FILL, SWT.FILL, true, false  xbm
		 GridData data = new GridData(GridData.FILL_BOTH); 
		 data.widthHint = 350;
		 data.heightHint = 295;
		 data.grabExcessHorizontalSpace = true;
		 tableViewer.getTable().setLayoutData(data);  //表格的布局 

		 //创建表格列的标题
		 int width = 1;
		 for (int i = 0; i < stationData.getColumnCount(); i ++){
			 width = stationData.getColumnWidth(i);
			 TableColumn column = new TableColumn(table, SWT.NONE);
			 column.setWidth((int)(width * 8)); 
			 column.setText(stationData.getColumnHeads()[i]);//设置表头
			 column.setAlignment(SWT.LEFT);//对齐方式SWT.LEFT
			 if( i == 0)//站名
			 {
				 //列的选择事件  实现排序 
				 column.addSelectionListener(new SelectionAdapter() {   
					 boolean sortType = true; //sortType记录上一次的排序方式，默认为升序   
					 public void widgetSelected(SelectionEvent e) {   
						 sortType = !sortType;//取反。下一次排序方式要和这一次的相反      
						 tableViewer.setSorter(new StationSorter(sortType,stationData.columnHeads[0]));    
					 }   
				 });
			 }
		 }
	        
	     /*tableLayout.addColumnData(new ColumnWeightData(8, 8, false));//设置列宽为8像素
	      TableColumn column_one = new TableColumn(table, SWT.NONE);//SWT.LEFT
	      column_one.setText(stationData.COLUMN_HEADINGS[0]);//设置表头
	      column_one.setAlignment(SWT.LEFT);//对齐方式SWT.LEFT 
	      column.setWidth(10);//宽度 
	     */
		 
		//设置标题的提供者  
		tableViewer.setLabelProvider(new TableLabelProvider());
		
		//设置表格视图的内容提供者
		tableViewer.setContentProvider(new TableContentProvider());
		
		//设置列的属性.
		tableViewer.setColumnProperties(stationData.columnHeads);
		
		//定义每一列的别名
		tableViewer.setColumnProperties(new String[] {"name", "down", "up", "map" });
       
		//设置每一列的单元格编辑组件CellEditor  
        CellEditor[] celleditors = new CellEditor[5];
        //文本编辑框
        celleditors[0] = null;
        celleditors[1] = new TextCellEditor(table);
        celleditors[2] = new TextCellEditor(table);
       //CheckboxCellEditor(table) 复选框
        celleditors[3] = new ComboBoxCellEditor(table, StationData.MAPS, SWT.READ_ONLY);//下拉框
       
        Text text = (Text) celleditors[1].getControl();// 设置第down列只能输入数值
        text.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {
        		//输入控制键，输入中文，输入字符，输入数字 正整数验证   
    			Pattern pattern = Pattern.compile("[0-9]\\d*"); //正则表达式  
    			Matcher matcher = pattern.matcher(e.text);   
    			if (matcher.matches()) //处理数字   
    			{
    				/*if(Integer.parseInt(e.text) != 0)//确保输入的数字不是0
    					e.doit = true;
    				else
    					e.doit = false;
						*/
					e.doit = true;
    			}
    			else if (e.text.length() > 0) //字符: 包含中文、空格   
    				e.doit = false;
    			else//控制键  
    				e.doit = true;   
    		}   
        });
      
       Text text1 = (Text) celleditors[2].getControl();// 设置第up列只能输入数值
        text1.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {
        		String inStr = e.text;
        		if (inStr.length() > 0) {
        			e.doit = NumberUtils.isDigits(inStr);
        		}
        	}
        });
      
        table.addMouseMoveListener(new MouseMoveListener( ){
        	public void mouseMove(MouseEvent e)
        	{
        		if(StationData.reloadFlag){
        			getStationInfo();
        			openCurrentTable(row);
        			StationData.reloadFlag = false;
        			//System.out.println("entry");
        		}
        	}
        	});
        /*table.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				getStationInfo();
				openCurrentTable(row);	
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
	    });
        */
        tableViewer.setCellEditors(celleditors);
        
        //设置单元的更改器  
        tableViewer.setCellModifier(new TableCellModifier());
        
        tableViewer.addFilter(new TableViewerFilter());//过滤器
     
        //构造工具条
        Composite buttonComposite = new Composite(group,SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));//使工具条居中
    	 Action actionModify = new Action("更新") {  
			 public void run() {
				//取得用户所选择的第一行, 若没有选择则为null
				 IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();  
				 //获取选中的第一行数据 
				 Station station = (Station) selection.getFirstElement();
				 if (station != null ) {
					 if( updateStationInfo(station.getStation_downnumber(), station.getStation_upnumber(),
							               station.getStation_graph(),station.getStation_name())){
						 showMsg("成功更新!");
						 //表格的刷新方法,界面会重新读取数据并显示
						 
						 pushCommand();
						 
						 tableViewer.refresh();//false
					 }
					 else{
						 showMsg("更新失败!");
						// tableViewer.refresh();//false
					 }
				 }
				 else{
					 showMsg("请选取进行更新的行！");  
				 }
			 } 
		 };
		 
		
		 Action actionDelete = new Action("删除") {  
			 public void run() {
				 //取得用户所选择的第一行, 若没有选择则为null
				 IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();  
				 //获取选中的第一个数据 
				 Station station = (Station) selection.getFirstElement();
				 if (station != null ) {
		             //先预先移动到下一行
		             Table table = tableViewer.getTable();
		             
		             //int i = table.getSelectionIndex(); //取得当前所选行的序号，如没有则返回-1
		            // table.setSelection(i + 1); //当前选择行移下一行
		             //确认删除  
					 MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_INFORMATION);  
					 messageBox.setText("提示信息");  
					 messageBox.setMessage("确定要删除此记录吗?");  
					 // SWT.YES 是  // SWT.NO 否 // SWT.CANCEL 取消 // SWT.RETRY 重试// SWT.ABORT 放弃// SWT.IGNORE 忽略  
					 if (messageBox.open() == SWT.YES) {  
						 if(deleteStationInfo(station.getStation_name())) //从数据库中删除记录
						 {
							// showMsg("成功删除!");
							 ((List)tableViewer.getInput()).remove(station); //数据模型的List容器中删除
							 stationData.remove(station.getStation_name());
							 openCurrentTable(row);
							 tableViewer.remove(station);//从表格界面上删除
							 
							 pushCommand();
						 }
						 else
							 showMsg("删除失败!");
					 }
				 }
				 else{
					 showMsg("请选取要删除的纪录！"); 
				 }
			 }
			};
			 
			 
		 Action actionClear = new Action("清空") {//清除所显示内容，点击保存后更新库  
			 public void run() {  
				 if(clearStationInfo()){
					 showMsg("清空操作成功!");
					 stationData.removeAll();
					 openCurrentTable(row);
					 
					 pushCommand();
				 }
				 else
					 showMsg("清空操作失败!");
			 }  
		 };  
		 Action actionHelp = new Action("帮助") {  
			 public void run() {
				 String str =  "更新：\n\r" +
				 			   "先对某行内容进行修改,然后点击更新进行保存\n\r" +
			                   "清空：\n\r" +
			                   "从库中物理删除所有记录";
				 showMsg(str);
			 }  
		 };  
		 
		 Action nextPage = new Action("下一页") {  
			 public void run() {
				 row++;
				 if (row > stationData.getTotalPageNum()){
					 row--;
					 return;
				 }
				 openCurrentTable(row);

			 }  
		 };  
		 Action prevPage = new Action("上一页") {  
			 public void run() {
				 row--;			
				 if (row < 1){
					 row++;
					 return;
				 }
				 openCurrentTable(row);
			 }  
		 };
		 	
		 
		 Action refresh = new Action("刷新") {  
			 public void run() {
				 getStationInfo();
				 openCurrentTable(row);
			 }  
		 };
		 
		 //工具条  
		 ToolBar toolBar = new ToolBar(buttonComposite, SWT.FLAT|SWT.RIGHT);//|SWT.BORDER
		 
		 //工具条管理器  
		 ToolBarManager manager = new ToolBarManager(toolBar);
		 
		// manager.add(refresh);
		 //manager.add(new Separator());
		 
		 manager.add(nextPage);
		 manager.add(prevPage);
		 
		 manager.add(new Separator());
		 
		 manager.add(actionModify);
		 manager.add(actionDelete);
		 manager.add(actionClear);
		 manager.add(new Separator());
		 manager.add(actionHelp); 
		 manager.update(true);
		 
		 //选中某行时，改变行的颜色
		 table.addListener(SWT.EraseItem, new Listener() {
			  	   public void handleEvent(Event event) {
			  	      event.detail &= ~SWT.HOT;
			  	      if ((event.detail & SWT.SELECTED) == 0) 
			  	    	  return; 
			  	      int clientWidth = table.getClientArea().width;
			  	      GC gc = event.gc;
			  	      Color oldForeground = gc.getForeground();
			  	      Color oldBackground = gc.getBackground();
			  	      gc.setForeground(red);
			  	      //gc.setBackground(yellow);
			  	      gc.fillGradientRectangle(0, event.y, clientWidth, event.height, false);
			  	      gc.setForeground(oldForeground);
			  	      gc.setBackground(oldBackground);
			  	      event.detail &= ~SWT.SELECTED;
			  	   }
			  	});
		 
		//在tableviewer内部为数据记录和tableItem之间的映射创建一个hash表，这样可以加快tableItem的和记录间的查找速度
		 //必须保证存在要显示的数据，否则程序出错。所以这里不能用下面的语句
		// tableViewer.setUseHashlookup(true);//必须在setInput之前加入才有效
		 //从服务器获取数据  
		  getStationInfo();  
		 //通过setInput为table添加了一个list后，只要对这个list里的元素进行添加和删除，
		 //table中的数据就会自动添加和删除，当然每次操作后需要调用refresh方法对tableviewer进行刷新。
		  //打开界面所显示的内容
       	 tableViewer.setInput(stationData.getData());//自动输入数据   即将数据显示在表格中
       	 tableViewer.setItemCount(stationData.PAGE_SIZE);  //设置显示的Item数
      }////createComp
	
	
	/**
	* 通过将检索结果结合中的记录分页加载的方法，实现控制检索结果记录的分页显示与快速加载
	* @param facttable  显示检索结果的GUI表格对象
	* @param pagenum   显示在表格对象中的检索结果页码，从1开始计算
	*/
	private void openCurrentTable(final int pagenum) {
		
		table.clearAll(); //清空表格所有项目中的数据
		//数据记录在检索结果结合中的下标开始值
		final int recordstart = (pagenum - 1) * pageSize;
		// 当前表格能显示的最后一条记录在检索结果集合中的索引号
		int end = recordstart + pageSize;
		// 若表格可显示的记录大于检索结果记录的总数，就只显示到最后一条检索结果记录
		final int recordend = Math.min(end, stationData.getRowCount());// 显示的最后一条记录在检索结果集合中的索引号

		// 当前页面显示的检索结果记录数
		final int currentdispnum = recordend - recordstart;
		//根据当前的页码，从检索结果集合中加载相应的数据到表的item域中  
		tableViewer.setInput(stationData.getRow(recordstart, recordstart+currentdispnum));//自动输入数据
		tableViewer.refresh();//刷新表格false 
	}
	
	//从服务器获取信息.
	private void getStationInfo() {
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Station");
		
		//不带参数的sql语句的使用方法
		msg.setSql("select * from Station order by Station_name ");// limit 16
		String paprams = "null";//
		msg.setParams(paprams);//转换为json字符串进行传递
		
		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return;		
		}else{
			List<Station> list = JsonUtil.getList4Json(listString,Station.class);
			if(list.size() <= 0)
				return;
			stationData.setData(list);
		}
	}
	private boolean clearStationInfo(){//清空所有记录
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHDELETE);
		//从一个表中进行删除操作
		/*
		msg.setCommandType(Constants.TYPE_CLIENT_SQLDELETE);
		msg.setDataBean("District");
		msg.setSql("delete from District");
		*/
		String[]sqlArray = new String[6];
		sqlArray[0] = "delete from Station"; 
		sqlArray[1] = "delete from District";
		sqlArray[2] = "delete from Train";
		sqlArray[3] = "delete from StationDistrictRelation";
		sqlArray[4] = "delete from TrainDistrictRelation";
		sqlArray[5] = "delete from Plan";
		String sqlStr = JsonUtil.array2json(sqlArray);
		msg.setSql(sqlStr);
		
		String paprams = "null";
		msg.setParams(paprams);
		
		
		String list = synClientSupport.sqlMessageSend(msg);
		if(list == null)
			return false;		
		else
			return true;	 
	}
	
	//参数为车站ID
	//所有相关的记录都将删除. ??????????????
	public boolean deleteStationInfo(String stationName)
	{
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		
		//从列车区段表District中获取车站ID等于stationID的所有区段的ID
		List<String> districtIDList = new ArrayList<String>();
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("District");
		String sqlStr = "select * from District where District_startstationname='"+ stationName + "' or District_endstationname='" + stationName + "'";
		msg.setSql(sqlStr);
		String paprams = "null";
		msg.setParams(paprams);
		String listString = synClientSupport.sqlMessageSend(msg);
		if(listString == null){//此情况不会出现
			districtIDList = null;		
		}else{
			List<District> list = JsonUtil.getList4Json(listString,District.class);
			if(list.size() <= 0)
				districtIDList = null;
			else{
				for(int i = 0; i < list.size(); i++){
					District data = (District)list.get(i);
					districtIDList.add(data.getDistrict_name());
				}
			}
		}
	
		//从列车表Train中获取车站ID等于stationID的所有车次的ID
		List<String> trainIDList = new ArrayList<String>();
		msg.setDataBean("Train");
		sqlStr = "select * from Train where Train_startstationname='"+ stationName + "' or Train_endstationname='" +  stationName + "'";
		msg.setSql(sqlStr);
		paprams = "null";
		msg.setParams(paprams);
		listString = synClientSupport.sqlMessageSend(msg);
		if(listString == null){//此情况不会出现
			trainIDList = null;		
		}else{
			List<Train> list = JsonUtil.getList4Json(listString,Train.class);
			if(list.size() <= 0)
				trainIDList = null;
			else{
				for(int i = 0; i < list.size(); i++){
					Train data = (Train)list.get(i);
					trainIDList.add(data.getTrain_name());
				}
			}
		}

		//批量删除
		//涉及到的表:Station, District Train StationDistrictRelation TrainDistrictRelation
		int districtNumber = 0;
		int trainNumber = 0;
		if(districtIDList != null)
			districtNumber = districtIDList.size();
		if(trainIDList != null)
			trainNumber = trainIDList.size();
		
		String[]sqlArray = new String[5 + districtNumber*2 + trainNumber];
		
		int index = 0;
		
		sqlArray[index++] = "delete from Station where Station_name='" + stationName + "'"; //number 1
		sqlArray[index++] = "delete from StationDistrictRelation where Station_name='"+ stationName + "'";//number 1
		sqlArray[index++] = "delete from Plan where Station_name='"+ stationName +"' or Prestation_name='" + stationName + "'";//number 1
		
		if(districtIDList != null){
			sqlArray[index++] = "delete from District where District_startstationname='"+ stationName +"' or District_endstationname='" + stationName + "'";//number 1
			for(int i = 0; i < districtNumber; i++){//number districtNumber*2
			 sqlArray[index++] = "delete from StationDistrictRelation where District_name='"+ districtIDList.get(i)+ "'";
			 sqlArray[index++] = "delete from TrainDistrictRelation where District_name='"+ districtIDList.get(i) + "'";
			}
		}
		if(trainIDList != null){
			sqlArray[index++] = "delete from Train where Train_startstationname='"+ stationName + "' or Train_endstationname='" +  stationName + "'";//number 1
			for(int i = 0; i < trainNumber; i++){//number trainNumber 
				 sqlArray[index++] = "delete from TrainDistrictRelation where Train_name='"+ trainIDList.get(i) + "'";
			}
		}
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHDELETE);
		sqlStr = JsonUtil.array2json(sqlArray);
		msg.setSql(sqlStr);
		paprams = "null";
		msg.setParams(paprams);
		
		listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	//向服务器发送更新报文
	public boolean updateStationInfo(int downAvailLaneNum,int upAvailLaneNum,String map, String stationName)
	{
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLUPDATE);
		msg.setDataBean("Station");
		msg.setSql("update Station set Station_downnumber=? ," +
		   		  "Station_upnumber=?,Station_graph=? where Station_name =?");
		if(downAvailLaneNum == 0)
			downAvailLaneNum = 1;
		if(upAvailLaneNum == 0)
			upAvailLaneNum = 1;
		Object[] params = new Object[]{downAvailLaneNum,upAvailLaneNum,map,stationName};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);
		
		String list = synClientSupport.sqlMessageSend(msg);//同步通信
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
	
	//通过替换过滤器来实现各种各样的过滤
	public class TableViewerFilter extends ViewerFilter {
	    public boolean select(Viewer viewer, Object parentElement, Object element) {
	       /* Station station = (Station) element;
	        return p.getName().startsWith("张1");
	        */
	        return true;
	    }
	}
	
	//对指定列进行排序
	class StationSorter extends ViewerSorter {
		 boolean direction = false;//记录上一次的排序方式
		 private int propertyIndex;
		 public StationSorter(boolean direction, String sortByProperty) {
			 this.direction = direction;
			 for (int i = 0; i < (stationData.columnHeads).length; i++) {  
				 if (stationData.columnHeads[i].equals(sortByProperty)) {  
					 this.propertyIndex = i;  
					 return;  
				 }  
			 }  
		 }  
		 public int compare(Viewer viewer, Object e1, Object e2) {
			 Station info1 = (Station) e1;  
			 Station info2 = (Station) e2;
			 switch (propertyIndex) {  
			 	case 0:
			 		//return info1.name.compareTo(info2.name);
			 		return direction ? info1.getStation_name().compareTo(info2.getStation_name()):
			 			               info2.getStation_name().compareTo(info1.getStation_name());
			 		/*case 3:  对于checkcell
				 if (bug1.isSolved == bug2.isSolved)  
					 return 0;  
				 if (bug1.isSolved)  
					 return 1;  
				 else  
					 return -1;*/  
			 	default:  
			 		return 0;
			 }
		 }  
	 }
	
	
	//对单条记录进行处理
	/**
     * 标签器方法返回的是各列的记录的文字
     * 参数1:输入的对象
     * 参数2:列号
     * 返回值:注意一定要避免Null值,否则出错
     */
	public class TableLabelProvider extends LabelProvider implements ITableLabelProvider,ITableFontProvider,ITableColorProvider {
		
		//本方法用于显示图片
		public Image getColumnImage(Object element, int columnIndex) {
			/*
			Station station = (Station) element;  
			// 只让“陈刚”这条记录显示图片  
			if (station.getName().equals("陈刚") || station.getName().equals("周阅")) {  
				if (columnIndex == 0)// 第一列要显示的图片  
					return images[0];  
				if (columnIndex == 2)// 根据性别显示不同的图标  
					return o.isSex() ? images[1] : images[2];  
			} */ 

			return null;
		}
		
		//用于显示文字
		//getColumnText方法参数是1个object对象和需要写入数据的列数，返回一个String对象。他的作用相当于把每个字段的值写入到表格中
		   /* 这里的element参数，其实就是我们二维数组中的每一个一维数组，其中保存的是 
		    * 每一行的所有数据，通过column这个参数依次写入到表格中的各个字段上。 
		    * column具体值是多少我们并不知道，因此需要我们来判断，并根据他里面的值 
		    * 来决定我们要将哪个数据返回。同样，该方法是不能返回NULL的。必须先检查数有效性。
		   */ 
		public String getColumnText(Object element, int columnIndex) {
			Station station = (Station) element;
            if(station == null)//解决表中空数据出现的错误
            	return null;
			switch (columnIndex) {
			case 0:
				return station.getStation_name();
			case 1:
				return String.valueOf(station.getStation_downnumber());
			case 2:
				return String.valueOf(station.getStation_upnumber());
			case 3:
				return station.getStation_graph();
			}
			return null;
		}
		
		
		//创建几个图像  
		/*private Image[] images = new Image[] { new Image(null, "icons/22.jpg"), new Image(null, "icons/33.jpg"), new Image(null, "icons/44.jpg") };  
		//当TableViewer对象被关闭时触发执行此方法  
		public void dispose() {  
		   //SWT组件的原则：自己创建，自释放  
		  for (Image image : images) {  
		       image.dispose();  
		} 
		*/
		//ITableFontProvider 对指定列设置字体
		FontRegistry registry = new FontRegistry();
		public Font getFont(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return registry.getBold(Display.getCurrent().getSystemFont().getFontData()[0].getName());
			}
			return null;
		}
		//ITableColorProvide
		
        //指定列变颜色  
		public Color getBackground(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return white;
			}
			return null;
		}
		public Color getForeground(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return red;
			}
			return null;
		}
		private Color[] bg = new Color[]{red,white};
		private Color[] force = new Color[]{red,red};
		//实现隔列换色
		/*
		public Color getForeground(Object element, int columnIndex) {
	        return force[columnIndex%2];
	    }
	    public Color getBackground(Object element, int columnIndex) {
	        return bg[columnIndex%2];
	    }*/
		//实现隔行换色
/*	    private Object current = null;
	    private int currentColor = 0;
	    public Color getForeground(Object element, int columnIndex) {
	        return force[currentColor];
	    }
	    public Color getBackground(Object element, int columnIndex) {
	        if (current != element) {
	            currentColor = 1 - currentColor;
	            current = element;
	        }
	        return bg[currentColor];
	    }
	*/	
		
	}

	//通过接口IStructuredContentProvider和接口ITableLabelProvider将数据写入到表格中///
	//如果让tableviewer显示数据，必须给它提供内容器和标签器，内容器的作用是从List(也可以是其他的集合类）中提取出一个对象
	//(例如People对应着表格的一行，数据库的一条记录),标签器的作用是从一个对象中提取出一个字段
	
	//设置表格视图的内容器    对所有记录集中的记录进行处理
	public class TableContentProvider implements IStructuredContentProvider {
        //接收到1个Object对象，这个Object对象可以是容器类对象或者数组，然后返回一个一维Object数组。
		//他的作用就是通过传递进来的参数来自动设置表格中的数据需要占多少行		
		
		//这里的参数就是使用setInput()传进的参数
		public Object[] getElements(Object parent) {
			List results = new ArrayList();
			
			if (parent instanceof ArrayList) {
				results = (ArrayList) parent;
			}
			return results.toArray(); //将List转化为数组
		}
       //当TableViewer对象被关闭时触发执行此方法
		public void dispose() {
			//	System.out.println("Disposing ...");  
		}
		//当TableViewer再次调用setInput()时触发此方法执行 
		public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {
			//System.out.println("Input changed: old=" + oldInput + ", new=" + newInput); 
	
		}
	}
	
	//设置单元的更改器
    class TableCellModifier implements ICellModifier {
    	  /* 是否可以修改此单元格。这里设置了任一单元格都可以修改。  
    	   * @param element 表格记录对象
            * @param property 列的别名
            * 用来判断哪一个属性可写
         */ 
          public boolean canModify(Object element,String property) {
        	  //根据每一行的对象动态显示ComboBoxCellEditor列表中的数据
        	     //设置同一列的不同行中的下拉列表的内容不同
              //BusinessField 是一个实体类
/*              BusinessField o = (BusinessField) element;
              String[] items=o.getFieldAttributes();
               tv.getCellEditors()[1]=new ComboBoxCellEditor(tv.getTable(), items, SWT.READ_ONLY);
               */
                return true;
          }
          /**
           * 当单击单元格出现CellEditor时应该显示什么值。由此方法决定，
           * 返回单元格的当前值
           * 返回某个属性的值
           */ 
          public Object getValue(Object element,String property) {
                Object result = null;
                Station station = (Station) element;
                if(station == null)
                	return null;
                
                //使用别名的方法
                if(property.equals("name"))	
                	return station.getStation_name();
                else
                if(property.equals("down"))	
                	return String.valueOf(station.getStation_downnumber());
                else
                if(property.equals("up"))	
                	return String.valueOf(station.getStation_upnumber());
                else
                if (property.equals("map")) //ComboBoxCellEditor要求返回下拉框中的索引值
                	return new Integer(getNameIndex(station.getStation_graph()));
              
               //直接使用索引
              /*  List list = Arrays.asList(stationData.columnHeads);
                int columnIndex = list.indexOf(property);
                //再返回对应的数据
                switch (columnIndex) {
                case 0://id
    				return String.valueOf(station.getStation_id());
    			case 1://name
    				return station.getStation_name();
    			case 2://down
    				return String.valueOf(station.getStation_downnumber());
    			case 3://up
    				return String.valueOf(station.getStation_upnumber());
    			case 4://map ，因为用的是下拉框，所以要返回一个Integer表示当前选中的index
    				return new Integer(getNameIndex(station.getStation_graph()));
                }
                */
                return result;
          }
          
          private int getNameIndex(String name) {
        	  for (int i = 0; i < StationData.MAPS.length; i++) {
        		  if (StationData.MAPS[i].equals(name))
        			  return i;
        	  }
        	  return -1;
          }
          //当用户对单元格内容进行修改 时，此方法执行. 为某个属性赋值
          public void modify(Object element, String property, Object value) {
        	  TableItem tableItem = (TableItem) element;
        	  if(tableItem == null){
        		  //tableViewer.update(null, null);
        		  return;
        	  }
        	  Station station = (Station) tableItem.getData();
        	  if(station == null)
        	  {
        		 //tableViewer.update(null, null);
              	  return;
        	  }
        	  
              if(property.equals("name")){
            	  String name = (String) value;
        		  station.setStation_name(name);
              }
              else
              if(property.equals("down")){
            	  String down = (String) value;
        		  if (down.length() > 0) {
        			  station.setStation_downnumber(Integer.parseInt(down));
        		  }
              }
              else
              if(property.equals("up")){	
            	  String up = (String) value;
        		  if (up.length() > 0) {
        			  station.setStation_upnumber(Integer.parseInt(up));
        		  }
              }
              else
              if (property.equals("map")){
            	  Integer comboIndex = (Integer) value;
                  if(comboIndex.intValue() != -1){
                	  String mapName = StationData.MAPS[comboIndex.intValue()];
                	  station.setStation_graph(mapName);
       		      }
              }
        	  
        	 // System.out.println("Modiy:" + value +"::"+columnIndex);
        	  /*List list = Arrays.asList(stationData.columnHeads);
        	  int columnIndex = list.indexOf(property);
        	  switch (columnIndex) {
        	  case 0:
        		  String id = (String) value;
        		  if (id.length() > 0) {
        			  station.setStation_id(Integer.parseInt(id));
        		  }
        		  break;
        	  case 1:
        		  String name = (String) value;
        		  if (name.length() > 0) {
        			  station.setStation_name(name);
        		  }
        		  break;

        	  case 2:
        		  String down = (String) value;
        		  if (down.length() > 0) {
        			  station.setStation_downnumber(Integer.parseInt(down));
        		  }
        		  break;
        	  case 3:
        		  String up = (String) value;
        		  if (up.length() > 0) {
        			  station.setStation_upnumber(Integer.parseInt(up));
        		  }
        		  break;
         	case 4:
         	   Integer comboIndex = (Integer) value;
               if(comboIndex.intValue() != -1){
            	  String mapName = StationData.MAPS[comboIndex.intValue()];
    			  station.setStation_graph(mapName);
    		  }
    		  break;
    	  }
    	  */
        	  tableViewer.update(station, null);
        }
    }  
    
    
	private void pushCommand(){
		TrainData.initial();
		DistrictData.initial();
		StationData.reloadFlag = true;
		DistrictStationData.initial();
		//DistrictStationData.reloadFlag = true;
	}

}
