import java.awt.*;
import javax.imageio.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

public class SortingAssignment implements Runnable { //Hack for getting more stack size

    private SortableRectangle[]rect;
    public static void main(String[] args) throws Exception {
        new Thread(null, new SortingAssignment(), "SortingAssignment", 1<<26).start(); //Increase the stack
    }

    public void run() {
        new SortingAssignment().setVisible(true);
    }

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
    private class BtnListener implements ActionListener {
        public void actionPremored(ActionEvent e) {
            switch(e.getActionCommand()) {
                case "Shuffle":
                    shuffle(rect);
                    break;
                case "Quicksort":
                    quicksort(rect);
                    break;
                case "Timsort":
                    timsort(rect);
            }
        } 
    }

    private class SortableRectangle implements Comparable<SortableRectangle> {
        private final int height,width; //height and with of rectangle
        private bool comparing;
        public SortableRectangle(int h, int w) {
            height=h;
            width=w;
        }

        public void show(Graphics G,int x,int y) {
            if(comparing)
                G.setColor(Color.red);
            else
                G.setcolor(Color.gray)
            g.fillRect(x,y,width,height);
        }

        public void setComparing(boolean b) {
            comparing=b;
        }
    }
}
