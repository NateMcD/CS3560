package MeetingScheduler;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.*;

public class MeetingManager implements ActionListener 
{
	private JFrame frame;
	private JPanel currentPanel;
	private String UserID;
	final DefaultListModel<String> model = new DefaultListModel<>();
	final JList<String> list = new JList<>(model);
	private boolean bInitialized = false;
	
	public MeetingManager ()
	{
		InitEmployees();
		InitRooms();
	}
	
	public void InitEmployees()
	{
		EmployeeSet.Put(new Employee("Admin", true));
		
		String[] employeesToAdd = {"Nate", "Nick", "Angel"};
		for (int i = 0; i < employeesToAdd.length; i++)
		{
			EmployeeSet.Put(new Employee(employeesToAdd[i], false));
			NotificationsSet.Put("Admin", new ArrayList<Meeting>());
		}
	}
	
	public void InitRooms()
	{
		for (int i = 1; i <= 4; i++)
		{
			RoomSet.Put(new Room(i));
			RoomScheduleSet.Put(i, new ArrayList<Meeting>());
		}
	}
	
	public void InitializeFrame()
	{
		bInitialized = true;
		frame = new JFrame("Meeting Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(350, 300);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menuCategory = new JMenu("File");
		menuBar.add(menuCategory);
		JMenuItem menuItemEmployee = new JMenuItem("Go to Login");
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemEmployee.addActionListener(this);
		menuItemExit.addActionListener(this);
		menuCategory.add(menuItemEmployee);
		menuCategory.add(menuItemExit);
		
		frame.getContentPane().add(BorderLayout.NORTH, menuBar);
		currentPanel = new JPanel();
		frame.getContentPane().add(currentPanel);
		frame.setVisible(true);
	}
	
	public void ShowLoginUI()
	{
		if (!bInitialized)
		{
			InitializeFrame();
		}
		currentPanel.setLayout(new BorderLayout());
		
		model.removeAllElements();
		Iterator<Entry<String, Employee>> esIterator = EmployeeSet.GetIterator();
		while (esIterator.hasNext())
		{
			Map.Entry<String, Employee> mapEntry = (Map.Entry<String, Employee>)esIterator.next();
			Employee employee = mapEntry.getValue();
			model.addElement(employee.ID);
		}
		
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		JScrollPane userSelectionPane = new JScrollPane(list);
		userSelectionPane.setPreferredSize(new Dimension(250, 80));
		currentPanel.add(userSelectionPane, BorderLayout.CENTER);
		
		JButton button = new JButton("Login");
		currentPanel.add(button, BorderLayout.SOUTH);
		button.addActionListener(this);
		
		
		currentPanel.revalidate();
		currentPanel.repaint();
	}
	
	public void ShowActionUI()
	{
		JLabel userLabel = new JLabel("Logged in as: " + UserID);
		currentPanel.add(BorderLayout.NORTH, userLabel);
		
		JPanel actionPanel = new JPanel();
		
		JButton newMeetingB = new JButton("New Meeting");
		newMeetingB.addActionListener(this);
		actionPanel.add(newMeetingB);
		
		JButton seeMeetingsB = new JButton("See Meetings");
		seeMeetingsB.addActionListener(this);
		actionPanel.add(seeMeetingsB);
		
		currentPanel.add(BorderLayout.CENTER, actionPanel);
		currentPanel.revalidate();
		currentPanel.repaint();
	}
	
	public Date GetDate(String dateString, String timeString)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
		try {
			return formatter.parse(dateString + " " + timeString);
		} catch (ParseException e) {
		}
		return new Date();
	}
	
