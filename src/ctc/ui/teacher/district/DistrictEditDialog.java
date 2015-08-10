package ctc.ui.teacher.district;

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

import ctc.ui.teacher.districtstation.DistrictStationData;
import ctc.ui.teacher.districttrainrelation.DistrictTrainData;
import ctc.ui.teacher.train.TrainData;
import ctc.ui.teacher.trainPlan.TrainPlanData;

public class DistrictEditDialog extends Dialog {
	
	private DistrictSearchLayout stationDistrictSearchLayout; 
	private String[] row = null;
	private Shell shell = null;
	
	private Button saveButton = null;
	private Text id,stationNum;
	private Combo stationBureau,stationEnd,stationStart;
	DistrictData districtData = new DistrictData();
	
	public DistrictEditDialog(Shell arg0) {
		this(arg0, SWT.APPLICATION_MODAL);
		stationDistrictSearchLayout = new DistrictSearchLayout();
		
	}

	public DistrictEditDialog(Shell arg0, int arg1) {
		super(arg0, arg1);
	}

	public void show(){
		shell = new Shell(this.getParent(), SWT.DIALOG_TRIM| SWT.APPLICATION_MODAL);
		shell.setText("区段信息");
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
		
		final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("区段开始站");
		stationStart = new Combo(group,SWT.READ_ONLY);
		stationStart.setItems (districtData.stationName);
		
		
		stationStart.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (districtData.editStartFlag){
						districtData.reloadFlag = true;
					}
					else
						return;
					if (! districtData.reloadFlag)
						return;
					stationDistrictSearchLayout.getStationInfo();
					stationStart.setItems(districtData.stationName);
					stationStart.setText(row[1]);
					districtData.reloadFlag = false;
					districtData.editStartFlag = false;
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		
		final GridData gd_stationStart = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		gd_stationStart.widthHint = 100;
		stationStart.setLayoutData(gd_stationStart);
		stationStart.setText(row[1]);
		
		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("区段终止站");
		stationEnd = new Combo(group,SWT.READ_ONLY);
		stationEnd.setItems (districtData.stationName);
		
		stationEnd.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (districtData.editEndFlag){
						districtData.reloadFlag = true;
					}
					else
						return;
					if (! districtData.reloadFlag)
						return;
					
					stationDistrictSearchLayout.getStationInfo();
					
					stationEnd.setItems(districtData.stationName);
					stationEnd.setText(row[2]);
					districtData.reloadFlag = false;
					districtData.editEndFlag = false;
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		
		
		final GridData gd_stationEnd = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		gd_stationEnd.widthHint = 100;
		stationEnd.setLayoutData(gd_stationEnd);
		stationEnd.setText(row[2]);
		
		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label.setText("区段站数量");
		stationNum = new Text(group, SWT.BORDER);
		final GridData gd_stationNum = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		gd_stationNum.widthHint = 120;
		stationNum.setLayoutData(gd_stationNum);
		stationNum.setText(row[3]);

		final Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("区段所属局");
		stationBureau = new Combo(group,SWT.DROP_DOWN);
		stationBureau.setItems (districtData.bureau);
		final GridData gd_stationBureau = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
		gd_stationBureau.widthHint = 105;
		stationBureau.setLayoutData(gd_stationBureau);
		stationBureau.setText(row[4]);
		
		Group groupRight = new Group(shell, SWT.SHADOW_IN);
		groupRight.setText("不可修改信息");
		GridLayout layout2 = new GridLayout();
		layout2.numColumns=2;
		groupRight.setLayout(layout2);
		
		final Label label_5 = new Label(groupRight, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD|SWT.MULTI));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_5.setText("原区段名称");		
		id = new Text(groupRight, SWT.READ_ONLY | SWT.BORDER);
		id.setLayoutData(new GridData(120, SWT.DEFAULT));
		id.setText(row[0]);

		new Label(groupRight, SWT.SEPARATOR).setVisible(false); //invisible label to fill the left side
		saveButton = new Button(groupRight, SWT.PUSH); //button on the right
		saveButton.setText("更新");
		saveButton.addSelectionListener( new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
					if (e.getSource() == DistrictEditDialog.this.saveButton){
						DistrictEditDialog.this.saveAll();
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
		row = stationDistrictSearchLayout.getData();
	}
	
	
	private void saveAll(){
		
		if ( (stationStart.getText().length() == 0)||
			 (stationEnd.getText().length() == 0)||
			 (stationNum.getText().length() == 0 )||
			 (stationBureau.getText().length() == 0 ))
		 {
				showMsg("请选取或输入所有选项的值!");
				return;
		}
		if ( stationStart.getText().equalsIgnoreCase(stationEnd.getText()))
		{
			showMsg("起始站与终止站不能相同!");
			return;
		}
		
		String districtName = stationStart.getText() + "-" + stationEnd.getText();
        
        int number = Integer.parseInt(stationNum.getText());

        if(stationDistrictSearchLayout.updateDistrictInfo(stationStart.getText(),stationEnd.getText(),number,stationBureau.getText(),districtName)){
        	
        	id.setText(districtName);
        	
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
		DistrictTrainData.initial();
		DistrictStationData.initial();
		DistrictData.initial();
		TrainPlanData.initial();
	}
}
