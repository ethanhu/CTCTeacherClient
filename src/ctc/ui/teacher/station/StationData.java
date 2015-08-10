package ctc.ui.teacher.station;

import java.util.ArrayList;
import java.util.List;
import ctc.pojobean.Station;

public class StationData {
	
	static boolean reloadFlag = false; 
	
	private int[] columnWidths = {15,10,10,10};
	final static int PAGESIZE = 16;//表格上的每页所显示的记录条数
	int PAGE_SIZE = 16;
	public static String[] columnHeads = {"车站名称","下行车道数", "上行车道数", "站场图"};
	public static String[] MAPS = { "库表格式", "文件格式"};
	
	private static StationData thisData = null;
	public static StationData getInstance(){
		if (thisData == null){
			thisData = new StationData();
		}
		return thisData;
	}
	public StationData(){
		super();
	}
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
		 stationList.clear();//全部删除集合中的元素
	}
	
	//保存检索结果的集合类对象
	private ArrayList<Station> stationList = new ArrayList<Station>();

	//根据SQL语句在数据库中查询数据，并将获取的数据赋值个ArrayList
	public void setData(List<Station> list){
		stationList = (ArrayList<Station>)list;
	}
	//保证首次装入tableviewer的数据z
	public ArrayList<Station> getData() {

		if(stationList == null)
			return null;
	
		int count = stationList.size();
		
		if( count == 0)
			return null;
		else
		if( count < PAGE_SIZE)
			PAGE_SIZE = count;
		else
		if( count >= PAGE_SIZE && count <= PAGESIZE){
			PAGE_SIZE = count;
		}else
		if( count > PAGESIZE){
			PAGE_SIZE = PAGESIZE;
		}

		ArrayList<Station> list = new ArrayList<Station>();
		for(int i = 0; i < PAGE_SIZE; i++)
			list.add(stationList.get(i));
		return list;
	}

	public void remove(String stationName){
		for(int i = 0; i < stationList.size(); i++)
		{
			Station data = stationList.get(i);
			if(stationName.equalsIgnoreCase(data.getStation_name()))
				stationList.remove(i); 
		}
	}
	public ArrayList<Station> getRow(int low,int high){
		ArrayList<Station> list = new ArrayList<Station>();
		for(int i = low; i< high; i++)
			list.add(stationList.get(i));
		return list;
	}
	
	public int getRowCount(){//检索结果的总数
		return stationList.size();	
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