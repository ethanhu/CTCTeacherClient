package ctc.ui.teacher.train;

import java.util.ArrayList;
import java.util.List;
import ctc.pojobean.*;

public class TrainData {
	
	private static TrainData thisData = null;
	public static TrainData getInstance(){
		if (thisData == null){
			thisData = new TrainData();
		}
		return thisData;
	}
	public TrainData(){
		super();
	}
	//控制Combo的显示
    public static boolean reloadFlag = true; 
    static boolean startFlag = true;
    static boolean endFlag = true;
    
	 static boolean editEndFlag  = false;
	 static boolean editStartFlag  = false;
    
    public static void initial(){ 
    	reloadFlag = true;
    	
    	startFlag = true;
    	endFlag = true;
    	
      	editStartFlag  = true;
	   	editEndFlag  = true;
    	
    }
    
	static String [] direct = {"上行","下行"};
	
	static String [] stationName;//车站名称
	public void setStationList(List<Station> list) {
		stationName = new String[list.size()];
		
		if (list == null)
			return;
		
		for(int i = 0; i < list.size(); i++)
		{
			Station data = list.get(i);
			stationName[i] = data.getStation_name();
		}
	}
	
//////////////////////////////////////////////////
	
	//以下为浏览用
	int PAGE_SIZE = 16;
	final static int PAGESIZE = 16;//表格上的每页所显示的记录条数
	public static String[] columnHeads = {"车次名称","始发站","到达站", "车次方向","车速"};
	private int[] columnWidths = {10,10,10,8,7};
	//保存检索结果的集合类对象
	private ArrayList<Train> trainList = new ArrayList<Train>();
	
	public int[] getColumnWidths() {
		return columnWidths;
	}
	public String getColumnHead(int i){
		return getColumnHeads()[i];
	}
	public String[] getColumnHeads() {
		return columnHeads;
	}
	public int getColumnWidth(int i){
		return getColumnWidths()[i];
	}
	public int getColumnCount(){
		return columnHeads.length;
	}
	
	public void removeAll(){
		 trainList.clear();//全部删除集合中的元素
	}

	//根据SQL语句在数据库中查询数据，并将获取的数据赋值个ArrayList
	public void setData(List<Train> list){
		trainList = (ArrayList<Train>)list;
	}
	//保证首次装入tableviewer的数据
	public ArrayList<Train> getData() {
		int count = trainList.size();
		
		if( count == 0)
			return null;
		else
		if( count < PAGE_SIZE)
			PAGE_SIZE = count;
		else
		if( count >= PAGE_SIZE)
			PAGE_SIZE = PAGESIZE;
		
		ArrayList<Train> list = new ArrayList<Train>();
		
		for(int i = 0; i< PAGE_SIZE; i++)
			list.add(trainList.get(i));
		
		return list;
	}

	public void remove(String trainName){
		for(int i = 0; i < trainList.size(); i++)
		{
			Train data = trainList.get(i);
			if(trainName.equalsIgnoreCase(data.getTrain_name()))
				trainList.remove(i); 
		}
	}
	public ArrayList<Train> getRow(int low,int high){
		ArrayList<Train> list = new ArrayList<Train>();
		for(int i = low; i< high; i++)
			list.add(trainList.get(i));
		return list;
	}
	
	public int getRowCount(){//检索结果的总数
		return trainList.size();	
	}
	//在表格控件上显示所有检索结果所需的总页数
	public int getTotalPageNum() {
		int totalPage = 1;// 检索结果显示的总页数
		if (PAGE_SIZE != 0) {
			double temp = (float) getRowCount() / (float) PAGE_SIZE;
			// 凑整取整数
			totalPage = (int) Math.ceil(temp);
		}
		if (totalPage == 0) {
			totalPage = 1;
		}
		return totalPage;
	}

}