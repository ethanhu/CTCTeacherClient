package ctc.ui.teacher.personal;

import java.util.List;
import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.swtdesigner.SWTResourceManager;
import ctc.constant.Constants;
import ctc.pojobean.Teacher;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.util.JsonUtil;


public class TeacherPasswordLayout{

	private static SynClientSupport synClientSupport;
	private static Shell shell;
    private Teacher teacher;
    private static String userName;
    private static String password;
    private static int teacher_id;
    
	public TeacherPasswordLayout(Shell shell, MinaClient minaClient,String userName, String password){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.userName = userName;
		this.password = password;
		this.shell = shell;
		getTeacherID();//获取该用户在库teacher中的ID
	}
	
	private void getTeacherID() {
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("teacher");
		msg.setSql("select * from teacher where Teacher_name = ? and Teacher_password = ?");
		Object[] params = new Object[]{userName,password};
		String paprams = JsonUtil.array2json(params);//带参数的sql语句的使用方法
		msg.setParams(paprams);//转换为json字符串进行传递

		String listString = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(listString == null){//此情况不会出现
			teacher_id = -1;		
		}else{
			List<Teacher> list = JsonUtil.getList4Json(listString,Teacher.class);
			Teacher teacher = new Teacher();
			for(int i=0;i <list.size();i++){//此循环只执行一次
				teacher = new Teacher();
				teacher = list.get(i);
				//System.out.println(teacher.getTeacher_name() +"::"+ teacher.getTeacher_password());
			} 
			teacher_id = teacher.getTeacher_id();
		}
	}
	private boolean updateTeacherInfo(String newUserName, String newPassword){
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLUPDATE);
		msg.setDataBean("Teacher");
		msg.setSql("update Teacher set Teacher_name = ?,Teacher_password = ? where Teacher_id =?");
		Object[] params = new Object[]{newUserName,newPassword,teacher_id};
		String paprams = JsonUtil.array2json(params);//带参数的sql语句的使用方法
		//String paprams = "null";//不带参数的sql语句的使用方法
		msg.setParams(paprams);//转换为json字符串进行传递
		
		String list = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}
	private boolean deleteTeacherInfo(String userName, String password){
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLDELETE);
		msg.setDataBean("Teacher");
		msg.setSql("delete from Teacher where Teacher_id = ?");
		Object[] params = new Object[]{teacher_id};
		String paprams = JsonUtil.array2json(params);//带参数的sql语句的使用方法
		//String paprams = "null";//不带参数的sql语句的使用方法
		msg.setParams(paprams);//转换为json字符串进行传递
		String list = synClientSupport.sqlMessageSend(msg);//演示同步通信
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
		
	}
	
	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.NONE);
		
		//"fillx", "[right]rel[grow,fill]", "[]10[]"含义为 new LC().fillX(),new AC().align("right").gap("rel").grow().fill(), new AC().gap("10");
		comp.setLayout( new MigLayout());  
		
		createGroup(comp, "教师个人信息维护","pos 0.5al 0.2al",null);
		
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
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		comp.setLayout(gridLayout);
		
		final Label label = new Label(comp, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		//创建默认GridData对象.data = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		//GridData(int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace) 
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    //data.horizontalSpan = 2;
	    label.setLayoutData(data);
		label.setText("用户名:");
		final Text userNameT = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 100;
	    userNameT.setLayoutData(data);
	    userNameT.setText(userName);
	    
		final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("密码:");
		
		final Text passwordT = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 100;
	    passwordT.setLayoutData(data);
	    passwordT.setText(password);
		
		Button OKButton = new Button(comp, SWT.PUSH);//SWT.DOWN
		OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
		OKButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		OKButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				//SWT.ICON_ERROR SWT.ICON_INFORMATION SWT.ICON_QUESTION SWT.ICON_WARNING  SWT.ICON_WORKING
				MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
				mb.setText("提示信息");//消息框的标题
				if (updateTeacherInfo(userNameT.getText(),passwordT.getText()))
				{
					mb.setMessage("成功更新个人信息!");//消息框的提示文字
				}
				else{
					mb.setMessage("更新个人信息失败!");//消息框的提示文字
				}
				mb.open();
			}
		});
		OKButton.setText("更新");
		
		Button deleteButton = new Button(comp, SWT.DOWN);
		deleteButton.setForeground(SWTResourceManager.getColor(255, 0, 250));
		deleteButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
				mb.setText("提示信息");//消息框的标题
				if (deleteTeacherInfo(userNameT.getText(),passwordT.getText()))
				{
					mb.setMessage("成功删除个人信息!");//消息框的提示文字
				}
				else{
					mb.setMessage("删除个人信息失败!");//消息框的提示文字
				}
				mb.open();
			}
		});
		deleteButton.setText("删除");
	  
	  }
}
