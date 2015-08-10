package ctc.ui.teacher;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

import ctc.constant.Constants;
import ctc.ui.CTCTeacherMain;

/**
 * This class contains the menu for the Password application
 */
public class CTCTeacherMenu {
  Menu menu = null;

  /**
   * Constructs a Menu
   * @param shell the parent shell
   */
  
  
  public CTCTeacherMenu(final Shell shell) {
    // Create the menu
    menu = new Menu(shell, SWT.BAR);//BAR用于主菜单
    
    /////////////////
    MenuItem item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
    item.setText("系统配置");
    Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);
 
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("终端配置");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        //CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.CTC_STATION_LAYOUT);
      }
    });
    
    item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
    item.setText("车站");
    dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);

    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("创建车站基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_STATION_CREATE);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("查询车站基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_STATION_SEARCH);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("浏览车站基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_STATION_BROWSER);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("绘制站场图");
    item.setEnabled(false);
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
      CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.CTC_STATION_LAYOUT);
      }
    });
    
    //区段管理
    item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
    item.setText("区段");
    dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);

    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("创建区段基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_DISTRICT_CREATE);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("查询区段基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_DISTRICT_SEARCH);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("浏览区段基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_DISTRICT_BROWSER);
      }
    });
    
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("定义区段与车站关系");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_DISTRICT_STATION_CREATE);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("查询区段与车站关系");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_DISTRICT_STATION_SEARCH);
      }
    });
   
    ///////////////    
    //Create the File top-level menu
    item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
    item.setText("列车");
    dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);

    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("创建列车基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_TRAIN_CREATE);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("查询列车基本信息");
    item.addSelectionListener(new SelectionAdapter() {
    public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_TRAIN_SEARCH);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("浏览列车基本信息");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_TRAIN_BROWSER);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("定义区段与列车关系");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_DISTRICT_TRAIN_CREATE);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("查询区段与列车关系");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_DISTRICT_TRAIN_SEARCH);
      }
    });
    
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("定义列车行车计划_1");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_TRAIN_PLAN_CREATE);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    
    //hu 2010-11-2 修改
    //item.setText("定义列车行车计划_2");
    item.setText("调度主任");   
    // item.setEnabled(false);//设置成不可选
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
      CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.CTC_TRAIN_PLAN_LAYOUT);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("查询列车运营计划");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_TRAIN_PLAN_SEARCH);
      }
    });
    
    //new MenuItem(dropMenu, SWT.SEPARATOR);//SEPARATOR分隔符 显示一个横线，把几个选项隔开
     /////////////
     item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
     item.setText("实验");
    // item.setEnabled(false);//设置成不可选
     dropMenu = new Menu(shell, SWT.DROP_DOWN);
     item.setMenu(dropMenu);
     
     item = new MenuItem(dropMenu, SWT.NULL);
     item.setText("设置实验参数");
     item.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.EXPERIMENT_SET);
       }
     });

     item = new MenuItem(dropMenu, SWT.NULL);
     item.setText("启动实验");
     item.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.EXPERIMENT_START);
       }
     });
     
     item = new MenuItem(dropMenu, SWT.NULL);
     item.setText("关闭实验");
     item.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.EXPERIMENT_CLOSE);
       }
     });

     ////////////
     item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
     item.setText("人员");
     dropMenu = new Menu(shell, SWT.DROP_DOWN);
     item.setMenu(dropMenu);
     item = new MenuItem(dropMenu, SWT.NULL);
     
     item.setText("教师信息维护");
     item.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_PASSWORD_UPDATE);
       }
     });
     
     item = new MenuItem(dropMenu, SWT.NULL);
     item.setText("学员信息录入");
     item.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.STUDENT_INFO_INPUT);
       }
     });
     item = new MenuItem(dropMenu, SWT.NULL);
     item.setText("学员信息查询");
     item.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.STUDENT_INFO_SEARCH);
       }
     });

    // Create Help
    item = new MenuItem(menu, SWT.CASCADE);
    item.setText("帮助");
    dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);
    
    // Create Help->About
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("30秒倒计时");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_COUNT_DOWN);
      }
    });
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("计时");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().teacherMenuDispatcher(Constants.TEACHER_COUNT_UP);
      }
    });

    // Create Help->About
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("关于\tCtrl+A");
    item.setAccelerator(SWT.CTRL + 'A');
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCTeacherMain.getApp().about();
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
