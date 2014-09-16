//Travel time structure
public class TravelTimeStruct
{
	public int No;
	public String StartAddress;
	public double StartLon;
	public double StartLat;	
	public String EndAddress;
	public double EndLon;
	public double EndLat;
	public int OriginTravelTime;
	
	public TravelTimeStruct()
	{
		No = -1;
		StartAddress = null;
		StartLon = -1.0;
		StartLat = -1.0;		
		EndAddress = null;
		EndLon = -1.0;
		EndLat = -1.0;
		OriginTravelTime = -1;	
	}
}
