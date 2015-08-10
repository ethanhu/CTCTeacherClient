package ctc.ui.teacher.train;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import ctc.ui.teacher.districttrainrelation.DistrictTrainData;
import ctc.ui.teacher.trainPlan.TrainPlanData;

public class TrainEditDialog extends Dialog {
	
	private TrainSearchLayout trainSearchLayout; 
	private String[] row = null;
	private Shell shell = null;
	
	private Button saveButton = null;
	private Text id,trainName,trainSpeed;
	
	private Combo trainDirect,trainEnd,trainStart;
	TrainData trainData = new TrainData();
	
	public TrainEditDialog(Shell arg0) {
		this(arg0, SWT.APPLICATION_MODAL);
		trainSearchLayout = new TrainSearchLayout();
		
	}

	public TrainEditDialog(Shell arg0, int arg1) {
		super(arg0, arg1);
	}

	public void show(){
		shell = new Shell(this.getParent(), SWT.DIALOG_TRIM| SWT.APPLICATION_MODAL);
		shell.setText("列车信息");
		shell.setBounds(this.getDialogBounds(180,450));//高度250  宽度
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
		
		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("列车名称");
		trainName = new Text(group, SWT.BORDER);
		trainName.setLayoutData(new GridData(124, SWT.DEFAULT));
		trainName.setText(row[0]);

		
		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("列车始发站");
		trainStart = new Combo(group,SWT.READ_ONLY);
		trainStart.setItems (trainData.stationName);
		trainStart.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (trainData.editStartFlag){
						trainData.reloadFlag = true;
					}
					else
						return;
					if (! trainData.reloadFlag)
						return;
					trainSearchLayout.getStationInfoList();
					trainStart.setItems(trainData.stationName);
					trainStart.setText(row[2]);
					trainData.reloadFlag = false;
					trainData.editStartFlag = false;
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		GridData data = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		data.widthHint = 100;
		trainStart.setLayoutData(data);
		trainStart.setText(row[1]);
		
		final Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("列车到达站");
		trainEnd = new Combo(group,SWT.READ_ONLY);
		trainEnd.setItems (trainData.stationName);
		trainEnd.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (trainData.editEndFlag){
						trainData.reloadFlag = true;
					}
					else
						return;
					if (! trainData.reloadFlag)
						return;
					trainSearchLayout.getStationInfoList();
					trainEnd.setItems(trainData.stationName);
					trainEnd.setText(row[3]);
					trainData.reloadFlag = false;
					trainData.editEndFlag = false;
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		data = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		data.widthHint = 100;
		trainEnd.setLayoutData(data);
		trainEnd.setText(row[2]);
		
		final Label label_4 = new Label(group, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_4.setText("车速");
		trainSpeed = new Text(group, SWT.BORDER);
		data = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		data.widthHint = 120;
		trainSpeed.setLayoutData(data);
		trainSpeed.setText(row[3]);

		final Label label_5 = new Label(group, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_5.setText("列车方向");
		trainDirect = new Combo(group,SWT.DROP_DOWN);
		trainDirect.setItems (trainData.direct);
		data = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		data.widthHint = 105;
		trainDirect.setLayoutData(data);
		trainDirect.setText(row[4]);
		
		Group groupRight = new Group(shell, SWT.SHADOW_IN);
		groupRight.setText("不可修改信息");
		GridLayout layout2 = new GridLayout();
		layout2.numColumns=2;
		groupRight.setLayout(layout2);
		
		final Label label_6 = new Label(groupRight, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD|SWT.MULTI));
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_6.setText("原列车名称");		
		id = new Text(groupRight, SWT.READ_ONLY | SWT.BORDER);
		id.setLayoutData(new GridData(120, SWT.DEFAULT));
		id.setText(row[0]);

		new Label(groupRight, SWT.SEPARATOR).setVisible(false); //invisible label to fill the left side
		saveButton = new Button(groupRight, SWT.PUSH); //button on the right
		saveButton.setText("更新");
		saveButton.addSelectionListener( new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
					if (e.getSource() == TrainEditDialog.this.saveButton){
						TrainEditDialog.this.saveAll();
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
		row = trainSearchLayout.getData();
	}
	
	private void saveAll(){
		
		
		if ( (trainStart.getText().length() == 0)||
			 (trainEnd.getText().length() == 0)||
			 (trainSpeed.getText().length() == 0 )||
			 (trainName.getText().length() == 0 ))
		 {
				showMsg("请选取或输入所有选项的值!");
				return;
		}
		
		if (trainStart.getText().equalsIgnoreCase(trainEnd.getText())){
			showMsg("车次的始发站与终点站不能相同!");
			return;
		}
		
		String stationStartID = trainStart.getText();
	    String stationEndID = trainEnd.getText();
	    int speed = Integer.parseInt(trainSpeed.getText());
	    int direct = getNameIndex(trainDirect.getText());
		
        if(trainSearchLayout.updateTrainInfo(stationStartID,stationEndID,speed,direct,trainName.getText(),id.getText()))
        {
        	//解决用户对车次名称进行更新的情况
			id.setText(trainName.getText().trim());
			
        	pushCommand();
			showMsg("操作成功！");
        }
		else
			showMsg("操作失败！");//消息框的提示文字
		
	}
	
	private int getNameIndex(String name) {
  	  for (int i = 0; i < trainData.direct.length; i++) {
  		  if (trainData.direct[i].equals(name))
  			  return i;
  	  }
  	  return -1;
    }
	
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
	
	private void pushCommand(){
		DistrictTrainData.initial();
		TrainPlanData.initial();
	}
}
