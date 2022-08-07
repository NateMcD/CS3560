package MeetingScheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class RoomScheduleSet
{
	// Room ID to Array of Meetings booked in it
	public static HashMap<Integer, List<Meeting>> Map = new HashMap<Integer, List<Meeting>>();
	
	public static void Put(Integer roomID, List<Meeting> meetings)
	{
		Map.put(roomID, meetings);
	}
	
	public static List<Meeting> Get(Integer roomID)
	{
		return Map.get(roomID);
	}
	
	public static Iterator<Entry<Integer, List<Meeting>>> GetIterator()
	{
		return Map.entrySet().iterator();
	}
	
	public static void Remove(Integer roomID)
	{
		Map.remove(roomID);
	}
	
	public static boolean ContainsKey(Integer roomID)
	{
		return Map.containsKey(roomID);
	}
}
