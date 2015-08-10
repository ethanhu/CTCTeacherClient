package ctc.ui.admin.data;

import java.util.List;
import ctc.pojobean.Teacher;
import ctc.ui.common.AbstractData;

public class TeacherData extends AbstractData{
	
	public TeacherData(){
		super();
	}
	
	private static TeacherData thisData = null; 
	/*private String[][] data = {
					{"1298374","Gervase", "很好", "Gallant" },
					{"1298373","Garry", "Q", "Purcell"},
					{"1298372","Harvey","","Hines", "12 Hospital Rd"},
					{"1298375","Smith","B","Robert", "1233 64th St West"}
				};
	*/
	private static String[][] data;
	//List<Teacher> 用于同服务器进行通信. data用于屏幕显示
	public static void setData(List<Teacher> list){
		int n = list.size();
		data = new String[n][3];//4同表Teacher的字段个数相对应
		/*for(int i = 0; i < n; i++){
			data[i] = new String[4];
		}*/
		for(int i = 0; i < n; i++){
			Teacher tempArray = (Teacher)list.get(i);
			data[i][0] = String.valueOf(tempArray.getTeacher_id());
			data[i][1] = tempArray.getTeacher_name();
			data[i][2] = tempArray.getTeacher_password();
		}
	}

	private int[] columnWidths = {10,15,15};
	private String[] columns = {"ID","学员账号","密码"};

	public static TeacherData getInstance(){
		if (thisData == null){
			thisData = new TeacherData();
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
