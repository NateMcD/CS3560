package MeetingScheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class RoomSet
{
	// Room Number to Room
	public static HashMap<Integer, Room> Map = new HashMap<Integer, Room>();
	
	public static void Put(Room r)
	{
		Map.put(r.ID, r);
	}
	
	public static Room Get(Integer roomID)
	{
		return Map.get(roomID);
	}
	
	public static Iterator<Entry<Integer, Room>> GetIterator()
	{
		return Map.entrySet().iterator();
	}
	
	public static void Remove(Integer roomID)
	{
		Map.remove(roomID);
	}
}
