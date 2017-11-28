import java.awt.*;
import javax.imageio.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

public class SortingAssignment {

    private Rectangle[]rect;
    public static void main(String[] args) {
        public SortingAssignment() {
            BtnListener btnl = new BtnListener();
            JButton shuffle = new JButton("Shuffle");
            shuffle.addActionListener(btnl);
            JButton qsort = new JButton("Quicksort");
            JButton tsort = new JButton("Timsort");
            JPanel content = new JPanel();
            JPanel btns = new JPanel();
            content.setLayout(new BorderLayout());
            btns.setLayout(new FlowLayout());
            btns.add(shuffle);
            btns.add(qsort);
            btns.add(tsort);
            
        }
    }
    private class BtnListener implements ActionListener {
        public void actionPremored(ActionEvent e) {
            switch(e.getActionCommand()) {
                case "Shuffle":
                    rect.shuffle();
                    break;
                case "Quicksort":
                    rect.quicksort();
                    break;
                case "Timsort":
                    rect.timsort();
            }
        } 
    }
}
