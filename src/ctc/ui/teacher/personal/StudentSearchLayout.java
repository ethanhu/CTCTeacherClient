package ctc.ui.teacher.personal;

import java.awt.Toolkit;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import ctc.constant.Constants;
import ctc.pojobean.*;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.util.JsonUtil;


public class StudentSearchLayout {

	private static SynClientSupport synClientSupport;
	private static Shell shell;
	private Text text;

	public StudentSearchLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}

	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"学员信息查询","pos 0.5al 0.2al",null);
		return  comp;
	}

	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}
	
	//向服务器发送更新该学员的信息报文
	public static boolean updateStudentInfo(String newUserName, String newPassword,String role,String id){
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLUPDATE);
		msg.setDataBean("Student");
		msg.setSql("update Student set Student_name = ?,Student_password = ? ,Student_role = ? where Student_id =?");
		Object[] params = new Object[]{newUserName,newPassword,role,id};
		String paprams = JsonUtil.array2json(params);//带参数的sql语句的使用方法
		msg.setParams(paprams);//转换为json字符串进行传递
		String list = synClientSupport.sqlMessageSend(msg);//同步通信
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	//从服务器获取该学员的信息.并写入StudentData类的String[][] data中
	private boolean getStudentInfo(String code) {
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Student");
		
		//带输入参数
		msg.setSql("select * from Student where Student_name=?");
		Object[] params = new Object[]{code};
		String paprams = JsonUtil.array2json(params);//带参数的sql语句的使用方法
		
		//不带输入参数
		/*msg.setSql("select * from student");
		String paprams = "null";//不带参数的sql语句的使用方法
	    */
		
		msg.setParams(paprams);//转换为json字符串进行传递
		
		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return false;		
		}else{
			List<Student> list = JsonUtil.getList4Json(listString,Student.class);
			Student student = new Student();
			if(list.size() <= 0)
				return false;
			StudentData.setData(list);
			return true;
		}
	}
	
	private void showMsg(String str){
		Toolkit.getDefaultToolkit().beep();//报警
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
	
	private void createComp(Composite group) {

		Listener aboutListener = new Listener() {
			public void handleEvent(Event e) {
				final Shell s = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				s.setText("说明");
				GridLayout layout = new GridLayout(1, false);
				layout.verticalSpacing = 20;
				layout.marginHeight = layout.marginWidth = 10;
				s.setLayout(layout);
				Label label = new Label(s, SWT.NONE);
				label.setText("请输入学员的学号!");
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
		Listener retrievalListener = new Listener() {//如果空显示对话框,非空显示查询结果
			public void handleEvent(Event e) {
				String code = StudentSearchLayout.this.text.getText(); 
				if(code.length() == 0){
					showMsg("请输入学员账号!");
					/*MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_QUESTION);
					dialog.setText("提示信息");
					dialog.setMessage("请输入学员账号!");
					if (e.type == SWT.Close)
						e.doit = false;
					if (dialog.open() == SWT.OK)
					*/
						return;
				}else{
					if(getStudentInfo(code))
					{
						new StudentDataGrid().initialize(shell);
					}else{
						showMsg("该学员不存在!");	
						return;
					}
				}
			}
		};
		
		//GridLayout(int numColumns, boolean makeColumnsEqualWidth) 
		GridLayout layout = new GridLayout(2, false);//1
		layout.verticalSpacing = 10;
		group.setLayout(layout);

		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
		label.setLayoutData(data);
		label.setText("用户账号:");

		text = new Text(group,SWT.BORDER  | SWT.SINGLE);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 15;
		data.widthHint = 150;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);

		Composite locationComp = new Composite(group,SWT.FLAT );
		locationComp.setBounds(20, 20, 300, 200);
		locationComp.setLayout(new RowLayout( ));

		ToolBar toolBar = new ToolBar(group, SWT.FLAT);
		ToolItem retrievalToolItem = new ToolItem(toolBar, SWT.PUSH);
		retrievalToolItem.setText("查询");
		retrievalToolItem.addListener(SWT.Selection, retrievalListener);
		ToolItem aboutToolItem = new ToolItem(toolBar, SWT.PUSH);
		aboutToolItem.setText("帮助");
		aboutToolItem.addListener(SWT.Selection, aboutListener);

	}



}


