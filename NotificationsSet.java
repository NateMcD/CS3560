package MeetingScheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class NotificationsSet
{
	// Employee Name to Array of Meetings they've been invited to
	public static HashMap<String, List<Meeting>> Map = new HashMap<String, List<Meeting>>();
	
	public static void Put(String employeeID, List<Meeting> meetings)
	{
		Map.put(employeeID, meetings);
	}
	
	public static List<Meeting> Get(String employeeID)
	{
		return Map.get(employeeID);
	}
	
	public static Iterator<Entry<String, List<Meeting>>> GetIterator()
	{
		return Map.entrySet().iterator();
	}
	
	public static void Remove(String employeeID)
	{
		Map.remove(employeeID);
	}
	
	public static boolean ContainsKey(String employeeID)
	{
		return Map.containsKey(employeeID);
	}
}
