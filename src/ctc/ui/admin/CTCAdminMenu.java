package ctc.ui.admin;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

import ctc.constant.Constants;
import ctc.ui.CTCAdminMain;

/**
 * This class contains the menu for the Password application
 */
public class CTCAdminMenu {
  Menu menu = null;

  /**
   * Constructs a Menu
   * @param shell the parent shell
   */
  
  
  public CTCAdminMenu(final Shell shell) {
    // Create the menu
    menu = new Menu(shell, SWT.BAR);//BAR用于主菜单
   ///////////////
    //Create the File top-level menu
    MenuItem item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
    item.setText("文件");
    Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);
    new MenuItem(dropMenu, SWT.SEPARATOR);//SEPARATOR分隔符 显示一个横线，把几个选项隔开
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("退出\tAlt+F4");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
    	  CTCAdminMain.getApp().closeWindow();
    	  shell.close();
      }
    });
    
    /////////////
    item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
    item.setText("维护");
    dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);
    
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("管理员信息维护");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCAdminMain.getApp().menuDispatcher(Constants.ADMIN_PASSWORD_UPDATE);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("教师信息录入");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCAdminMain.getApp().menuDispatcher(Constants.ADMIN_TEACHER_INPUT);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("教师信息查询");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCAdminMain.getApp().menuDispatcher(Constants.ADMIN_TEACHER_SEARCH);
      }
    });
    
      /////////////////已经全部实现
    // Create Help
    item = new MenuItem(menu, SWT.CASCADE);
    item.setText("帮助");
    dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);
    // Create Help->About
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("关于\tCtrl+A");
    item.setAccelerator(SWT.CTRL + 'A');
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCAdminMain.getApp().about();
      }
    });
  }

  /**
   * Gets the underlying menu
   * 
   * @return Menu
   */
  public Menu getMenu() {
    return menu;
  }
  
}
