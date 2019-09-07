import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Stack;

public class Visualizer extends JFrame{
    TileButton[][] gridArray = new TileButton[20][20];
    JPanel gridPanel = new JPanel(new GridLayout(20,20));
    int [][] repArray = new int[20][20];
    String currentKey = "";
    String algoKeyResult  = "A*";
    boolean isEndPointMoving = false;
    TileButton movingButton;

    //starting conditions
    int sRow = 18;
    int sCol = 19;
    int eRow = 5;
    int eCol = 2;


    public Visualizer(){
        super("Visualizer");
        this.setSize(800,820);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initGrid();
        initUI();
        this.setVisible(true);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
    }

    public void initPoints(){
        repArray[sRow][sCol] = 10;
        repArray[eRow][eCol] = 20;
    }
    //used to create the UI
    public void initUI(){
        JButton retry = new JButton("Reset");
        initPoints();
        JButton runSearch = new JButton("Run " + algoKeyResult);
        JPanel actions = new JPanel(new GridLayout(1, 2));
        actions.add(runSearch);
        actions.add(retry);
        this.add(actions, BorderLayout.SOUTH);
        //actions to reset the board
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyCode = e.getKeyChar();
               switch (keyCode){
                   //set the current key or algortithm to a certain algo based on character clicked
                   case 'c':
                       currentKey = "C";
                       break;
                   case 'e':
                       currentKey = "E";
                       break;
                   case 'a':
                       algoKeyResult = "A*";
                       runSearch.setText("Run " + algoKeyResult);
                       break;
                   case 'b':
                       algoKeyResult = "BFS";
                       runSearch.setText("Run " + algoKeyResult);
                       break;
                   default:
                       currentKey = "";


               }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                currentKey = "";
            }
        });
        //removes focus
        runSearch.setFocusable(false);
        retry.setFocusable(false);
        retry.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for(int i = 0; i < repArray.length;i ++){
                            for(int j = 0 ; j <repArray[i].length; j++){

                                if(repArray[i][j] == -5 || repArray[i][j] == 1){
                                    repArray[i][j] = 0;
                                }
                            }
                        }
                        modifyGrid(repArray);
                    }
                }
        );
        //actions to start the board
        runSearch.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Controller c = new Controller(copyArray(repArray), sRow, sCol, eRow, eCol, algoKeyResult);
                        LocNode finalNode = c.getPath();
                        ColorChanger colorChanger = new ColorChanger(c.requestHistory(), finalNode, gridArray, c.requestCalculations());
                        colorChanger.start();


                    }
                }
        );

    }

    //reset repArray based on tiles
    public void computeGrid(){
        for(int i = 0; i < gridArray.length; i++){
            for(int j = 0; j <gridArray[0].length; j++){
                if(gridArray[i][j].isClicked){
                    repArray[i][j] = -1;
                }
                else{
                    if(repArray[i][j] != 20 && repArray[i][j] != 10){
                        repArray[i][j] = 0;
                    }
                }
            }
        }

    }


    public int[][] copyArray(int [][] array){
        int [][] newArray = new int[array.length][];
        for(int i = 0; i < array.length; i++){
            newArray[i] = array[i].clone();
        }
        return newArray;
    }

    public void modifyGrid(int [][] repArray){
        for(int i = 0; i < repArray.length; i++)
            for(int j = 0; j < repArray[i].length; j++){
                if(repArray[i][j] != -1){
                    if(repArray[i][j] == 10){
                        System.out.println("Ran across it");
                        gridArray[i][j].setBackground(Color.orange);
                    }
                    else if(repArray[i][j] == 20){
                        System.out.println("Ran across it");
                        gridArray[i][j].setBackground(Color.orange);
                    }
                    else{
                        gridArray[i][j].setBackground(Color.white);
                        gridArray[i][j].isClicked = false;
                    }
                    gridArray[i][j].setText("");

            }
    }
        this.revalidate();
            this.repaint();

}

    public void handleMouseClicks(int row, int col, MouseEvent e){
        TileButton clickedButton = (TileButton) e.getSource();
        //swapping the end points
        if(isEndPointMoving && !(clickedButton.val > 0)){
            if(movingButton.val == 10){
                repArray[sRow][sCol] = 0;
                repArray[row][col] = 10;
                sRow = row;
                sCol = col;

            }
            else{
                repArray[eRow][eCol] = 0;
                repArray[row][col] = 20;
                eRow = row;
                eCol = col;
            }
            isEndPointMoving = false;
            movingButton = null;
            this.remove(gridPanel);
            initGrid();

        }else{
            if(clickedButton.isClicked){
                clickedButton.setBackground(Color.white);
            }
            else{
                clickedButton.setBackground(Color.black);
            }
            clickedButton.isClicked = !clickedButton.isClicked;
        }
        computeGrid();

    }

    public void handleMouseEntered(MouseEvent e){
        TileButton clickedButton = (TileButton) e.getSource();
        //show the hover effect of the end point when the mouese comes into the tile
        if(isEndPointMoving && !(clickedButton.val > 0)){
            clickedButton.setBackground(Color.orange);

        }
    }

    public void handleMouseExited(MouseEvent e){
        TileButton clickedButton = (TileButton) e.getSource();
        //show the hover effect of the end point when the moves leaves the Tile
        if(isEndPointMoving){
            if(clickedButton.isClicked){
                clickedButton.setBackground(Color.black);
            }
            else{
                clickedButton.setBackground(Color.white);
            }
        }
    }

    public void handleMouseMoved(MouseEvent e){
        TileButton clickedButton = (TileButton) e.getSource();
        //if the end point isnt moving create and destory walls
        if(!(isEndPointMoving && !(clickedButton.val > 0))){
            Color color = null;
            boolean willBeAWall = false;
            if(currentKey.equals("C")){
                color = Color.black;
                willBeAWall = true;
            }
            else if(currentKey.equals("E")){
                color = Color.white;

            }

            if(color != null){
                clickedButton.setBackground(color);
                clickedButton.isClicked = willBeAWall;
                computeGrid();
            }

        }

    }

    public void initGrid(){
        gridPanel = new JPanel(new GridLayout(20,20));
        for(int i = 0; i < gridArray.length; i++)
            for(int j = 0; j < gridArray.length; j++){

                final int row = i;
                final int col = j;

                if(!((row == sRow && col == sCol) || (row == eRow && col == eCol))){
                    gridArray[row][col] = new TileButton();
                    gridArray[row][col].setFocusable(false);
                    gridArray[row][col].setMargin(new Insets(0, 0, 0, 0));
                    //keep walls
                    if(repArray[row][col] == -1){
                        gridArray[row][col].setBackground(Color.black);
                        gridArray[row][col].isClicked = true;
                    }
                     //handles click of the TileButton to turn the button to a wall
                     gridArray[row][col].addMouseListener(new MouseListener() {
                         @Override


                         public void mouseClicked(MouseEvent e) {
                            handleMouseClicks(row, col, e);
                         }

                         @Override
                         public void mousePressed(MouseEvent e) {

                         }

                         @Override
                         public void mouseReleased(MouseEvent e) {

                         }

                         @Override
                         public void mouseEntered(MouseEvent e) {
                             handleMouseEntered(e);
                         }

                         @Override
                         public void mouseExited(MouseEvent e) {
                                handleMouseExited(e);
                         }
                     });

                     //handles the auto-wall creation of the button
                     gridArray[i][j].addMouseMotionListener(new MouseMotionListener() {
                         @Override
                         public void mouseDragged(MouseEvent e) {
                         }

                         @Override
                         public void mouseMoved(MouseEvent e) {
                             handleMouseMoved(e);
                         }


                     });
                 }

                 //this is for end points
                 else{
                     if(sRow == row && sCol == col ){
                         gridArray[i][j] = new TileButton(10);
                     }
                     else{
                         gridArray[i][j] = new TileButton(20);
                     }
                     gridArray[i][j].setBackground(Color.ORANGE);
                     gridArray[i][j].setFocusable(false);
                     gridArray[i][j].setMargin(new Insets(0, 0, 0, 0));

                     gridArray[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if(!isEndPointMoving){
                                movingButton = (TileButton) e.getSource();
                            }
                            isEndPointMoving = !isEndPointMoving;
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {

                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {

                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                 }
                    gridPanel.add(gridArray[i][j]);
            }

            this.add(gridPanel);
            this.revalidate();
            this.repaint();
        }

    public static void main(String[] args) {
        new Visualizer();
    }


}

class TileButton extends JButton{
    boolean isClicked = false;
    int val = -999;

    public TileButton(){
        super();
        this.setBackground(Color.WHITE);
    }

    public TileButton(int v){
        super();
        this.val = v;
        this.setBackground(Color.orange);
    }

}


//used to show the actual algorithm path and show calculations with a delay
class ColorChanger extends Thread{
    LocNode result;
    TileButton [][] gridArray;
    LinkedList<int [][]> history;
    LinkedList<int [][]> calculations;
    public ColorChanger(LinkedList h, LocNode r, TileButton[][] g, LinkedList c){
        super();
        result = r;
        gridArray = g;
        history = h;
        calculations =c;

    }
    public void run(){
        //show the history of the moves with a delay
        while(!history.isEmpty()){
            try{
                this.sleep(25);
                int [][] gridMoment = history.getLast();
                processGridMoment(gridMoment);
                if(calculations != null){
                    calculateGridMoment(history.getLast(),calculations.getLast());
                }
            }
            catch(Exception e1){
                e1.printStackTrace();
            }
            history.removeLast();
            if(calculations != null){
                calculations.removeLast();
            }

        }
        if(result == null){
            JOptionPane.showMessageDialog(null, "There is not path");
        }
        else{
            //result delay
            while(result != null){
                try{
                    this.sleep(100);
                    gridArray[result.row][result.col].setBackground(Color.cyan);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                result = result.previous;
            }
        }



    }

    public void processGridMoment(int [][] moment){
        for(int i =0; i < moment.length; i++){
            for(int j = 0; j < moment[i].length; j++){
                if(moment[i][j] == -5){
                    gridArray[i][j].setBackground(Color.RED);
                }
                if(moment[i][j] == 1){
                    gridArray[i][j].setBackground(Color.green);
                }
            }
        }
    }

    public void calculateGridMoment(int [][] moment, int [][] calcMoment){

        for(int i =0; i < moment.length; i++){
            for(int j = 0; j < moment[i].length; j++){
                if(moment[i][j] == -5 || moment[i][j] == 1){
                    gridArray[i][j].setText(calcMoment[i][j] + "");
                    gridArray[i][j].setForeground(Color.white);
                    gridArray[i][j].setFont(new Font("Helvetica", Font.BOLD, 12));



                }

            }
        }
    }
}