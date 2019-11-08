/*
Anthony & Henry
Sorting Assignment
Mergesort vs Quicksort (and Bubblesort and Combsort too)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AC_HT_Sorting extends JFrame {
    private static JPanel content;
    //buttons
    private static JButton shuffle = new JButton("Shuffle");
    private static JButton Mergesort = new JButton("Mergesort");
    private static JButton Combsort = new JButton("Combsort");
    private static JButton Bubblesort = new JButton("Bubblesort");
    private static JButton Quicksort = new JButton("Quicksort");
    //menu
    private static JComboBox<Integer> barsMenu = new JComboBox<>(new Integer[]{20, 100, 1000, 10000, 1000000});
    private static int barCnt = 20; //number of bars
    private static DrawArea area;
    //for some sorting algorithms
    private static Bar[] bars;
    private static Bar tempMergBars[];
    private static int[] arr;
    private static int[] tempMergArr;
    private static boolean[] check;
    //number of compaisons and swaps
    private static long numComparisons = 0;
    private static long numSwaps = 0;
    private static JLabel comparisons = new JLabel("     Comparisons: " + numComparisons);
    private static JLabel swaps = new JLabel("Swaps: " + numSwaps);
    //delay between each animation
    private static int delay;
    private static JLabel delayLabel = new JLabel("    Delay: ");

    public AC_HT_Sorting() {
        ButtonListener buttonAction = new ButtonListener();
        shuffle.addActionListener(buttonAction);
        Mergesort.addActionListener(buttonAction);
        Combsort.addActionListener(buttonAction);
        Bubblesort.addActionListener(buttonAction);
        Quicksort.addActionListener(buttonAction);
        barsMenu.addActionListener(new ActionListener() { //gets the selected number of bars
            @Override
            public void actionPerformed(ActionEvent e) {
                barCnt = barsMenu.getItemAt(barsMenu.getSelectedIndex());
                bars = new Bar[barCnt];
                for (int i = 0; i < bars.length; i++) {
                    bars[i] = new Bar(i);
                }
                if (barCnt == 1000000)
                    Bubblesort.setEnabled(false); //bubblesort is too slow for 1 000 000
                else
                    Bubblesort.setEnabled(true);
                repaint();

            }
        });
        bars = new Bar[20]; //default to 20
        for (int i = 0; i < bars.length; i++) {
            bars[i] = new Bar(i);
        }
        //gui stuff
        content = new JPanel();
        content.setLayout(new BorderLayout());
        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());
        area = new DrawArea(1450, 900);

        options.add(shuffle);
        options.add(Mergesort);
        options.add(Combsort);
        options.add(Quicksort);
        options.add(Bubblesort);
        options.add(barsMenu);
        options.add(comparisons);
        options.add(swaps);
        options.add(delayLabel);
        content.add(options, "North");
        content.add(area, "South");
        setContentPane(content);
        pack();
        setTitle("Sorting - Anthony & Henry");
        setSize(1500, 960);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    class DrawArea extends JPanel {
        public DrawArea(int width, int height) {
            this.setPreferredSize(new Dimension(width, height)); // size
        }

        //calculates width height position of bar given barCnt and window size
        //(stolen off roger)
        public void paintComponent(Graphics g) {
            double d = 0.0;
            for (int i = 0, j = 0; i < bars.length; i++) {
                int h = (int) Math.round((bars[i].value() + 1.0) * (getHeight() - 35) / barCnt);
                int w = (int) (Math.round((i + 1.0) * getWidth() / barCnt) - Math.round(d));
                bars[i].show(g, j, getHeight() - h, w, h);

                d += 1.0 * getWidth() / barCnt;
                j += w;
            }
        }

    }
    //buttons stuff
    //delays for animation set for some arbitrary value divided by the number of bars
    //so its not too slow or fast
    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            numComparisons = 0;
            numSwaps = 0;
            if (e.getActionCommand().equals("Shuffle"))
                shuffle();
            else if (e.getActionCommand().equals("Mergesort")) {
                delay = 4500 / barCnt;
                delayLabel.setText("    Delay: " + delay + " ms");
                revalidate();
                mergeSort();
            } else if (e.getActionCommand().equals("Combsort")) {
                delay = 2000 / barCnt;
                delayLabel.setText("    Delay: " + delay + " ms");
                revalidate();
                combsort();
            } else if (e.getActionCommand().equals("Bubblesort")) {
                delay = 500 / barCnt;
                delayLabel.setText("    Delay: " + delay + " ms");
                revalidate();
                bubblesort();
            } else if (e.getActionCommand().equals("Quicksort")) {
                delay = 4500 / barCnt;
                delayLabel.setText("    Delay: " + delay + " ms");
                revalidate();
                quickSort();
            }
            content.repaint();
        }
    }

    public static void main(String[] args) {
        AC_HT_Sorting window = new AC_HT_Sorting();
        window.setVisible(true);
    }

    public void refresh() { //updates graphics and delays
        //uses paintimmediatly instead of repaint
        content.paintImmediately(0, 0, 1500, 960);
        comparisons.setText("     Comparisons: " + numComparisons);
        swaps.setText("Swaps: " + numSwaps);
        revalidate();

        try { //pause after updating graphics
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shuffle() { //randomize
        List<Bar> temp = Arrays.asList(bars);
        Collections.shuffle(temp);
        bars = temp.toArray(bars);
        numSwaps = 0;
        numComparisons = 0;
        refresh();
    }

    //******************************MERGESORT ALGORITHM***********************************
    //only calls refresh() to animate for less than 10 000 bars (too slow otherwise)
    //for 10 000 or 1 000 000 elements, it just immediately sorts with no delay and displays comparisons and swaps

    public void mergeSort() { //sets up the int array
        arr = new int[bars.length];
        for (int i = 0; i < bars.length; i++) {
            arr[i] = bars[i].value();
        }
        tempMergBars = new Bar[bars.length];
        tempMergArr = new int[bars.length];
        check = new boolean[bars.length];
        doMergeSort(0, bars.length - 1);
        refresh();

    }

    private void doMergeSort(int lowerIndex, int higherIndex) { //split into halves and then merge
        if (lowerIndex < higherIndex) {
            int middle = lowerIndex + (higherIndex - lowerIndex) / 2;
            // Below step sorts the left side of the array
            doMergeSort(lowerIndex, middle);
            // Below step sorts the right side of the array
            doMergeSort(middle + 1, higherIndex);
            // Now merge both sides
            mergeParts(lowerIndex, middle, higherIndex);
            if (barCnt < 10000) {
                for (Bar b : bars) b.deHighlight();
            }
        }
    }

    private void mergeParts(int lowerIndex, int middle, int higherIndex) { //helper method
        int counter = 0;
        for (int i = lowerIndex; i <= higherIndex; i++) {
            tempMergArr[i] = arr[i];
            tempMergBars[i] = bars[i];
        }
        int i = lowerIndex;
        int j = middle + 1;
        int k = lowerIndex;
        while (i <= middle && j <= higherIndex) {
            check[i] = true;
            check[j] = true;
            counter++;
            if (tempMergArr[i] <= tempMergArr[j]) {
                arr[k] = tempMergArr[i];
                bars[k] = tempMergBars[i];
                i++;
            } else {
                arr[k] = tempMergArr[j];
                bars[k] = tempMergBars[j];
                j++;

            }
            k++;
        }
        while (i <= middle) {
            arr[k] = tempMergArr[i];
            bars[k] = tempMergBars[i];
            k++;
            i++;
        }
        if (barCnt < 10000) {
            for (int x = 0; x < bars.length; x++) {
                if (check[x]) {
                    check[x] = false;
                    bars[x].compareHighlight();
                    numComparisons++;
                }
                else
                    bars[x].deHighlight();
            }
            refresh();
        }
        else {
            numComparisons += counter;
        }
    }

    //******************************COMBSORT ALGORITHM***********************************
    //only calls refresh() to animate for less than 10 000 bars (too slow otherwise)
    //for 10 000 or 1 000 000 elements, it just immediately sorts with no delay and displays comparisons and swaps

    private int getNextGap(int gap) { //helper method
        gap = (gap * 10) / 13;
        if (gap < 1)
            return 1;
        return gap;
    }

    public void combsort() {
        arr = new int[bars.length];
        for (int i = 0; i < bars.length; i++) {
            arr[i] = bars[i].value();
        }
        int n = arr.length;
        int gap = n;
        boolean swapped = true;
        while (gap != 1 || swapped) {
            gap = getNextGap(gap);
            swapped = false;
            for (int i = 0; i < n - gap; i++) {
                if (barCnt < 10000) {
                    bars[i].compareHighlight();
                    bars[i + gap].compareHighlight();
                    refresh();
                }
                numComparisons++;
                if (arr[i] > arr[i + gap]) {
                    int temp1 = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = temp1;
                    if (barCnt < 10000) {
                        bars[i].swapHighlight();
                        bars[i + gap].swapHighlight();
                        refresh();
                    }
                    numSwaps++;
                    Bar temp2 = bars[i];
                    bars[i] = bars[i + gap];
                    bars[i + gap] = temp2;
                    if (barCnt < 10000) {
                        bars[i].deHighlight();
                        bars[i + gap].deHighlight();
                        refresh();
                    }
                    swapped = true;
                } else {
                    if (barCnt < 10000) {
                        bars[i].deHighlight();
                        bars[i + gap].deHighlight();
                    }
                }
            }
        }
        refresh();
    }
    //******************************BUBBLESORT ALGORITHM***********************************
    //only calls refresh() to animate for less than 1000 bars (too slow otherwise)
    //for 1000, 10 000 or 1 000 000 elements, it just immediately sorts with no delay and displays comparisons and swaps

    public void bubblesort() {
        arr = new int[bars.length];
        for (int i = 0; i < bars.length; i++) {
            arr[i] = bars[i].value();
        }
        int n = arr.length;

        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                numComparisons++;
                if (barCnt < 1000) {
                    bars[j].compareHighlight();
                    bars[j + 1].compareHighlight();
                    refresh();
                }

                if (arr[j] > arr[j + 1]) {
                    int temp1 = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp1;

                    numSwaps++;
                    if (barCnt < 1000) {
                        bars[j].swapHighlight();
                        bars[j + 1].swapHighlight();
                        refresh();
                    }

                    Bar temp2 = bars[j];
                    bars[j] = bars[j + 1];
                    bars[j + 1] = temp2;

                    if (barCnt < 1000) {
                        bars[j].deHighlight();
                        bars[j + 1].deHighlight();
                        refresh();
                    }
                }
                else if (barCnt < 1000) {
                    bars[j].deHighlight();
                    bars[j + 1].deHighlight();
                }
            }
        }
        refresh();
    }
//******************************QUICKSORT ALGORITHM***********************************
//only calls refresh() to animate for less than 10 000 bars (too slow otherwise)
//for 10 000 or 1 000 000 elements, it just immediately sorts with no delay and displays comparisons and swaps

    public void quickSort() { //sets up the int array
        int[] temp = new int[bars.length];
        for (int i = 0; i < bars.length; i++)
            temp[i] = bars[i].value();
        quickSortAlg(0, bars.length - 1);
        refresh();
    }
    private void quickSortAlg(int left, int right) {
        int i = left, j = right;
        Bar tmp;
        int loc = (i+j)/2;
        int pivot = bars[(i+j)/2].value();

        while (i <= j) {
            while (bars[i].value()< pivot) {
                numComparisons++;
                if (barCnt < 10000) {
                    bars[i].compareHighlight();
                    bars[loc].pivotHighlight();
                    refresh();
                    bars[i].deHighlight();
                    bars[loc].deHighlight();
                }
                i++;
            }
            while (bars[j].value() > pivot) {
                numComparisons++;
                if (barCnt < 10000) {
                    bars[j].compareHighlight();
                    bars[loc].pivotHighlight();
                    refresh();
                    bars[j].deHighlight();
                    bars[loc].deHighlight();
                }
                j--;
            }
            if (i <= j) {
                numSwaps++;
                if (barCnt < 10000) {
                    bars[j].swapHighlight();
                    bars[i].swapHighlight();
                    refresh();
                }
                tmp = bars[i];
                bars[i] = bars[j];
                bars[j] = tmp;
                if (barCnt < 10000) {
                    bars[j].deHighlight();
                    bars[i].deHighlight();
                }
                i++;
                j--;
            }
        }

        if (left < j)
            quickSortAlg(left, j);
        if (i < right)
            quickSortAlg(i, right);
    }

}
//********************************************************************
class Bar implements Comparable<Bar>{ //object for each bar on screen
    private int h;
    private Color color;
    public Bar (int height) {
        h = height;
        color = Color.DARK_GRAY;
    }
    public int value() {
        return h;
    }
    public void swapHighlight() { //RED FOR BARS IT SWAPS
        color = Color.RED;
    }
    public void compareHighlight() { //GREEN FOR BARS IT COMPARES
        color = Color.GREEN;
    }
    public void pivotHighlight() { //CYAN FOR THE PIVOT (quicksort)
        color = Color.CYAN;
    }
    public void deHighlight() { //reset color
        color = Color.DARK_GRAY;
    }
    public void show (Graphics g, int x, int y, int w, int h) { //paints the rectangle
        Graphics2D g2 = (Graphics2D) g;
        Rectangle bar = new Rectangle(x, y, w, h);
        g2.setColor(color);
        g2.fill(bar);
    }
    @Override
    public int compareTo (Bar e) { //unused
        return Integer.compare(h, e.h);
    }
}
