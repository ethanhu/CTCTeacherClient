package ctc.ui.teacher.district;

import java.util.List;
import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;
import ctc.constant.Constants;
import ctc.pojobean.*;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.util.JsonUtil;


public class DistrictSearchLayout {

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;

	DistrictData districtData = new DistrictData();
	
	public DistrictSearchLayout(){
	}
	
	public DistrictSearchLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}

	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"查询区段信息","pos 0.5al 0.2al",null);
		return  comp;
	}

	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}
	
	private void createComp(Composite group) {
	
		getStationInfo();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		group.setLayout(gridLayout);
		
		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label.setLayoutData(data);
		label.setText("选取区段起始站:");
		final Combo stationStart = new Combo(group,SWT.READ_ONLY);
		stationStart.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (districtData.searchStartFlag){
						districtData.reloadFlag = true;
					}
					if (! districtData.reloadFlag)
						return;
					
					getStationInfo();//从服务器获取车站信息
		
					stationStart.setItems(districtData.stationName);
					districtData.reloadFlag = false;
					districtData.searchStartFlag = false;
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		/*stationStart.removeAll();
		if(districtData.stationName != null)
			stationStart.setItems (districtData.stationName);
		if(districtData.stationMap != null)
			stationStart.setData(districtData.stationMap);
		stationStart.select(0);
		*/
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    stationStart.setLayoutData(data);
	    
	    final Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		label_1.setText("选取区段结束站:");
		final Combo stationEnd = new Combo(group, SWT.READ_ONLY);
		stationEnd.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (districtData.searchEndFlag){
						districtData.reloadFlag = true;
					}
					if (! districtData.reloadFlag)
						return;
					
					getStationInfo();//从服务器获取车站信息
					
					stationEnd.setItems(districtData.stationName);
					districtData.reloadFlag = false;
					districtData.searchEndFlag = false;
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		/*
		stationEnd.removeAll();
		if(districtData.stationName != null)
			stationEnd.setItems (districtData.stationName);
		if(districtData.stationMap != null)
			stationEnd.setData(districtData.stationMap);
		stationEnd.select(0);
		*/
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    stationEnd.setLayoutData(data);
		
	    Button OKButton = new Button(group, SWT.PUSH);//SWT.DOWN
	    OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
	    OKButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    OKButton.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(final SelectionEvent e) {
	    		
	    		if ( (stationStart.getText().length() == 0)||
					 (stationEnd.getText().length() == 0)){
						showMsg("请选取查询条件的值!");
						return;
					}
	    		String districtName = stationStart.getText() + "-" + stationEnd.getText();

				if (getDistrictInfo(districtName)){
					
					DistrictEditDialog form = new DistrictEditDialog(DistrictSearchLayout.this.shell);
					form.show();
				}
				else
					showMsg("该区段信息不存在!");//消息框的提示文字
	    	    }
	    });
	    OKButton.setText("查询");

	}
	
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");
		mb.setMessage(str);
		mb.open();
	}
	
	
	public void getStationInfo() {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Station");
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
	//从服务器获取District信息
	private boolean getDistrictInfo(String districtName) {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("District");

		msg.setSql("select * from District where District_name=?");
		Object[] params = new Object[]{districtName};
	
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return false;		
		}else{
			List<District> list = JsonUtil.getList4Json(listString,District.class);
			if(list.size() <= 0)
				return false;
			else{
				this.setData(list);//结果集中只有1项
				return true;	
			}
		}
	}
	

	private void setData(List<District> list){
		data = new String[5];
		District district = (District)list.get(0);
		data[0] = district.getDistrict_name();
		data[1]= district.getDistrict_startstationname();
		data[2] = district.getDistrict_endstationname();
		data[3] = String.valueOf(district.getDistrict_stationnumber());
		data[4] = district.getDistrict_railwaybureau();
	}
	private static String[]data;
	public String[] getData(){
		return data;
	}
	//向服务器发送更新报文
	public static boolean updateDistrictInfo(String stationStartName,String stationEndName,int number,
				String stationBureau,String districtName)
	{
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLUPDATE);
		msg.setDataBean("District");
		msg.setSql("update District set District_startstationname=?,District_endstationname=? ," +
				   "District_stationnumber=?,District_railwaybureau=? where District_name=?");
		
		Object[] params = new Object[]{stationStartName,stationEndName,number,stationBureau,districtName};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);
		
		String list = synClientSupport.sqlMessageSend(msg);//同步通信
		if(list == null){
			return false;		
		}else{
			return true;	 
		}
	}

}


