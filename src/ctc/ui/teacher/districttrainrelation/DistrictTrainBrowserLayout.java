package ctc.ui.teacher.districttrainrelation;

import java.util.*;
import java.util.List;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


import ctc.constant.*;
import ctc.pojobean.*;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.ui.teacher.district.DistrictData;
import ctc.util.JsonUtil;

public class DistrictTrainBrowserLayout extends Dialog {

	
	final Color red =  Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	final Color yellow =  Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	final Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

	private static SynClientSupport synClientSupport = new SynClientSupport();
	
	private Table table;
    private TableViewer tableViewer;
    
	static int pageSize; //表格的每页所显示的记录条数
	static int row = 1;//当前要显示的页
	static String districttrainID;

	private Shell shell;
	private Shell oldShell;
	
	DistrictTrainData districtTrainData = DistrictTrainData.getInstance();
	
	public DistrictTrainBrowserLayout(Shell arg0,SynClientSupport synClientSupport,String districttrainID) {
		this(arg0, SWT.APPLICATION_MODAL);
		this.synClientSupport = synClientSupport;
		pageSize = districtTrainData.PAGE_SIZE;
		this.districttrainID = districttrainID;
	}
	public DistrictTrainBrowserLayout(Shell arg0, int arg1) {
		super(arg0, arg1);
		oldShell = arg0; 
	}
	public Rectangle getDialogBounds(int height, int width){
		//Rectangle temp1 = this.getParent().getBounds();
		Rectangle temp = oldShell.getBounds();
		return new Rectangle(temp.x + temp.width/2 - width/2,temp.y + temp.height/2 - height/2 - 50,width, height);
	}
	
	public void show(){
		shell = new Shell(this.getParent(), SWT.DIALOG_TRIM| SWT.APPLICATION_MODAL);
		shell.setText("车站与区次关系信息");
		
		shell.setBounds(this.getDialogBounds(400,350));//高度 宽度
	    shell.setLayout(new FillLayout());
		
		this.initWidgets();

		shell.open(); 
		Display display = this.getParent().getDisplay(); 
		while (!shell.isDisposed()) { 
			if (!display.readAndDispatch()) 
			display.sleep(); 
		} 	
	}
	private void initWidgets() {
		
		Group group = new Group(shell, SWT.SHADOW_IN);
		
	    GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        group.setLayout(layout);
        
        //TableViewer是通过Table来布局的
		table = new Table(group, SWT.SINGLE | SWT.FULL_SELECTION |SWT.BORDER | SWT.VIRTUAL);//注意此处的设置
        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);
        
        //指定Table单元格的宽度和高度
        table.addListener(SWT.MeasureItem, new Listener() { 
       	     public void handleEvent(Event event) { 
       	         event.width = table.getGridLineWidth(); 
       	         event.height = (int) Math.floor(event.gc.getFontMetrics().getHeight() * 1.5); 
         }});
        
        //表格的视图
        tableViewer = new TableViewer(table);
        //标题和网格线可见
       // tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		 //设置填充  
		 GridData data = new GridData(GridData.FILL_BOTH); 
		 data.widthHint = 390;
	     data.heightHint = 295;
	     data.grabExcessHorizontalSpace = true;
		 tableViewer.getTable().setLayoutData(data);  //表格的布局 
		 
		//创建表格列的标题
		int width = 1;
			for (int i = 0; i < districtTrainData.getColumnCount(); i ++){
				width = districtTrainData.getColumnWidth(i);
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setWidth((int)(width * 8)); 
				column.setText(districtTrainData.getColumnHeads()[i]);//设置表头
				column.setAlignment(SWT.LEFT);//对齐方式SWT.LEFT
				if( i == 1)//车站名
				{
					//列的选择事件  实现排序 
					column.addSelectionListener(new SelectionAdapter() {   
			 			boolean sortType = true; //sortType记录上一次的排序方式，默认为升序   
			 			public void widgetSelected(SelectionEvent e) {   
			 				sortType = !sortType;//取反。下一次排序方式要和这一次的相反      
			 				tableViewer.setSorter(new Sorter(sortType,districtTrainData.columnHeads[1]));    
			 			}   
			 		});
				}
			}
	        
		table.addMouseMoveListener(new MouseMoveListener( ){
		       	public void mouseMove(MouseEvent e)
		       	{
		       		if(DistrictData.reloadFlag){
		       			
		       			getTrainInfo();
		       			
		       			openCurrentTable(row);
		       			DistrictData.reloadFlag = false;
		       		}
		       	}
		 });
	    
