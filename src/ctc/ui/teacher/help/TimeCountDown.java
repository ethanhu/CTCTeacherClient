package ctc.ui.teacher.help;

//此类主要演示程序如何运行在一个非用户UI线程中

//方法syncExec()和asyncExec()的区别在于前者要在指定的线程执行结束后才返回，而后者则无论指定的线程是否执行都会立即返回到当前线程。
import org.eclipse.swt.SWT;  
import org.eclipse.swt.graphics.Rectangle;  
import org.eclipse.swt.layout.GridData;  
import org.eclipse.swt.layout.GridLayout;  
import org.eclipse.swt.widgets.Composite;  
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;  
import org.eclipse.swt.widgets.Label;  
import org.eclipse.swt.widgets.Shell;  
  
public class TimeCountDown extends Dialog implements Runnable{  

	private static final int CountDownTime = 30; //倒計時，30秒  
  
    protected Shell shell;  
    private Thread clocker;  
    private Label countLabel;  
  
    private static Display display;
    
    public TimeCountDown(Shell parent){
    	super(parent, SWT.NONE);
    }
    
    public void open() {  
    	display = getParent().getDisplay();
        createContents();  
        centerShell(display, shell);    //讓窗口居中顯示  
        shell.open();  
        shell.layout();  
        
        clocker = new Thread(this);  
        clocker.start();  
        
        while (!shell.isDisposed()) {  
            if (!display.readAndDispatch())  
                display.sleep();  
        }  
    }  
  
  
    protected void createContents() {  
        shell = new Shell(getParent(), SWT.NONE | SWT.APPLICATION_MODAL);  
        shell.setLayout(new GridLayout());  
        shell.setSize(120, 40);  
    
        Composite panel = new Composite(shell, SWT.NONE);  
        panel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));  
        final GridLayout gridLayout = new GridLayout();  
        gridLayout.numColumns = 3;  
        panel.setLayout(gridLayout);  
          
        final Label label = new Label(panel, SWT.NONE);  
        label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));  
        label.setText("倒计时：");  
  
        countLabel = new Label(panel, SWT.NONE);  
        countLabel.setText("30");  
  
        final Label label_2 = new Label(panel, SWT.NONE);  
        label_2.setText("秒");  
          
          
    }  
  
    public void run() {  
        int i = CountDownTime;  //設置倒計時時間  
        while(i > 0) {  
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            i--;  
            final int temp = i;  
            //在其他線程中調用UI線程（即修改界面元素），需要使用Display.getDefault().asyncExec()方法  
            Display.getDefault().asyncExec(new Runnable() {  
                public void run() {  
                    countLabel.setText(temp + "");  
                }  
            });  
        }  
          
        Display.getDefault().asyncExec(new Runnable() {  
            public void run() {  
                shell.dispose();//倒計時完成，退出窗口。  
            }  
        });  
    }  
      
    //居中顯示shell  
    private void centerShell(Display display, Shell shell) {  
        Rectangle displayBounds = display.getPrimaryMonitor().getBounds();  
        Rectangle shellBounds =shell.getBounds();  
        int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;  
        int y = displayBounds.y - 450 + (displayBounds.height - shellBounds.height)>>1;  
        shell.setLocation(x, y);  
    }  
  
} 

