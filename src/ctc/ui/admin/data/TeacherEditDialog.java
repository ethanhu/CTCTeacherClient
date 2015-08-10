package ctc.ui.admin.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import ctc.ui.admin.AdminTeacherInfoSearchLayout;
import ctc.ui.admin.data.TeacherData;


public class TeacherEditDialog extends Dialog {
	private String key = null;
	private String[] row = null;
	private Shell shell = null;
	
	private Button saveButton = null;
	
	private Text  id = null;
	private Text nameCode = null;
	private Text password = null;
		
	
	public TeacherEditDialog(Shell arg0) {
		this(arg0, SWT.APPLICATION_MODAL);
		
	}

	public TeacherEditDialog(Shell arg0, int arg1) {
		super(arg0, arg1);
		
	}

	public void show(){
		shell = new Shell(this.getParent(), SWT.DIALOG_TRIM| SWT.APPLICATION_MODAL);
		shell.setText("编辑教师信息");
		shell.setBounds(this.getDialogBounds(120,450));//高度 宽度
		shell.setLayout(new FillLayout());
		
		this.initData();
		this.initWidgets();

		shell.open(); 
		Display display = this.getParent().getDisplay(); 
		while (!shell.isDisposed()) { 
			if (!display.readAndDispatch()) 
			display.sleep(); 
		} 	
	}
	
	public void initWidgets(){
		Group group = new Group(shell,  SWT.SHADOW_IN);
		group.setText("可修改信息");
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;//两列列示子组件
		layout.verticalSpacing = 6;//子组件的垂直间距
		group.setLayout(layout);
		
		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label.setText("账号");
		nameCode = new Text(group, SWT.BORDER);
		nameCode.setLayoutData(new GridData(124, SWT.DEFAULT));
		nameCode.setText(row[1]);


		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("密码");
		password = new Text(group, SWT.BORDER);
		password.setLayoutData(new GridData(124, SWT.DEFAULT));
		password.setText(row[2]);

		Group groupRight = new Group(shell, SWT.SHADOW_IN);
		groupRight.setText("不可修改信息");
		GridLayout layout2 = new GridLayout();
		layout2.numColumns=2;
		groupRight.setLayout(layout2);
		//group2.setLayoutData(gridData);
		
		final Label label_3 = new Label(groupRight, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("学员ID");		
		id = new Text(groupRight, SWT.READ_ONLY | SWT.BORDER);
		id.setLayoutData(new GridData(120, SWT.DEFAULT));
		id.setText(row[0]);

		new Label(groupRight, SWT.SEPARATOR).setVisible(false); //invisible label to fill the left side
		saveButton = new Button(groupRight, SWT.PUSH); //button on the right
		saveButton.setText("保存");
		saveButton.addSelectionListener( new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
					if (e.getSource() == TeacherEditDialog.this.saveButton){
						TeacherEditDialog.this.saveAll();
						TeacherEditDialog.this.askQuit();	
					}
			}
		}
		);
	}
	
	public Rectangle getDialogBounds(int height, int width){
		Rectangle temp = this.getParent().getBounds();
		return new Rectangle(temp.x + 35,temp.y + 150,width, height);
	}
	
	/**
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param string
	 */
	public void setKey(String string) {
		key = string;
	}
	
	/**
	 * set up one row of data for the form
	 */
	private void initData(){
		row = TeacherData.getInstance().find(this.getKey());
	}
	
	
	private void saveAll(){
		String userName = nameCode.getText();
		String passwordValue = password.getText();
		String idValue = id.getText();
		if(AdminTeacherInfoSearchLayout.updateTeacherInfo(userName,passwordValue,idValue))
		{
			row[0] = idValue;
			row[1] = userName;
			row[2] = passwordValue;
		}
		else{
			MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
			mb.setText("提示信息");//消息框的标题
			mb.setMessage("更新操作失败！");//消息框的提示文字
			mb.open();
		}
	}
	
	private void askQuit(){
		MessageBox mb = new MessageBox(shell,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
		mb.setText("提示信息");
		mb.setMessage("编辑已经完成?");
		int reply = mb.open();
		if (reply == SWT.OK){
			shell.dispose();	
		}	
	}
	

	public static void main(String[] args) {
	}

}
