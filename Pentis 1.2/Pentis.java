import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Font;

import java.util.Scanner;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;


public class Pentis extends Canvas implements KeyListener,WindowListener{
	final int	MAIN_SCREEN			= 0,
				MAIN_MENU			= 1,
				SET_CONTROLS		= 2,
				ENTERING_HIGH_SCORE	= 3,
				HIGH_SCORES			= 4,
				GAME_SETUP			= 5,
				PLAYING				= 6,
				PAUSED				= 7,
				GAME_OVER			= 8;

	int state=MAIN_SCREEN;
	int menuPoint = 0;
	boolean adjustingControls = false;
	boolean keyError = false;

	private boolean gameRunning = true;
	int x=4,y=-2;
	Font font = new Font("Dialog",Font.PLAIN,20);
	int keydcount=0,keyacount=0;
	private BufferStrategy strategy;
	int xPos=0,yPos=0;
	final int width=800,height=600;
	final int boardx=240,boardy=20,nextx=560,nexty=260;
	int decendCounter = 0;
	int gameEndingCounter;
	Image block = Toolkit.getDefaultToolkit().getImage("block.png");
	float brightness = 0.5f;
	int NoP;
	final int boardWidth=13,boardHeight=25;
	int[][][] pieces = new int[5][5][20];
	int[][] board = new int[boardHeight][boardWidth];

//WORK ON THESE!!!!
	int nextPieceNum;
	int currentPieceNum;
	int[] pieceOrder;

	int[][] currentPiece = {{0,0,0,0,0},
							{0,0,0,0,0},
							{0,0,0,0,0},
							{0,0,0,0,0},
							{0,0,0,0,0}};

	int[][] pieceTemp={	{0,0,0,0,0},
						{0,0,0,0,0},
						{0,0,0,0,0},
						{0,0,0,0,0},
						{0,0,0,0,0}};

	int[][] pentisLogo={{1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
						{1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,0,0,0,5,5,0,0,0,0,0,0},
						{1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,0,0,0,5,5,0,0,0,0,0,0},
						{1,1,0,0,1,1,0,0,2,2,2,2,0,0,3,0,3,3,3,0,0,4,4,4,4,4,4,0,0,0,0,0,6,6,6,0},
						{1,1,1,1,1,1,0,2,2,2,2,2,2,0,3,3,3,3,3,3,0,4,4,4,4,4,4,0,5,5,0,6,6,6,6,6},
						{1,1,1,1,1,0,0,2,2,0,0,2,2,0,3,3,0,0,3,3,0,0,0,4,4,0,0,0,5,5,0,6,6,0,0,0},
						{1,1,0,0,0,0,0,2,2,2,2,2,2,0,3,3,0,0,3,3,0,0,0,4,4,0,0,0,5,5,0,6,6,6,6,6},
						{1,1,0,0,0,0,0,2,2,0,0,0,0,0,3,3,0,0,3,3,0,0,0,4,4,0,0,0,5,5,0,0,0,0,6,6},
						{1,1,0,0,0,0,0,2,2,2,2,2,2,0,3,3,0,0,3,3,0,0,4,4,4,0,0,0,5,5,0,6,6,6,6,6},
						{1,1,0,0,0,0,0,0,2,2,2,2,0,0,3,3,0,0,3,3,0,0,4,4,0,0,0,0,5,5,0,0,6,6,6,0}};

	int[] scores=new int[20];
	StringBuffer[] scoreHolders=new StringBuffer[20];

	int level;
	int score;
	int linesCleared;
	int dropPoints=0;
	int highScorePosit=20;

	DebugWindow debug = new DebugWindow("Debug",null,JFrame.HIDE_ON_CLOSE,false);

	GameKey enterKey,down,left,right,up,softDrop,hardDrop,moveLeft,moveRight,rotateCW,rotateCCW,mirrorHoriz,mirrorVert,pauseKey;

	FrameRateLimiter fps = new FrameRateLimiter(50);

	public Pentis(){
		System.setOut(debug.getPrintStream());
		debug.setLocation(width+2,0);

		enterKey = new GameKey(KeyEvent.	VK_ENTER,5);//KeyEvent.KEY_LOCATION_STANDARD
		down = new GameKey(KeyEvent.		VK_DOWN,5);
		left = new GameKey(KeyEvent.		VK_LEFT,5);
		right = new GameKey(KeyEvent.		VK_RIGHT,5);
		up = new GameKey(KeyEvent.			VK_UP,5);

		softDrop = new GameKey(KeyEvent.	VK_DOWN,5);
		hardDrop = new GameKey(KeyEvent.	VK_SPACE,5);
		moveLeft = new GameKey(KeyEvent.	VK_LEFT,5);
		moveRight = new GameKey(KeyEvent.	VK_RIGHT,5);
		rotateCW = new GameKey(KeyEvent.	VK_V,5);
		rotateCCW = new GameKey(KeyEvent.	VK_C,5);
		mirrorHoriz = new GameKey(KeyEvent.	VK_X,5);
		mirrorVert = new GameKey(KeyEvent.	VK_Z,5);
		pauseKey = new GameKey(KeyEvent.	VK_P,5);

		getPieces();
		getScores();
		loadPreferences();
		JFrame container = new JFrame("Pentis");
		container.setIconImage(new ImageIcon("mylogo.png").getImage());
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(width-2,height-2));
		panel.setLayout(null);
		setBounds(0,0,width,height);
		container.setLocation(xPos,yPos);
		panel.add(this);
		setIgnoreRepaint(true);
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel.setBackground(Color.BLACK);
		container.addWindowListener(this);
		addKeyListener(this);
		requestFocus();
		createBufferStrategy(2);
		strategy = getBufferStrategy();
	}

