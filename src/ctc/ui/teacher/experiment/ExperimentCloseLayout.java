package ctc.ui.teacher.experiment;

import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;
import ctc.transport.MinaClient;
import ctc.transport.SynClientSupport;
import ctc.transport.message.*;
import ctc.constant.Constants;

public class ExperimentCloseLayout{

	private static SynClientSupport synClientSupport;
	private static Shell shell;
	
	public ExperimentCloseLayout(Shell shell, MinaClient minaClient){
		synClientSupport = minaClient.getSynClientSupport();//同步通信
		this.shell = shell;
	}
	
	public Composite create(Composite parentComp) {
		Composite comp = new Composite(parentComp, SWT.EMBEDDED);
		comp.setLayout( new MigLayout());
		createGroup(comp,"关闭实验","pos 0.5al 0.2al",null);
		return  comp;
	}
	
	private void createGroup(Composite parent, String title, String position, Object layout)
	{
		Group comp = new Group(parent, SWT.PUSH | SWT.NO_BACKGROUND |SWT.SHADOW_OUT);
		comp.setText(title.length() == 0 ? "\"\"" : title);
		comp.setLayoutData(layout != null ? layout : position);
		createComp(comp);
	}
	
	private void createComp(Composite comp) {
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;//列数目
		comp.setLayout(gridLayout);

		new Label(comp, SWT.NONE);
		Text text = new Text(comp, SWT.READ_ONLY);
		text.setText("此操作将导致所有学生的客户端退出运行！");
		
		new Label(comp, SWT.NONE);
		Button closeButton = new Button(comp, SWT.DOWN);
		closeButton.setForeground(SWTResourceManager.getColor(255, 0, 250));
		closeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(! closeExperiment())
					showMsg("操作失败！");
				else{
					showMsg("操作成功！");
				}
			}
		});
		closeButton.setText("关闭");
	}
	
	private void showMsg(String str){
		//Toolkit.getDefaultToolkit().beep();//报警
		MessageBox mb = new MessageBox(shell, SWT.ABORT | SWT.ICON_INFORMATION);
		mb.setText("提示信息");
		mb.setMessage(str);
		mb.open();
	}
	
	private boolean closeExperiment(){

		ExperimentCommandMessage sMsg = new ExperimentCommandMessage();

		sMsg.setCommandMode(Constants.MODE_CS_SYN_CLIENT);
		sMsg.setUserRole(Constants.USER_ROLE_TEACHER);//设置用户角色
		sMsg.setCommandType(Constants.TYPE_CLIENT_EXPERIMENT_CLOSE);
		
		return synClientSupport.ExperimentCommandMessageSend(sMsg);//同步通信

	}
}
