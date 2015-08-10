package ctc.ui.teacher.district;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;
import ctc.constant.Constants;
import ctc.pojobean.Station;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.ui.teacher.districtstation.DistrictStationData;
import ctc.ui.teacher.districttrainrelation.DistrictTrainData;
import ctc.ui.teacher.experiment.ExperimentData;
import ctc.ui.teacher.trainPlan.TrainPlanData;
import ctc.util.JsonUtil;


public class DistrictCreateLayout{

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
    
	DistrictData districtData = new DistrictData();
	
	public DistrictCreateLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout( new MigLayout());  
		createGroup(comp, "定义区段基本信息","pos 0.5al 0.2al",null);
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
		
		getStationInfo();//从服务器获取车站信息
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		comp.setLayout(gridLayout);
		
		final Label label = new Label(comp, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label.setLayoutData(data);
		label.setText("区段起始站:");
		final Combo stationStart = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		stationStart.removeAll(); //先清空stationLayout
		stationStart.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (districtData.startFlag){
						districtData.reloadFlag = true;
					}
					if (! districtData.reloadFlag)
						return;
					
					getStationInfo();//从服务器获取车站信息
					
					if(districtData.stationName != null){
						stationStart.setItems(districtData.stationName);
						
						districtData.reloadFlag = false;
						districtData.startFlag = false;
					}
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		/*if(districtData.stationName != null)
			stationStart.setItems (districtData.stationName);
		if(districtData.stationMap != null)
			stationStart.setData(districtData.stationMap);
		stationStart.select(0);
		//stationLayout_1.setBounds(28, 34, 102, 20);
		 */
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    stationStart.setLayoutData(data);
	    
	    final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("区段终止站:");
		final Combo stationEnd = new Combo(comp, SWT.READ_ONLY);//SWT.DROP_DOWN 用户可以输入内容
		stationEnd.removeAll();
		stationEnd.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (districtData.endFlag){
						districtData.reloadFlag = true;
					}
					if (! districtData.reloadFlag)
						return;
					
					getStationInfo();//从服务器获取车站信息
					
					if(districtData.stationName != null){
						stationEnd.setItems(districtData.stationName);
						
						districtData.reloadFlag = false;
						districtData.endFlag = false;
					}
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		/*if(districtData.stationName != null)
			stationEnd.setItems (districtData.stationName);
		if(districtData.stationMap != null)
			stationEnd.setData(districtData.stationMap);
		stationEnd.select(0);
		*/
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    stationEnd.setLayoutData(data);
	    
		final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("区段内车站数量:");
		final Text stationNum = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    stationNum.setLayoutData(data);
	    stationNum.addVerifyListener(listener);//检查监听器,每输入一个字符都回触发
	    stationNum.setText("10");//默认值
	    stationNum.setTextLimit(3);  //最都只能输入3位数字
	    
	    final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("区段所属铁路局:");
		final Combo stationBureau = new Combo(comp,SWT.DROP_DOWN);
		stationBureau.removeAll(); //先清空stationLayout
		stationBureau.setItems (districtData.bureau);
		stationBureau.select(0); //设置要显示的第一项
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 132;
	    stationBureau.setLayoutData(data);
	    
		Button OKButton = new Button(comp, SWT.PUSH);//SWT.DOWN
		OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
		OKButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		OKButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
				if ( (stationNum.getText().length() == 0)||
					 (stationStart.getText().length() == 0)||
					 (stationEnd.getText().length() == 0)||
					 (stationBureau.getText().length() == 0))
				{
					showMsg("请输入或选取所有项的值!");
					return;
				}
				
				if ( stationStart.getText().equalsIgnoreCase(stationEnd.getText()))
				{
					showMsg("起始站与终止站不能相同!");
					return;
				}
					
                String districtName = stationStart.getText() + "-" + stationEnd.getText();
                int number = Integer.parseInt(stationNum.getText());

                if (insertDistrictInfo(stationStart.getText(),stationEnd.getText(),number,stationBureau.getText(),districtName)){

					pushCommand();
					
					showMsg("操作成功!");//消息框的提示文字
				}
				else
					showMsg("操作失败(该区段信息已存在)!");//消息框的提示文字
			 }
		   });
		OKButton.setText("保存");
		
		Button deleteButton = new Button(comp, SWT.DOWN);
		deleteButton.setForeground(SWTResourceManager.getColor(255, 0, 250));
		deleteButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				stationNum.setText("10");
				stationBureau.select(0);
			}
		});
		deleteButton.setText("复位");
	  }

	VerifyListener listener = new VerifyListener() {
		//doit属性如果为true,则字符允许输入,反之不允许
		public void verifyText(VerifyEvent e) {   
			//输入控制键，输入中文，输入字符，输入数字 正整数验证   
			Pattern pattern = Pattern.compile("[0-9]\\d*"); //正则表达式  
			Matcher matcher = pattern.matcher(e.text);   
			if (matcher.matches()) //处理数字   
			{
				e.doit = true;
			}
			else if (e.text.length() > 0) //字符: 包含中文、空格   
				e.doit = false;
			else//控制键  
				e.doit = true;   
		}   
	};
	
	private void showMsg(String str){
		//Toolkit.getDefaultToolkit().beep();//报警
		//SWT.ICON_ERROR SWT.ICON_INFORMATION SWT.ICON_QUESTION SWT.ICON_WARNING  SWT.ICON_WORKING
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");//消息框的标题
		mb.setMessage(str);//消息框的提示文字
		mb.open();
	}
	

	private void getStationInfo() {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Station");

		//不带参数的sql语句的使用方法
		msg.setSql("select * from Station ");// limit 16
		String paprams = "null";//
		msg.setParams(paprams);//转换为json字符串进行传递

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return;		
		}else{
			List<Station> list = JsonUtil.getList4Json(listString,Station.class);
			districtData.setStationList(list);
		}
	}

	private boolean insertDistrictInfo(String stationStartID, String stationEndID,int number,String bureauName,String districtName){
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLINSERT);
		msg.setDataBean("District");

		msg.setSql("insert into District(District_startstationname,District_endstationname,District_stationnumber," +
				"District_railwaybureau,District_name) Values(?,?,?,?,?)");

		Object[] params = new Object[]{stationStartID,stationEndID,number,bureauName,districtName};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String list = synClientSupport.sqlMessageSend(msg);

		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}
	private void pushCommand(){
		DistrictStationData.initial();
		DistrictTrainData.initial();
		DistrictData.initial();
		ExperimentData.initial();
		TrainPlanData.initial();
	}
}
