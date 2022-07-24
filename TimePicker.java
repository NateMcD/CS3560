package MeetingScheduler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
class TimePicker {
    int hour = 6;
    int minute = 0;
    boolean am = false;
    JDialog d;
 
    public TimePicker(JFrame parent) {
    	d = new JDialog();
    	d.setModal(true);
       JPanel p1 = new JPanel(new GridLayout(0, 2));
        p1.setPreferredSize(new Dimension(430, 120));
    	d.setLayout(new GridLayout(0, 2));
		SpinnerNumberModel hourModel = new SpinnerNumberModel(6, 1, 12, 1);
		JSpinner hourSpinner = new JSpinner(hourModel);
		SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 45, 15);
		JSpinner minuteSpinner = new JSpinner(minuteModel);
		String[] amStrings = { "AM", "PM" };
		SpinnerListModel amModel = new SpinnerListModel(amStrings);
		JSpinner amSpinner = new JSpinner(amModel);
		d.add(new JLabel("Select Hour"));
		d.add(hourSpinner);
		d.add(new JLabel("Select Minute"));
		d.add(minuteSpinner);
		d.add(new JLabel("Select AM or PM"));
		d.add(amSpinner);
		JButton acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                hour = (int)hourSpinner.getValue();
                minute = (int)minuteSpinner.getValue();
                if (amSpinner.getValue() == "AM")
                	am = true;
                else
                	am = false;
            	d.dispose();
            }
        });
		d.add(acceptButton);
		d.pack();
		d.setVisible(true);
    }
 
    
    public String setTime() {
    	String formattedTime = new String(hour + ":" + minute + " ");
    	if (am)
    		formattedTime += "AM";
    	else
    		formattedTime += "PM";
        return formattedTime;
    }
}