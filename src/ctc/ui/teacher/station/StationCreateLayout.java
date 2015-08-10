package ctc.ui.teacher.station;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;
import ctc.constant.Constants;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.ui.teacher.district.DistrictData;
import ctc.ui.teacher.districtstation.DistrictStationData;
import ctc.ui.teacher.train.TrainData;
import ctc.util.JsonUtil;


public class StationCreateLayout{

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
    
	public StationCreateLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout( new MigLayout());  
		createGroup(comp, "定义车站基本信息","pos 0.5al 0.2al",null);
		
		return comp;
	}
	
	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}
	
	VerifyListener listener = new VerifyListener() {
		//doit属性如果为true,则字符允许输入,反之不允许
		public void verifyText(VerifyEvent e) {   
			//输入控制键，输入中文，输入字符，输入数字 正整数验证   
			Pattern pattern = Pattern.compile("[0-9]\\d*"); //正则表达式  
			Matcher matcher = pattern.matcher(e.text);   
			if (matcher.matches()) //处理数字   
			{
				/*if(Integer.parseInt(e.text) != 0)//确保输入的数字不是0
					e.doit = true;
				else
					e.doit = false;
					*/
				e.doit = true;
			}
			else if (e.text.length() > 0) //字符: 包含中文、空格   
				e.doit = false;
			else//控制键  
				e.doit = true;   
		}   
	};

	private void createComp(Composite comp) {
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		comp.setLayout(gridLayout);
		
		final Label label = new Label(comp, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label.setLayoutData(data);
		label.setText("车站名称:");
		final Text stationName = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    stationName.setLayoutData(data);
	    
	    
		final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("下行可用车道:");
		final Text downAvailLaneNum = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    downAvailLaneNum.setLayoutData(data);
	    downAvailLaneNum.addVerifyListener(listener);//检查监听器,每输入一个字符都回触发
	    downAvailLaneNum.setText("2");//默认值
	    downAvailLaneNum.setTextLimit(3);  //最都只能输入3位数字
	    
	    final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("上行可用车道:");
		final Text upAvailLaneNum = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    upAvailLaneNum.setLayoutData(data);
	    upAvailLaneNum.addVerifyListener(listener);
	    upAvailLaneNum.setText("2");//默认值
	    upAvailLaneNum.setTextLimit(3);  //最都只能输入3位数字
	    
	    
	    final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("站场图:");
		final Combo stationLayout = new Combo(comp, SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		stationLayout.removeAll(); //先清空stationLayout
		stationLayout.setItems (StationData.MAPS);
		stationLayout.select(0); //设置"库表保存模式"为第一项
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    stationLayout.setLayoutData(data);
	   
	    /*
	    final Label label_4 = new Label(comp, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_4.setText("说明:");
		final Text memo = new Text(comp, SWT.BORDER | SWT.MULTI  | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 80;
	    data.widthHint = 132;
	    memo.setLayoutData(data);
	    */
		Button OKButton = new Button(comp, SWT.PUSH);//SWT.DOWN
		OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
		OKButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		OKButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (stationName.getText().length() == 0){
					showMsg("请输入车站名称!");//消息框的提示文字
					return;
				}
				if (insertStationInfo(stationName.getText().trim(),downAvailLaneNum.getText().trim(),
									  upAvailLaneNum.getText().trim(),stationLayout.getText().trim()/*,memo.getText().trim()*/)){
					
					pushCommand();
					
					showMsg("操作成功!");
				}
				else
					showMsg("操作失败(该车站信息已存在)!");
			}
		});
		OKButton.setText("保存");
		
		Button deleteButton = new Button(comp, SWT.DOWN);
		deleteButton.setForeground(SWTResourceManager.getColor(255, 0, 250));
		deleteButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				stationName.setText("");
				downAvailLaneNum.setText("");
				upAvailLaneNum.setText("");
				//memo.setText("");
				stationLayout.select(0);
			}
		});
		deleteButton.setText("复位");
	  }
	
	private void showMsg(String str){
		//Toolkit.getDefaultToolkit().beep();//报警
		//SWT.ICON_ERROR SWT.ICON_INFORMATION SWT.ICON_QUESTION SWT.ICON_WARNING  SWT.ICON_WORKING
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
	
	private boolean insertStationInfo(String stationName,String downAvailLaneNum,String upAvailLaneNum,
									  String stationLayout/*,String memo*/){
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLINSERT);
		msg.setDataBean("Station");
		
		msg.setSql("insert into Station(Station_name,Station_downnumber,Station_upnumber,Station_graph)" +
					" Values(?,?,?,?)");
		if(downAvailLaneNum.length() == 0)
			downAvailLaneNum = "2";
		int down = Integer.parseInt(downAvailLaneNum);
		if(upAvailLaneNum.length() == 0)
			upAvailLaneNum = "2";
		int up = Integer.parseInt(upAvailLaneNum);
		
		Object[] params = new Object[]{stationName,down,up,stationLayout};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);
		
		String list = synClientSupport.sqlMessageSend(msg);//演示同步通信
		
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}
	
	private void pushCommand(){
		TrainData.initial();
		DistrictData.initial();
		StationData.reloadFlag = true;
		DistrictStationData.initial();
	}

}
