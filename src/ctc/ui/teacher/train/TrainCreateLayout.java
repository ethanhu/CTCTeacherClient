package ctc.ui.teacher.train;

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
import ctc.ui.teacher.districttrainrelation.DistrictTrainData;
import ctc.ui.teacher.trainPlan.TrainPlanData;
import ctc.util.JsonUtil;


public class TrainCreateLayout{

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;
    
	TrainData trainData = new TrainData();
	
	public TrainCreateLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout( new MigLayout());  
		createGroup(comp, "定义列车基本信息","pos 0.5al 0.2al",null);
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
		
		//getStationInfoMap();//从服务器获取车站信息
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		comp.setLayout(gridLayout);
		
		final Label label_4 = new Label(comp, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_4.setText("列车名称:");
		final Text name = new Text(comp, SWT.BORDER);
		GridData data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    name.setLayoutData(data);
	    
		final Label label = new Label(comp, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label.setLayoutData(data);
		label.setText("始发站:");
		final Combo trainStart = new Combo(comp,SWT.READ_ONLY);//定义一个只读的下拉框 //SWT.SIMPLE（无须单击下拉框，列表一直显示）
		trainStart.removeAll(); //先清空trainLayout
		//用于保证能实时更新要显示的内容
		trainStart.addFocusListener(new FocusListener(){
		public void focusGained(FocusEvent e) {
				if (trainData.startFlag){
					trainData.reloadFlag = true;
				}
				if (! trainData.reloadFlag)
					return;
				
				getStationInfoList();//从服务器获取车站信息
				
				if (trainData.stationName != null){
					trainStart.setItems (trainData.stationName);
					trainData.reloadFlag = false;
					trainData.startFlag = false;
				}
				/*for(int i=0; i<trainData.trainName.length; i++)
					trainStart.add(trainData.trainName[i]);
				*/
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
	    });
		/*if(trainData.trainName != null)
			trainStart.setItems (trainData.trainName);
		if(trainData.trainMap != null)
			trainStart.setData(trainData.trainMap);
		trainStart.select(0);*/
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    trainStart.setLayoutData(data);
	    
	    final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("到达站:");
		final Combo trainEnd = new Combo(comp, SWT.READ_ONLY);//SWT.DROP_DOWN 用户可以输入内容
		trainEnd.removeAll();
		trainEnd.addFocusListener(new FocusListener(){
		public void focusGained(FocusEvent e) {
			if (trainData.endFlag){
				trainData.reloadFlag = true;
			}
			if (! trainData.reloadFlag)
				return;
			
			getStationInfoList();//从服务器获取车站信息
			
			if (trainData.stationName != null){
				trainEnd.setItems (trainData.stationName);
				trainData.reloadFlag = false;
				trainData.endFlag = false;
			}
     	 }
			@Override
		public void focusLost(FocusEvent e) {
			}
		});
		
		/*if(trainData.trainName != null)
			trainEnd.setItems (trainData.trainName);
		if(trainData.trainMap != null)
			trainEnd.setData(trainData.trainMap);
		trainEnd.select(0);*/
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    trainEnd.setLayoutData(data);
	    
		final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_2.setText("最大车速:");
		final Text trainSpeed = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    trainSpeed.setLayoutData(data);
	    trainSpeed.addVerifyListener(listener);//检查监听器,每输入一个字符都回触发
	    trainSpeed.setText("10");//默认值
	    trainSpeed.setTextLimit(3);  //最都只能输入3位数字
	    
	    final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_3.setText("列车方向:");
		final Combo trainDirect = new Combo(comp,SWT.DROP_DOWN);
		trainDirect.removeAll(); //先清空trainLayout
		trainDirect.setItems (trainData.direct);
		trainDirect.select(0); //设置要显示的第一项
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 132;
	    trainDirect.setLayoutData(data);
	    
	    ///暂不处理????????????????????????????????
	   /* final Label label_5 = new Label(comp, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_5.setText("教师编号:");
		final Text teacherID = new Text(comp, SWT.BORDER);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 150;
	    teacherID.setLayoutData(data);
	    teacherID.setText("目前没有处理");
	    */
	    
		Button OKButton = new Button(comp, SWT.PUSH);//SWT.DOWN
		OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
		OKButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		OKButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
				if ( (name.getText().length() == 0)||
					 (trainStart.getText().length() == 0)||
					 (trainEnd.getText().length() == 0)||
					 (trainSpeed.getText().length() == 0))
				{
					showMsg("请输入或选取所有选项的值!");
					return;
				}
				if (trainStart.getText().equalsIgnoreCase(trainEnd.getText())){
					showMsg("车次的始发站与终点站不能相同!");
					return;
				}
				
				String selText = trainStart.getText();
                String trainStartID = selText;
                
                selText = trainEnd.getText();
                String trainEndID = selText;
                
                int speed = Integer.parseInt(trainSpeed.getText());
                int director = getDirectIndex(trainDirect.getText());
                
              if (insertTrainInfo(name.getText(),trainStartID,trainEndID,speed,director)){
            	  TrainData.reloadFlag = true;{
            		  pushCommand();
            		  showMsg("操作成功!");
            	  }
              }
				else
					showMsg("操作失败(该车次信息已存在)!");
			 }
		   });
		OKButton.setText("保存");
		
		Button deleteButton = new Button(comp, SWT.DOWN);
		deleteButton.setForeground(SWTResourceManager.getColor(255, 0, 250));
		deleteButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				name.setText("");
				//trainStart.select(0);
				//trainEnd.select(0);
				trainSpeed.setText("200");
				trainDirect.select(0);
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
	
	private int getDirectIndex(String name) {
  	  for (int i = 0; i < trainData.direct.length; i++) {
  		  if (TrainData.direct[i].equals(name))
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
	
   //从服务器获取车站信息
	private void getStationInfoList() {
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Station");

		msg.setSql("select * from Station");// limit 16 order by Station_id
		String paprams = "null";//
		msg.setParams(paprams);//转换为json字符串进行传递

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return;		
		}else{
			List<Station> list = JsonUtil.getList4Json(listString,Station.class);
			trainData.setStationList(list);
		}
	}

	private boolean insertTrainInfo(String name,String trainStartID,String trainEndID,int speed,int director){
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLINSERT);
		msg.setDataBean("Train");

		msg.setSql("insert into Train(Train_name,Train_startstationname,Train_endstationname," +
				"Train_maxspeed,Train_direction) Values(?,?,?,?,?)");

		Object[] params = new Object[]{name,trainStartID,trainEndID,speed,director};
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
		DistrictTrainData.initial();
		TrainPlanData.initial();
	}

}
