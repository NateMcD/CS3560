package MeetingScheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MeetingSet
{
	// Meeting Title to Meeting
	public static HashMap<String, Meeting> Map = new HashMap<String, Meeting>();
	
	public static void Put(Meeting m)
	{
		Map.put(m.Title, m);
	}
	
	public static Meeting Get(String meetingTitle)
	{
		return Map.get(meetingTitle);
	}
	
	public static Iterator<Entry<String, Meeting>> GetIterator()
	{
		return Map.entrySet().iterator();
	}
	
	public static void Remove(String meetingTitle)
	{
		Map.remove(meetingTitle);
	}
}
