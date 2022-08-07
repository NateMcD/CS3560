package MeetingScheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class EmployeeSet
{
	// Employee Name to Employee
	public static HashMap<String, Employee> Map = new HashMap<String, Employee>();
	
	public static void Put(Employee e)
	{
		Map.put(e.ID, e);
	}
	
	public static Employee Get(String ID)
	{
		return Map.get(ID);
	}
	
	public static Iterator<Entry<String, Employee>> GetIterator()
	{
		return Map.entrySet().iterator();
	}
	
	public static void Remove(String ID)
	{
		Map.remove(ID);
	}
}
