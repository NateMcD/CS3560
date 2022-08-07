package MeetingScheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class MembershipSet
{
	// Meeting title to Array of Employees
	public static HashMap<String, List<String>> Map = new HashMap<String, List<String>>();
	
	public static void Put(String meetingTitle, List<String> Employees)
	{
		Map.put(meetingTitle, Employees);
	}
	
	public static List<String> Get(String meetingTitle)
	{
		return Map.get(meetingTitle);
	}
	
	public static Iterator<Entry<String, List<String>>> GetIterator()
	{
		return Map.entrySet().iterator();
	}
	
	public static void Remove(String meetingTitle)
	{
		Map.remove(meetingTitle);
	}
}
