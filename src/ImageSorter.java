import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

public class ImageSorter extends JFrame {
    private JComboBox<String> sortComboBox;
    private JPanel imagePanel;
    Circle[] circles;

    public ImageSorter() {
        setTitle("Image Sorter");
        setBounds(10,10,1900, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sortComboBox = new JComboBox<>(new String[]{"Bubble sort", "Insertion sort", "Selection sort", "Merge sort", "Heap sort", "Quick sort"});
        JButton sortButton = new JButton("Sort");
        imagePanel = new JPanel();

        JPanel menu = new JPanel();
        menu.setBounds(10, 10, 700, 60);
        menu.setLayout(new GridLayout());
        menu.add(sortComboBox);
        menu.add(sortButton);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(menu,BorderLayout.NORTH);
        container.add(imagePanel, BorderLayout.SOUTH);

        circles = generateCircles();

        updateImagePanel(circles);

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortImages(circles);
            }
        });
    }
    private void sortImages(Circle[] circles) {
        String selectedSort = (String) sortComboBox.getSelectedItem();

        Thread sortingThread = new Thread(() -> {
            switch (selectedSort) {
                case "Bubble sort":
                    bubbleSort(circles);
                    break;
                case "Insertion sort":
                    insertionSort(circles);
                    break;
                case "Merge sort":
                    mergeSort(circles, 0, circles.length - 1);
                    break;
                case "Selection sort":
                    selectionSort(circles);
                    break;
                case "Quick sort":
                    quickSort(circles, 0, circles.length - 1);
                    break;
                case "Heap sort":
                    heapSort(circles);
                    break;
            }
        });
        sortingThread.start();
    }

    private Circle[] generateCircles() {
        Circle[] circles = new Circle[32];
        for (int i = 0; i < circles.length; i++) {
            int radius = (int) (Math.random() * 30) + 10;
            circles[i] = new Circle(radius);
        }
        return circles;
    }

    private void printCircles(Circle[] circles) {
        for (Circle circle : circles) {
            System.out.print(circle.getRadius() + " ");
        }
        System.out.println();
    }

    private void updateImagePanel(Circle[] circles) {
        imagePanel.removeAll();
        for (Circle circle : circles) {
            ImageIcon icon = createCircleIcon(circle.getRadius());
            JLabel label = new JLabel(icon);
            imagePanel.add(label);
        }
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private ImageIcon createCircleIcon(int radius) {
        int diameter = radius * 2;
        ImageIcon icon = new ImageIcon(new ImageIcon("src/pizza.png").getImage().getScaledInstance(diameter, diameter, Image.SCALE_DEFAULT));
        return icon;
    }

    private void slow(){
        SwingUtilities.invokeLater(() -> updateImagePanel(circles));
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void bubbleSort(Circle[] circles) {
        for (int i = 0; i < circles.length - 1; i++) {
            for (int j = 0; j < circles.length - i - 1; j++) {
                if (circles[j].getRadius() > circles[j + 1].getRadius()) {
                    Circle temp = circles[j];
                    circles[j] = circles[j + 1];
                    circles[j + 1] = temp;
                    slow();
                }
            }
        }
    }

    private void insertionSort(Circle[] circles) {
        for (int i = 1; i < circles.length; i++) {
            Circle key = circles[i];
            int j = i - 1;
            while (j >= 0 && circles[j].getRadius() > key.getRadius()) {
                circles[j + 1] = circles[j];
                j = j - 1;
            }
            circles[j + 1] = key;

            slow();
        }
    }

    private void mergeSort(Circle[] circles, int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            mergeSort(circles, l, m);
            mergeSort(circles, m+1, r);
            merge(circles,l,r);
            slow();
        }
    }

    private void merge(Circle[] circles, int l, int r) {
        int m = (l+r)/2;
        int n1 = m - l + 1;
        int n2 = r - m;

        Circle[] L = new Circle[n1], R = new Circle[n2];

        for (int j = 0; j < n1; j++) L[j] = circles[j];
        for (int j = 0; j < n2; j++) R[j] = circles[m+1+j];

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (L[i].getRadius() <= R[j].getRadius()) {
                circles[k] = L[i];
                i++;
            } else {
                circles[k] = R[j];
                j++;
            }
            k++;
        }
        while (i < n1) {
            circles[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            circles[k] = R[j];
            j++;
            k++;
        }
    }

    private void selectionSort(Circle[] circles) {
        int n = circles.length;
        for (int i = 0; i < n-1; i++) {
            int minIndex = i;
            for (int j = i+1; j < n; j++) {
                if (circles[j].getRadius() < circles[minIndex].getRadius()) {
                    minIndex = j;
                }
            }
            Circle temp = circles[minIndex];
            circles[minIndex] = circles[i];
            circles[i] = temp;
            slow();
        }
    }

    private void quickSort(Circle[] circles, int low, int high) {
        if (low < high) {
            int pi = partition(circles, low, high);
            quickSort(circles, low, pi - 1);
            quickSort(circles, pi + 1, high);
        }
    }

    private int partition(Circle[] circles, int low, int high) {
        int pivot = circles[high].getRadius();
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (circles[j].getRadius() <= pivot) {
                i++;
                Circle temp = circles[i];
                circles[i] = circles[j];
                circles[j] = temp;
                slow();
            }
        }
        Circle temp = circles[i + 1];
        circles[i + 1] = circles[high];
        circles[high] = temp;
        return i + 1;
    }

    private void heapSort(Circle[] circles) {
        int n = circles.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(circles, n, i);
        }
        for (int i = n - 1; i > 0; i--) {
            Circle temp = circles[0];
            circles[0] = circles[i];
            circles[i] = temp;
            heapify(circles, i, 0);
            slow();
        }
    }

    private void heapify(Circle[] circles, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < n && circles[l].getRadius() > circles[largest].getRadius()) {
            largest = l;
        }
        if (r < n && circles[r].getRadius() > circles[largest].getRadius()) {
            largest = r;
        }
        if (largest != i) {
            Circle swap = circles[i];
            circles[i] = circles[largest];
            circles[largest] = swap;
            heapify(circles, n, largest);
        }
    }



    public static void main(String[] args) {
        ImageSorter imageSorter = new ImageSorter();
        imageSorter.setVisible(true);
    }
}