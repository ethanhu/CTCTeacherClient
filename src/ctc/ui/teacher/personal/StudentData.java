package ctc.ui.teacher.personal;

import java.util.List;
import ctc.pojobean.Student;
import ctc.ui.common.AbstractData;

public class StudentData extends AbstractData{
	
	public StudentData(){
		super();
	}
	
	private static StudentData thisData = null; 
	/*private String[][] data = {
					{"1298374","Gervase", "很好", "Gallant" },
					{"1298373","Garry", "Q", "Purcell"},
					{"1298372","Harvey","","Hines", "12 Hospital Rd"},
					{"1298375","Smith","B","Robert", "1233 64th St West"}
				};
	*/
	private static String[][] data;
	//List<Student> 用于同服务器进行通信. data用于屏幕显示
	public static void setData(List<Student> list){
		int n = list.size();
		data = new String[n][4];//4同表Student的字段个数相对应
		/*for(int i = 0; i < n; i++){
			data[i] = new String[4];
		}*/
		for(int i = 0; i < n; i++){
			Student tempArray = (Student)list.get(i);
			data[i][0] = String.valueOf(tempArray.getStudent_id());
			data[i][1] = tempArray.getStudent_name();
			data[i][2] = tempArray.getStudent_password();
			data[i][3] = tempArray.getStudent_role();
		}
	}

	private int[] columnWidths = {10,15,15,10};
	private String[] columns = {"ID","学员账号","密码", "角色"};

	public static StudentData getInstance(){
		if (thisData == null){
			thisData = new StudentData();
		}
		return thisData;
	}
	
	public String[] getColumns() {
		return columns;
	}

	public int[] getColumnWidths() {
		return columnWidths;
	}

	public String[][] getData() {
		return data;
	}
	
	public String[] find(String key){
		for (int i = 0; i < this.getData().length; i++){
			if (key != null && key.equals(this.getData()[i][0])){
				return this.getData()[i];
			}
		}
		return null;
	}
}
