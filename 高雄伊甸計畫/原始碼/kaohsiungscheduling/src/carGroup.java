import java.util.ArrayList;
import java.util.List;


public class carGroup{
	int cartimeindex;	
	List<List<DriverTable>> carGroup;	
	public carGroup(int timelength){
		//ªì©l¤Æ
		 carGroup = new ArrayList<List<DriverTable>>();		
		 for(int i=0;i<timelength;i++)
		 carGroup.add(new ArrayList<DriverTable>());
		
	}
	public void addCar(DriverTable DriverNode,int timeindex)
	{		
		 carGroup.get(timeindex).add(DriverNode);		 
	}	
	public void removeCar(int timeindex,int index)
	{
		carGroup.get(timeindex).remove(index);		
	}
	public List<DriverTable> getCar(int timeindex)
	{	 		
		return carGroup.get(timeindex);	
	}	
	
}