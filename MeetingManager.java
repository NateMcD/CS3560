package cs3560Project;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MeetingManager implements ActionListener
{
	private JFrame frame;
	private String[] employeeNames;
	private JPanel currentPanel;
	private JList list;
	private JScrollPane userSelectionPane;
	private String UserID;
	private boolean bInitialized = false;
	
	public MeetingManager ()
	{
		employeeNames = new String[]{"Admin", "Nate", "Demi"};
	}
	
	public void InitializeFrame()
	{
		bInitialized = true;
		frame = new JFrame("Meeting Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menuCategory = new JMenu("File");
		menuBar.add(menuCategory);
		JMenuItem menuItemEmployee = new JMenuItem("New Employee");
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemEmployee.addActionListener(this);
		menuItemExit.addActionListener(this);
		menuCategory.add(menuItemEmployee);
		menuCategory.add(menuItemExit);
		
		frame.getContentPane().add(BorderLayout.NORTH, menuBar);
	}
	
	public void ShowLoginUI()
	{
		if (!bInitialized)
		{
			InitializeFrame();
		}
		currentPanel = new JPanel();
		currentPanel.setLayout(new BorderLayout());
		
		list = new JList(employeeNames);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		
		userSelectionPane = new JScrollPane(list);
		userSelectionPane.setPreferredSize(new Dimension(250, 80));
		currentPanel.add(userSelectionPane, BorderLayout.CENTER);
		
		JButton button = new JButton("Login");
		currentPanel.add(button, BorderLayout.SOUTH);
		button.addActionListener(this);
		
		
		frame.getContentPane().add(currentPanel);
		frame.setVisible(true);
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
		currentPanel.setLayout(new GridLayout(0, 2));
		
		JLabel meetingTitleL = new JLabel("Meeting Title: ");
		JTextField meetingTitleTF = new JTextField();
		currentPanel.add(meetingTitleL);
		currentPanel.add(meetingTitleTF);
		
		currentPanel.revalidate();
		currentPanel.repaint();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Exit":
				System.exit(0);
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
				
			break;
			case "Cancel Meeting":
				
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