	public void gameLoop() {
		while (gameRunning) {
			logic();
			paint();
			debug.updateText();
			fps.limitFPS();
		}
		savePreferences();
	}

	public void paint(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setFont(font);
		if(state==MAIN_SCREEN){
			g.setColor(Color.BLACK);
			g.fillRect(0,0,width,height);
			g.setColor(Color.WHITE);
			g.drawString("Press Enter",347,450);
			g.drawString("Version 1.2",43,270);
			g.setColor(Color.CYAN);
			g.drawString("Created by Henrik Leppä (2011)",17,570);
			for(int j=0;j<10;j++){
				for(int i=0;i<36;i++){
					if(pentisLogo[j][i]>0){
						g.setColor(Color.getHSBColor((1.0f/6)*(pentisLogo[j][i]-1),1.0f,brightness));
						g.fillRect(40+i*20,40+j*20,20,20);
						g.drawImage(block,40+i*20,40+j*20,20,20,null);
					}
				}
			}
		}
		else if(state==MAIN_MENU){
			g.setColor(Color.getHSBColor(0.5f, 0.5f, 0.5f));
			g.fillRect(0,0,width,height);

			g.setColor(Color.BLACK);
			g.drawString("Main menu",320,60);

			if(menuPoint==0){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			g.drawString("Start game",320,200);
			drawButton(g,310,174,190,40);

			if(menuPoint==1){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			g.drawString("Change keys",320,240);
			drawButton(g,310,214,190,40);

			if(menuPoint==2){g.setColor(Color.ORANGE);}
			else{g.setColor(Color.RED);}
			g.drawString("Instructions",320,280);
			drawButton(g,310,254,190,40);

			if(menuPoint==3){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			g.drawString("See high scores",320,320);
			drawButton(g,310,294,190,40);

			if(menuPoint==4){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			g.drawString("Quit",320,360);
			drawButton(g,310,334,190,40);
		}
		else if(state==SET_CONTROLS){
			g.setColor(Color.getHSBColor(0.5f, 0.5f, 0.5f));
			g.fillRect(0,0,width,height);

			g.setColor(Color.BLACK);
			g.drawString("Controls menu",320,60);

			if(menuPoint==0){g.setColor(Color.ORANGE);}
			else{g.setColor(Color.RED);}
			drawButton(g,296,134,190,40);
			g.drawString("Return defaults",320,160);

			if(menuPoint==1){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,174,190,40);
			drawButton(g,411,174,190,40);
			g.drawString("Soft drop",220,200);
			g.drawString(softDrop.getCodeAsString(),420,200);

			if(menuPoint==2){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,214,190,40);
			drawButton(g,411,214,190,40);
			g.drawString("Hard drop",220,240);
			g.drawString(hardDrop.getCodeAsString(),420,240);

			if(menuPoint==3){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,254,190,40);
			drawButton(g,411,254,190,40);
			g.drawString("Move left",220,280);
			g.drawString(moveLeft.getCodeAsString(),420,280);

			if(menuPoint==4){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,294,190,40);
			drawButton(g,411,294,190,40);
			g.drawString("Move right",220,320);
			g.drawString(moveRight.getCodeAsString(),420,320);

			if(menuPoint==5){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,334,190,40);
			drawButton(g,411,334,190,40);
			g.drawString("Rotate CW",220,360);
			g.drawString(rotateCW.getCodeAsString(),420,360);

			if(menuPoint==6){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,374,190,40);
			drawButton(g,411,374,190,40);
			g.drawString("Rotate CCW",220,400);
			g.drawString(rotateCCW.getCodeAsString(),420,400);

			if(menuPoint==7){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,414,190,40);
			drawButton(g,411,414,190,40);
			g.drawString("Mirror horizontally",220,440);
			g.drawString(mirrorHoriz.getCodeAsString(),420,440);

			if(menuPoint==8){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,454,190,40);
			drawButton(g,411,454,190,40);
			g.drawString("Mirror vertically",220,480);
			g.drawString(mirrorVert.getCodeAsString(),420,480);

			if(menuPoint==9){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,210,494,190,40);
			drawButton(g,411,494,190,40);
			g.drawString("Pause",220,520);
			g.drawString(pauseKey.getCodeAsString(),420,520);

			if(menuPoint==10){g.setColor(Color.WHITE);}
			else{g.setColor(Color.BLACK);}
			drawButton(g,296,534,190,40);
			g.drawString("Return to main menu",300,560);

			if(adjustingControls){
				g.setColor(Color.DARK_GRAY);
				g.fillRect(200,150,450,200);
				g.setColor(Color.WHITE);
				g.drawString("Press the key you want to assign",220,200);
				switch(menuPoint){
					case 1:g.drawString("Current key: " + softDrop.getCodeAsString(),220,240);break;
					case 2:g.drawString("Current key: " + hardDrop.getCodeAsString(),220,240);break;
					case 3:g.drawString("Current key: " + moveLeft.getCodeAsString(),220,240);break;
					case 4:g.drawString("Current key: " + moveRight.getCodeAsString(),220,240);break;
					case 5:g.drawString("Current key: " + rotateCW.getCodeAsString(),220,240);break;
					case 6:g.drawString("Current key: " + rotateCCW.getCodeAsString(),220,240);break;
					case 7:g.drawString("Current key: " + mirrorHoriz.getCodeAsString(),220,240);break;
					case 8:g.drawString("Current key: " + mirrorVert.getCodeAsString(),220,240);break;
					case 9:g.drawString("Current key: " + pauseKey.getCodeAsString(),220,240);break;
				}
				if(keyError){
					g.setColor(Color.RED);
					g.drawString("Java cannot recognize this key,",220,280);
					g.drawString("pick another one!",220,320);
				}
			}
		}
		else if(state==HIGH_SCORES||state==ENTERING_HIGH_SCORE){
			g.setColor(Color.GRAY);
			g.fillRect(0,0,width,height);
			g.setColor(Color.BLACK);
			g.fillRect(boardx+20,boardy+20,20*boardWidth,20*boardHeight);

			drawArea(g,boardx,boardy,boardWidth,boardHeight);

			g.setColor(Color.WHITE);
			g.drawString("High scores",270,60);
			for(int n=0;n<20;n++){
				if(scoreHolders[n]!=null){
					if(n==highScorePosit&&state==ENTERING_HIGH_SCORE){
						g.setColor(Color.GREEN);
					}
					else{
						g.setColor(Color.WHITE);
					}
					g.drawString(n+1+". "+scoreHolders[n]+"   "+scores[n],270,80+n*20);
				}
			}
			if(state==ENTERING_HIGH_SCORE){
				g.setColor(Color.RED);
				g.drawString("Congratulations,",320,480);
				g.drawString("type your name,",320,500);
				g.drawString("and press Enter",320,520);
			}
		}

		else if(state==PLAYING||state==PAUSED||state==GAME_OVER){
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0,0,width,height);

			g.setColor(Color.BLACK);
			g.fillRect(boardx+20,boardy+20,20*boardWidth,20*boardHeight);
			g.fillRect(nextx+20,nexty+20,100,100);

//			drawArea(g,boardx,boardy,boardWidth,boardHeight);
			drawButton(g,boardx,boardy,boardWidth*20+40,boardHeight*20+40);
			drawInside(g,boardx,boardy,boardWidth*20+40,boardHeight*20+40);

//			drawArea(g,nextx,nexty,5,5);
			drawButton(g,nextx,nexty,5*20+40,5*20+40);
			drawInside(g,nextx,nexty,5*20+40,5*20+40);

			g.setColor(Color.WHITE);
			g.drawString("Lines cleared",550,20);
			g.drawString(""+linesCleared,680,20);
			g.drawString("Level",550,40);
			g.drawString(""+level,680,40);
			g.drawString("Score",550,60);
			g.drawString(""+score,680,60);
			g.drawString("High score",550,80);
			if(score>scores[0]){
				g.drawString(""+score,680,80);
			}
			else{
				g.drawString(""+scores[0],680,80);
			}


			if(state==PAUSED){
				g.setColor(Color.WHITE);
				g.drawString("Paused",boardx+118,boardy+280);
			}
			else if(state==PLAYING||state==GAME_OVER){
				for(int j=0;j<5;j++){
					for(int i=0;i<5;i++){
						if(currentPiece[j][i]>0&&y+j>=0){
							if(state==GAME_OVER&&y+j<=gameEndingCounter/5){
								g.setColor(Color.GRAY);
							}
							else{
								g.setColor(Color.getHSBColor((1.0f/NoP)*currentPiece[j][i],1.0f,brightness));
							}
							g.fillRect((boardx+20+(x+i)*20),(boardy+20+(y+j)*20),20,20);
							g.drawImage(block,(boardx+20+(x+i)*20),(boardy+20+(y+j)*20),20,20,null);
						}
					}
				}

				for(int j=0;j<boardHeight;j++){
					for(int i=0;i<boardWidth;i++){
						if(board[j][i]>0){
							if(state==GAME_OVER&&j<=gameEndingCounter/5){
								g.setColor(Color.GRAY);
							}
							else{
								g.setColor(Color.getHSBColor((1.0f/NoP)*board[j][i],1.0f,brightness));
							}
							g.fillRect((boardx+20+(i)*20),(boardy+20+(j)*20),20,20);
							g.drawImage(block,(boardx+20+(i)*20),(boardy+20+(j)*20),20,20,null);
						}
					}
				}

				for(int j=0;j<5;j++){
					for(int i=0;i<5;i++){
						if(pieces[j][i][nextPieceNum]>0){
							if(state==GAME_OVER){
								g.setColor(Color.GRAY);
							}
							else{
								g.setColor(Color.getHSBColor((1.0f/NoP)*pieces[j][i][nextPieceNum],1.0f,brightness));
							}
							g.fillRect((nextx+20+(i)*20),(nexty+20+(j)*20),20,20);
							g.drawImage(block,(nextx+20+(i)*20),(nexty+20+(j)*20),20,20,null);
						}
					}
				}
			}
		}
		g.dispose();
		strategy.show();
	}

	public void drawArea(Graphics2D g,int x,int y,int width,int height){
		g.drawImage(block,x,y,20,20,null);
		g.drawImage(block,x+20*width+20,y,20,20,null);
		g.drawImage(block,x,y+20*height+20,20,20,null);
		g.drawImage(block,x+20*width+20,y+20*height+20,20,20,null);
		g.drawImage(block,x+20,y,x+20*width+20,y+20,5,0,15,20,null);
		g.drawImage(block,x+20,y+20*height+20,x+20*width+20,y+20*height+40,5,0,15,20,null);
		g.drawImage(block,x,y+20,x+20,y+20*height+20,0,5,20,15,null);
		g.drawImage(block,x+20*width+20,y+20,x+20*width+40,y+20*height+20,0,5,20,15,null);
	}
	public void drawButton(Graphics2D g,int x,int y,int width,int height){
		g.drawImage(block,x,y,x+5,y+5,0,0,5,5,null);
		g.drawImage(block,x+width-5,y,x+width,y+5,15,0,20,5,null);
		g.drawImage(block,x,y+height-5,x+5,y+height,0,15,5,20,null);
		g.drawImage(block,x+width-5,y+height-5,x+width,y+height,15,15,20,20,null);
		g.drawImage(block,x+5,y,x+width-5,y+5,5,0,15,5,null);
		g.drawImage(block,x+5,y+height-5,x+width-5,y+height,5,15,15,20,null);
		g.drawImage(block,x,y+5,x+5,y+height-5,0,5,5,15,null);
		g.drawImage(block,x+width-5,y+5,x+width,y+height-5,15,5,20,15,null);
	}
	public void drawInside(Graphics2D g,int x,int y,int width,int height){
		g.drawImage(block,x+15,y+15,x+20,y+20,20,20,15,15,null);
		g.drawImage(block,x+width-20,y+15,x+width-15,y+20,5,20,0,15,null);
		g.drawImage(block,x+15,y+height-20,x+20,y+height-15,20,5,15,0,null);
		g.drawImage(block,x+width-20,y+height-20,x+width-15,y+height-15,5,5,0,0,null);
		g.drawImage(block,x+20,y+15,x+width-20,y+20,5,20,15,15,null);
		g.drawImage(block,x+20,y+height-20,x+width-20,y+height-15,5,5,15,0,null);
		g.drawImage(block,x+15,y+20,x+20,y+height-20,20,5,15,15,null);
		g.drawImage(block,x+width-20,y+20,x+width-15,y+height-20,5,5,0,15,null);
	}

	public void logic(){
		if(state==MAIN_SCREEN){
			if(enterKey.isPressable()){
				enterKey.lock();
				state=MAIN_MENU;
			}
		}
		else if(state==MAIN_MENU){
			if(down.isPressable()){
				down.lock();
				menuPoint++;
			}
			if(up.isPressable()){
				up.lock();
				menuPoint--;
			}
			if(menuPoint<0){menuPoint=4;}
			if(menuPoint>4){menuPoint=0;}
			if(enterKey.isPressable()){
				enterKey.lock();
				switch(menuPoint){
					case 0:	state=GAME_SETUP;	break;
					case 1:	state=SET_CONTROLS;	menuPoint=0;	break;
					case 2:						break;
					case 3:	state=HIGH_SCORES;	break;
					case 4:	gameRunning=false;	break;
				}
			}
		}
		else if(state==SET_CONTROLS){
			if(down.isPressable()&&!adjustingControls){
				down.lock();
				menuPoint++;
			}
			if(up.isPressable()&&!adjustingControls){
				up.lock();
				menuPoint--;
			}
			if(menuPoint<0){menuPoint=10;}
			if(menuPoint>10){menuPoint=0;}
			if(enterKey.isPressable()&&!adjustingControls){
				enterKey.lock();
				switch(menuPoint){
					case 0:			break;
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:		adjustingControls = true; break;
					case 10:	state=MAIN_MENU;menuPoint=0;	break;
				}
			}
		}

		else if(state==GAME_SETUP){
			setNewPiece();
			for(int j=0;j<5;j++){
				for(int i=0;i<5;i++){
					currentPiece[j][i]=0;
				}
			}
			for(int j=0;j<boardHeight;j++){
				for(int i=0;i<boardWidth;i++){
					board[j][i]=0;
				}
			}
			setNewPiece();
			level=1;
			score=0;
			linesCleared=0;
			state=PLAYING;
		}
		else if(state==HIGH_SCORES){
			if(enterKey.isPressable()){enterKey.lock();state=MAIN_MENU;menuPoint=0;}
		}
		else if(state==ENTERING_HIGH_SCORE){
			if(enterKey.isPressable()){state=HIGH_SCORES;enterKey.lock();saveScores();}
		}
		else if(state==PLAYING){
			if(pauseKey.isPressable()){
				state=PAUSED;pauseKey.lock();
			}
			else{
				boolean decendCounterCalled=false;

				if(moveRight.isPressed()){
					if(keydcount==0){x++;if(checkoutOfBounds("piece")){x--;}}
					else if(keydcount>25){if(keydcount%2==0){x++;if(checkoutOfBounds("piece")){x--;}}}
					keydcount++;
				}else{keydcount=0;}

				if(moveLeft.isPressed()){
					if(keyacount==0){x--;if(checkoutOfBounds("piece")){x++;}}
					else if(keyacount>25){if(keyacount%2==0){x--;if(checkoutOfBounds("piece")){x++;}}}
					keyacount++;
				}else{keyacount=0;}
				if(softDrop.isPressed()){y++;dropPoints++;}
				if(hardDrop.isPressable()){
					hardDrop.lock();
					do{
						y++;dropPoints++;

					}while(!checkoutOfBounds("piece"));
				}
				if(rotateCW.isPressable()){turnClockwise();rotateCW.lock();}
				if(rotateCCW.isPressable()){turnCounterClockwise();rotateCCW.lock();}
				if(mirrorHoriz.isPressable()){flipHorizontally();mirrorHoriz.lock();}
				if(mirrorVert.isPressable()){flipVertically();mirrorVert.lock();}

				if(decendCounter>=50){y++;decendCounter-=50;decendCounterCalled=true;}
				else{decendCounter+=level;}

				if(checkoutOfBounds("piece")){
					if(softDrop.isPressed()||hardDrop.isPressed()){y--;dropPoints--;}
					if(decendCounterCalled&&checkoutOfBounds("piece")){y--;}
					placePiece();
				}
			}
		}
		else if(state==PAUSED){
			if(pauseKey.isPressable()){state=PLAYING;pauseKey.lock();}
		}
		else if(state==GAME_OVER){
			if(gameEndingCounter<boardHeight*5){
				gameEndingCounter++;
			}
			else{
				highScorePosit=20;
				for(int i=0;i<20;i++){
					if(score>scores[i]){
						highScorePosit=i;
						break;
					}
				}
				if(highScorePosit<20){
					for(int i=20-1;i>highScorePosit;i--){
						scores[i]=scores[i-1];
						scoreHolders[i]=scoreHolders[i-1];
					}
					scores[highScorePosit]=score;
					scoreHolders[highScorePosit]=new StringBuffer("");;

					state=ENTERING_HIGH_SCORE;
				}
				else{
					state=HIGH_SCORES;
				}
			}
		}
		else{
			System.out.println("Unknown GameState error!");
			gameRunning=false;
		}
	}

	public void turnClockwise(){
		for(int j=0;j<5;j++){
			for(int i=0;i<5;i++){
				pieceTemp[i][4-j] = currentPiece[j][i];
			}
		}
		if(!checkoutOfBounds("pieceTemp")){
			for(int j=0;j<5;j++){
				for(int i=0;i<5;i++){
					currentPiece[j][i] = pieceTemp[j][i];
				}
			}
		}
	}
	public void turnCounterClockwise(){
		for(int j=0;j<5;j++){
			for(int i=0;i<5;i++){
				pieceTemp[4-i][j] = currentPiece[j][i];
			}
		}
		if(!checkoutOfBounds("pieceTemp")){
			for(int j=0;j<5;j++){
				for(int i=0;i<5;i++){
					currentPiece[j][i] = pieceTemp[j][i];
				}
			}
		}
	}
	public void flipVertically(){
		for(int j=0;j<5;j++){
			for(int i=0;i<5;i++){
				pieceTemp[4-j][i] = currentPiece[j][i];
			}
		}
		if(!checkoutOfBounds("pieceTemp")){
			for(int j=0;j<5;j++){
				for(int i=0;i<5;i++){
					currentPiece[j][i] = pieceTemp[j][i];
				}
			}
		}
	}
	public void flipHorizontally(){
		for(int j=0;j<5;j++){
			for(int i=0;i<5;i++){
				pieceTemp[j][4-i] = currentPiece[j][i];
			}
		}
		if(!checkoutOfBounds("pieceTemp")){
			for(int j=0;j<5;j++){
				for(int i=0;i<5;i++){
					currentPiece[j][i] = pieceTemp[j][i];
				}
			}
		}
	}
	public boolean checkoutOfBounds(String which){
		boolean outOfBounds = false;
		for(int j=0;j<5;j++){
			for(int i=0;i<5;i++){
				if(y+j>=0){
					if(currentPiece[j][i]>0&&which=="piece"){
						if(y+j>=boardHeight){outOfBounds=true;break;}
						else if(x+i<0||x+i>=boardWidth||board[y+j][x+i]>0){outOfBounds=true;break;}
					}
					else if(pieceTemp[j][i]>0&&which=="pieceTemp"){
						if(y+j>=boardHeight){outOfBounds=true;break;}
						else if(x+i<0||x+i>=boardWidth||board[y+j][x+i]>0){outOfBounds=true;break;}
					}
					else{outOfBounds=false;}
				}
				else{
					if(currentPiece[j][i]>0&&which=="piece"){
						if(y+j>=boardHeight){outOfBounds=true;break;}
						else if(x+i<0||x+i>=boardWidth){outOfBounds=true;break;}
					}
					else if(pieceTemp[j][i]>0&&which=="pieceTemp"){
						if(y+j>=boardHeight){outOfBounds=true;break;}
						else if(x+i<0||x+i>=boardWidth){outOfBounds=true;break;}
					}
					else{outOfBounds=false;}
				}
			}
			if(outOfBounds){break;}
		}
		return outOfBounds;
	}
	public void placePiece(){
		score+=dropPoints;
		if(score>999999999){score=999999999;}
		dropPoints=0;
		for(int j=0;j<5;j++){
			for(int i=0;i<5;i++){
				if(y+j>=0){
					if(currentPiece[j][i]>0){
						board[y+j][x+i] = currentPiece[j][i];
					}
				}
			}
		}

		x=4;y=-2;if(checkoutOfBounds("piece")){state=GAME_OVER;gameEndingCounter=0;}
		setNewPiece();
		int newLinesCleared = 0;
		for(int j=0;j<boardHeight;j++){
			int rowCount=0;
			for(int i=0;i<boardWidth;i++){
				if(board[j][i]>0){rowCount++;}
			}
			if(rowCount==boardWidth){
				for(int J=j;J>0;J--){
					for(int I=0;I<boardWidth;I++){
						board[J][I]=board[J-1][I];
					}
				}
				for(int I=0;I<boardWidth;I++){
					board[0][I]=0;
				}

				newLinesCleared++;
			}
		}
		switch(newLinesCleared){
			case 0:break;
			case 1:	score +=  40	*(level);	break;
			case 2:	score +=  100	*(level);	break;
			case 3:	score +=  300	*(level);	break;
			case 4:	score +=  1200	*(level);	break;
			case 5:	score +=  60000	*(level);	break;
			default: System.out.println("You're cheating somehow!");
		}
		if(score>999999999){score=999999999;}
		linesCleared+=newLinesCleared;
		level=linesCleared/10+1;
	}

	public void setNewPiece(){
		for(int i=0;i<5;i++){
			for(int j=0;j<5;j++){
				currentPiece[j][i]=pieces[j][i][nextPieceNum];
			}
		}
		int newPieceNum;

		do{
			newPieceNum = (int) (Math.random()*NoP);
		}while(newPieceNum==currentPieceNum && newPieceNum==nextPieceNum);

		currentPieceNum = nextPieceNum;
		nextPieceNum = newPieceNum;
	}
	public void getPieces(){
		try{
			int i,j,k=0,a=0;
			Scanner inputFile;
			File file = new File("Pieces");
			inputFile = new Scanner(file);
			String buffer = "";
			while(inputFile.hasNextLine()){
				buffer += inputFile.nextLine();
			}
			for(k=0;k<100;k++){
				for(i=0;i<5;i++){
					for(j=0;j<5;j++){
						if(a>=buffer.length()){break;}
						switch(buffer.charAt(a)){
							case '.': pieces[i][j][k]=0;break;
							case '#': pieces[i][j][k]=k+1;break;
							default: System.out.println("File loading error!"); break;

						}
						a++;
					}
					if(a>=buffer.length()){break;}
				}
				if(a>=buffer.length()){break;}
			}
			NoP=k+1;
			pieceOrder = new int[NoP];
			shufflePieceOrder();

			nextPieceNum = (int) (Math.random()*NoP);
		}
		catch(Exception exception){
			System.out.println("File not found!");
			gameRunning = false;
		}
	}
	public void shufflePieceOrder(){
		for(int a=0;a<NoP;a++){
			boolean used = true;
			while(used){
				pieceOrder[a] = (int) (Math.random()*NoP);
				used = false;
				for(int b=0;b<a;b++){
					if(pieceOrder[a]==pieceOrder[b]){
						used = true;break;
					}
				}
			}
		}
	}
	public void getScores(){
		try{
			Scanner inputFile;
			File file = new File("Scores");
			inputFile = new Scanner(file);
			for(int n=0;n<20;n++){
				if(inputFile.hasNext()){
					scoreHolders[n]=new StringBuffer(inputFile.next());
				}
				if(inputFile.hasNextInt()){
					scores[n]=inputFile.nextInt();
				}
			}


		}catch(Exception e){}

	}
	public void saveScores(){
		try{
			Writer output = null;
			output = new BufferedWriter(new FileWriter("Scores"));
			for(int n=0;n<20;n++){
				if(scoreHolders[n]!=null){
					output.write(scoreHolders[n]+" "+scores[n]+"\r\n");
				}
			}
			output.close();

		}catch(Exception e){}
	}
	public void loadPreferences(){
		try{
			Scanner inputFile;
			File file = new File("Preferences.ini");
			inputFile = new Scanner(file);

			while(inputFile.hasNextLine()){
/*				if(inputFile.next().startsWith("WindowX=")){
					xPos = inputFile.nextInt();
				}
				if(inputFile.next().startsWith("WindowY=")){
					yPos = inputFile.nextInt();
				}
*/
				String line = inputFile.nextLine();

				if(line.startsWith("softDrop=")){
					softDrop.setCode(line);
				}
				if(line.startsWith("hardDrop=")){
					hardDrop.setCode(line);
				}
				if(line.startsWith("moveLeft=")){
					moveLeft.setCode(line);
				}
				if(line.startsWith("moveRight=")){
					moveRight.setCode(line);
				}
				if(line.startsWith("rotateCW=")){
					rotateCW.setCode(line);
				}
				if(line.startsWith("rotateCCW=")){
					rotateCCW.setCode(line);
				}
				if(line.startsWith("mirrorHoriz=")){
					mirrorHoriz.setCode(line);
				}
				if(line.startsWith("mirrorVert=")){
					mirrorVert.setCode(line);
				}
				if(line.startsWith("pauseKey=")){
					pauseKey.setCode(line);
				}
				if(line.startsWith("debug?=") && line.endsWith("true")){
					debug.setVisible(true);
				}
			}
		}
		catch(Exception e){}
	}
	public void savePreferences(){
		System.out.println("saving preferences");
		try{
			Writer output = null;
			output = new BufferedWriter(new FileWriter("Preferences.ini"));

			for(int i=0;i<10;i++){
				String line = "";

				switch(i){
					case 0: line = "softDrop=\t" + softDrop.getCodeAsString();break;
					case 1: line = "hardDrop=\t" + hardDrop.getCodeAsString();break;
					case 2: line = "moveLeft=\t" + moveLeft.getCodeAsString();break;
					case 3: line = "moveRight=\t" + moveRight.getCodeAsString();break;
					case 4: line = "rotateCW=\t" + rotateCW.getCodeAsString();break;
					case 5: line = "rotateCCW=\t" + rotateCCW.getCodeAsString();break;
					case 6: line = "mirrorHoriz=\t" + mirrorHoriz.getCodeAsString();break;
					case 7: line = "mirrorVert=\t" + mirrorVert.getCodeAsString();break;
					case 8: line = "pauseKey=\t" + pauseKey.getCodeAsString();break;
					case 9: line = "debug?=\t\t" + debug.isVisible();break;
				}
				output.write(line + "\r\n");
			}
			output.close();
			System.out.println("preferences saved");
		}catch(Exception e){System.out.println("saving error");}
	}

	public void keyTyped(KeyEvent e){
		try{
			if(e.getKeyChar()!=''&&state==ENTERING_HIGH_SCORE){
				scoreHolders[highScorePosit].append(e.getKeyChar());
			}
/*			else if(e.getKeyChar()=='+'){
				brightness += 0.1f;
			}
			else if(e.getKeyChar()=='-'){
				brightness -= 0.1f;
			}

			if(brightness > 1.0f){
				brightness = 1.0f;
			}
			else if(brightness < 0.0f){
				brightness = 0.0f;
			}
*/		}
		catch(Exception ex){}
	}
	public void keyPressed(KeyEvent e){
		System.out.println("Key press: "+KeyEvent.getKeyText(e.getKeyCode())+"("+e.getKeyLocation()+")");
		if(!adjustingControls){
			enterKey	.processKeyPress(e);
			down		.processKeyPress(e);
			left		.processKeyPress(e);
			right		.processKeyPress(e);
			up			.processKeyPress(e);
			softDrop	.processKeyPress(e);
			hardDrop	.processKeyPress(e);
			moveLeft	.processKeyPress(e);
			moveRight	.processKeyPress(e);
			rotateCW	.processKeyPress(e);
			rotateCCW	.processKeyPress(e);
			mirrorHoriz	.processKeyPress(e);
			mirrorVert	.processKeyPress(e);
			pauseKey	.processKeyPress(e);
			}
		if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE&&scoreHolders[highScorePosit].length()>0&&state==ENTERING_HIGH_SCORE){
			scoreHolders[highScorePosit].setLength(scoreHolders[highScorePosit].length()-1);
		}
		if(state==SET_CONTROLS&&adjustingControls){
			if(e.getKeyCode()==KeyEvent.VK_UNDEFINED){
				keyError = true;
			}
			else{
				switch(menuPoint){
					case 1:softDrop.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 2:hardDrop.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 3:moveLeft.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 4:moveRight.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 5:rotateCW.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 6:rotateCCW.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 7:mirrorHoriz.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 8:mirrorVert.setCode(e.getKeyCode(),e.getKeyLocation());break;
					case 9:pauseKey.setCode(e.getKeyCode(),e.getKeyLocation());break;
				}
				adjustingControls = false;
				keyError = false;
			}
		}
	}
	public void keyReleased(KeyEvent e){
		enterKey	.processKeyReleased(e);
		down		.processKeyReleased(e);
		left		.processKeyReleased(e);
		right		.processKeyReleased(e);
		up			.processKeyReleased(e);
		softDrop	.processKeyReleased(e);
		hardDrop	.processKeyReleased(e);
		moveLeft	.processKeyReleased(e);
		moveRight	.processKeyReleased(e);
		rotateCW	.processKeyReleased(e);
		rotateCCW	.processKeyReleased(e);
		mirrorHoriz	.processKeyReleased(e);
		mirrorVert	.processKeyReleased(e);
		pauseKey	.processKeyReleased(e);

/*		if(e.getKeyCode()==0){
			System.out.println("Unknown key!");
		}
		else{
			System.out.println(e.getKeyText(e.getKeyCode())+" "+e.getKeyLocation());
		}
*/
	}

	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){
		System.exit(0);
	}
	public void windowClosing(WindowEvent e){
		savePreferences();
		System.exit(0);
	}
	public void windowDeactivated(WindowEvent e){
		if(state==PLAYING){state=PAUSED;}
	}
	public void windowDeiconified(WindowEvent e){
		if(state==PLAYING){state=PAUSED;}
	}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}

	public static void main(String[] args){
		Pentis game1 = new Pentis();
		for(String s: args){
			if(s.compareToIgnoreCase("debug")==0){
				game1.debug.setVisible(true);
			}
		}
		game1.gameLoop();
		System.exit(0);

	}
}