	public String IsMeetingValid(Date StartDate, int RoomID, List<String> attendees)
	{
		Iterator<Entry<String, Meeting>> meetingIterator = MeetingSet.GetIterator();
		while (meetingIterator.hasNext())
		{
			Map.Entry<String, Meeting> meetingEntry = (Map.Entry<String, Meeting>)meetingIterator.next();
			Meeting meeting = meetingEntry.getValue();
			
			// check if meeting is going on during an existing one
			boolean bSameTime = (StartDate.equals(meeting.StartTime) || (StartDate.after(meeting.StartTime) && StartDate.before(meeting.EndTime)));
			if (!bSameTime)
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(StartDate);
				cal.add(Calendar.HOUR, 1);
				Date AltStartDate = cal.getTime();
				bSameTime = (AltStartDate.equals(meeting.StartTime) || (AltStartDate.after(meeting.StartTime) && AltStartDate.before(meeting.EndTime)));
			}
			if (bSameTime)
			{
				if (meeting.DesignatedRoom.ID == RoomID)
				{
					// Room unvailable
					return "Room Unavailable";
				}
				
				for (int i = 0; i < attendees.size(); i++)
				{
					if (meeting.EmployeesAttending.containsKey(attendees.get(i)))
					{
						// Attendee conflict
						return "Employee Conflict";
					}
				}
			}
		}
		return "";
	}
	
	public void DeleteMeetingFromUser(Employee e, String meetingTitle)
	{
		e.AcceptedMeetings.removeIf(m -> (m.Title == meetingTitle));
		e.OwnedMeetings.removeIf(m -> (m.Title == meetingTitle));
		e.InvitedMeetings.removeIf(m -> (m.Title == meetingTitle));
	}
	
	public void DeleteMeetings(List<String> meetingsToDelete)
	{
		// iterate through all users, delete references to this meeting title
		for (int i = 0; i < meetingsToDelete.size(); i++)
		{
			String meetingToDeleteName = meetingsToDelete.get(i);
			Iterator<Entry<String, Employee>> esIterator = EmployeeSet.GetIterator();
			while (esIterator.hasNext())
			{
				Map.Entry<String, Employee> mapEntry = (Map.Entry<String, Employee>)esIterator.next();
				String mapKey = mapEntry.getKey();
				Employee mapValue = mapEntry.getValue();
				DeleteMeetingFromUser(mapValue, meetingToDeleteName);
				
				if (EmployeeScheduleSet.ContainsKey(mapKey))
				{
					List<Meeting> meetingsFromES = EmployeeScheduleSet.Get(mapKey);
					if (!meetingsFromES.isEmpty())
						meetingsFromES.removeIf(m -> (m.Title == meetingToDeleteName));
				}
				
				if (NotificationsSet.ContainsKey(mapKey))
				{
					List<Meeting> meetingsFromNS = NotificationsSet.Get(mapKey);
					if (!meetingsFromNS.isEmpty())
						meetingsFromNS.removeIf(m -> (m.Title == meetingToDeleteName));
				}
			}
			
			if (RoomScheduleSet.ContainsKey(MeetingSet.Get(meetingToDeleteName).DesignatedRoom.ID))
			{
				List<Meeting> meetingsFromRS = RoomScheduleSet.Get(MeetingSet.Get(meetingToDeleteName).DesignatedRoom.ID);
				if (meetingsFromRS.isEmpty())
					meetingsFromRS.removeIf(m -> (m.Title == meetingToDeleteName));
			}
			
			MeetingSet.Remove(meetingToDeleteName);
		}
	}
	
	public void DeclineMeetings(List<String> meetingsToDecline)
	{
		Employee e = EmployeeSet.Get(UserID);
		for (int i = 0; i < meetingsToDecline.size(); i++)
		{
			String meetingToDeclineName = meetingsToDecline.get(i);
			if (!e.AcceptedMeetings.isEmpty())
				e.AcceptedMeetings.removeIf(m -> (m.Title == meetingToDeclineName));
			if (!e.InvitedMeetings.isEmpty())
				e.InvitedMeetings.removeIf(m -> (m.Title == meetingToDeclineName));
			if (!e.OwnedMeetings.isEmpty())
				e.OwnedMeetings.removeIf(m -> (m.Title == meetingToDeclineName));
		}
	}
	
	public void AcceptMeetings(List<String> meetingsToAccept)
	{
		Employee e = EmployeeSet.Get(UserID);
		for (int i = 0; i < meetingsToAccept.size(); i++)
		{
			String meetingToAcceptName = meetingsToAccept.get(i);
			boolean bHasMeeting = false;
			if (!e.AcceptedMeetings.isEmpty())
			{
				List<Meeting> eAcceptedMeeting = e.AcceptedMeetings;
				for (Meeting m : eAcceptedMeeting)
				{
					if (m.Title == meetingToAcceptName)
						bHasMeeting = true;
				}
			}
			
			if (!bHasMeeting)
				e.AcceptedMeetings.add(MeetingSet.Get(meetingToAcceptName));
			if (!e.InvitedMeetings.isEmpty())
				e.InvitedMeetings.removeIf(m -> (m.Title == meetingToAcceptName));
			if (!e.OwnedMeetings.isEmpty())
				e.OwnedMeetings.removeIf(m -> (m.Title == meetingToAcceptName));
		}
	}
	
	public void ShowExistingMeetingUI()
	{
		currentPanel.setLayout(new BorderLayout(10,25));
		Employee e = EmployeeSet.Get(UserID);
		
	// OWNED MEETING SECTION
		JPanel ownedMeetingPanel = new JPanel();
		ownedMeetingPanel.setLayout(new BorderLayout());
		
		JLabel ownedMeetingsLabel = new JLabel("Owned Meetings");
		ownedMeetingPanel.add(ownedMeetingsLabel, BorderLayout.NORTH);
		
		final DefaultListModel<String> ownedMeetingsModel = new DefaultListModel<>();
		if (UserID == "Admin")
		{
			Iterator<Entry<String, Meeting>> meetingIterator = MeetingSet.GetIterator();
			while (meetingIterator.hasNext())
			{
				Map.Entry<String, Meeting> meetingEntry = (Map.Entry<String, Meeting>)meetingIterator.next();
				Meeting m = meetingEntry.getValue();
				ownedMeetingsModel.addElement(m.Title);
			}
		}
		else
		{
			for (int i = 0; i < e.OwnedMeetings.size(); i++)
			{
				ownedMeetingsModel.addElement(e.OwnedMeetings.get(i).Title);
			}
		}
		JList<String> ownedMeetingsList = new JList<>(ownedMeetingsModel);
		ownedMeetingsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane ownedMeetingSelectionPane = new JScrollPane(ownedMeetingsList);
		ownedMeetingSelectionPane.setPreferredSize(new Dimension(250, 80));
		ownedMeetingPanel.add(ownedMeetingSelectionPane, BorderLayout.CENTER);
		
		JButton deleteMeetingB = new JButton("Delete Meeting");
		ownedMeetingPanel.add(deleteMeetingB, BorderLayout.SOUTH);
		currentPanel.add(ownedMeetingPanel, BorderLayout.NORTH);
		
		deleteMeetingB.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent ae) {
	            	List<String> meetingsToDelete = ownedMeetingsList.getSelectedValuesList();
	            	if (meetingsToDelete.isEmpty())
	            		return;
	            	
	            	DeleteMeetings(meetingsToDelete);
	            	CleanPanel();
					ShowExistingMeetingUI();
					frame.pack();
	            }
		});
		
	// ACCEPTED MEETING SECTION
		JPanel attendingMeetingPanel = new JPanel();
		attendingMeetingPanel.setLayout(new BorderLayout());
		
		JLabel attendingMeetingsLabel = new JLabel("Attending Meetings");
		attendingMeetingPanel.add(attendingMeetingsLabel, BorderLayout.NORTH);
		
		final DefaultListModel<String> attendingMeetingsModel = new DefaultListModel<>();
		for (int i = 0; i < e.AcceptedMeetings.size(); i++)
		{
			attendingMeetingsModel.addElement(e.AcceptedMeetings.get(i).Title);
		}
		JList<String> attendingMeetingsList = new JList<>(attendingMeetingsModel);
		attendingMeetingsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane attendingMeetingSelectionPane = new JScrollPane(attendingMeetingsList);
		attendingMeetingSelectionPane.setPreferredSize(new Dimension(250, 80));
		attendingMeetingPanel.add(attendingMeetingSelectionPane, BorderLayout.CENTER);
		
		JButton declineMeetingB = new JButton("Decline Meeting");
		attendingMeetingPanel.add(declineMeetingB, BorderLayout.SOUTH);
		currentPanel.add(attendingMeetingPanel, BorderLayout.CENTER);
		
		declineMeetingB.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent ae) {
	            	List<String> meetingsToDecline = attendingMeetingsList.getSelectedValuesList();
	            	if (meetingsToDecline.isEmpty())
	            		return;
	            	
	            	DeclineMeetings(meetingsToDecline);
	            	CleanPanel();
					ShowExistingMeetingUI();
					frame.pack();
	            }
		});
		
	// INVITED MEETING SECTION
		JPanel invitedMeetingPanel = new JPanel();
		invitedMeetingPanel.setLayout(new BorderLayout());
		
		JLabel invitedMeetingsLabel = new JLabel("Invited Meetings");
		invitedMeetingPanel.add(invitedMeetingsLabel, BorderLayout.NORTH);
		
		final DefaultListModel<String> invitedMeetingsModel = new DefaultListModel<>();
		for (int i = 0; i < e.InvitedMeetings.size(); i++)
		{
			invitedMeetingsModel.addElement(e.InvitedMeetings.get(i).Title);
		}
		JList<String> invitedMeetingsList = new JList<>(invitedMeetingsModel);
		invitedMeetingsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane invitedMeetingSelectionPane = new JScrollPane(invitedMeetingsList);
		invitedMeetingSelectionPane.setPreferredSize(new Dimension(250, 80));
		invitedMeetingPanel.add(invitedMeetingSelectionPane, BorderLayout.CENTER);
		
		JPanel invitedMeetingButtonPanel = new JPanel();
		invitedMeetingButtonPanel.setLayout(new BorderLayout());
		
		JButton acceptMeetingB = new JButton("Accept Meeting");
		invitedMeetingButtonPanel.add(acceptMeetingB, BorderLayout.WEST);
		JButton rejectMeetingB = new JButton("Reject Meeting");
		invitedMeetingButtonPanel.add(rejectMeetingB, BorderLayout.EAST);
		invitedMeetingPanel.add(invitedMeetingButtonPanel, BorderLayout.SOUTH);
		currentPanel.add(invitedMeetingPanel, BorderLayout.SOUTH);
		
		acceptMeetingB.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent ae) {
	            	List<String> meetingsToAccept = invitedMeetingsList.getSelectedValuesList();
	            	if (meetingsToAccept.isEmpty())
	            		return;
	            	
	            	AcceptMeetings(meetingsToAccept);
	            	CleanPanel();
					ShowExistingMeetingUI();
					frame.pack();
	            }
		});
		
		rejectMeetingB.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent ae) {
	            	List<String> meetingsToDecline = invitedMeetingsList.getSelectedValuesList();
	            	if (meetingsToDecline.isEmpty())
	            		return;
	            	
	            	DeclineMeetings(meetingsToDecline);
	            	CleanPanel();
					ShowExistingMeetingUI();
					frame.pack();
	            }
		});
		
		
	// frame section
		currentPanel.revalidate();
		currentPanel.repaint();
		frame.pack();
	}
	
	public void ShowNewMeetingUI()
	{
		currentPanel.setLayout(new FlowLayout());
		
		JLabel meetingTitleL = new JLabel("Meeting Title: ");
		JTextField meetingTitleTF = new JTextField("", 20);
		currentPanel.add(meetingTitleL);
		currentPanel.add(meetingTitleTF);
		
		JLabel taskL = new JLabel("Task: ");
		JTextField taskTF = new JTextField("", 20);
		currentPanel.add(taskL);
		currentPanel.add(taskTF);
		
		// sanitize people who can attend
		model.removeAllElements();
		Iterator<Entry<String, Employee>> esIterator = EmployeeSet.GetIterator();
		while (esIterator.hasNext())
		{
			Map.Entry<String, Employee> mapEntry = (Map.Entry<String, Employee>)esIterator.next();
			Employee employee = mapEntry.getValue();
			if (employee.ID != UserID && employee.ID != "Admin")
				model.addElement(employee.ID);
		}
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane userSelectionPane = new JScrollPane(list);
		userSelectionPane.setPreferredSize(new Dimension(250, 80));
		currentPanel.add(userSelectionPane, BorderLayout.CENTER);
		
		JButton dateButton = new JButton("Select Date");
		JLabel dateLabel = new JLabel("Missing Date");
		dateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	JFrame dateFrame = new JFrame("Date Selection");
            	dateFrame.setLocation(0,0);
            	dateFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        		dateFrame.setSize(300, 300);
            	dateLabel.setText(new DatePicker(dateFrame, MeetingSet.Map).setPickedDate());
            }
        });
		currentPanel.add(dateButton);
		currentPanel.add(dateLabel);
		
		JButton timeButton = new JButton("Select Time");
		JLabel timeLabel = new JLabel("Missing Time");
		timeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	JFrame timeFrame = new JFrame("Time Selection");
            	timeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	timeFrame.setSize(300, 300);
            	timeFrame.setLayout(new GridLayout(0, 2));
            	timeLabel.setText(new TimePicker(timeFrame).setTime());
            }
        });
		currentPanel.add(timeButton);
		currentPanel.add(timeLabel);
		
		List<Integer> RoomIDs = new ArrayList<Integer>();
		Iterator<Entry<Integer, Room>> roomIterator = RoomSet.GetIterator();
		while (roomIterator.hasNext())
		{
			Map.Entry<Integer, Room> roomEntry = (Map.Entry<Integer, Room>)roomIterator.next();
			Room room = roomEntry.getValue();
			RoomIDs.add(room.ID);
		}
		SpinnerListModel roomModel = new SpinnerListModel(RoomIDs);
		JSpinner roomSpinner = new JSpinner(roomModel);
		currentPanel.add(new JLabel("Select Room"));
		currentPanel.add(roomSpinner);
		
		JButton validateButton = new JButton("Check Schedule");
		currentPanel.add(validateButton);
		validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Date StartDate = GetDate(dateLabel.getText(), timeLabel.getText());
				List<String> attendees = list.getSelectedValuesList();
				if (attendees.isEmpty())
					return;
				
				if (UserID != "Admin")
					attendees.add(UserID);
				String meetingValidity = IsMeetingValid(StartDate, (int)roomSpinner.getValue(), attendees);
				
				boolean bMeetingValid = (meetingValidity == "");
				
				String action = ae.getActionCommand();
				if (action == "Check Schedule" || 
						action == "Room Unavailable" ||
						action == "Employee Conflict")
				{
					if (bMeetingValid)
					{
						validateButton.setText("Make Meeting");
					}
					else
					{
						validateButton.setText(meetingValidity);
					}
				}
				else if (ae.getActionCommand() == "Make Meeting")
				{
					if (!bMeetingValid)
					{
						validateButton.setText(meetingValidity);
						return;
					}
					
					// finalize meeting, store meeting in hash set
	            	Date startDate = GetDate(dateLabel.getText(), timeLabel.getText());
	            	Calendar cal = Calendar.getInstance();
	            	cal.setTime(startDate);
	            	cal.add(Calendar.HOUR, 1);
	            	Date endDate = cal.getTime();
	            	
	            	Meeting newMeeting = new Meeting(
	            			meetingTitleTF.getText(),
	            			taskTF.getText(),
	            			startDate,
	            			endDate,
	            			RoomSet.Get((Integer)roomSpinner.getValue()),
	            			UserID);
	            	
	            	MeetingSet.Put(newMeeting);
	            	
	            	// Add meeting to owner
	            	EmployeeSet.Get(UserID).MeetingOwn(newMeeting);
	            	
	            	// Add invited employees to meeting
	            	// Add meeting to employee's pending meetings
	            	List<String> listSelectedValues = list.getSelectedValuesList();
	            	for (int i = 0; i < listSelectedValues.size(); i++)
	            	{
	            		EmployeeSet.Get(listSelectedValues.get(i)).MeetingInvite(newMeeting);
	            		newMeeting.InviteEmployee(listSelectedValues.get(i));
	            		if (NotificationsSet.ContainsKey(listSelectedValues.get(i)))
	            		{
	            			NotificationsSet.Get(listSelectedValues.get(i)).add(newMeeting);
	            		}
	            		else
	            		{
	            			List<Meeting> meetingList = new ArrayList<Meeting>();
	            			meetingList.add(newMeeting);
	            			NotificationsSet.Put(listSelectedValues.get(i), meetingList);
	            		}
	            	}
	            	newMeeting.EmployeesAttending.put(UserID, true);
	            	
	            	int selectedRoomID = (int)roomModel.getValue();
	            	RoomScheduleSet.Get(selectedRoomID).add(newMeeting);
	            	MembershipSet.Put(newMeeting.Title, list.getSelectedValuesList());
	            	
	            	CleanPanel();
	            	ShowActionUI();
				}
			}
		});
		
		currentPanel.revalidate();
		currentPanel.repaint();
	}
	
	public String ShowDateSelection()
	{
		JFrame dateFrame = new JFrame("Date Selection");
		dateFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dateFrame.setSize(300, 300);
		
		DatePicker datePicker = new DatePicker(dateFrame, MeetingSet.Map);
		return datePicker.setPickedDate();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Exit":
				System.exit(0);
			break;
			case "Go to Login":
				CleanPanel();
				ShowLoginUI();
				frame.pack();
				break;
			case "Login":
				UserID = list.getSelectedValue().toString();
			case "Home":
				CleanPanel();
				ShowActionUI();
				frame.pack();
			break;
			case "New Meeting":
				CleanPanel();
				ShowNewMeetingUI();
				frame.pack();
			break;
			case "See Meetings":
				CleanPanel();
				ShowExistingMeetingUI();
				frame.pack();
			break;
		}
		
	}
	
	public void CleanPanel()
	{
		currentPanel.removeAll();
		currentPanel.revalidate();
		currentPanel.repaint();
	}
	
	public static void main(String[] args)
	{
		MeetingManager mm = new MeetingManager();
		mm.ShowLoginUI();
	}

}
