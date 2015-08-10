package ctc.ui.teacher;

import org.eclipse.swt.widgets.Shell;

import ctc.data.ConfigureFile;
import ctc.data.LoginEntry;
import ctc.ui.CTCTeacherMain;

public class DisplayLoginEntryDialog {
	
	public DisplayLoginEntryDialog() { }	    
	
	public boolean createContents(Shell shell) {
		
		ConfigureFile configureFile = CTCTeacherMain.getConfigureFile();
		configureFile.init();//获取配置文件中的相关信息
		
	   	LoginEntryDialog dlg = new LoginEntryDialog(shell);
	   	LoginEntry entry = dlg.open();
	    if (entry == null){//退出系统
			return false;
	    }
	  
	    //对输入密码进行判断????
		configureFile.save(entry);//保存用户新输入的参数
		 
		
		return true;
	}

	
}
