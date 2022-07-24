package MeetingScheduler;

import java.util.ArrayList;
import java.util.List;

public class Employee
{
	public String ID;
	public boolean bAdmin;
	public List<Meeting> OwnedMeetings;
	public List<Meeting> AcceptedMeetings;
	public List<Meeting> InvitedMeetings;
	
	public Employee()
	{
		InitMeetings();
	}
	
	public Employee(String InID, boolean bInAdmin)
	{
		InitMeetings();
		ID = InID;
		bAdmin = bInAdmin;
	}
	
	public void InitMeetings()
	{
		OwnedMeetings = new ArrayList<Meeting>();
		AcceptedMeetings = new ArrayList<Meeting>();
		InvitedMeetings = new ArrayList<Meeting>();
	}
	
	public void MeetingOwn(Meeting m)
	{
		OwnedMeetings.add(m);
	}
	
	public void MeetingAccept(Meeting m)
	{
		InvitedMeetings.remove(m);
		AcceptedMeetings.add(m);
	}
	
	public void MeetingInvite(Meeting m)
	{
		InvitedMeetings.add(m);
	}
}
