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

        private static void shuffle(Object O[]) {
            for(int i=1;i<O.length;i++) {
                int ind=(int)(Math.random()*i); //Get index
                Object tmp=O[i]; //Swap
                O[i]=O[ind];
                O[ind]=tmp;
            }
        }

        private static void quicksort(Comparable C[]) {
            quicksort(C,0,C.length-1);
        }

        private static void quicksort(Comparable C[], int lft, int rht) {
            if(lft<rht) {
                int prt=lft-1; //Get partition
                Comparable pivot=C[rht]; //Get pivot value
                for(int i=lft;i<rht;i++) {
                    C[i].setCompare(true);
                    pivot.setCompare(true);
                    repaint();
                    if(C[i].compareTo(pivot)<=0) {
                        Comparable tmp=C[++prt];
                        C[prt]=C[i];
                        C[i]=tmp;
                    }
                    C[prt].setCompare(false);
                    pivot.setCompare(false);
                    repaint();
                }
                //Move parition into place
                Comparable tmp=C[++prt];
                C[prt]=C[rht];
                C[rht]=tmp;
                //Recursively quicksort
                quicksort(C,lft,prt-1);
                quicksort(C,prt+1,rht);
        }
    }

    private static void timsort(Comparable C[]) {
        Queue<Pair<Integer,Integer>>Q=new LinkedList<>();
        for(int i=0,prevInd=0,prevE=null;i<C.length;i++) {
            if(prevE==null) {
                prevE=C[i];
                prevInd=i;
            } else {
                prevE.setCompare(true);
                C[i].setCompare(true);
                repaint();
                if(prevE.compareTo(C[i])<=0) {
                    prevE=C[i];
                } else {
                    Q.push(new Pair(prevInd,i));
                    prevInd=i+1;
                    prevE=null;
                }
            }
        }
        while(Q.size()>1) {
            Pair P1=Q.poll();
            while(P1.second>Q.peek().first) {
                Q.offer(P1);
                P1=Q.poll();
            }
            P2=Q.poll();
            //P1 and P2 are two adjacent intervals
            for(int i=P1.first,j=P2.;
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

class Pair<T1,T2>{
    public T1 first;
    public T2 second;

    public Pair(T1 t1, T2 r2) {
        first=t1;
        second=t2;
    }
}
