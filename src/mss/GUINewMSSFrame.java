// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [GUINewMSSFrame] 11-21-2015 
package mss;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUINewMSSFrame extends JFrame implements ActionListener {
    
    private final JComboBox<Integer> numberOfRooms;
    private final JButton createButton;
    private final JButton openButton;
    private final JButton cancelButton;
    
    
    GUINewMSSFrame() {
        super("New Schedule");
        setSize(560, 150); // set 500x500 px size
        setLayout(new BorderLayout());  // set layout to border layout
        
        Integer[] integers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }; // array to hold num of room possibilities
        numberOfRooms = new JComboBox(integers); // create a new combo box for specifying num of rooms
        JLabel roomsLabel = new JLabel("Num of Rooms: ");
        Container roomsContainer = new Container();
        roomsContainer.setLayout(new GridLayout(1,2));
        
        roomsContainer.add(roomsLabel);
        roomsContainer.add(numberOfRooms);
                
        Container centerContainer = new Container();
        centerContainer.setLayout(new BorderLayout());
        
        JLabel information = new JLabel("Create a new schedule by specifying the number of available rooms or open a saved file.");
        centerContainer.add(information, BorderLayout.NORTH); // add info container in center
        centerContainer.add(roomsContainer, BorderLayout.CENTER);
        
        add(centerContainer, BorderLayout.CENTER);
        
        // creates the buttons with their labels
        createButton = new JButton("Create");
        openButton = new JButton("Open File");
        cancelButton = new JButton("Cancel");
        
        // add action listener to buttons (this object)
        createButton.addActionListener(this);
        openButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        // create a new container for the buttons
        Container buttonsContainer = new Container();
        buttonsContainer.setLayout(new FlowLayout());
        // add the buttons to the container
        buttonsContainer.add(createButton);
        buttonsContainer.add(openButton);
        buttonsContainer.add(cancelButton);
        
        add(buttonsContainer, BorderLayout.SOUTH); // add buttonsContainer on the bottom
    }

    private void createMSSWithRooms(int numOfRooms) {
        
        MSS scheduler = new MSS(numOfRooms); // creates new scheduler with numOfRooms rooms
        MSSFrame mainFrame = new MSSFrame(scheduler); // creates a frame from the scheduler
        mainFrame.setDefaultCloseOperation(MSSFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true); // display the main frame
        dispose(); // get rid of current popup
        scheduler.saveMSS();
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == createButton)
            createMSSWithRooms(((Integer)numberOfRooms.getSelectedItem()).intValue()); // create scheduler with x rooms
        else if (e.getSource() == openButton)
            ; // open file open dialog
        else if (e.getSource() == cancelButton)
            dispose(); // get rid of current window
    
    }

    
    
}
