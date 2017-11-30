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
        new Thread(null, new SortingAssignment(), "SortingAssignment", 1<<26).start(); //Increase the stack
    }

    public void run() {
        new SortingAssignment().setVisible(true);
    }

    public SortingAssignment() {
        barCnt=20;
        initBars();
        setMinimumSize(new Dimension(800,500));
        setPreferredSize(new Dimension(800,500));
        D=new DrawArea(getWidth(),getHeight()-100);
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
        cntrls[4] = new JComboBox<Integer>(new Integer[]{20,50,100,1000});
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
        content.add(D,"South");
        setContentPane(content);
        pack();
        setTitle("Quicksort Vs Timsort - Visual Comparision");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //Handle resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                D.setHeight(getHeight()-100);
                D.setWidth(getWidth());
                new Thread() {
                    public void run() {
                        setBarSize();
                    }
                }.start();
            }
        });
    }

    private void initBars() {
        rect=new Bar[barCnt];
        for(int i=0;i<barCnt;i++)
            rect[i]=new Bar(i);
        setBarSize();
    }

    private void setBarSize() {
        //The problem is that you can't render fractions of a pixel
        //The solution is to not render some bars
        double d=0.0;
        for(int i=0;i<barCnt;i++) {
            rect[i].setHeight((rect[i].val+1)*(getHeight()-100)/barCnt);
            rect[i].setWidth((int)(Math.round((rect[i].val+1.0)*getWidth()/barCnt)-Math.round(d)));
            d+=1.0*getWidth()/barCnt;
        }
        repaint();
    }

    private class BtnListener implements ActionListener {

        //To reduce redudant code
        private abstract class BarThread extends Thread {
            public void run() {
                //Reenable all components
                for(Component c:cntrls)
                    c.setEnabled(true);
            }
        }

        public void actionPerformed(ActionEvent e) {
            //Disable all components
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
                            worstcase();
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

        private void worstcase() {
            initBars();
            for(int i=0;2*i<rect.length;i++) {
                Bar tmp=rect[i];
                rect[i]=rect[rect.length-1-i];
                rect[rect.length-1-i]=tmp;
            }
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
                    rect[i].setComparing(false);
                    pivot.setComparing(false);
                    repaint();
                    Thread.sleep(DELAY);
                    if(rect[i].compareTo(pivot)<=0) {
                        Bar tmp=rect[++prt];
                        rect[prt]=rect[i];
                        rect[i]=tmp;
                    }
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
        protected int height,width,val; //height and with of rectangle
        private boolean comparing;

        public Bar(int v) {
            height=width=0;
            comparing=false;
            val=v;
        }

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

        public void setHeight(int height) {
            this.height=height;
        }

        public void setWidth(int width) {
            this.width=width;
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

        public void setHeight(int height) {
            this.height = height;
            setSize(width,height);
        }

        public void setWidth(int width) {
            this.width = width;
            setSize(width,height);
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
