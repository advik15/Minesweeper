import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.border.*;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;


public class Minesweeper extends JPanel implements ActionListener,MouseListener
{
    JFrame frame;
    JToggleButton[][] grid;
    int dimR=9,dimC=9,numMines=10,numNotSelected;
    JPanel gridPanel;
    JButton button, reset;
    int buttonSize = 50;
    boolean firstClick = true;
	boolean gameOn;
	JMenuItem beginner, intermediate, expert;

	ImageIcon mineIcon, flag;
	ImageIcon[] numbers;

	GraphicsEnvironment ge;
	Font clockFont;

	Timer timer;
	int timePassed = 0;
	JTextField time;



	ImageIcon dead0, wait0, win0, smile0;


    public Minesweeper()
    {
        frame = new JFrame("Minesweeper");
        frame.add(this);

        mineIcon = new ImageIcon("mine.png");
		mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(buttonSize, buttonSize,Image.SCALE_SMOOTH));


		flag = new ImageIcon("flag.png");
		flag = new ImageIcon(flag.getImage().getScaledInstance(buttonSize, buttonSize,Image.SCALE_SMOOTH));


		numbers = new ImageIcon[8];
		for(int x = 0; x < 8; x++){
			numbers[x] = new ImageIcon((x+1)+ ".png");
			numbers[x] = new ImageIcon(numbers[x].getImage().getScaledInstance(buttonSize, buttonSize,Image.SCALE_SMOOTH));
		}

		dead0 = new ImageIcon("dead0.png");
		dead0 = new ImageIcon(dead0.getImage().getScaledInstance(buttonSize, buttonSize,Image.SCALE_SMOOTH));

		wait0 = new ImageIcon("wait0.png");
		wait0 = new ImageIcon(wait0.getImage().getScaledInstance(buttonSize, buttonSize,Image.SCALE_SMOOTH));

		win0 = new ImageIcon("win0.png");
		win0 = new ImageIcon(win0.getImage().getScaledInstance(buttonSize, buttonSize,Image.SCALE_SMOOTH));

		smile0 = new ImageIcon("smile0.png");
		smile0 = new ImageIcon(smile0.getImage().getScaledInstance(buttonSize, buttonSize,Image.SCALE_SMOOTH));


        setGrid(dimR,dimC);

        button = new JButton();

		JMenuBar bar = new JMenuBar();


		JMenu difficulty = new JMenu("Difficulty");

		bar.add(difficulty);

		beginner = new JMenuItem("Beginner");
		beginner.addActionListener(this);

		intermediate = new JMenuItem("Intermediate");
		intermediate.addActionListener(this);

		expert = new JMenuItem("Expert");
		expert.addActionListener(this);

		difficulty.add(beginner);
		difficulty.add(intermediate);
		difficulty.add(expert);
		reset = new JButton();
		reset.setIcon(smile0);
		reset.setFocusable(false);
		reset.addActionListener(this);
		bar.add(reset);

		bar.setLayout(new GridLayout(1,3));

		try{
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			clockFont = Font.createFont(Font.TRUETYPE_FONT, new  File("digital-7.ttf"));
			ge.registerFont(clockFont);

		}catch(IOException|FontFormatException e){

		}
		time = new JTextField();
		time.setFont(clockFont.deriveFont(18f));
		time.setBackground(Color.BLACK);
		time.setForeground(Color.GREEN);
		time.setEditable(false);
		time.setText("   "+0);
		bar.add(time);



		frame.add(bar, BorderLayout.NORTH);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public void setGrid(int dimR, int dimc)
    {
        if(gridPanel!=null){
            frame.remove(gridPanel);
        }
        gridPanel = new JPanel();
        grid = new JToggleButton[dimR][dimC];
        gridPanel.setLayout(new GridLayout(dimR,dimC));


        for(int r = 0; r<dimR; r++){
            for(int c = 0; c < dimC; c++){
                grid[r][c] = new JToggleButton();
                grid[r][c].setFocusable(false);
                grid[r][c].addMouseListener(this);
                grid[r][c].putClientProperty("row",r);
                grid[r][c].putClientProperty("column",c);
                grid[r][c].putClientProperty("state",0);
                gridPanel.add(grid[r][c]);
            }
        }
		numNotSelected = dimR * dimC;
		gameOn = true;

        frame.setSize(buttonSize*dimC,buttonSize*dimR);
        frame.add(gridPanel,BorderLayout.CENTER);
        frame.revalidate();
    }

