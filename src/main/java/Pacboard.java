import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

// TODO 9: Refactor this class name according to the correct naming convention.
// TODO 10: Identify 2 wrongly-named methods and 3 wrongly-named variables in this class and rename appropriately.
public class Pacboard extends JPanel{

    Timer redrawTimer;
    ActionListener redrawAL;

    private int[][] map;
    Image[] mapSegments;

    Image foodImage;
    Image[] pfoodImage;

    Image goImage;
    Image vicImage;

    private Pacman pacman;
    ArrayList<Food> foods;
    ArrayList<PowerUpFood> pu_foods;
    java.util.List<Ghost> ghosts;
    ArrayList<TeleportTunnel> teleports;

    boolean isCustom = false;
    boolean isGameOver = false;
    boolean isWin = false;
    boolean drawScore = false;
    boolean clearScore = false;
    int scoreToAdd = 0;

    int score;
    JLabel scoreboard;

    LoopPlayer siren;
    boolean mustReactivateSiren = false;
    LoopPlayer pac6;

    public Point ghostBase;

    private int m_x;
    private int m_y;

    MapData md_backup;
    PacWindow windowParent;

    public Pacboard(JLabel scoreboard, MapData md, PacWindow pw){
        this.scoreboard = scoreboard;
        this.setDoubleBuffered(true);
        md_backup = md;
        windowParent = pw;
        
        setM_x(md.getX());
        setM_y(md.getY());
        this.setMap(md.getMap());

        this.isCustom = md.isCustom();
        this.ghostBase = md.getGhostBasePosition();

        //loadMap();

        setPacman(new Pacman(md.getPacmanPosition().x,md.getPacmanPosition().y,this));
        addKeyListener(getPacman());

        foods = new ArrayList<>();
        pu_foods = new ArrayList<>();
        ghosts = new ArrayList<>();
        teleports = new ArrayList<>();



        if(!isCustom) {
            for (int i = 0; i < getM_x(); i++) {
                for (int j = 0; j < getM_y(); j++) {
                    if (getMap()[i][j] == 0)
                        foods.add(new Food(i, j));
                }
            }
        }else{
            foods = md.getFoodPositions();
        }



        pu_foods = md.getPufoodPositions();

        ghosts = get_ghosts(md);

        teleports = md.getTeleports();

        setLayout(null);
        setSize(20* getM_x(),20* getM_y());
        setBackground(Color.black);

        mapSegments = new Image[28];
        mapSegments[0] = null;


        for(int ms=1;ms<28;ms++){
            try {
                mapSegments[ms] = ImageIO.read(this.getClass().getResource("resources/images/map segments/"+ms+".png"));
            }catch(Exception e){}
        }

        pfoodImage = new Image[5];
        for(int ms=0 ;ms<5;ms++){
            try {
                pfoodImage[ms] = ImageIO.read(this.getClass().getResource("resources/images/food/"+ms+".png"));
            }catch(Exception e){}
        }
        try{
            foodImage = ImageIO.read(this.getClass().getResource("resources/images/food.png"));
            goImage = ImageIO.read(this.getClass().getResource("resources/images/gameover.png"));
            vicImage = ImageIO.read(this.getClass().getResource("resources/images/victory.png"));
            //pfoodImage = ImageIO.read(this.getClass().getResource("/images/pfood.png"));
        }catch(Exception e){}


        redrawAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //Draw Board
                repaint();
            }
        };

        // TODO 8: Define a constant "TIMER_DELAY" for the "16" indicated below
        redrawTimer = new Timer(16,redrawAL);
        redrawTimer .start();

        //SoundPlayer.play("pacman_start.wav");
        siren = new LoopPlayer("siren.wav");
        pac6 = new LoopPlayer("pac6.wav");
        siren.start();
    }

    // TODO 7: Refactor this implementation to use a pipeline structure (Java streams map functio)
    java.util.List<Ghost> get_ghosts(MapData md) {

        java.util.List<Ghost> ghostInstances = new ArrayList<>();

        for(GhostData gd : md.getGhostsData()){
            switch(gd.getType()) {
                case RED:
                    ghostInstances.add(new RedGhost(gd.getX(), gd.getY(), this));
                    break;
                case PINK:
                    ghostInstances.add(new PinkGhost(gd.getX(), gd.getY(), this));
                    break;
                case CYAN:
                    ghostInstances.add(new CyanGhost(gd.getX(), gd.getY(), this));
                    break;
            }
        }

        return ghostInstances;
    }

    private void collisionTest(){
        Rectangle pr = new Rectangle(getPacman().pixelPosition.x+13, getPacman().pixelPosition.y+13,2,2);
        Ghost ghostToRemove = null;
        for(Ghost g : ghosts){
            Rectangle gr = new Rectangle(g.pixelPosition.x,g.pixelPosition.y,28,28);

            if(pr.intersects(gr)){
                if(!g.isDead()) {
                    if (!g.isWeak()) {
                        //Game Over
                        siren.stop();
                        SoundPlayer.play("pacman_lose.wav");
                        getPacman().moveTimer.stop();
                        getPacman().animTimer.stop();
                        g.moveTimer.stop();
                        isGameOver = true;
                        scoreboard.setText("    Press R to try again !");
                        //scoreboard.setForeground(Color.red);
                        break;
                    } else {
                        //Eat Ghost
                        SoundPlayer.play("pacman_eatghost.wav");
                        //getGraphics().setFont(new Font("Arial",Font.BOLD,20));
                        drawScore = true;
                        scoreToAdd++;
                        if(ghostBase!=null)
                            g.die();
                        else
                            ghostToRemove = g;
                    }
                }
            }
        }

        if(ghostToRemove!= null){
            ghosts.remove(ghostToRemove);
        }
    }

    private void update(){

        Food foodToEat = null;
        //Check food eat
        for(Food f : foods){
            if(getPacman().getLogicalPosition().x == f.position.x && getPacman().getLogicalPosition().y == f.position.y)
                foodToEat = f;
        }
        if(foodToEat!=null) {
            SoundPlayer.play("pacman_eat.wav");
            foods.remove(foodToEat);
            score ++;
            scoreboard.setText("    Score : "+score);

            if(foods.size() == 0){
                siren.stop();
                pac6.stop();
                SoundPlayer.play("pacman_intermission.wav");
                isWin = true;
                getPacman().moveTimer.stop();
                for(Ghost g : ghosts){
                    g.moveTimer.stop();
                }
            }
        }

        PowerUpFood puFoodToEat = null;
        //Check pu food eat
        for(PowerUpFood puf : pu_foods){
            if(getPacman().getLogicalPosition().x == puf.position.x && getPacman().getLogicalPosition().y == puf.position.y)
                puFoodToEat = puf;
        }
        if(puFoodToEat!=null) {
            //SoundPlayer.play("pacman_eat.wav");
            switch(puFoodToEat.type) {
                case 0:
                    //PACMAN 6
                    pu_foods.remove(puFoodToEat);
                    siren.stop();
                    mustReactivateSiren = true;
                    pac6.start();
                    for (Ghost g : ghosts) {
                        g.weaken();
                    }
                    scoreToAdd = 0;
                    break;
                default:
                    SoundPlayer.play("pacman_eatfruit.wav");
                    pu_foods.remove(puFoodToEat);
                    scoreToAdd = 1;
                    drawScore = true;
            }
            //score ++;
            //scoreboard.setText("    Score : "+score);
        }

        //Check Ghost Undie
        for(Ghost g:ghosts){
            if(g.isDead() && g.logicalPosition.x == ghostBase.x && g.logicalPosition.y == ghostBase.y){
                g.undie();
            }
        }

        //Check Teleport
        for(TeleportTunnel tp : teleports) {
            if (getPacman().getLogicalPosition().x == tp.getFrom().x && getPacman().getLogicalPosition().y == tp.getFrom().y && getPacman().activeMove == tp.getReqMove()) {
                //System.out.println("TELE !");
                getPacman().setLogicalPosition(tp.getTo());
                getPacman().pixelPosition.x = getPacman().getLogicalPosition().x * 28;
                getPacman().pixelPosition.y = getPacman().getLogicalPosition().y * 28;
            }
        }

        //Check isSiren
        boolean isSiren = true;
        for(Ghost g:ghosts){
            if(g.isWeak()){
                isSiren = false;
            }
        }
        if(isSiren){
            pac6.stop();
            if(mustReactivateSiren){
                mustReactivateSiren = false;
                siren.start();
            }

        }



    }


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //Draw Walls
        g.setColor(Color.blue);
        for(int i = 0; i< getM_x(); i++){
            for(int j = 0; j< getM_y(); j++){
                if(getMap()[i][j]>0){
                    //g.drawImage(10+i*28,10+j*28,28,28);
                    g.drawImage(mapSegments[getMap()[i][j]],10+i*28,10+j*28,null);
                }
            }
        }

        //Draw Food
        g.setColor(new Color(204, 122, 122));
        for(Food f : foods){
            //g.fillOval(f.position.x*28+22,f.position.y*28+22,4,4);
            g.drawImage(foodImage,10+f.position.x*28,10+f.position.y*28,null);
        }

        //Draw PowerUpFoods
        g.setColor(new Color(204, 174, 168));
        for(PowerUpFood f : pu_foods){
            //g.fillOval(f.position.x*28+20,f.position.y*28+20,8,8);
            g.drawImage(pfoodImage[f.type],10+f.position.x*28,10+f.position.y*28,null);
        }

        //Draw Pacman
        switch(getPacman().activeMove){
            case NONE:
            case RIGHT:
                g.drawImage(getPacman().getPacmanImage(),10+ getPacman().pixelPosition.x,10+ getPacman().pixelPosition.y,null);
                break;
            case LEFT:
                g.drawImage(ImageHelper.flipHor(getPacman().getPacmanImage()),10+ getPacman().pixelPosition.x,10+ getPacman().pixelPosition.y,null);
                break;
            case DOWN:
                g.drawImage(ImageHelper.rotate90(getPacman().getPacmanImage()),10+ getPacman().pixelPosition.x,10+ getPacman().pixelPosition.y,null);
                break;
            case UP:
                g.drawImage(ImageHelper.flipVer(ImageHelper.rotate90(getPacman().getPacmanImage())),10+ getPacman().pixelPosition.x,10+ getPacman().pixelPosition.y,null);
                break;
        }

        //Draw Ghosts
        for(Ghost gh : ghosts){
            g.drawImage(gh.getGhostImage(),10+gh.pixelPosition.x,10+gh.pixelPosition.y,null);
        }

        if(clearScore){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drawScore = false;
            clearScore =false;
        }

        if(drawScore) {
            //System.out.println("must draw score !");
            g.setFont(new Font("Arial",Font.BOLD,15));
            g.setColor(Color.yellow);
            Integer s = scoreToAdd*100;
            g.drawString(s.toString(), getPacman().pixelPosition.x + 13, getPacman().pixelPosition.y + 50);
            //drawScore = false;
            score += s;
            scoreboard.setText("    Score : "+score);
            clearScore = true;

        }

        if(isGameOver){
            g.drawImage(goImage,this.getSize().width/2-315,this.getSize().height/2-75,null);
        }

        if(isWin){
            g.drawImage(vicImage,this.getSize().width/2-315,this.getSize().height/2-75,null);
        }


    }


    @Override
    public void processEvent(AWTEvent ae){

        if(ae.getID()==Messeges.UPDATE) {
            update();
        }else if(ae.getID()==Messeges.COLTEST) {
            if (!isGameOver) {
                collisionTest();
            }
        }else if(ae.getID()==Messeges.RESET){
            if(isGameOver)
                restart();
        }else {
            super.processEvent(ae);
        }
    }
    
    public void restart(){

        siren.stop();

        new PacWindow();
        windowParent.dispose();

    }


    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public int getM_x() {
        return m_x;
    }

    public void setM_x(int m_x) {
        this.m_x = m_x;
    }

    public int getM_y() {
        return m_y;
    }

    public void setM_y(int m_y) {
        this.m_y = m_y;
    }

    public Pacman getPacman() {
        return pacman;
    }

    public void setPacman(Pacman pacman) {
        this.pacman = pacman;
    }
}