		//设置标题的提供者  
		tableViewer.setLabelProvider(new TableLabelProvider());
		
		//设置表格视图的内容提供者
		tableViewer.setContentProvider(new TableContentProvider());
		
		//设置列的属性.
		tableViewer.setColumnProperties(districtTrainData.columnHeads);
		
		//定义每一列的别名
		tableViewer.setColumnProperties(new String[] {"districtID", "stationID"});
       
		//设置每一列的单元格编辑组件CellEditor  
        CellEditor[] celleditors = new CellEditor[2];
        //文本编辑框
        celleditors[0] = null;
        celleditors[1] = null;

        tableViewer.setCellEditors(celleditors);
        
        //设置单元的更改器  
        tableViewer.setCellModifier(new TableCellModifier());
        
        //构造工具条
        Composite buttonComposite = new Composite(group,SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));//使工具条居中
    	
		Action actionDelete = new Action("删除") {  
			 public void run() {
				 //取得用户所选择的第一行, 若没有选择则为null
				 IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();  
				 //获取选中的第一个数据 
				 TrainDistrictRelation data = (TrainDistrictRelation) selection.getFirstElement();
				 if (data != null ) {
		             //先预先移动到下一行
		             Table table = tableViewer.getTable();
		             //确认删除  
					 MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_INFORMATION);  
					 messageBox.setText("提示信息");  
					 messageBox.setMessage("确定要删除此记录吗?");  
					 if (messageBox.open() == SWT.YES) {  
						 if(deleteDistrictInfo(data.getDistrict_name(),data.getTrain_name())) //从数据库中删除记录
						 {
							// showMsg("成功删除!");
							 ((List)tableViewer.getInput()).remove(data); //数据模型的List容器中删除
							 districtTrainData.remove(data.getTrain_name());
							 openCurrentTable(row);
							 tableViewer.remove(data);//从表格界面上删除
						 }
						 else
							 showMsg("删除失败!");
					 }
				 }
				 else{
					 showMsg("请选取要删除的记录！"); 
				 }
			 }
			};
			 
			 
		 Action actionClear = new Action("清空") {//清除所显示内容，点击保存后更新库  
			 public void run() {
				 MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_INFORMATION);  
				 messageBox.setText("提示信息");  
				 messageBox.setMessage("确定要清空区段内所有车次信息吗?");
				 if (messageBox.open() == SWT.YES){
					 if(clearDistrictInfo()){
						 showMsg("清空操作成功!");
						 districtTrainData.removeAll();
						 openCurrentTable(row);
					 }
					 else
						 showMsg("清空操作失败!");
				 }
			 }  
		 };  
		 Action actionHelp = new Action("帮助") {  
			 public void run() {
				 String str =  "删除：\n\r" +
				 			   "删除某一车次信息\n\r" +
			                   "清空：\n\r" +
			                   "删除所有车次信息";
				 showMsg(str);
			 }  
		 };  
		 
		 Action nextPage = new Action("下一页") {  
			 public void run() {
				 row++;
				 if (row > districtTrainData.getTotalPageNum()){
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
		 	
		 //工具条  
		 ToolBar toolBar = new ToolBar(buttonComposite, SWT.FLAT|SWT.RIGHT);//|SWT.BORDER
		 
		 //工具条管理器  
		 ToolBarManager manager = new ToolBarManager(toolBar);
		 
		 manager.add(nextPage);
		 manager.add(prevPage);
		 
		 manager.add(new Separator());
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
		 
		 tableViewer.setInput(districtTrainData.getData());//自动输入数据   即将数据显示在表格中
		 tableViewer.setItemCount(districtTrainData.PAGE_SIZE);  //设置显示的Item数
      }////createComp
	
	
	
	//对单条记录进行处理
	public class TableLabelProvider extends LabelProvider implements ITableLabelProvider,ITableFontProvider,ITableColorProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			TrainDistrictRelation data = (TrainDistrictRelation) element;
            if(data == null)
            	return null;
            //"districtID", "stationID" 
			switch (columnIndex) {
			case 0:
				return data.getDistrict_name();
			case 1:
				return data.getTrain_name();
			}
			return null;
		}
		FontRegistry registry = new FontRegistry();
		public Font getFont(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return registry.getBold(Display.getCurrent().getSystemFont().getFontData()[0].getName());
			}
			return null;
		}
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
	}
	//设置表格视图的内容器    对所有记录集中的记录进行处理
	public class TableContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			List results = new ArrayList();
			
			if (parent instanceof ArrayList) {
				results = (ArrayList) parent;
			}
			return results.toArray();
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
        //"districtID", "stationID"}
          public Object getValue(Object element,String property) {
                Object result = null;
                TrainDistrictRelation data = (TrainDistrictRelation) element;
                if(data == null)
                	return null;
                
                //使用别名的方法
               if(property.equals("districtID"))
                	return data.getDistrict_name();
                else
                if (property.equals("stationID"))
                	return data.getTrain_name();

               return result;
          }
         
          //当用户对单元格内容进行修改 时，此方法执行. 为某个属性赋值
          public void modify(Object element, String property, Object value) {
        	  TableItem tableItem = (TableItem) element;
        	  if(tableItem == null){
        		  return;
        	  }
        	  TrainDistrictRelation data = (TrainDistrictRelation) tableItem.getData();
        	  if(data == null)
              	  return;

        	  if(property.equals("districtID")){
        		  String districtID = (String) value;
        		  data.setDistrict_name(districtID);
        		 
        	  }
              else
              if(property.equals("stationID")){
            	  String stationID = (String) value;
      			  data.setDistrict_name(stationID);
        		  
              }
        	 tableViewer.update(data, null);
        }
    }

	//对指定列进行排序
	class Sorter extends ViewerSorter {
		 boolean direction = false;//记录上一次的排序方式
		 private int propertyIndex;
		 public Sorter(boolean direction, String sortByProperty) {
			 this.direction = direction;
			 for (int i = 0; i < (districtTrainData.columnHeads).length; i++) {  
				 if (districtTrainData.columnHeads[i].equals(sortByProperty)) {  
					 this.propertyIndex = i;  
					 return;  
				 }  
			 }  
		 }  
		 public int compare(Viewer viewer, Object e1, Object e2) {
			 TrainDistrictRelation info1 = (TrainDistrictRelation) e1;  
			 TrainDistrictRelation info2 = (TrainDistrictRelation) e2;
			 
			 switch (propertyIndex) {  
			 	case 1:
			 		return direction ? info1.getTrain_name().compareTo(info2.getTrain_name()):
			 	                       info2.getTrain_name().compareTo(info1.getTrain_name());
			 	default:  
			 		return 0;
			 }
		 }  
	 }
	
	//从服务器获取信息.
	public void getTrainInfo() {
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Train");
		
		//不带参数的sql语句的使用方法
		msg.setSql("select * from Train");// limit 16
		String paprams = "null";//
		msg.setParams(paprams);//转换为json字符串进行传递
		
		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return;		
		}else{
			List<Train> list = JsonUtil.getList4Json(listString,Train.class);
			districtTrainData.setTrainList(list);
		}
	}
	
	private void openCurrentTable(final int pagenum) {
		table.clearAll(); //清空表格所有项目中的数据
		//数据记录在检索结果结合中的下标开始值
		final int recordstart = (pagenum - 1) * pageSize;
		// 当前表格能显示的最后一条记录在检索结果集合中的索引号
		int end = recordstart + pageSize;
		// 若表格可显示的记录大于检索结果记录的总数，就只显示到最后一条检索结果记录
		final int recordend = Math.min(end, districtTrainData.getRowCount());// 显示的最后一条记录在检索结果集合中的索引号

		//当前页面显示的检索结果记录数
		final int currentdispnum = recordend - recordstart;
		//根据当前的页码，从检索结果集合中加载相应的数据到表的item域中  
		tableViewer.setInput(districtTrainData.getRow(recordstart, recordstart+currentdispnum));//自动输入数据
		tableViewer.refresh();//刷新表格false 
	}
	
	private boolean clearDistrictInfo(){//清空所有记录
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLDELETE);
		msg.setDataBean("TrainDistrictRelation");
		msg.setSql("delete from TrainDistrictRelation where District_name=?");
		
		Object[] params = new Object[]{this.districttrainID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);
		String list = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(list == null)
			return false;		
		else
			return true;	 
	}
	
	//向服务器发送删除报文
	public boolean deleteDistrictInfo(String districtID, String stationID)
	{
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLDELETE);
		msg.setDataBean("TrainDistrictRelation");
		msg.setSql("delete from TrainDistrictRelation where District_name =? and Train_name=?");
		
		Object[] params = new Object[]{districtID,stationID};
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
	

}