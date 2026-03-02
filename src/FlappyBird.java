import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public class FlappyBird extends JPanel implements ActionListener,KeyListener{
    
    int boardWIdth = 360;
    int boardHeight = 640;

    //Image
    Image backgroundimg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardWIdth/8;
    int birdY = boardHeight/2;
    int birdWidth =34;
    int birdHeight =24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img=img;
        }
    }


    //pipes
    int pipeX = boardWIdth;
    int pipeY = 0;
    int pipeWidth = 64;
    int PipeHeight =512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = PipeHeight;
        Image img;

        boolean passed =false;

        Pipe(Image img){
            this.img = img;
        }
    }

   // game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;



    FlappyBird(){
        setPreferredSize(new Dimension(boardWIdth, boardHeight));
        //setBackground(Color.blue);

        setFocusable(true);
        addKeyListener(this);
        

        //load Image
        backgroundimg = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place placePipesTimer
        placePipesTimer = new Timer(1500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        placePipesTimer.start();

        //game Timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes(){
        int randomPipeY =(int) (pipeY - PipeHeight/4 - Math.random()*(PipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

         Pipe bottomPipe = new Pipe(bottomPipeImg);
         bottomPipe.y = topPipe.y + PipeHeight + openingSpace;
         pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }




    public void draw(Graphics g){
        //background
        g.drawImage(backgroundimg, 0, 0, boardWIdth, boardHeight , null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null );

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width , pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font ("Arial", Font.PLAIN,32));
        if (gameOver){
            g.drawString("Game Over : " + String.valueOf((int) score), 10,35);
        }else {
            g.drawString(String.valueOf((int)score),10,35);
        }
    }



    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
              pipe.passed = true;
              score += 0.5;
            }

             if(collision(bird, pipe)) {
                gameOver=true;
        }

        }


        if (bird.y > boardHeight){
            gameOver = true;
        }

    }

     boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            placePipesTimer.stop();
            gameLoop.stop();

        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
       if (e.getKeyCode() == KeyEvent.VK_SPACE){
        velocityY = -9;
        if(gameOver){
            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            score=0;
            gameOver = false;
            gameLoop.start();
            placePipesTimer.start();
        }
       }
    }

     @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
       
    }
}
