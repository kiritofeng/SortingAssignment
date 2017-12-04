import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Counter extends JDialog {

    private JLabel swaps,comps;
    private int swapCnt,compCnt,sz;
    public Counter() {
        sz=100;
        setMinimumSize(new Dimension(400,80));
        setPreferredSize(new Dimension(400,80));
        swaps = new JLabel("Swaps: --");
        comps = new JLabel("Comparisions: --");
        JButton btn = new JButton("Sort!");
        JComboBox<Integer>ele = new JComboBox<>(new Integer[]{100,100000,1000000});
        btn.addActionListener(new btnListener());
        ele.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sz=(Integer)ele.getSelectedItem();
                swaps.setText("Swaps: --");
                comps.setText("Comparisions: --");
            }
        });
        JPanel lbls = new JPanel();
        JPanel content = new JPanel();
        lbls.setLayout(new FlowLayout(FlowLayout.CENTER,20,0));
        lbls.add(swaps);
        lbls.add(comps);
        JPanel cntrls = new JPanel();
        new BoxLayout(cntrls,BoxLayout.PAGE_AXIS);
        cntrls.add(btn);
        cntrls.add(ele);
        content.setLayout(new BorderLayout());
        content.add(lbls,"North");
        content.add(cntrls,"Center");
        setContentPane(content);
        pack();
        setTitle("Timsort");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new Counter().setVisible(true);
    }

    private class btnListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            switch(e.getActionCommand()) {
                case "Sort!":
                    swapCnt = compCnt = 0;
                    timsort();
                    swaps.setText("Swaps: "+swapCnt);
                    comps.setText("Comparisions: "+compCnt);
                    repaint();
                    break;
            }
        }
    }

    /**
     * This an implementation of the Fisher-Yates Shuffle
     * It preforms an in place shuffle of the array rect
     * Time Complexity: O(N)
     * Auxiliary Space Complexity: O(1)
     */
    private static void shuffle(int[]arr) {
        for(int i=1;i<arr.length;i++) {
            int ind=(int)(Math.random()*i);
            int tmp=arr[ind];
            arr[ind]=arr[i];
            arr[i]=tmp;
        }
    }

    // A helper class for the sorts
    private class Pair {
        int first, second;
        public Pair(int f, int s) {
            first=f;
            second=s;
        }
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
     */
    private void timsort() {
        int[]arr = new int[sz];
        for(int i=0;i<arr.length;i++)
            arr[i]=i;
        shuffle(arr);
        Queue<Pair> Q = new LinkedList<>(); // The FIFO data structure, for keeping track of runs
        int prevInd = 0; // The starting index of the run
        int prevE = 0; // The previous element
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                    prevE = arr[i];
                    prevInd = i;
            } else {
                if (prevE > arr[i]) {
                    compCnt++;
                    // This element belongs to its own run
                    Q.offer(new Pair(prevInd, i - 1)); // Add the old run to the FIFO data structure
                    prevInd = i; // The next starting index of the run
                }
                prevE = arr[i]; // The new element to compare to
            }
        }
        // Add in the last run
        Q.offer(new Pair(prevInd, arr.length - 1));
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
            int[] tmp = new int[P2.second - P1.first + 1]; // Construct a temp array to hold the elements in order
            for (int i = P1.first, j = P2.first, k = 0; i <= P1.second || j <= P2.second; ) {
                // Merge the two runs
                if (i > P1.second)
                    tmp[k++] = arr[j++];
                else if (j > P2.second)
                    tmp[k++] = arr[i++];
                else {
                    compCnt++;
                    if (arr[i] <= arr[j])
                        tmp[k++] = arr[i++];
                    else
                        tmp[k++] = arr[j++];
                }
            }
            //Copy the array over
            for (int i = P1.first, j = 0; j < tmp.length; ) {
                arr[i++] = tmp[j++];
                swapCnt++;
            }
            Q.offer(new Pair(P1.first, P2.second));
        }
    }
}
