import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class SortingAssignment extends JFrame implements Runnable { //Hack for getting more stack size

    private Bar[]rect;
    private DrawArea D;
    private int barCnt;
    private static int DELAY=100;
    private JComponent cntrls[];

    public static void main(String[] args) throws Exception {
        System.setProperty("sun.java2d.opengl", "true"); // Make the graphics appear smoothly on linux
        new Thread(null, new SortingAssignment(), "SortingAssignment", 1<<26).start(); //Increase the stack
    }

    public void run() {
        new SortingAssignment().setVisible(true);
    }

    public SortingAssignment() {
        barCnt=20; // there are initially only 20 bars
        initBars();
        setMinimumSize(new Dimension(800,500));
        setPreferredSize(new Dimension(800,500));
        D=new DrawArea(getWidth(),getHeight()-100);
        // Set up controls
        cntrls = new JComponent[5];
        BtnListener btnl = new BtnListener();
        cntrls[0] = new JButton("Shuffle");
        ((JButton)cntrls[0]).addActionListener(btnl);
        cntrls[1] = new JButton("Worst Case");
        ((JButton)cntrls[1]).addActionListener(btnl);
        cntrls[2] = new JButton("Quicksort");
        ((JButton)cntrls[2]).addActionListener(btnl);
        cntrls[3] = new JButton("Timsort");
        ((JButton)cntrls[3]).addActionListener(btnl);
        cntrls[4] = new JComboBox<Integer>(new Integer[]{20,50,100,1000}); // don't have too many bars
        ((JComboBox<Integer>)cntrls[4]).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new Thread() {
                    public void run() {
                        barCnt = (Integer)((JComboBox<Integer>)cntrls[4]).getSelectedItem();
                        DELAY = 2000/barCnt;
                        initBars();
                    }
                }.start();

            }
        });
        JPanel content = new JPanel();
        JPanel btns = new JPanel();
        content.setLayout(new BorderLayout());
        btns.setLayout(new FlowLayout());
        for(JComponent jc:cntrls)
            btns.add(jc);
        content.add(btns,"North");
        content.add(D,"Center");
        setContentPane(content);
        pack();
        setTitle("Quicksort Vs Timsort - Visual Comparision");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // To initialize the bars
    private void initBars() {
        rect=new Bar[barCnt];
        for(int i=0;i<barCnt;i++)
            rect[i]=new Bar(i);
        repaint();
    }

    private class BtnListener implements ActionListener {

        // To reduce redundant code
        private abstract class BarThread extends Thread {
            public void run() {
                // Re-enable all components
                for(Component c:cntrls)
                    c.setEnabled(true);
            }
        }

        public void actionPerformed(ActionEvent e) {
            // Disable all components while preforming actions
            for(Component c:cntrls)
                c.setEnabled(false);
            switch(e.getActionCommand()) {
                case "Shuffle":
                    new BarThread() {
                        public void run() {
                            shuffle();
                            super.run();
                        }
                    }.start();
                    break;
                case "Worst Case":
                    new BarThread() {
                        public void run() {
                            worstCase();
                            super.run();
                        }
                    }.start();
                    break;
                case "Quicksort":
                    new BarThread() {
                        public void run() {
                            try {
                                quicksort();
                            } catch (InterruptedException IE) {
                                //Do Nothing
                            }
                            super.run();
                        }
                    }.start();
                    break;
                case "Timsort":
                    new BarThread() {
                        public void run() {
                            try {
                                timsort();
                            } catch(InterruptedException IE) {
                                //Do nothing
                            }
                            super.run();
                        }
                    }.start();
            }
            repaint();  //Update the component
        }

        /**
         * This an implementation of the Fisher-Yates Shuffle
         * It preforms an in place shuffle of the array rect
         * Time Complexity: O(N)
         * Auxiliary Space Complexity: O(1)
         */
        private void shuffle() {
            for(int i=1;i<rect.length;i++) {
                int ind=(int)(Math.random()*i); //Get index
                Bar tmp=rect[i]; //Swap
                rect[i]=rect[ind];
                rect[ind]=tmp;
            }
            repaint();
        }

        /**
         * Generates a worst case for the quicksort and timsort
         * The worst case of timsort is when the array is sorted in reverse
         * The worst case of this implementation of quicksort is an almost sorted array
         * Time Complexity: O(N)
         * Auxiliary Space Complexity: O(1)
         */
        private void worstCase() {
            initBars();
            for(int i=0;2*i<rect.length;i++) {
                Bar tmp=rect[i];
                rect[i]=rect[rect.length-1-i];
                rect[rect.length-1-i]=tmp;
            }
        }

        /**
         * An iterative implementation of quicksort
         * Uses an explicit stack to avoid recursion
         * This is necessary because of the use of synchronized blocks
         * Best case time complexity: O(N log N)
         * Worst case time complexity: O(N^2)
         * Auxiliary Space Complexity: O(1)
         * @throws InterruptedException
         */
        private void quicksort() throws InterruptedException {
            Stack<Pair> S = new Stack(); // Use stacks to simulate recursion
            S.push(new Pair(0,rect.length-1));
            while(!S.empty()) {
                int lft=S.peek().first,rht=S.peek().second;
                S.pop();
                if (lft < rht) {
                    int prt = lft - 1; // Get partition
                    Bar pivot = null; // Declare it outside synchronized blocks
                    synchronized (rect) {
                        pivot = rect[rht]; //Get pivot value
                    }
                    for (int i = lft; i < rht; i++) {
                        synchronized (rect) {
                            // Change colours
                            rect[i].setComparing(true);
                            pivot.setComparing(true);
                        }
                        // Display color change
                        repaint();
                        // Let people see colour change
                        Thread.sleep(DELAY);
                        synchronized (rect) {
                            // Reset colours
                            rect[i].setComparing(false);
                            pivot.setComparing(false);
                            if (rect[i].compareTo(pivot) <= 0) {
                                // Swap
                                Bar tmp = rect[++prt];
                                rect[prt] = rect[i];
                                rect[i] = tmp;
                            }
                        }
                    }
                    synchronized (rect) {
                        // Move partition into place
                        Bar tmp = rect[++prt];
                        rect[prt] = rect[rht];
                        rect[rht] = tmp;
                    }
                    repaint(); // Finalize changes
                    // Simulate recursion with stack
                    S.push(new Pair(prt + 1, rht));
                    S.push(new Pair(lft, prt - 1));
                }
            }
            repaint(); // Finalize graphical changes
        }

        /**
         * An iterative implementation of timsort
         * Timsort is similar to mergesort, in the sense that they merge two sorted arrays into one
         * But timsort optimizes by first finding runs
         * A run is a subarray which is already sorted
         * Whereas mergesort's base case is just to use each element individually, timsort's is the runs
         * Once two adjacent runs are merged, this becomes a new run
         * This implementation uses an FIFO data structure to keep track of the runs
         * Thus while this has the same asymptotic time complexity as a standard timsort
         * It is in practice slower
         * Best case time complexity: O(N)
         * (This is when then the array is presorted)
         * Worst case time complexity: O(N log N)
         * Auxiliary space complexity: O(N)
         * @throws InterruptedException
         */
        private void timsort() throws InterruptedException {
            Queue<Pair> Q = new LinkedList<>(); // The FIFO data structure, for keeping track of runs
            int prevInd = 0; // The starting index of the run
            Bar prevE = null; // The previous element
            for (int i = 0; i < rect.length; i++) {
                if (prevE == null) {
                    synchronized (rect) {
                        prevE = rect[i];
                        prevInd = i;
                    }
                } else {
                    synchronized (rect) {
                        // Graphically demonstrate the comparision
                        prevE.setComparing(true);
                        rect[i].setComparing(true);
                    }
                    // Show changes
                    repaint();
                    Thread.sleep(DELAY);
                    synchronized (rect) {
                        // Done demonstrating; actually compare
                        prevE.setComparing(false);
                        rect[i].setComparing(false);
                        if (prevE.compareTo(rect[i]) > 0) {
                            // This element belongs to its own run
                            Q.offer(new Pair(prevInd, i - 1)); // Add the old run to the FIFO data structure
                            prevInd = i; // The next starting index of the run
                        }
                        prevE = rect[i]; // The new element to compare to
                    }
                }
            }
            synchronized (rect) {
                // Add in the last run
                Q.offer(new Pair(prevInd, rect.length - 1));
            }
            // While there is more than one run; i.e. while there are still runs to merge
            while (Q.size() > 1) {
                Pair P1 = Q.poll(); // Get the first run
                while (P1.second > Q.peek().first) { // Edge case is where the first run is at the end of the array
                    // Push this run to the end of the queue
                    Q.offer(P1);
                    // This run should be the one at the beginning of the array
                    P1 = Q.poll();
                }
                Pair P2 = Q.poll(); // Get the next run
                //P1 and P2 are two adjacent run
                Bar[] tmp = new Bar[P2.second - P1.first + 1]; // Construct a temp array to hold the elements in order
                for (int i = P1.first, j = P2.first, k = 0; i <= P1.second || j <= P2.second; ) {
                    // Merge the two runs
                    if (i > P1.second)
                        synchronized (rect) {
                            tmp[k++] = rect[j++];
                        }
                    else if (j > P2.second)
                        synchronized (rect) {
                            tmp[k++] = rect[i++];
                        }
                    else {
                        synchronized (rect) {
                            // Graphically demonstrate comparision
                            rect[i].setComparing(true);
                            rect[j].setComparing(true);
                        }
                        // Draw
                        repaint();
                        // Let them see it
                        Thread.sleep(DELAY);
                        synchronized (rect) {
                            // Done graphically comparing
                            rect[i].setComparing(false);
                            rect[j].setComparing(false);
                            // Actually copmare
                            if (rect[i].compareTo(rect[j]) <= 0)
                                tmp[k++] = rect[i++];
                            else
                                tmp[k++] = rect[j++];
                        }
                    }
                }
                //Copy the array over
                synchronized (rect) {
                    for (int i = P1.first, j = 0; j < tmp.length; )
                        rect[i++] = tmp[j++];
                }
                repaint(); // Show them what it looks like
                Q.offer(new Pair(P1.first, P2.second));
            }
            repaint(); // Finalize graphics
        }
    }

    private class Bar implements Comparable<Bar> {
        protected final int val; // the value the bar represents, used to determine height
        private boolean comparing; // whether or not the bar is currently being used in a comparision

        public Bar(int v) {
            comparing=false;
            val=v;
        }

        public void show(Graphics G,int x,int y,int width,int height) {
            synchronized(rect) {
                if (comparing) // Bar is being compared; show in a different colour
                    G.setColor(Color.RED);
                else
                    G.setColor(Color.GRAY);
                // Draw bar
                G.fillRect(x, y, width, height);
            }
        }

        public void setComparing(boolean b) {
            comparing=b;
        }

        public int compareTo(Bar b) {
            return val-b.val;
        }
    }

    private class DrawArea extends JPanel {
        public DrawArea(int w,int h) {
            setPreferredSize(new Dimension(w,h));
        }

        public void paintComponent(Graphics G) {
            double d=0.0;
            for(int i=0,j=0;i<rect.length;i++) {
                // Generate the height and width
                // The width calculation means that not all bars are the same width
                // This allows for the bars appear across the full width, regardless of the width of the window
                int h=(int)Math.round((rect[i].val+1.0)*getHeight()/barCnt),w=(int)(Math.round((i+1.0)*getWidth()/barCnt)-Math.round(d));
                rect[i].show(G,j,getHeight()-h,w,h);
                // d is the true width
                d+=1.0*getWidth()/barCnt;
                // j is the width that will be displayed on screen
                j+=w;
            }
        }
    }
}

// A helper class for the two sorts
class Pair {
    public int first, second;

    public Pair(int f, int s) {
        first=f;
        second=s;
    }
}