    public void dropMine(int currRow, int currCol)
    {
        int count = numMines;
        while(count > 0)
        {
            int row = (int)(Math.random()*dimR);
            int col = (int)(Math.random()*dimC);

            int state = (int)grid[row][col].getClientProperty("state");
            if(state == 0 && Math.abs(row-currRow)>1 && Math.abs(col-currCol)>1)
            {
                //change the state of the button
                grid[row][col].putClientProperty("state", -1);
                count--;
            }
        }
        // used to count the mines around
       for(int r = 0; r < dimR; r++){
            for(int c = 0; c < dimC; c++){
              int mineCount = 0;
              int buttonState = (int)grid[r][c].getClientProperty("state");
              if(buttonState==-1) {
                  for (int rr = r - 1; rr <= r + 1; rr++) {
                      for (int cc = c - 1; cc <= c + 1; cc++) {
                          try {
                              int localButton = (int) grid[rr][cc].getClientProperty("state");
                              if (localButton != -1)
                                  grid[rr][cc].putClientProperty("state",localButton+1);

                          } catch (ArrayIndexOutOfBoundsException e) {
                          }
                      }
                  }
              }
            }
        }
/*
        for(int r = 0; r < dimR; r++) {
            for (int c = 0; c < dimC; c++) {
                int state = (int)grid[r][c].getClientProperty("state");
                grid[r][c].setText(""+state);
            }

        }

*/
    }

	public void expand(int row, int col){
		if(!grid[row][col].isSelected()){
			grid[row][col].setSelected(true);
			grid[row][col].setEnabled(false);
			numNotSelected--;
		}
		int state = (int)grid[row][col].getClientProperty("state");
		if(state>0)
		{
			grid[row][col].setIcon(numbers[state-1]);
			grid[row][col].setDisabledIcon(numbers[state-1]);

		}


		else
		{

			for(int r = row-1;r<=row+1;r++)
			{
				for(int c = col-1; c <= col+1;c++)
				{
					try
					{
						if(!grid[r][c].isSelected())
							expand(r,c);
					}catch(ArrayIndexOutOfBoundsException e){
					}
				}
			}

		}

	}


    public void mouseReleased(MouseEvent e)
    {
		reset.setIcon(smile0);
        int row = (int)((JToggleButton)e.getComponent()).getClientProperty("row");
        int column = (int)((JToggleButton)e.getComponent()).getClientProperty("column");

    	if(gameOn)
    	{
			if(e.getButton()==1 && grid[row][column].isEnabled())
			{

				if(firstClick)
				{
					timer = new Timer();
					timer.schedule(new UpdateTimer(),0,1000);


					dropMine(row,column);
					firstClick = false;
				}

				grid[row][column].setSelected(true);
				grid[row][column].setEnabled(false);

				numNotSelected--;
				int state = (int)grid[row][column].getClientProperty("state");
				if(state == -1)
				{
					gameOn = false;
					timer.cancel();
					grid[row][column].setContentAreaFilled(false);
					grid[row][column].setOpaque(true);
					grid[row][column].setBackground(Color.RED);
					displayMines();
					disableGrid();

					//JOptionPane.showMessageDialog(null, "You Lose!");

				}
				else
				{
					expand(row,column);
					checkWin();

				}
			}

			else if(e.getButton()==3)
			{
				if(!grid[row][column].isSelected())
				{
					if(grid[row][column].getIcon()==null)
					{
						grid[row][column].setIcon(flag);
						grid[row][column].setDisabledIcon(flag);

						grid[row][column].setEnabled(false);
					}
					else
					{
						grid[row][column].setIcon(null);
						grid[row][column].setDisabledIcon(null);

						grid[row][column].setEnabled(true);
					}

				}



			}


		}

	}

	public void disableGrid(){
		for(int r = 0; r< grid.length; r++){
			for(int c = 0; c < grid[0].length; c++){
				grid[r][c].setEnabled(false);
			}
		}
	}


	public void checkWin()
	{
		System.out.println(numNotSelected);
		if(numNotSelected == numMines){
			timer.cancel();
			reset.setIcon(win0);
			gameOn = false;
			disableGrid();
			//JOptionPane.showMessageDialog(null, "You Win!");
		}


	}

	public void displayMines(){
		for(int r = 0; r < grid.length;r++){
			for(int c = 0; c < grid[0].length; c++){
				int state = (int)grid[r][c].getClientProperty("state");
				if(state == -1){
					grid[r][c].setIcon(mineIcon);
					reset.setIcon(dead0);
					grid[r][c].setDisabledIcon(mineIcon);
					grid[r][c].setEnabled(false);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e){
		if(e.getSource() == beginner){
			dimR = 9;
			dimC= 9;
			numMines = 10;
		}

		if(e.getSource() == intermediate){
			dimR = 16;
			dimC= 16;
			numMines = 40;
		}

		if(e.getSource() == expert){
			dimR = 16;
			dimC= 40;
			numMines = 99;
		}

		reset.setIcon(smile0);
		reset.setDisabledIcon(smile0);

		if(timer!= null){
			timer.cancel();
		}
		timePassed=0;
		time.setText("   "+ timePassed);

		setGrid(dimR,dimC);
		firstClick = true;
		gameOn = true;


	}






    public void mousePressed(MouseEvent e)
    {
		reset.setIcon(wait0);
		reset.setDisabledIcon(wait0);
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public static void main(String[]args){
        Minesweeper app = new Minesweeper();

    }


    public class UpdateTimer extends TimerTask
    {
		public void run()
		{
			if(gameOn)
			{
				timePassed++;
				time.setText("   " +timePassed);
			}
		}
	}

}
