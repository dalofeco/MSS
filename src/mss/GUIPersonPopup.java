/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mss;
import javax.swing.JDialog;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class GUIPersonPopup extends JDialog implements ActionListener {
    private final MSS scheduler;
    
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField phoneNumberField;
    
    private final JButton createButton;
    private final JButton cancelButton;
    
    private final MSSFrame parentFrame;
    
    private boolean editingMode = false; 
    private Person originalPerson = null; // only used in editing mode to keep track of original entry
    
    GUIPersonPopup(MSSFrame owner) {
        super(owner, "New Person");
        parentFrame = owner;
        setLayout(new GridLayout(4,1));
        setSize(400, 200);
        
        scheduler = owner.getScheduler(); // set scheduler for access when adding new person
        
        final int TEXT_FIELD_WIDTH = 20;
        
        // FIRST NAME CONTAINER
        
        Container firstNameCont = new Container();
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.CENTER);
        firstNameCont.setLayout(layout);
        
        JLabel firstNameLabel = new JLabel("First Name: ");
        firstNameField = new JTextField(TEXT_FIELD_WIDTH);
        firstNameCont.add(firstNameLabel);
        firstNameCont.add(firstNameField);
        
        // END FIRST NAME CONT
        // LAST NAME CONTAINER
        
        Container lastNameCont = new Container();
        lastNameCont.setLayout(layout);
        
        JLabel lastNameLabel = new JLabel("Last Name: ");
        lastNameField = new JTextField(TEXT_FIELD_WIDTH);
        lastNameCont.add(lastNameLabel);
        lastNameCont.add(lastNameField);
        
        // END LAST NAME CONT
        // PHONE NUMBER CONTAINER
        
        Container phoneNumberCont = new Container();
        phoneNumberCont.setLayout(layout);
        
        JLabel phoneNumberLabel = new JLabel("Phone Number: ");
        phoneNumberField = new JTextField(TEXT_FIELD_WIDTH);
        phoneNumberCont.add(phoneNumberLabel);
        phoneNumberCont.add(phoneNumberField);
        
        // END PHONE NUMBER CONT
        // CREATE AND CANCEL BUTTONS CONTAINER
        
        Container buttonsCont = new Container();
        buttonsCont.setLayout(layout);
        
        createButton = new JButton("Create");
        createButton.addActionListener(this);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        
        buttonsCont.add(createButton);
        buttonsCont.add(cancelButton);
        
        
        add(firstNameCont);
        add(lastNameCont);
        add(phoneNumberCont);
        add(buttonsCont);
    }
    
    GUIPersonPopup(MSSFrame owner, Person person) {  // editing mode constructor
        this(owner);
        firstNameField.setText(person.getFirstName());  // set the fields to the values
        lastNameField.setText(person.getLastName());    // in the person object being edited
        phoneNumberField.setText(person.getPhoneNumber());
        createButton.setText("Save"); // modify the create button to say "Save" instead
        editingMode = true; // enables editing mode in the class
        originalPerson = person; // save the person object being edited within the class
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == createButton) {
            if (!firstNameField.getText().equals("") ) {
                if(!lastNameField.getText().equals("")) {
                    if (!phoneNumberField.getText().equals("")) {
                        addPerson();
                    } else
                        JOptionPane.showMessageDialog(this, "Please fill in the phone number.");
                } else 
                    JOptionPane.showMessageDialog(this, "Please fill in the first name.");
            } else
                JOptionPane.showMessageDialog(this, "Please fill in the first name.");
        } else if (ae.getSource() == cancelButton)
            dispose(); // get rid of this dialog window
    }
    
    private void addPerson() {
        
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        
        boolean passFirst = true;
        if (firstName.equals("")) // if first name is empty, do not accept it;
            passFirst = false;
        else
            for (char c : firstName.toCharArray())  // verify all firstName characters 
                if (!Character.isAlphabetic(c))     // are alphabetic characters (letters)
                    passFirst = false;

        
        boolean passLast = true;
        if (lastName.equals("")) // if last name is empty, do not accept it;
            passLast = false;
        else
            for (char c : lastName.toCharArray()) // verify all lastName characters are letters
                if (!Character.isAlphabetic(c))
                    passLast = false;

        boolean passPhone = verifyPhoneInput(phoneNumber); // verify phone input is appropriate for Person
        
        Person personToAdd = new Person(firstName, lastName, phoneNumber);
        
        if (passFirst && passLast && passPhone) {
            
            
            
            if (editingMode) {
                scheduler.updatePerson(originalPerson, personToAdd); // updates all meetings entries for this person in MSS scheduler
                scheduler.deletePerson(originalPerson);  // if in editing mode, delete the original one to prevent duplicates
            }
            scheduler.addPerson(personToAdd);
            
            parentFrame.refreshLists();
            dispose();
        }
        else if (passFirst && passLast)
            JOptionPane.showMessageDialog(this, "Please fix your phone number' format. Ex: 8111112232 or 814-403-2232");
        else if (passFirst && passPhone)
            JOptionPane.showMessageDialog(this, "Please fix your last name. (only alphabetical characters allowed)");
        else if (passLast && passPhone)
            JOptionPane.showMessageDialog(this, "Please fix your first name. (only alphabetical characters allowed)");
        else
            JOptionPane.showMessageDialog(this, "Please fill in all fields appropriately.");

    }
    
    private boolean verifyPhoneInput(String phone) {
        boolean pass = true;
        int loopLimit = 0;
        int limit1 = 20, limit2 = 20; // initial default values high than loop limit 12, so no limit is applied
        
        
        if (phone.length() == 12) {// should include dashes at indexes 3 and 7 if length is 12
            limit1 = 3;
            limit2 = 7;
            loopLimit = 12;
        } else if (phone.length() == 10)
            loopLimit = 10;
        else
            pass = false;
        
        for (int i = 0; i < loopLimit; i++)
            if (i == limit1 || i == limit2) {// check for dashes at 3 and 7
                if (phone.charAt(i) != '-')  // if not a dash, pass
                    pass = false;
            } else if (!Character.isDigit(phone.charAt(i))) // if not a number, set pass to false
                pass = false;
        
        
        return pass;
    }
}
