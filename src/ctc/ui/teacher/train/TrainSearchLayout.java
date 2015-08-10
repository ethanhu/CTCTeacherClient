package ctc.ui.teacher.train;

import java.util.List;
import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
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


public class TrainSearchLayout {

	private static SynClientSupport synClientSupport = new SynClientSupport();
	private static Shell shell;

	TrainData trainData = new TrainData();
	
	public TrainSearchLayout(){
	}
	
	public TrainSearchLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}

	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"查询列车信息","pos 0.5al 0.2al",null);
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
		
		getStationInfoList();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;//列数目
		group.setLayout(gridLayout);
		
		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
	    GridData data = new GridData(SWT.RIGHT,SWT.CENTER, true, false);
	    label.setLayoutData(data);
		label.setText("列车名称:");
		
		final Text text = new Text(group,SWT.BORDER  | SWT.SINGLE);
		data = new GridData(SWT.LEFT,SWT.CENTER, true, false);
	    data.heightHint = 15;
	    data.widthHint = 128;
	    text.setLayoutData(data);
	    
	    
	    Button OKButton = new Button(group, SWT.PUSH);//SWT.DOWN
	    OKButton.setForeground(SWTResourceManager.getColor(255, 250, 0));
	    OKButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    OKButton.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(final SelectionEvent e) {
	    		String selText = text.getText();
				if(selText.length() == 0){
					showMsg("请输入列车名称!");
					return;
				}else{
					if(getTrainInfo(selText))
					{
						TrainEditDialog form = new TrainEditDialog(TrainSearchLayout.this.shell);
						form.show();
					}else{
						showMsg("该列车不存在!");	
						return;
					}
				}
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
	
	public void getStationInfoList() {
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
			trainData.setStationList(list);
		}
	}

	//从服务器获取Train信息
	private boolean getTrainInfo(String districtName) {

		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLQUERY);
		msg.setDataBean("Train");

		msg.setSql("select * from Train where Train_name=?");
		Object[] params = new Object[]{districtName};
	
		String paprams = JsonUtil.array2json(params);
		msg.setParams(paprams);

		String listString = synClientSupport.sqlMessageSend(msg);//同步通信
		if(listString == null){//此情况不会出现
			return false;		
		}else{
			List<Train> list = JsonUtil.getList4Json(listString,Train.class);
			if(list.size() <= 0)
				return false;
			else{
				this.setData(list);//结果集中只有1项
				return true;	
			}
		}
	}
	

	private void setData(List<Train> list){
		data = new String[5];
		Train train = (Train)list.get(0);
		data[0] = train.getTrain_name();
		data[1]= train.getTrain_startstationname();
		data[2] = train.getTrain_endstationname();
		data[3] = String.valueOf(train.getTrain_maxspeed());
		data[4] = trainData.direct[train.getTrain_direction()];
		//data[6] = String.valueOf(train.getTecher_id());
	}
	
	private static String[]data;
	public String[] getData(){
		return data;
	}
	//向服务器发送更新报文
	public static boolean updateTrainInfo(String trainStartID,String trainEndID,int speed,
						   int direct,String name,String id)
	{
       
		SQLRequestMessage msg = new SQLRequestMessage();
		msg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		msg.setCommandType(Constants.TYPE_CLIENT_SQLUPDATE);
		msg.setDataBean("Train");
		msg.setSql("update Train set Train_startstationname=?,Train_endstationname=? ," +
				   "Train_maxspeed=?,Train_direction=?,Train_name=? where Train_name =?");
		
		Object[] params = new Object[]{trainStartID,trainEndID,speed,direct,name,id};
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


