package ctc.ui.teacher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import ctc.*;
import ctc.data.*;
import ctc.ui.CTCTeacherMain;


/**
 * This class is the dialog to input a new param
 */
public class LoginEntryDialog extends Dialog {
  private LoginEntry entry;

  /**
   * Constructs a EntryDialog
   * 
   * @param shell the parent shell
   */
  public LoginEntryDialog(Shell shell) {
	  super(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
  }
 
  /**
   * Opens the dialog
   * 
   * @return PasswordEntry
   */
  public LoginEntry open() {
	Display display = getParent().getDisplay();
	
    // Create the dialog window
    Shell shell = new Shell(getParent(), getStyle());
    shell.setMinimumSize(new Point(200, 200));
    
      
    shell.setText("登录");
    createContents(shell);
    shell.pack();
    
   //使对话框窗口处于屏幕中间
	Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
    Rectangle shellBounds = shell.getBounds();// 获取屏幕高度和宽度
    int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
    int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
    shell.setLocation(x, y);//定位窗口坐标

    shell.open();
    
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    return entry;
  }

  /**
   * Creates the window's contents
   * 
   * @param shell the parent shell
   */
  private void createContents(final Shell shell) {
	  
	//建立一个默认的GridLayout布局
	GridLayout gridLayout = new GridLayout();
	gridLayout.marginRight = 60;
	gridLayout.numColumns = 2;//列数目
	gridLayout.makeColumnsEqualWidth = true; //强制全部的列拥有相同的宽度
//	gridLayout.horizontalSpacing =1;//控制一行中两个网格间组件的宽度,像素为单位
	//gridLayout.verticalSpacing = 15;//一列中两个网络间组件的宽度,像素为单位
	gridLayout.marginHeight = 30;//控制顶部和底部组件离边缘的距离空间,以像素为单位.
	//gridLayout.marginWidth=20;//  	控制左边和右边组件离边缘的距离空间,以像素为单位.
	
	 //为Shell设置布局对象
    shell.setLayout(gridLayout);

    //为shell生成一个背景色
    //final Color bkColor = new Color(Display.getCurrent(),200,110,100);
  //  shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
  
    
    Label label = new Label(shell, SWT.NONE);
    //创建默认GridData对象.
    GridData data = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
    label.setLayoutData(data);
    label.setText("CTC服务器IP:");
    
    final Text ctcServerIP = new Text(shell, SWT.BORDER);
    ctcServerIP.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    String word = CTCTeacherMain.getConfigureFile().getCtcServerIP();
    if(word ==null)
    	word = "127.0.0.1";
    ctcServerIP.setText(word);
    
    
    
    label = new Label(shell, SWT.NONE);
    //创建默认GridData对象.
    data = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
    label.setLayoutData(data);
    label.setText("端口号:");
    
    final Text ctcServerPort = new Text(shell, SWT.BORDER);
    ctcServerPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    word = CTCTeacherMain.getConfigureFile().getCtcServerPort();
    if(word ==null)
    	word = "9999";
    ctcServerPort.setText(word);
    
    
    
    label = new Label(shell, SWT.NONE);
    //创建默认GridData对象.
    data = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
    label.setLayoutData(data);
    label.setText("用户名:");
    
    final Text userName = new Text(shell, SWT.BORDER);
    userName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    
    
    label = new Label(shell, SWT.NONE);
    //创建默认GridData对象.
    data = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
    label.setLayoutData(data);
    label.setText("密码:");
    final Text password = new Text(shell, SWT.BORDER);
    
    password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    // Create the OK button
    final Button ok = new Button(shell, SWT.RIGHT);
    ok.setAlignment(SWT.RIGHT);
    ok.setText("登录");
    ok.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    ok.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        entry = new LoginEntry(userName.getText(), password.getText(),ctcServerIP.getText(),ctcServerPort.getText());
       // bkColor.dispose();
        shell.close();
      }
    });

    // Create the Cancel button
    final Button cancel = new Button(shell, SWT.PUSH);
    cancel.setAlignment(SWT.RIGHT);
    cancel.setText("取消");
    cancel.setLayoutData(new GridData());
    
  //当主窗口关闭时，会触发DisposeListener。这里用来释放Panel的背景色。
    shell.addDisposeListener(new DisposeListener(){
        public void widgetDisposed(DisposeEvent e) {
           // bkColor.dispose();
            shell.close();
        }
    });

    cancel.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        entry = null;
      //  bkColor.dispose();
        shell.close();
      }
    });

  
    // Allow user to press Enter to accept and close
    shell.setDefaultButton(ok);
  }

  /**
   * Gets the entry
   * 
   * @return PasswordEntry
   */
  public LoginEntry getEntry() {
    return entry;
  }

  /**
   * Sets the entry
   * 
   * @param entry The entry to set.
   */
  public void setEntry(LoginEntry entry) {
    this.entry = entry;
  }
}
