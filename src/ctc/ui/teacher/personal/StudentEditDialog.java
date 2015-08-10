package ctc.ui.teacher.personal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.swtdesigner.SWTResourceManager;



public class StudentEditDialog extends Dialog {
	private String key = null;
	private String[] row = null;
	private Shell shell = null;
	
	private Button saveButton = null;
	
	private Text  id = null;
	private Text nameCode = null;
	private Text password = null;
	private Combo role = null;
	
	
	public StudentEditDialog(Shell arg0) {
		this(arg0, SWT.APPLICATION_MODAL);
		
	}

	public StudentEditDialog(Shell arg0, int arg1) {
		super(arg0, arg1);
		
	}

	public void show(){
		shell = new Shell(this.getParent(), SWT.DIALOG_TRIM| SWT.APPLICATION_MODAL);
		shell.setText("编辑学员信息");
		shell.setBounds(this.getDialogBounds(140,450));//高度 宽度
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
		
		//网格式布局，它把父组件分成一个表格，默认情况下每个子组件占据一个单元格的空间，每个子组件按添加到父组件的顺序排列在表格中
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;//两列列示子组件
		//layout.makeColumnsEqualWidth = true;//子组件是否有相同的列宽，true时表示每列的列宽相等。
		//layout.marginLeft  = 2;//当前组件距离父组件左边距的像素点个数。
		//layout.marginRight = 2;//当前组件距离父组件右边距的像素点个数。
		//layout.marginTop; //当前组件距离父组件上边距的像素点个数。
		//layout.marginBottom;//当前组件距离父组件下边距的像素点个数。
		//layout.horizontalSpacing  = 1;//子组件的水平间距。
		layout.verticalSpacing = 6;//子组件的垂直间距
		group.setLayout(layout);
		
		//控制子组件在网格中的位置大小等相关显示信息
		/*
		gridData.horizontalAlignment：//表示水平对齐方式。
		gridData.verticalAlignment：//表示子组件的垂直对齐方式，值和水平方式一样。
		gridData.horizontalIndent：//子组件水平偏移多少像素。此属性和“horizontalAlignment = GridData.BEGINNING”属性一起使用。
		gridData.horizontalSpan：//组件水平占据几个网格。
		gridData.grabExcessHorizontalSpace：//当父组件大小改变时，子组件是否以水平方向抢占空间。
		gridData.grabExcessVerticalSpace：//当父组件大小改变时，子组件是否以垂直方向抢占空间。
		gridData.widthHint：//子组件的宽度为多少像素（前提是未设置其他相关属性）。
		gridData.heightHint：//子组件的高度为多少像素（前提是未设置其他相关属性）。 
		*/
		
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

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("角色");
		role = new Combo (group, SWT.READ_ONLY);
		role.setItems (new String [] {"行车调度员", "主任调度员"});
		role.setLayoutData(new GridData(100, SWT.DEFAULT));
		role.setText(row[3]);
		
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
					if (e.getSource() == StudentEditDialog.this.saveButton){
						StudentEditDialog.this.saveAll();
						StudentEditDialog.this.askQuit();	
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
		row = StudentData.getInstance().find(this.getKey());
	}
	
	
	private void saveAll(){
		String userName = nameCode.getText();
		String passwordValue = password.getText();
		String idValue = id.getText();
		String roleValue = role.getText();
		if(StudentSearchLayout.updateStudentInfo(userName,passwordValue,roleValue,idValue))
		{
			row[0] = idValue;
			row[1] = userName;
			row[2] = passwordValue;
			row[3] = roleValue;	
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
