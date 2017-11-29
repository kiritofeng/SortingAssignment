import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class SortingAssignment extends JFrame implements Runnable { //Hack for getting more stack size

    private Bar[]rect;
    private DrawArea D;
    private int barCnt;
    private static int DELAY=100;

    public static void main(String[] args) throws Exception {
        new Thread(null, new SortingAssignment(), "SortingAssignment", 1<<26).start(); //Increase the stack
    }

    public void run() {
        new SortingAssignment().setVisible(true);
    }

    public SortingAssignment() {
        barCnt=20;
        initBars();
        D=new DrawArea(1000,400);
        BtnListener btnl = new BtnListener();
        JButton shuffle = new JButton("Shuffle");
        shuffle.addActionListener(btnl);
        JButton qsort = new JButton("Quicksort");
        qsort.addActionListener(btnl);
        JButton tsort = new JButton("Timsort");
        tsort.addActionListener(btnl);
        JComboBox<Integer>num = new JComboBox<>(new Integer[]{20,50,100,1000});
        num.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new Thread() {
                    public void run() {
                        barCnt = (Integer) num.getSelectedItem();
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
        btns.add(shuffle);
        btns.add(qsort);
        btns.add(tsort);
        btns.add(num);
        content.add(btns,"North");
        content.add(D,"South");
        setContentPane(content);
        pack();
        setTitle("Quicksort Vs Timsort - Visual Comparision");
        setSize(1000,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initBars() {
        rect=new Bar[barCnt];
        for(int i=0;i<barCnt;i++) {
            rect[i]=new Bar((i+1)*400/barCnt,1000/barCnt,i);
        }
        repaint();
    }

    private class BtnListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            switch(e.getActionCommand()) {
                case "Shuffle":
                    new Thread() {
                        public void run() {
                            shuffle();
                        }
                    }.start();
                    break;
                case "Quicksort":
                    new Thread() {
                        public void run() {
                            try {
                                quicksort();
                            } catch (InterruptedException IE) {
                                //Do Nothing
                            }
                        }
                    }.start();
                    break;
                case "Timsort":
                    new Thread() {
                        public void run() {
                            try {
                                timsort();
                            } catch(InterruptedException IE) {
                                //Do nothing
                            }
                        }
                    }.start();
            }
            repaint();
        }

        private void shuffle() {
            for(int i=1;i<rect.length;i++) {
                int ind=(int)(Math.random()*i); //Get index
                Bar tmp=rect[i]; //Swap
                rect[i]=rect[ind];
                rect[ind]=tmp;
            }
            repaint();
        }

        private void quicksort() throws InterruptedException {
            quicksort(0,rect.length-1);
        }

        private void quicksort(int lft,int rht) throws InterruptedException {
            if(lft<rht) {
                int prt=lft-1; //Get partition
                Bar pivot=rect[rht]; //Get pivot value
                for(int i=lft;i<rht;i++) {
                    rect[i].setComparing(true);
                    pivot.setComparing(true);
                    repaint();
                    Thread.sleep(DELAY);
                    if(rect[i].compareTo(pivot)<=0) {
                        Bar tmp=rect[++prt];
                        rect[prt]=rect[i];
                        rect[i]=tmp;
                        rect[prt].setComparing(false);
                    } else
                        rect[i].setComparing(false);
                    pivot.setComparing(false);
                    repaint();
                    Thread.sleep(DELAY);
                }
                //Move parition into place
                Bar tmp=rect[++prt];
                rect[prt]=rect[rht];
                rect[rht]=tmp;
                repaint();
                //Recursively quicksort
                quicksort(lft,prt-1);
                quicksort(prt+1,rht);
            }
            repaint();
        }

        private void timsort() throws InterruptedException {
            Queue<Pair>Q=new LinkedList<>();
            int prevInd=0;
            Bar prevE=null;
            for(int i=0;i<rect.length;i++) {
                if(prevE==null) {
                    prevE=rect[i];
                    prevInd=i;
                } else {
                    prevE.setComparing(true);
                    rect[i].setComparing(true);
                    repaint();
                    Thread.sleep(DELAY);
                    prevE.setComparing(false);
                    rect[i].setComparing(false);
                    repaint();
                    Thread.sleep(DELAY);
                    if(prevE.compareTo(rect[i])<=0) {
                        prevE=rect[i];
                    } else {
                        Q.offer(new Pair(prevInd,i-1));
                        prevInd=i;
                        prevE=rect[i];
                    }
                }
            }
            Q.offer(new Pair(prevInd,rect.length-1));
            while(Q.size()>1) {
                Pair P1=Q.poll();
                while(P1.second>Q.peek().first) {
                    Q.offer(P1);
                    P1=Q.poll();
                }
                Pair P2=Q.poll();
                //P1 and P2 are two adjacent intervals
                Bar[]tmp=new Bar[P2.second-P1.first+1];
                for(int i=P1.first,j=P2.first,k=0;i<=P1.second||j<=P2.second;) {
                    if(i>P1.second)
                        tmp[k++]=rect[j++];
                    else if(j>P2.second)
                        tmp[k++]=rect[i++];
                    else {
                        rect[i].setComparing(true);
                        rect[j].setComparing(true);
                        repaint();
                        Thread.sleep(DELAY);
                        rect[i].setComparing(false);
                        rect[j].setComparing(false);
                        repaint();
                        Thread.sleep(DELAY);
                        if(rect[i].compareTo(rect[j])<=0)
                            tmp[k++]=rect[i++];
                        else
                            tmp[k++]=rect[j++];
                    }
                }
                //Copy the array over
                for(int i=P1.first,j=0;j<tmp.length;)
                    rect[i++]=tmp[j++];
                repaint();
                Q.offer(new Pair(P1.first,P2.second));
            }
        }
    }

    private class Bar implements Comparable<Bar> {
        protected final int height,width,val; //height and with of rectangle
        private boolean comparing;
        public Bar(int h, int w, int v) {
            comparing=false;
            height=h;
            width=w;
            val=v; //Tiebreak if there are too many bars
        }

        public void show(Graphics G,int x,int y) {
            if(comparing)
                G.setColor(Color.RED);
            else
                G.setColor(Color.GRAY);
            G.fillRect(x,y,width,height);
        }

        public void setComparing(boolean b) {
            comparing=b;
        }

        public int compareTo(Bar b) {
            return val-b.val;
        }
    }

    private class DrawArea extends JPanel {
        private int width,height;
        public DrawArea(int w,int h) {
            width=w;
            height=h;
            setPreferredSize(new Dimension(width,height));
        }

        public void paintComponent(Graphics G) {
            for(int i=0,j=0;i<rect.length;i++) {
                rect[i].show(G,j,height-rect[i].height);
                j+=rect[i].width;
            }
        }
    }
}

class Pair {
    public int first, second;

    public Pair(int f, int s) {
        first=f;
        second=s;
    }
}

