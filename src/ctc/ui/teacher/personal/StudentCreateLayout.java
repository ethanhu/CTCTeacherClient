package ctc.ui.teacher.personal;

import java.util.*;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import ctc.constant.*;
import ctc.pojobean.*;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.util.JsonUtil;

//tableviewer是扩展Table

public class StudentCreateLayout{

	private static SynClientSupport synClientSupport;
	private static Shell shell;
	
	private Table table;
    private TableViewer tableViewer;
    private final static String[] COLUMN_HEADINGS = {"用户账号","密码"};
    
	private List<StudentInfo> studentInfoList =  new ArrayList<StudentInfo>();//用于屏幕上显示
	private List<Student> studentList =  new ArrayList<Student>();//用于同服务器进行通信
	private List<String> nameList =  new ArrayList<String>();//用于检查用户名是否有重名现象
	
	public StudentCreateLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
		getStudentInfo();//获取student中已有的用户名和密码信息
	}
	
	private void getStudentInfo() {
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Student");
		msg.setSql("select * from Student");
		String paprams = "null";//不带参数的sql语句的使用方法
		msg.setParams(paprams);
		String listString = synClientSupport.sqlMessageSend(msg);//演示同步通信
	//	System.out.println("listString:" +listString);
		
		if(listString == null){//此情况不会出现
			return;		
		}else{//将Student对象转换为studentInfo对象
			
			studentList = JsonUtil.getList4Json(listString,Student.class);
			
			if (studentList.size() <= 0 )
				return;
			for (int i = 0; i < studentList.size(); i++) {  
				Student info = studentList.get(i);
				studentInfoList.add(new StudentInfo(info.getStudent_name(),info.getStudent_password()));
			}  
			
		//	System.out.println("studentInfoList:"+ studentInfoList.size());
		}
	}
	
	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout( new MigLayout());
		createGroup(comp,"批量输入学员信息","pos 0.5al 0.2al",null);
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
		//GridLayout(int numColumns, boolean makeColumnsEqualWidth) 
	    GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        group.setLayout(layout);
        
        //制作表格  
        //MULTI可多选  H_SCROLL有水平 滚动条、V_SCROLL有垂直滚动条、BORDER有边框、FULL_SELECTION整行选择 
        table = new Table(group, SWT.BORDER | SWT.V_SCROLL| SWT.FULL_SELECTION);
        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);
        /*Creates a column width with the given weight and minimum width.true if the column is resizable, and false if size of the column is fixed
        (10, 100, true)*/
        tableLayout.addColumnData(new ColumnWeightData(8, 8, false));//设置列宽为8像素
        TableColumn column_one = new TableColumn(table, SWT.NONE);//SWT.LEFT
        column_one.setText(COLUMN_HEADINGS[0]);//设置表头
        column_one.setAlignment(SWT.LEFT);//对齐方式SWT.LEFT 
        //column.setWidth(10);//宽度 
        
        tableLayout.addColumnData(new ColumnWeightData(8,8,false));//(15, 200,true)
        TableColumn column_two = new TableColumn(table, SWT.NONE);
        column_two.setText(COLUMN_HEADINGS[1]);
        column_two.setAlignment(SWT.LEFT);//SWT.LEFT
                
        //表格的视图
        tableViewer = new TableViewer(table);
        //标题和网格线可见
        tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		
		//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用  
		table.addListener(SWT.MeasureItem, new Listener(){  
			public void handleEvent(Event event){  
				event.width = table.getGridLineWidth();//设置宽度  
				event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight()*2);  
			}  
		});  
        
		 //设置填充  
		 //GridData data = new GridData(SWT.LEFT,SWT.CENTER, true, false);//SWT.FILL, SWT.FILL, true, false  xbm
		 GridData data = new GridData(GridData.FILL_BOTH);  //加此一句张开面板 
		 data.widthHint = 300;//add xbm
	     data.heightHint = 300;
	     data.grabExcessHorizontalSpace = true;
		 tableViewer.getTable().setLayoutData(data);  //表格的布局 
		 
		 
		//设置表格视图的内容提供者
		 tableViewer.setContentProvider(new TableContentProvider());
		 
		 //设置标题的提供者  
		 tableViewer.setLabelProvider(new TableLabelProvider());
		 
		//设置列的属性.  
        tableViewer.setColumnProperties(COLUMN_HEADINGS);
        
        //单元编辑器  
        CellEditor[] celleditors = new CellEditor[2];
        //文本编辑框
        celleditors[0] = new TextCellEditor(table);
        celleditors[1] = new TextCellEditor(table);
        //CheckboxCellEditor(table) 复选框
        tableViewer.setCellEditors(celleditors);

        //设置单元的更改器  
        tableViewer.setCellModifier(new TableCellModifier());
		 
       //列的选择事件  实现排序 
 		/*column_one.addListener(SWT.Selection, new Listener() {  
			 public void handleEvent(Event event) {  
				 tableViewer.setSorter(new StudentInfoSorter(COLUMN_HEADINGS[0]));  
			 }  
		 });  
 		*/
 		column_one.addSelectionListener(new SelectionAdapter() {   
 			boolean sortType = true; //sortType记录上一次的排序方式，默认为升序   
 			public void widgetSelected(SelectionEvent e) {   
 				sortType = !sortType;//取反。下一次排序方式要和这一次的相反      
 				tableViewer.setSorter(new StudentInfoSorter(sortType,COLUMN_HEADINGS[0]));    
 			}   
 		});   

