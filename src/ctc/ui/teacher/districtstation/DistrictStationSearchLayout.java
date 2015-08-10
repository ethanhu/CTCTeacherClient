package ctc.ui.teacher.districtstation;


import java.util.HashMap;
import java.util.List;
import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import ctc.constant.Constants;
import ctc.pojobean.*;
import ctc.transport.*;
import ctc.transport.message.*;
import ctc.util.*;


public class DistrictStationSearchLayout {

	private static SynClientSupport synClientSupport;
	private Shell shell;
	DistrictStationData districtStationData = new DistrictStationData();

	public DistrictStationSearchLayout(){}
	public DistrictStationSearchLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();
		this.shell = shell;
	}

	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"查询区段内车站信息","pos 0.5al 0.2al",null);
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
		
		//getDistrictInfo();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		group.setLayout(gridLayout);
		
		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label.setLayoutData(data);
		label.setText("选取区段:");
		final Combo districtStation = new Combo(group,SWT.READ_ONLY);
		districtStation.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
					if (districtStationData.searchFlag){
						districtStationData.reloadFlag = true;
					}
					if (! districtStationData.reloadFlag)
						return;
					
					getDistrictInfo();//从服务器获取车站信息
					
					districtStation.setItems(districtStationData.districtName);
					districtStationData.reloadFlag = false;
					districtStationData.searchFlag = false;
				}
				@Override
				public void focusLost(FocusEvent e) {
				}
		    });
		
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 80;
	    districtStation.setLayoutData(data);
	  
	    new Label(group, SWT.NONE);
	    Button OKButton = new Button(group, SWT.PUSH);//SWT.DOWN
	    OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
	    OKButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	    OKButton.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(final SelectionEvent e) {
	    		if (districtStation.getText().length() == 0){
						showMsg("请选取查询条件!");
						return;
				}
					
	    		String selText = districtStation.getText();
	    	
				if (getDistrictStationInfo(selText)){
					new DistrictStationBrowserLayout(DistrictStationSearchLayout.this.shell,
													 DistrictStationSearchLayout.this.synClientSupport,
													 selText).show();
				}
				else
					showMsg("该区段内还没有定义车站!");
	    	}
	    });
	    OKButton.setText("查询");

	}
	private void showMsg(String str){
		//Toolkit.getDefaultToolkit().beep();//报警
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");
		mb.setMessage(str);
		mb.open();
	}
	
	public void getDistrictInfo() {
		
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("District");
		msg.setSql("select * from District order by District_name");
		String paprams = "null";//
		msg.setParams(paprams);//转换为json字符串进行传递

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return;		
		}else{
			List<District> list = JsonUtil.getList4Json(listString,District.class);
			districtStationData.setDistrictList(list);
		}
	}

	//从服务器获取District信息
	private boolean getDistrictStationInfo(String districtStationID) {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		
		msg.setDataBean("StationDistrictRelation");

		msg.setSql("select * from StationDistrictRelation where District_name=?");
		Object[] params = new Object[]{districtStationID};
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		//System.out.println("listString:"+listString);
		
		if(listString == null){//此情况不会出现
			return false;		
		}else{
			List<StationDistrictRelation> list = JsonUtil.getList4Json(listString,StationDistrictRelation.class);
			if(list.size() <= 0)
				return false;
			else{
				districtStationData.setData(list);
				return true;
			}
		}
	}

}


