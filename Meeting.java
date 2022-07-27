package MeetingScheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Meeting
{

	public String Title;
	public String Task;
	public Date StartTime;
	public Date EndTime;
	public Room DesignatedRoom;
	public String OwnerID;
	// Employee Names, with value True if attending/accepted, and value False if undecided
	public HashMap<String, Boolean> EmployeesAttending = new HashMap<String, Boolean>();

	public Meeting (String titleIn, String taskIn, Date startDate, Date endDate, Room roomIn, String OwnerIDIn)
	{
		Title = titleIn;
		Task = taskIn;
		StartTime = startDate;
		EndTime = endDate;
		DesignatedRoom = roomIn;
		OwnerID = OwnerIDIn;
	}
	
	public void InviteEmployee(String employeeID)
	{
		EmployeesAttending.put(employeeID, false);
	}
	
	public void InviteEmployees(List<String> employeeIDs)
	{
		for (int i = 0; i < employeeIDs.size(); i++)
		{
			EmployeesAttending.put(employeeIDs.get(i), false);
		}
	}
	
	public void EmployeeAccepted(String employeeID)
	{
		EmployeesAttending.put(employeeID, true);
	}
	
	public void EmployeeDeclined(String employeeID)
	{
		EmployeesAttending.remove(employeeID);
	}
}