/*		column_two.addListener(SWT.Selection, new Listener() {
			 public void handleEvent(Event event) {
				 tableViewer.setSorter(new StudentInfoSorter(COLUMN_HEADINGS[1]));  
			 }  
		 });  
*/
        //构造工具条
        Composite buttonComposite = new Composite(group,SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));//使工具条居中
        
        // 各种动作  
		 Action actionAddNew = new Action("添加") {//为表添加一条空记录，用于用户输入内容  
			 public void run() {  
				 StudentInfo studentInfo = new StudentInfo("输入用户账号","111111"); 
				 studentInfoList.add(studentInfo);
				
                 tableViewer.add(studentInfo);
                 table.setTopIndex(table.getItemCount());
                 table.select(table.getItemCount()-1);
                 tableViewer.editElement(studentInfo, 0);
                 
				 // 刷新表格  
				 tableViewer.refresh(false);
			 }  
		 };  
		 // 删除此项  
		 Action actionDelete = new Action("删除选中") {  
			 public void run() {
				 //获取选中的数据  
				 IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();  

				 //获取选中的第一个数据 
				 StudentInfo info = (StudentInfo) selection.getFirstElement();
				 if (info != null) {
		             //先预先移动到下一行
		             Table table = tableViewer.getTable();
		             int i = table.getSelectionIndex(); //取得当前所选行的序号，如没有则返回-1
		             table.setSelection(i + 1); //当前选择行移下一行
		             //确认删除  
					 MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_INFORMATION);  
					 messageBox.setText("确认");  
					 messageBox.setMessage("删除此学员吗?");  
					 // SWT.YES 是  // SWT.NO 否 // SWT.CANCEL 取消 // SWT.RETRY 重试// SWT.ABORT 放弃// SWT.IGNORE 忽略  
					 if (messageBox.open() == SWT.YES) {  
						 studentInfoList.remove(info);
						 tableViewer.remove(info);//从界面上删除 
					 }
				 }
				 else{
					 displayMessage("请选取要删除的学员！"); 
				 }
				 
				 /*Iterator iterator = selection.iterator();
				 while(iterator.hasNext()) {
					 Object obj = iterator.next();
					 //确认删除  
					 MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO);  
					 messageBox.setText("确认");  
					 messageBox.setMessage("你想删除此条信息吗?");  
					 // SWT.YES 是  // SWT.NO 否 // SWT.CANCEL 取消 // SWT.RETRY 重试// SWT.ABORT 放弃// SWT.IGNORE 忽略  
					 if (messageBox.open() == SWT.YES) {  
						 studentInfoList.remove(obj);
						 tableViewer.remove(obj);//从界面上删除 
						 tableViewer.getInput().remove(obj); //从数据模型中删除 
						 
						 //刷新表格  
						 //tableViewer.refresh(false);  
					 }
				 }*/
			 }  
		 };  
		 Action actionClear = new Action("清空") {//清除所显示内容，点击保存后更新库  
			 public void run() {  
				 studentInfoList.removeAll(studentInfoList);
				 nameList.removeAll(nameList);
				 // 刷新表格  
				 tableViewer.refresh(false);
				 clearStudentInfo(); 
			 }  
		 };  
		 
		 // 保存  
		 Action actionSave = new Action("保存") {  
			 public void run() {  
				 //refresh(true) has the same effect as refresh(). 
				 // If updateLabels is true then labels for otherwise unaffected elements are updated as well
				 tableViewer.refresh(false);
				 updateStudentInfo(studentInfoList);  
			 }  
		 };  
		 //此代码目前没用
		 // 视图过滤    用来显示未解决的BUG  
		 final ViewerFilter filter = new ViewerFilter() {  
			 public boolean select(Viewer viewer, Object parentElement, Object element) {
				return false;  
				// if (!((Bug) element).isSolved)  
					// return true;  
				 //return false;  
			 }  
		 };  
		 Action actionShowUnsolvedOnly = new Action("只显示为解决的") {  
			 public void run() {  
				 if (!isChecked()) {  
					 // 删除过滤  
					 tableViewer.removeFilter(filter);  
				 } else {  
					 // 如果被选中，则过滤  
					 tableViewer.addFilter(filter);  
				 }  
			 }  
		 };  
		 // 默认没有选中  
		 actionShowUnsolvedOnly.setChecked(false);  
		 
		 
		 Action actionHelp = new Action("帮助") {  
			 public void run() {
				 String str =  "删除选中：\n\r" +
				 			   "逻辑删除，只有点击保存按钮后才从库中物理删除\n\r\n\r" +
			                   "清空：\n\r" +
			                   "从库中物理删除所有记录";
				 displayMessage(str);
			 }  
		 };  
		 
		 
		 //工具条  
		 ToolBar toolBar = new ToolBar(buttonComposite, SWT.FLAT|SWT.RIGHT);//|SWT.BORDER
		 //工具条管理器  
		 ToolBarManager manager = new ToolBarManager(toolBar);  
		 //增加按钮  
		 manager.add(actionAddNew);  
		 manager.add(actionDelete);  
		// manager.add(new Separator());
		 manager.add(actionClear);
		 manager.add(actionSave);  
		 manager.add(actionHelp);
		 
		// manager.add(new Separator());  
		 //manager.add(actionShowUnsolvedOnly);  
		manager.update(true);
		 
		 // 从文件装载数据  
		 studentInfoList = loadStudentInfo();  
		 //通过setInput为table添加了一个list后，只要对这个list里的元素进行添加和删除，
		 //table中的数据就会自动添加和删除，当然每次操作后需要调用refresh方法对tableviewer进行刷新。
		 tableViewer.setInput(studentInfoList);//自动输入数据
		
        
      }
	
	
	///////////////////////////////////////////////////////////////////
	private void clearStudentInfo(){//清空所有记录
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLDELETE);
		msg.setDataBean("Student");
		msg.setSql("delete from student");
		String paprams = "null";
		msg.setParams(paprams);
		String list = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(list == null){
			displayMessage("清空操作失败!");		
		}else{
			displayMessage("清空操作成功!");	 
		}	
	}
	
	 //保存数据到服务器 
	private void updateStudentInfo(List<StudentInfo> list) {
		int length = list.size();
		if(length <= 0)
			return;
		String[]sqlStr = new String[length];
		int rowNumber = 0;
		
		for (int i = 0; i < list.size(); i++) {//获取每行的内容  
			StudentInfo info = list.get(i);

			//System.out.println("count:" +i+"//"+info.getName());
			if ( (info.getName()).trim().equalsIgnoreCase("输入用户账号"))
			{
				displayMessage("用户账号输入不完整!");
				nameList.removeAll(nameList);
				return;
			}

			if(nameList.contains(info.getName())){
				displayMessage("用户账号存在重名!");
				nameList.removeAll(nameList);
				return;
			}

			if ((info.getPassword()).trim().length() == 0)
				info.password = "111111";//如果没有输入密码的话,设置为系统默认密码
			
			nameList.add(info.getName());
			
			String inertStr = "insert into Student (Student_name,Student_password) Values('"+ info.getName() + "','"+info.getPassword()+"');";
			sqlStr[rowNumber] = inertStr;
			rowNumber++;
		}  
		//if(rowNumber)
		//////////
		
		if (saveStudentInfo(sqlStr))
		{
			displayMessage("保存信息成功!");
			nameList.removeAll(nameList);
		}
		else{
			displayMessage("保存信息失败\n\r" +
					      "(查看用户账号是否有重名现象\n\r" +
					      "或是否没有输入用户账号)");//消息框的提示文字
		}
	}
	
	private void displayMessage(String msg)
	{
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(msg);
		mb.open();
	}

	//先删除student表中所有记录,然后进行批量插入
	private boolean saveStudentInfo(String[] sqlStr){
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLBATCHUPDATE);
		msg.setDataBean("Student");
		//组装插入sql
		String sql = JsonUtil.array2json(sqlStr);//java字符串数组转换为json字符串
		msg.setSql(sql);
		String paprams = "null";//不带参数的sql语句的使用方法
		msg.setParams(paprams);//转换为json字符串进行传递
		
		//组装删除sql
		String delteSql = "delete from Student";
		msg.setSql_1(delteSql);
		msg.setParams_1(paprams);
		
		String list = synClientSupport.sqlMessageSend(msg);
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}
	 
	 //获取初始值
	 private List<StudentInfo> loadStudentInfo(){  
		 return studentInfoList;
	} 
  	 
	 class StudentInfoSorter extends ViewerSorter {
		 boolean direction = false;//记录上一次的排序方式
		 private int propertyIndex;
		 public StudentInfoSorter(boolean direction, String sortByProperty) {
			 this.direction = direction;
			 for (int i = 0; i < COLUMN_HEADINGS.length; i++) {  
				 if (COLUMN_HEADINGS[i].equals(sortByProperty)) {  
					 this.propertyIndex = i;  
					 return;  
				 }  
			 }  
		 }  
		 public int compare(Viewer viewer, Object e1, Object e2) {
			 StudentInfo info1 = (StudentInfo) e1;  
			 StudentInfo info2 = (StudentInfo) e2;
			 switch (propertyIndex) {  
			 	case 0:
			 		//return info1.name.compareTo(info2.name);
			 		return direction ? info1.name.compareTo(info2.name):info2.name.compareTo(info1.name);
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
	//设置表格视图的内容提供者
    public class TableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object parent) {
              List results = new ArrayList();
              if (parent instanceof ArrayList) {
                    results = (ArrayList) parent;
              }
              return results.toArray();
        }

        public void dispose() {
        //	System.out.println("Disposing ...");  
        }

        public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {
        	//System.out.println("Input changed: old=" + oldInput + ", new=" + newInput); 
        }

  }
	
	//设置标题的提供者
	 public class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		// 图形的列
		 public Image getColumnImage(Object element, int columnIndex) {
               return null;
         }
      // 文本的列 
         public String getColumnText(Object element, int columnIndex) {
               StudentInfo StudentInfo = (StudentInfo) element;
               
               if(StudentInfo == null)//解决表中空数据出现的错误
               	return null;
               
               switch (columnIndex) {
               case 0:
                     return StudentInfo.getName();
               case 1:
                     return StudentInfo.getPassword();
               }
               return null;
         }
   }
	
	//设置单元的更改器
    class TableCellModifier implements ICellModifier {
    	  /* 是否可以修改此单元格。这里设置了任一单元格都可以修改。  
    	   * @param element 表格记录对象
            * @param property 列的别名
         */ 
          public boolean canModify(Object element,String property) {
                return true;
          }
          /**
           * 当单击单元格出现CellEditor时应该显示什么值。由此方法决定，
           */ 
          public Object getValue(Object element,String property) {
                Object result = null;

                StudentInfo StudentInfo = (StudentInfo) element;
              
               //先拿到索引
                List list = Arrays.asList(COLUMN_HEADINGS);
                int columnIndex = list.indexOf(property);
                //再返回对应的数据
                switch (columnIndex) {
                case 0:
                      result = StudentInfo.getName();
                      break;
                case 1:
                      result = StudentInfo.getPassword();
                      break;
                }

                return result;
          }
          //当用户对单元格内容进行修改 时，此方法执行
          public void modify(Object element, String property,Object value) {
        	  List list = Arrays.asList(COLUMN_HEADINGS);
        	  int columnIndex = list.indexOf(property);

        	  TableItem tableItem = (TableItem) element;
        	  StudentInfo StudentInfo = (StudentInfo) tableItem.getData();

        	//  System.out.println("Modiy:" + value +"::"+columnIndex);
        	  switch (columnIndex) {
        	  case 0:
        		  String key = (String) value;
        		  if (key.length() > 0) {
        			  StudentInfo.setName(key);
        		  }
        		  break;

        	  case 1:
        		  String v = (String) value;
        		  if (v.length() > 0) {
        			  StudentInfo.setPassword(v);
        		  }
        		  break;
        	  }
        	  
        	  tableViewer.update(StudentInfo, null);
          }

    }    

    
}


