import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{

	static final int GAME_WIDTH = 500;
	static final int GAME_HEIGHT = 600;
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
	static final int BALL_DIAMETER = 20;
	static final int PADDLE_WIDTH = 100;
	static final int PADDLE_HEIGHT = 25;
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	Paddle paddle;
	Ball ball;
	Score score;
	
	GamePanel(){
		newPaddles();
		newBall();
		score = new Score(GAME_WIDTH,GAME_HEIGHT);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(SCREEN_SIZE);
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void newBall() {
		random = new Random();
		ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2),random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
	}
	public void newPaddles() {
		paddle = new Paddle(GAME_WIDTH/2 - PADDLE_WIDTH/2,GAME_HEIGHT, PADDLE_WIDTH,PADDLE_HEIGHT,1);
	}
	public void paint(Graphics g) {
		image = createImage(getWidth(),getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image,0,0,this);
	}
	public void draw(Graphics g) {
		paddle.draw(g);
		ball.draw(g);
		score.draw(g);
Toolkit.getDefaultToolkit().sync(); // I forgot to add this line of code in the video, it helps with the animation

	}
	public void move() {
		paddle.move();
		ball.move();
	}
	
	
	public void checkCollision() {
		if(ball.x <=0) {
			ball.setXDirection(-ball.xVelocity);
		}
		if(ball.y <=0) {
			ball.setYDirection(-ball.yVelocity);
		}
		if(ball.x >= GAME_WIDTH-BALL_DIAMETER) {
			ball.setXDirection(-ball.xVelocity);
		}
		
		//bounce ball off paddles
		if(ball.intersects(paddle)) {
			ball.yVelocity = -(ball.yVelocity);
			ball.xVelocity++; //optional for more difficulty
			if(ball.xVelocity>0)
				ball.xVelocity++; //optional for more difficulty
			else
				ball.yVelocity--;
			ball.setXDirection(ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}

		//stops paddles at window edges
		if(paddle.x<=0)
			paddle.x=0;
		if(paddle.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle.y = GAME_HEIGHT-PADDLE_HEIGHT;
		if(paddle.x >= (GAME_WIDTH-PADDLE_WIDTH))
			paddle.x = GAME_WIDTH-PADDLE_WIDTH;
		
		
		if(ball.y >= GAME_HEIGHT-BALL_DIAMETER) {
			int x = score.player1;
			score.player1 = 0;
			newPaddles();
			newBall();
			System.out.println("Player Max Streak achieve "+x);
		}
		if(ball.intersects(paddle)) {
			score.player1++;
			System.out.println("Player 1: "+score.player1);
		}
	}
	public void run() {
		//game loop
		long lastTime = System.nanoTime();
		double amountOfTicks =60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while(true) {
			long now = System.nanoTime();
			delta += (now -lastTime)/ns;
			lastTime = now;
			if(delta >=1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}
	
	public class AL extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			paddle.keyPressed(e);
		}
		public void keyReleased(KeyEvent e) {
			paddle.keyReleased(e);

		}
	}
}