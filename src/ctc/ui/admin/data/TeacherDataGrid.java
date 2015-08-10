package ctc.ui.admin.data;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;


public class TeacherDataGrid {
	private List fontResources = new ArrayList();
	private Button button = null;
	private Table table = null;
	private Shell shell;
	private Shell mainShell;

	 Listener exitListener = new Listener() {
	      public void handleEvent(Event e) {
	    	  shell.dispose();
	      }
	    };
	  
	  
	public TeacherDataGrid() {
		super();
	}

	public void initialize(Shell mainShell){
		this.mainShell = mainShell;
		shell = new Shell(mainShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		Display display = shell.getDisplay();

		shell.setSize(300,350);//宽度 高度
		shell.setText("查询结果");
		shell.addListener(SWT.Close, exitListener);


		this.setupWidgets(display, shell);

		shell.pack();
		Rectangle parentBounds = mainShell.getBounds();
		Rectangle bounds = mainShell.getBounds();
		int x = 100 + parentBounds.x + (parentBounds.width - bounds.width)/ 2;
		int y = 50 + parentBounds.y + (parentBounds.height - bounds.height)/ 2;
		shell.setLocation(x, y);
		shell.open ();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) 
				display.sleep (); 
		} 

		for (int i = 0; i < fontResources.size(); i++){
			((Font) fontResources.get(i)).dispose();
		}
		//display.dispose();
	}

	private void setupWidgets(Display display, Shell shell){
		FontData[] fd = shell.getFont().getFontData();

		//设置字体
		for (int i = 0; i < fd.length; i++) {
			fd[i].setHeight(10);
		}
		Font font = new Font(display,fd);
		fontResources.add(font);

		//tabs
		TeacherData data = TeacherData.getInstance();

		table = new Table(shell, SWT.SINGLE|SWT.FULL_SELECTION);
		table.setBounds(0, 0,320,320);//宽度 高度
		table.setLinesVisible(true);
		table.setFont(font);
		table.setHeaderVisible(true);

		int width = 0;
		//headers
		for (int i = 0; i < data.getColumnCount(); i ++){
			width = data.getColumnWidth(i);
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth((int)(width * 8)); //characters by pixels... rough guess
			column.setText(data.getHeaders()[i]);
		}

		TableItem[] items = new TableItem[data.getRowCount()]; // an item for each field
		
		//读取数据
		for (int i = 0 ; i < data.getRowCount(); i++){
			items[i] = new TableItem(table, SWT.NONE);
			items[i].setText(data.getRow(i));

		}

		//button
		button = new Button(shell, SWT.CENTER|SWT.PUSH);
		button.setBounds(130,325,50,30);

		button.setText("编辑") ;
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() == TeacherDataGrid.this.button){

					TableItem[] items = TeacherDataGrid.this.table.getSelection();
					if (items == null || items.length < 1){
						showMsg("请先选取要编辑的教师!");		
					} 
					else {
						//System.out.println("所选关键字:" + items[0]);
						TeacherEditDialog form = new TeacherEditDialog(TeacherDataGrid.this.mainShell);
						form.setKey(items[0].getText());
						form.show();
						TeacherDataGrid.this.refreshData();
					}

				}	
			}
		}	
		);  
	}
	
	private void showMsg(String str){
		Toolkit.getDefaultToolkit().beep();//报警
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
    //刷新显示的信息
	private void refreshData(){
		TeacherData data = TeacherData.getInstance();
		for (int i = 0; i < data.getRowCount(); i ++){
			table.getItem(i).setText(data.getRow(i));
		} 
	}
	

	public static void main(String[] args) {
	}
	
}
