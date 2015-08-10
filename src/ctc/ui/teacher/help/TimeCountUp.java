package ctc.ui.teacher.help;

//此类主要演示程序如何运行在一个非用户UI线程中

//方法syncExec()和asyncExec()的区别在于前者要在指定的线程执行结束后才返回，而后者则无论指定的线程是否执行都会立即返回到当前线程。
import org.eclipse.swt.SWT;  
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;  
import org.eclipse.swt.layout.*;  
import org.eclipse.swt.widgets.*;  

 
public class TimeCountUp extends Dialog implements Runnable{  

    protected Shell shell;  
    private Thread clocker;  
    private Label countLabel;  
    private boolean finished = false;
    private int CountUpTime = 0; //計時
    private static Display display;
    
    public TimeCountUp(Shell parent){
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
	Listener buttonListener = new Listener() {
		public void handleEvent(Event e) {
				finished = true;
		}
	};
  
    protected void createContents() {  
        shell = new Shell(getParent(), SWT.NONE | SWT.APPLICATION_MODAL);  
        shell.setLayout(new GridLayout());  
        shell.setSize(150, 40);//宽度 高度 
    
        Composite panel = new Composite(shell, SWT.FLAT);//SWT.NONE  
        panel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));  
        final GridLayout gridLayout = new GridLayout();  
        gridLayout.numColumns = 3;
        panel.setLayout(gridLayout); 
        
        final Color color = display.getSystemColor(SWT.COLOR_RED);
        Button button = new Button(panel, SWT.PUSH);
        button.setText("停止");
        button.setBackground(color);
        button.addListener(SWT.Selection, buttonListener);
          
        final Label label = new Label(panel, SWT.NONE);  
        label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));  
        label.setText("计时(秒)：");  
  
        countLabel = new Label(panel, SWT.NONE);  
        countLabel.setText("0   ");  
  
    }  
  
    public void run() {  
        while(true) {
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            CountUpTime++;  
            final int temp = CountUpTime;  
            //在其他線程中調用UI線程（即修改界面元素），需要使用Display.getDefault().asyncExec()方法  
            Display.getDefault().asyncExec(new Runnable() {  
                public void run() {  
                    countLabel.setText(temp + "");  
                }  
            });
        	 if(finished)
                 break;
        }  
          
        Display.getDefault().asyncExec(new Runnable() {  
            public void run() {  
                shell.dispose();//計時完成，退出窗口  
            }  
        });  
    }  
      
    //顯示shell  
    private void centerShell(Display display, Shell shell) {  
        Rectangle displayBounds = display.getPrimaryMonitor().getBounds();  
        Rectangle shellBounds =shell.getBounds();  
        int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;  
        int y = displayBounds.y - 450 + (displayBounds.height - shellBounds.height)>>1;  
        shell.setLocation(x, y);  
    }  
  
} 

