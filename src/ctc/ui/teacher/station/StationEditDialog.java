package ctc.ui.teacher.station;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.swtdesigner.SWTResourceManager;

import ctc.ui.teacher.district.DistrictData;
import ctc.ui.teacher.districtstation.DistrictStationData;
import ctc.ui.teacher.train.TrainData;

public class StationEditDialog extends Dialog {
	
	private StationSearchLayout stationOutlineSearchLayout; 
	private String[] row = null;
	private Shell shell = null;
	
	private Button saveButton = null;
	private Text id,stationName,downAvailLaneNum,upAvailLaneNum;
	private Combo stationLayout;
	
	
	public StationEditDialog(Shell arg0) {
		this(arg0, SWT.APPLICATION_MODAL);
		stationOutlineSearchLayout = new StationSearchLayout();
		
	}

	public StationEditDialog(Shell arg0, int arg1) {
		super(arg0, arg1);
	}

	public void show(){
		shell = new Shell(this.getParent(), SWT.DIALOG_TRIM| SWT.APPLICATION_MODAL);
		shell.setText("车站信息");
		shell.setBounds(this.getDialogBounds(150,450));//高度250  宽度
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
		label.setText("车站名称");
		stationName = new Text(group, SWT.BORDER);
		stationName.setLayoutData(new GridData(124, SWT.DEFAULT));
		stationName.setText(row[0]);


		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("下行车道数");
		downAvailLaneNum = new Text(group, SWT.BORDER);
		downAvailLaneNum.setLayoutData(new GridData(124, SWT.DEFAULT));
		downAvailLaneNum.setText(row[1]);
		
		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("上行车道数");
		upAvailLaneNum = new Text(group, SWT.BORDER);
		upAvailLaneNum.setLayoutData(new GridData(124, SWT.DEFAULT));
		upAvailLaneNum.setText(row[2]);

		final Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("站场图");
		stationLayout = new Combo (group, SWT.READ_ONLY);
		stationLayout.setItems (new String [] {"库表格式", "文件格式"});
		stationLayout.setLayoutData(new GridData(100, SWT.DEFAULT));
		stationLayout.setText(row[3]);
		
		/*final Label label_4 = new Label(group, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_4.setText("说明");
		memo = new Text(group, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		GridData data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 80;
	    data.widthHint = 124;
		memo.setLayoutData(data);
		memo.setText(row[5]);*/
		
		Group groupRight = new Group(shell, SWT.SHADOW_IN);
		groupRight.setText("不可修改信息");
		GridLayout layout2 = new GridLayout();
		layout2.numColumns=2;
		groupRight.setLayout(layout2);
		
		final Label label_5 = new Label(groupRight, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD|SWT.MULTI));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_5.setText("原车站名称");		
		id = new Text(groupRight, SWT.READ_ONLY | SWT.BORDER);
		id.setLayoutData(new GridData(120, SWT.DEFAULT));
		id.setText(row[0]);

		new Label(groupRight, SWT.SEPARATOR).setVisible(false); //invisible label to fill the left side
		saveButton = new Button(groupRight, SWT.PUSH); //button on the right
		saveButton.setText("保存");
		saveButton.addSelectionListener( new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
					if (e.getSource() == StationEditDialog.this.saveButton){
						StationEditDialog.this.saveAll();
					}
			}
		}
		);
	}
	
	public Rectangle getDialogBounds(int height, int width){
		Rectangle temp = this.getParent().getBounds();
		return new Rectangle(temp.x + 35,temp.y + 150,width, height);
	}
	

	private void initData(){
		row = stationOutlineSearchLayout.getData();
	}
	
	
	private void saveAll(){
		
		if (stationName.getText().length() == 0){
			showMsg("请输入车站名称!");//消息框的提示文字
			return;
		}
		
		if(stationOutlineSearchLayout.updateStationInfo(stationName.getText().trim(),downAvailLaneNum.getText().trim(),
									  upAvailLaneNum.getText().trim(),stationLayout.getText().trim(),id.getText())){
			
			//解决用户对车站名称进行更新的情况
			id.setText(stationName.getText().trim());
			
			pushCommand();
			
			showMsg("操作成功！");
		}
		else
			showMsg("操作失败！");//消息框的提示文字
		
	}
	
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
	
	private void pushCommand(){
		TrainData.initial();
		DistrictData.initial();
		StationData.reloadFlag = true;
		DistrictStationData.initial();
	}

}
