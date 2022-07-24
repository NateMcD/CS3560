package MeetingScheduler;

import java.awt.*;
import java.awt.event.*;
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
	
	// Meeting Title to Meeting
	HashMap<String, Meeting> MeetingSet = new HashMap<String, Meeting>();
	// Employee Name to Employee
	HashMap<String, Employee> EmployeeSet = new HashMap<String, Employee>();
	// Room Number to Room
	HashMap<Integer, Room> RoomSet = new HashMap<Integer, Room>();
	// Meeting title to Array of Employees
	HashMap<String, List<String>> MembershipSet = new HashMap<String, List<String>>();
	// Employee Name to Array of Meetings in their schedule
	HashMap<String, List<Meeting>> EmployeeScheduleSet = new HashMap<String, List<Meeting>>();
	// Room ID to Array of Meetings booked in it
	HashMap<Integer, List<Meeting>> RoomScheduleSet = new HashMap<Integer, List<Meeting>>();
	// Employee Name to Array of Meetings they've been invited to
	HashMap<String, List<Meeting>> NotificationsSet = new HashMap<String, List<Meeting>>();
	
	public MeetingManager ()
	{
		InitEmployees();
		InitRooms();
	}
	
	public void InitEmployees()
	{
		EmployeeSet.put("Admin", new Employee("Admin", true));
		
		String[] employeesToAdd = {"Nate", "Nick", "Angel"};
		for (int i = 0; i < employeesToAdd.length; i++)
		{
			EmployeeSet.put(employeesToAdd[i], new Employee(employeesToAdd[i], false));
			NotificationsSet.put("Admin", new ArrayList<Meeting>());
		}
	}
	
	public void InitRooms()
	{
		for (int i = 1; i <= 4; i++)
		{
			RoomSet.put(i, new Room(i));
			RoomScheduleSet.put(i, new ArrayList<Meeting>());
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
		Iterator<Entry<String, Employee>> esIterator = EmployeeSet.entrySet().iterator();
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
		
		JButton editMeetingB = new JButton("Edit Meeting");
		editMeetingB.addActionListener(this);
		actionPanel.add(editMeetingB);
		
		JButton cancelMeetingB = new JButton("Cancel Meeting");
		cancelMeetingB.addActionListener(this);
		actionPanel.add(cancelMeetingB);
		
		currentPanel.add(BorderLayout.CENTER, actionPanel);
		currentPanel.revalidate();
		currentPanel.repaint();
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
		Iterator<Entry<String, Employee>> esIterator = EmployeeSet.entrySet().iterator();
		while (esIterator.hasNext())
		{
			Map.Entry<String, Employee> mapEntry = (Map.Entry<String, Employee>)esIterator.next();
			Employee employee = mapEntry.getValue();
			if (employee.ID != UserID)
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
            	dateFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        		dateFrame.setSize(300, 300);
            	dateLabel.setText(new DatePicker(dateFrame).setPickedDate());
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
		Iterator<Entry<Integer, Room>> roomIterator = RoomSet.entrySet().iterator();
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
		
		
		JButton acceptButton = new JButton("Accept");
		currentPanel.add(acceptButton);
		acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	// finalize meeting, store meeting in hash set
            	Date startDate = new Date();
            	Date endDate = new Date();
            	// TODO: Parse dates into startDate and endDate
            	
            	Meeting newMeeting = new Meeting(
            			meetingTitleTF.getText(),
            			taskTF.getText(),
            			startDate,
            			endDate);
            	
            	MeetingSet.put(newMeeting.Title, newMeeting);
            	
            	// Add meeting to owner
            	EmployeeSet.get(UserID).MeetingOwn(newMeeting);
            	
            	// Add invited employees to meeting
            	// Add meeting to employee's pending meetings
            	for (int i = 0; i < list.getSelectedValuesList().size(); i++)
            	{
            		EmployeeSet.get(list.getSelectedValuesList().get(i)).MeetingInvite(newMeeting);
            		newMeeting.InviteEmployee(list.getSelectedValuesList().get(i));
            		NotificationsSet.get(list.getSelectedValuesList().get(i)).add(newMeeting);
            	}
            	
            	int selectedRoomID = (int)roomModel.getValue();
            	RoomScheduleSet.get(selectedRoomID).add(newMeeting);
            	MembershipSet.put(newMeeting.Title, list.getSelectedValuesList());
            	
            	CleanPanel();
            	ShowActionUI();
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
		
		//TODO: Sanitize DatePicker to only show valid times that all selected employees can attend
		
		DatePicker datePicker = new DatePicker(dateFrame);
		return datePicker.setPickedDate();
		//Calendar cal = Calendar.getInstance();
		//Date today = cal.getTime();
		//datePicker.setPickedDate(today.getDay() + "-" + today.getMonth() + "-" + today.getYear());)
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
				break;
			case "Login":
				UserID = list.getSelectedValue().toString();
			case "Home":
				CleanPanel();
				ShowActionUI();
			break;
			case "New Meeting":
				CleanPanel();
				ShowNewMeetingUI();
			break;
			case "Edit Meeting":
				// TODO
			break;
			case "Cancel Meeting":
				// TODO
			break;
			case "See Schedule":
				// TODO
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
