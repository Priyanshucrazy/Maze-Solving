package proj;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
public class Maze extends JFrame{
    final static int X = 1;
    //free space (white Square)
    final static int C = 0;
    //initial state
    final static int S = 2;
    //goal
    final static int E = 8;
    // the path
    final static int V = 9;
    //initial state (i,j)
    final static int START_I = 1, START_J = 1;
    //goal (i,j)
    final static int END_I = 2, END_J = 9;
    int[][] maze = new int[][]{ // the initial array for the maze
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 1, 0, 1, 1, 0, 8},
            {1, 0, 1, 1, 1, 0, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 1, 1, 1, 0, 1},
            {1, 1, 1, 1, 0, 1, 1, 1, 0, 1},
            {1, 1, 1, 1, 0, 1, 0, 0, 0, 1},
            {1, 1, 0, 1, 0, 1, 1, 0, 0, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 1}
    };

    int[][] arr;
    // Buttons For GUI (still not initialized)
    JButton solveStack;
    JButton solveBFS;
    JButton clear;
    JButton exit;
    JButton genRandom;
    // JLabel and JTextField For GUI (still not initialized)
    JLabel elapsedDfs;
    JTextField textDfs;
    JLabel elapsedBFS;
    JTextField textBFS;
    boolean repaint = false;
    // start time
    long startTime;
    //stop time
    long stopTime;
    //calculate the elapsed time
    long duration;
    //time for DFS
    double dfsTime;
    //time for BFS
    double bfsTime;

    int[][] savedMaze = clone();

    public Maze() {
        setTitle("Maze"); //Title For JFrame
        setSize(960, 530); // Size For JFrame (width,height)

        URL urlIcon = getClass().getResource("Maze.png"); // Path for image for The JFrame
        ImageIcon image = new ImageIcon(urlIcon);
// store the image in variable named image
        setIconImage(image.getImage());
// set The Image For JFrame
        setLocationRelativeTo(null);
// to make JFrame appear in the Middle of the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// To close the app when click on exit or (X)
        setLayout(null);

        // initialize objects for Jlabel and JTextField
        elapsedDfs = new JLabel("Elapsed Time :");
        elapsedBFS = new JLabel("Elapsed Time :");
        textDfs = new JTextField();
        textBFS = new JTextField();
        // initialize objects for Buttons
        solveStack = new JButton("Solve DFS");
        solveBFS = new JButton("Solve BFS");
        clear = new JButton("Clear");
        exit = new JButton("Exit");
        genRandom = new JButton("Generate Random Maze");
        // Add The Buttons to JFrame
        add(solveStack);
        add(solveBFS);
        add(clear);
        add(elapsedDfs);
        add(textDfs);
        add(elapsedBFS);
        add(textBFS);
        add(exit);
        add(genRandom);

        setVisible(true);

        solveStack.setBounds(500, 50, 100, 40);
        solveBFS.setBounds(630, 50, 100, 40);
        clear.setBounds(760, 50, 100, 40);
        exit.setBounds(760, 115, 100, 40);
        elapsedDfs.setBounds(500, 100, 100, 40);
        genRandom.setBounds(500, 180, 170, 40);
        elapsedBFS.setBounds(630, 100, 100, 40);
        textDfs.setBounds(500, 130, 100, 25);
        textBFS.setBounds(630, 130, 100, 25);
        // what happen when click on Generate Random Maze Button
        genRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x[][] = GenerateArray();
                repaint = true;

                restore(x);
                repaint(); // repaint the maze on the JFrame
            }
        });
        // what happen when click on Exit Button
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); //Close The App
            }
        });
        // what happen when click on Clear Button
        clear.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        if (arr == null) {
                                            repaint = true;
                                            restore(savedMaze);
                                            repaint();
                                        } else {
                                            repaint = true;
                                            restore(arr);
                                            repaint();
                                        }
                                        textBFS.setText("");
                                        textDfs.setText("");

                                    }
                                });
                // what happen when click on Solve DFS Button
                solveStack.addActionListener(new ActionListener() {
                                                 @Override
                                                 public void actionPerformed(ActionEvent e) {
                                                     if (arr == null) {
                                                         restore(savedMaze);
                                                         repaint = false;
                                                         solveStack();
                                                         repaint();
                                                     } else {
                                                         restore(arr);
                                                         repaint = false;
                                                         solveStack();
                                                         repaint();
                                                     }

                                                 }
                                             });
                        // what happen when click on Solve BFS Button
                        solveBFS.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (arr == null) {
                                    restore(savedMaze);
                                    repaint = false;
                                    solveQueue();
                                    repaint();
                                } else {
                                    restore(arr);
                                    repaint = false;
                                    solveQueue();
                                    repaint();
                                }

                            }
                        });
    }
    // get size of the maze
    public int Size() {
        return maze.length;
    }
    // Print the Maze to CL
    public void Print() {
        for (int i = 0; i < Size(); i++) { //go to every row
            for (int J = 0; J < Size(); J++) {
                System.out.print(maze[i][J]); //print the element
                System.out.print(' '); //print space
            }
            System.out.println(); // go to new line
        }
    }
    //return true if cell is within maze
    public boolean isInMaze(int i, int j) {
        if (i >= 0 && i < Size() && j >= 0 && j < Size()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isInMaze(MazePos pos) {
        return isInMaze(pos.i(), pos.j());
    }

    public int mark(int i, int j, int value) {
        assert (isInMaze(i, j));
        int temp = maze[i][j]; // store the original value in temp
        maze[i][j] = value;
        return temp; // return original value
    }
    public int mark(MazePos pos, int value) {
        return mark(pos.i(), pos.j(), value);
    }
    // return true if the node equal to v=9 (Green, Explored)
    public boolean isMarked(int i, int j) {
        assert (isInMaze(i, j));
        return (maze[i][j] == V);
    }
    public boolean isMarked(MazePos pos) {
        return isMarked(pos.i(), pos.j());
    }
    // return true if the node is equal to 0 (White, Unexplored)
    public boolean isClear(int i, int j) {
        assert (isInMaze(i, j));
        return (maze[i][j] != X && maze[i][j] != V);
    }
    public boolean isClear(MazePos pos) {
        return isClear(pos.i(), pos.j());
    }
    // to make sure if it is reach the goal (Goal Test)
    public boolean isFinal(int i, int j) {
        return (i == Maze.END_I && j == Maze.END_J);
    }

    public boolean isFinal(MazePos pos) {
        return isFinal(pos.i(), pos.j());
    }
    // make Copy from the original maze
    public int[][] clone() {
        int[][] mazeCopy = new int[Size()][Size()];
        for (int i = 0; i < Size(); i++) {
            for (int j = 0; j < Size(); j++) {
                mazeCopy[i][j] = maze[i][j];
            }
        }
        return mazeCopy;
    }
    // to restore the maze to the initial state
    public void restore(int[][] savedMazed) {
        for (int i = 0; i < Size(); i++) {
            for (int j = 0; j < Size(); j++) {
                maze[i][j] = savedMazed[i][j];
            }
        }
        maze[1][1] = 2; // the start point
        maze[2][9] = 8; // the goal
    }
    //generate random maze whith values 0 and 1
    public int[][] GenerateArray() {
        arr = new int[10][10];
        Random rnd = new Random();
        int min = 0;
        int high = 1;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int n = rnd.nextInt((high - min) + 1) + min;
                arr[i][j] = n;

            }
        }
        arr[0][1] = 0;arr[1][0] = 0;arr[2][1] = 0;arr[1][2] =
                0;//make sure all paths from initial state are legal moves (white block)
        arr[1][9] = 0;arr[2][8] = 0;arr[3][9] = 0;
//make sure all paths to goal are legal moves (white block)
// for(int i=0; i<10;i++){
// for(int j=0;j<10;j++){
// System.out.print(arr[i][j]);
// System.out.print(' ');
// }
// System.out.println("");
// }
        return arr;
    }

    //draw the maze on the JFrame
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.translate(70, 70);
        if (repaint == true) {
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[0].length; col++) {
                    Color color;
                    switch (maze[row][col]) {
                        case 1:
                            color = Color.darkGray; //block (black)
                            break;
                        case 8:
                            color = Color.RED; // goal (red)
                            break;
                        case 2:
                            color = Color.YELLOW; // initial state (yellow)
                            break;
                        // case '.' : color=Color.ORANGE; break;
                        default:
                            color = Color.WHITE; // white free
                    }
                    g.setColor(color);
                    g.fillRect(40 * col, 40 * row, 40, 40);
                    g.setColor(Color.BLUE);
                    g.drawRect(40 * col, 40 * row, 40, 40);

                }
            }
        }
        if (repaint == false) {
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[0].length; col++) {
                    Color color;
                    switch (maze[row][col]) {
                        case 1:
                            color = Color.darkGray;
                            break;
                        case 8:
                            color = Color.RED; // goal (red)
                            break;
                        case 2:
                            color = Color.YELLOW;
                            break;
                        case 9:
                            color = Color.green;
                            break;
                        default:
                            color = Color.WHITE;
                    }
                    g.setColor(color);
                    g.fillRect(40 * col, 40 * row, 40, 40);
                    g.setColor(Color.BLUE);
                    g.drawRect(40 * col, 40 * row, 40, 40);
                }
            }
        }
    }
    public static void main(String[] args) { // the main program
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Maze maze = new Maze();

            }
        });
    }
    public void solveStack() { //DFS correspond to Stack
        // start of the time
        startTime = System.nanoTime();
        //create stack of MazPos (MazPos (the node) is what we will be pushing and popping from the stack)
        Stack<MazePos> stack = new Stack<MazePos>();
        //insert the start node
        stack.push(new MazePos(START_I, START_I));
        MazePos crt; //current node
        MazePos next; //next node
        while (!stack.empty()) {//while stack not empty
            //get current position by popping from stack
            crt = stack.pop();
            if (isFinal(crt)) {
                break;
            }
            //mark the current position as explored
            mark(crt, V);
            //push its neighbours in the stack
            next = crt.north(); // go up from the current node
            if (isInMaze(next) && isClear(next)) {
                stack.push(next);
            }
            next = crt.east(); //go right from the current node
            if (isInMaze(next) && isClear(next)) {
                stack.push(next);
            }
            next = crt.west(); //go left from the current node
            if (isInMaze(next) && isClear(next)) {
                stack.push(next);
            }
            next = crt.south(); // go down from the current node
            if (isInMaze(next) && isClear(next)) {
                stack.push(next);
            }
        }
        if (!stack.empty()) {
                    stopTime = System.nanoTime();
            JOptionPane.showMessageDialog(rootPane, "You Got it");
        } else {
            JOptionPane.showMessageDialog(rootPane, "You Are stuck in the maze");
        }
        System.out.println("\nFind Goal By DFS : ");
        Print();
        // stop time

        duration = stopTime - startTime;
        dfsTime = (double)duration / 1000000; //convert to ms
        System.out.println(String.format("Time %1.3f ms", dfsTime));
        textDfs.setText(String.format("%1.3f ms", dfsTime));
    }
    public void solveQueue() { //BFS correspond to Queue.
        //start the timer
        startTime = System.nanoTime();
        LinkedList<MazePos> list = new LinkedList<MazePos>();
        // add initial node to the list
        list.add(new MazePos(START_I, START_J));
        MazePos crt, next;
        while (!list.isEmpty()) {
            //get current position
            crt = list.removeFirst();
            // to be sure if it reach the goal
            if (isFinal(crt)) {
                break;
            }
            //mark the current position as explored
            mark(crt, V);
            //add its neighbors in the queue
            next = crt.north(); //move up
            if (isInMaze(next) && isClear(next)) {
                list.add(next);
            }
            next = crt.east(); //move right
            if (isInMaze(next) && isClear(next)) {
                list.add(next);
            }
            next = crt.west(); //move left
            if (isInMaze(next) && isClear(next)) {
                list.add(next);
            }
            next = crt.south(); //move down
            if (isInMaze(next) && isClear(next)) {
                list.add(next);
            }
        }
        if (!list.isEmpty()) {
            stopTime = System.nanoTime(); //stop the timer
            JOptionPane.showMessageDialog(rootPane, "You Got it");
        } else {
            JOptionPane.showMessageDialog(rootPane, "You Are stuck in the maze");
        }
        System.out.println("\nFind Goal By BFS : ");
        Print();

        duration = stopTime - startTime;

        bfsTime = (double) duration / 1000000; //convert to ms
        System.out.println(String.format("Time %1.3f ms", bfsTime));
        textBFS.setText(String.format("%1.3f ms", bfsTime));

    }
}
