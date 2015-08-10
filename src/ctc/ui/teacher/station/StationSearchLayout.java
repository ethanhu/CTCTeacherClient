package ctc.ui.teacher.station;

import java.util.List;
import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.swtdesigner.SWTResourceManager;
import ctc.constant.Constants;
import ctc.pojobean.*;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.SQLRequestMessage;
import ctc.util.JsonUtil;


public class StationSearchLayout {

	private static SynClientSupport synClientSupport = new SynClientSupport();;
	private static Shell shell;
	private Text text;

	public StationSearchLayout(){
		
	}
	
	public StationSearchLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}

	public Composite create(Composite parentComp) {

		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"查询车站基本信息","pos 0.5al 0.2al",null);
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
		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				String stationName = StationSearchLayout.this.text.getText(); 
				if(stationName.length() == 0){
					showMsg("请输入要查询的车站名称!");
					return;
				}else{
					if(getStationInfo(stationName))
					{
						StationEditDialog form = new StationEditDialog(StationSearchLayout.this.shell);
						form.show();
					}else{
						showMsg("该车站信息不存在!");	
						return;
					}
				}
			}
		};

		GridLayout layout = new GridLayout(2, false);//1
		layout.verticalSpacing = 10;
		group.setLayout(layout);

		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
		label.setLayoutData(data);
		label.setText("车站名称:");

		text = new Text(group,SWT.BORDER  | SWT.SINGLE);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.heightHint = 15;
		data.widthHint = 150;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);

		Composite locationComp = new Composite(group,SWT.FLAT );
		locationComp.setBounds(20, 20, 300, 200);
		locationComp.setLayout(new RowLayout( ));

		ToolBar toolBar = new ToolBar(group, SWT.FLAT);
		ToolItem okbutton = new ToolItem(toolBar, SWT.PUSH);
		okbutton.setText("查询");
		okbutton.addListener(SWT.Selection, listener);
	}
	
	private void showMsg(String str){
		MessageBox mb = new MessageBox(shell, SWT.ABORT |  SWT.ICON_INFORMATION);
		mb.setText("提示信息");
		mb.setMessage(str);
		mb.open();
	}

	//从服务器获取信息.并写入StationData类的String[][] data中
	private boolean getStationInfo(String stationName) {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Station");

		msg.setSql("select * from Station where Station_name=?");
		Object[] params = new Object[]{stationName};
	
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return false;		
		}else{
			List<Station> list = JsonUtil.getList4Json(listString,Station.class);
			//System.out.println("list:" + list.size());
			if(list.size() <= 0)
				return false;
			else{
				this.setData(list);//结果集中只有1项
				return true;	
			}
		}
	}
	private void setData(List<Station> list){
		data = new String[4];//因为Station有4个字段
		Station station = (Station)list.get(0);
		data[0] = station.getStation_name();
		data[1] = String.valueOf(station.getStation_downnumber());
		data[2] = String.valueOf(station.getStation_upnumber());
		data[3] = station.getStation_graph();
	}
	private static String[]data;
	public String[] getData(){
		return data;
	}

     ////////////////////////////////
	//依据原车站名称进行更新
	//向服务器发送更新报文 供StationEditDialog调用
	public static boolean updateStationInfo(String stationName,String downAvailLaneNum,
						  String upAvailLaneNum, String stationLayout,String id)
	{
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLUPDATE);
		msg.setDataBean("Station");
		msg.setSql("update Station set Station_name=?,Station_downnumber=? ," +
				   "Station_upnumber=?,Station_graph=? where Station_name=?");
		if(downAvailLaneNum.length() == 0)
			downAvailLaneNum = "2";
		int down = Integer.parseInt(downAvailLaneNum);
		if(upAvailLaneNum.length() == 0)
			upAvailLaneNum = "2";
		int up = Integer.parseInt(upAvailLaneNum);
		
		Object[] params = new Object[]{stationName,down,up,stationLayout,id};
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


