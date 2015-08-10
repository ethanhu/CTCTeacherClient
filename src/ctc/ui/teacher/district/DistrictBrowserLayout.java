package ctc.ui.teacher.district;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.miginfocom.swt.MigLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import ctc.constant.*;
import ctc.pojobean.*;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.ui.teacher.districtstation.DistrictStationData;
import ctc.ui.teacher.districttrainrelation.DistrictTrainData;
import ctc.ui.teacher.experiment.ExperimentData;
import ctc.ui.teacher.trainPlan.TrainPlanData;
import ctc.util.JsonUtil;

public class DistrictBrowserLayout{

	final Color red =  Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	final Color yellow =  Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	final Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
	
	private Table table;
    private TableViewer tableViewer;
    static DistrictData districtData = DistrictData.getInstance();
	static int pageSize; //表格的每页所显示的记录条数
	static int row = 1;//当前要显示的页
	
    public DistrictBrowserLayout() {
		super();
	}
	public DistrictBrowserLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
		pageSize = districtData.PAGE_SIZE;
	}
	
	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"区段信息浏览","pos 0.5al 0.2al",null);
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
		
		getStationInfo();
		getDistrictInfo();
		
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
			for (int i = 0; i < districtData.getColumnCount(); i ++){
				width = districtData.getColumnWidth(i);
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setWidth((int)(width * 8)); 
				column.setText(districtData.getColumnHeads()[i]);//设置表头
				column.setAlignment(SWT.LEFT);//对齐方式SWT.LEFT
				if( i == 0)//区段开始站
				{
					//列的选择事件  实现排序 
					column.addSelectionListener(new SelectionAdapter() {   
			 			boolean sortType = true; //sortType记录上一次的排序方式，默认为升序   
			 			public void widgetSelected(SelectionEvent e) {   
			 				sortType = !sortType;//取反。下一次排序方式要和这一次的相反      
			 				tableViewer.setSorter(new DistrictSorter(sortType,districtData.columnHeads[0]));    
			 			}   
			 		});
				}
				if( i == 1)//区段结束站
				{
					//列的选择事件  实现排序 
					column.addSelectionListener(new SelectionAdapter() {   
			 			boolean sortType = true; //sortType记录上一次的排序方式，默认为升序   
			 			public void widgetSelected(SelectionEvent e) {   
			 				sortType = !sortType;//取反。下一次排序方式要和这一次的相反      
			 				tableViewer.setSorter(new DistrictSorter(sortType,districtData.columnHeads[1]));    
			 			}   
			 		});
				}
				
				
			}
	        
		table.addMouseMoveListener(new MouseMoveListener( ){
		       	public void mouseMove(MouseEvent e)
		       	{
		       		if(DistrictData.reloadFlag){
		       			getDistrictInfo();
		       			openCurrentTable(row);
		       			DistrictData.reloadFlag = false;
		       			//System.out.println("entry");
		       		}
		       	}
		 });	
			
		//设置标题的提供者  
		tableViewer.setLabelProvider(new TableLabelProvider());
		
		//设置表格视图的内容提供者
		tableViewer.setContentProvider(new TableContentProvider());
		
		//设置列的属性.
		tableViewer.setColumnProperties(districtData.columnHeads);
		
		//定义每一列的别名
		tableViewer.setColumnProperties(new String[] {"start","end","number","bureau" });
		
		//设置每一列的单元格编辑组件CellEditor  
        CellEditor[] celleditors = new CellEditor[4];
        //文本编辑框
        celleditors[0] = null;
        celleditors[1] = null;
        celleditors[2] = new TextCellEditor(table);
        celleditors[3] = new ComboBoxCellEditor(table,districtData.bureau,SWT.READ_ONLY);//区段所属铁路局 SWT.DROP_DOWN
        
        Text text = (Text) celleditors[2].getControl();// 设置第2列只能输入数值
        text.addVerifyListener(new VerifyListener() {
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
        });
      
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
				 District data = (District) selection.getFirstElement();
				 if (data != null ) {
					 
					  String stationStartID = data.getDistrict_startstationname();
			    	  String districtName = stationStartID;

			    	  String stationEndID = data.getDistrict_endstationname();

			    	  districtName += "-" + stationEndID;
					 
					 if( updateDistrictInfo(stationStartID,stationEndID,data.getDistrict_stationnumber(),
							                data.getDistrict_railwaybureau(),districtName)){
						 
						 pushCommand();
						 
						 showMsg("成功更新!");
						 //表格的刷新方法,界面会重新读取数据并显示
						 tableViewer.refresh();//false
					 }
					 else
						 showMsg("更新失败!");
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
				 District data = (District) selection.getFirstElement();
				 if (data != null ) {
		             //先预先移动到下一行
		             Table table = tableViewer.getTable();
		             //确认删除  
					 MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_INFORMATION);  
					 messageBox.setText("提示信息");  
					 messageBox.setMessage("确定要删除此记录吗?");  
					 // SWT.YES 是  // SWT.NO 否 // SWT.CANCEL 取消 // SWT.RETRY 重试// SWT.ABORT 放弃// SWT.IGNORE 忽略  
					 if (messageBox.open() == SWT.YES) {  
						 if(deleteDistrictInfo(data.getDistrict_name())) //从数据库中删除记录
						 {
							 ((List)tableViewer.getInput()).remove(data); //数据模型的List容器中删除
							 districtData.remove(data.getDistrict_name());
							 openCurrentTable(row);
							 tableViewer.remove(data);//从表格界面上删除
							 
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
				 if(clearDistrictInfo()){
					 showMsg("清空操作成功!");
					 districtData.removeAll();
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
				 if (row > districtData.getTotalPageNum()){
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
				// getStationInfoMap();
				 getDistrictInfo();
				 openCurrentTable(row);
			 }  
		 };
		 	
		 
		 //工具条  
		 ToolBar toolBar = new ToolBar(buttonComposite, SWT.FLAT|SWT.RIGHT);//|SWT.BORDER
		 //工具条管理器  
		 ToolBarManager manager = new ToolBarManager(toolBar);
		 
		 //manager.add(refresh);
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
			  	      gc.fillGradientRectangle(0, event.y, clientWidth, event.height, false);
			  	      gc.setForeground(oldForeground);
			  	      gc.setBackground(oldBackground);
			  	      event.detail &= ~SWT.SELECTED;
			  	   }
			  	});
		 
		 //tableViewer.setUseHashlookup(true);//必须在setInput之前加入才有效
		 
			
       	 tableViewer.setInput(districtData.getData());//自动输入数据   即将数据显示在表格中
       	
       	 tableViewer.setItemCount(districtData.PAGE_SIZE); //设置显示的Item数
      }////createComp
	
	//从服务器获取信息.
	private void getStationInfo() {
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Station");
		
		//不带参数的sql语句的使用方法
		msg.setSql("select * from Station");// limit 16
		String paprams = "null";//
		msg.setParams(paprams);//转换为json字符串进行传递
		
		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return;		
		}else{
			List<Station> list = JsonUtil.getList4Json(listString,Station.class);
			districtData.setStationList(list);
		}
	}
	
	private void getDistrictInfo() {
	
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("District");

		//不带参数的sql语句的使用方法
		msg.setSql("select * from District");// limit 16
		String paprams = "null";//
		msg.setParams(paprams);//转换为json字符串进行传递

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return;		
		}else{
			List<District> list = JsonUtil.getList4Json(listString,District.class);
			districtData.setData(list);
		}
	}
	
	private void openCurrentTable(final int pagenum) {
		
		table.clearAll(); //清空表格所有项目中的数据
		//数据记录在检索结果结合中的下标开始值
		final int recordstart = (pagenum - 1) * pageSize;
		// 当前表格能显示的最后一条记录在检索结果集合中的索引号
		int end = recordstart + pageSize;
		// 若表格可显示的记录大于检索结果记录的总数，就只显示到最后一条检索结果记录
		final int recordend = Math.min(end, districtData.getRowCount());// 显示的最后一条记录在检索结果集合中的索引号

		// 当前页面显示的检索结果记录数
		final int currentdispnum = recordend - recordstart;
		//根据当前的页码，从检索结果集合中加载相应的数据到表的item域中  
		tableViewer.setInput(districtData.getRow(recordstart, recordstart+currentdispnum));//自动输入数据
		tableViewer.refresh();//刷新表格false 
	}
	
	private boolean clearDistrictInfo(){//清空所有记录
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHDELETE);
		
		String[]sqlArray = new String[4];
		sqlArray[0] = "delete from District"; 
		sqlArray[1] = "delete from StationDistrictRelation";
		sqlArray[2] = "delete from TrainDistrictRelation";
		sqlArray[3] = "delete from Plan";
		
		String sqlStr = JsonUtil.array2json(sqlArray);
		msg.setSql(sqlStr);
		String paprams = "null";
		msg.setParams(paprams);
		
		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){
			return false;		
		}else{
			return true;	 
		}
 
	}
	
	//删除区段，应同时删除StationDistrictRelation和TrainDistrictRelation表中含有该区段的所有信息
	public boolean deleteDistrictInfo(String districtName)
	{
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHDELETE);

		//组装插入sql
		String[]sqlStr = new String[4];
		sqlStr[0] = "delete from District where District_name='" + districtName + "'";
		sqlStr[1] = "delete from StationDistrictRelation where District_name='" + districtName  + "'" ;
		//删除该区段内所定义的所有车次(列车)计划 ???
		sqlStr[2] = "DELETE FROM Plan WHERE Train_name IN " +
				"(SELECT Train_name FROM TrainDistrictRelation WHERE District_name='" + districtName +"')";
		
		sqlStr[3] = "delete from TrainDistrictRelation where District_name='" + districtName  + "'";
			
		String sql = JsonUtil.array2json(sqlStr);
		msg.setSql(sql);
		String paprams = "null";
		msg.setParams(paprams);
		
		String list = synClientSupport.sqlMessageSend(msg);//同步通信
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	//向服务器发送更新报文
	public boolean updateDistrictInfo(String stationStartID,String stationEndID,int stationnumber,
			                          String bureau,String districtName)
	{
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLUPDATE);
		msg.setDataBean("District");
		msg.setSql("update District set District_startstationname=?,District_endstationname=? ," +
				   "District_stationnumber=?,District_railwaybureau=? where District_name=?");

		Object[] params = new Object[]{stationStartID,stationEndID,stationnumber,bureau,districtName};
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
	        return true;
	    }
	}
	
	//对指定列进行排序
	class DistrictSorter extends ViewerSorter {
		 boolean direction = false;//记录上一次的排序方式
		 private int propertyIndex;
		 public DistrictSorter(boolean direction, String sortByProperty) {
			 this.direction = direction;
			 for (int i = 0; i < (districtData.columnHeads).length; i++) {  
				 if (districtData.columnHeads[i].equals(sortByProperty)) {  
					 this.propertyIndex = i;  
					 return;  
				 }  
			 }  
		 }  
		 public int compare(Viewer viewer, Object e1, Object e2) {
			 District info1 = (District) e1;  
			 District info2 = (District) e2;
			 
			 switch (propertyIndex) {  
			 	case 0:
			 		return direction ? info1.getDistrict_startstationname().compareTo(info2.getDistrict_startstationname()):
			 	                       info2.getDistrict_startstationname().compareTo(info1.getDistrict_startstationname());
			 	case 1:
			 		return direction ? info1.getDistrict_endstationname().compareTo(info2.getDistrict_endstationname()):
			 	                       info2.getDistrict_endstationname().compareTo(info1.getDistrict_endstationname());
			 	default:  
			 		return 0;
			 }
		 }  
	 }
	
	
	//对单条记录进行处理
	public class TableLabelProvider extends LabelProvider implements ITableLabelProvider,ITableFontProvider,ITableColorProvider {
		
		//本方法用于显示图片
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			District data = (District) element;
            if(data == null)//解决表中空数据出现的错误
            	return null;
            
            //"start","end","number","bureau"
			switch (columnIndex) {
			case 0:
				return data.getDistrict_startstationname();
			case 1:
				return data.getDistrict_endstationname(); 
			case 2:
				return String.valueOf(data.getDistrict_stationnumber());//将int型转为String型
			case 3:
				return data.getDistrict_railwaybureau();
			}
			return null;
		}
		
		//ITableFontProvider 对指定列设置字体
		FontRegistry registry = new FontRegistry();
		public Font getFont(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return registry.getBold(Display.getCurrent().getSystemFont().getFontData()[0].getName());
			}
			return null;
		}
        //指定列变颜色  
		public Color getBackground(Object element, int columnIndex) {
			if (columnIndex == 0 || columnIndex == 1) {
				return white;
			}
			return null;
		}
		public Color getForeground(Object element, int columnIndex) {
			if (columnIndex == 0 || columnIndex == 1) {
				return red;
			}
			return null;
		}
		
	}

	//设置表格视图的内容器    对所有记录集中的记录进行处理
	public class TableContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object parent) {
			List results = new ArrayList();
			
			if (parent instanceof ArrayList) {
				results = (ArrayList) parent;
			}
			return results.toArray(); //将List转化为数组
		}
       //当TableViewer对象被关闭时触发执行此方法
		public void dispose() {
		}
		//当TableViewer再次调用setInput()时触发此方法执行 
		public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {
			//System.out.println("Input changed: old=" + oldInput + ", new=" + newInput); 
		}
	}
	
	//设置单元的更改器
    class TableCellModifier implements ICellModifier {
          public boolean canModify(Object element,String property) {
                return true;
          }
          public Object getValue(Object element,String property) {
                Object result = null;
                District data = (District) element;
                if(data == null)
                	return null;
                //使用别名的方法
                //"start","end","number","bureau"
                if(property.equals("number"))	
                	return String.valueOf(data.getDistrict_name());
                else
                if(property.equals("start")){
                	return data.getDistrict_startstationname();
                }
                else
                if(property.equals("end"))	
                	return data.getDistrict_endstationname();
			    else
                if (property.equals("bureau")) //ComboBoxCellEditor要求返回下拉框中的索引值
                  	return new Integer(getBureauIndex(data.getDistrict_railwaybureau()));
               
                return result;
          }

          private int getBureauIndex(String name) {
        	  for (int i = 0; i < districtData.bureau.length; i++) {
        		  if (DistrictData.bureau[i].equals(name))
        			  return i;
        	  }
        	  return -1;
          }
        //"start","end","number","bureau"
          //当用户对单元格内容进行修改 时，此方法执行. 为某个属性赋值
          public void modify(Object element, String property, Object value) {
        	  TableItem tableItem = (TableItem) element;
        	  if(tableItem == null){
        		  return;
        	  }
        	  District data = (District) tableItem.getData();
        	  if(data == null)
              	  return;

              if(property.equals("number")){
            	  String number = (String) value;
        		  if (number.length() > 0) {
        			  data.setDistrict_stationnumber(Integer.parseInt(number));
        		  }
              }
              else
              if(property.equals("start")){
            	  String name = (String) value;
                  data.setDistrict_startstationname(name);
              }
              else
              if(property.equals("end")){
            	  String name = (String) value;
                  data.setDistrict_endstationname(name);
       		      
              }
              else
              if (property.equals("bureau")){
            	  Integer comboIndex = (Integer) value;
                  if(comboIndex.intValue() != -1){
                	  String mapName = districtData.bureau[comboIndex.intValue()];
                	  data.setDistrict_railwaybureau(mapName);
       		      }
              }
        	 tableViewer.update(data, null);
        }
    }
    
    
	private void pushCommand(){
		DistrictStationData.initial();
		DistrictTrainData.initial();
		ExperimentData.initial();
		TrainPlanData.initial();
	}
}
