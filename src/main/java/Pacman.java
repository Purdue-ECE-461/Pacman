import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Pacman implements KeyListener{

    //Move Vars
    Timer moveTimer;
    ActionListener moveAL;
    public MoveType activeMove;
    MoveType todoMove;
    boolean isStuck = true;

    //Animation Vars
    Timer animTimer;
    ActionListener animAL;
    Image[] pac;
    int activeImage = 0;
    int addFactor = 1;

    public Point pixelPosition;
    private Point logicalPosition;

    private Pacboard parentBoard;


    public Pacman (int x, int y, Pacboard pb) {

        setLogicalPosition(new Point(x,y));
        pixelPosition = new Point(28*x,28*y);

        parentBoard = pb;

        pac = new Image[5];

        activeMove = MoveType.NONE;
        todoMove = MoveType.NONE;

        try {
            pac[0] = ImageIO.read(this.getClass().getResource("resources/images/pac/pac0.png"));
            pac[1] = ImageIO.read(this.getClass().getResource("resources/images/pac/pac1.png"));
            pac[2] = ImageIO.read(this.getClass().getResource("resources/images/pac/pac2.png"));
            pac[3] = ImageIO.read(this.getClass().getResource("resources/images/pac/pac3.png"));
            pac[4] = ImageIO.read(this.getClass().getResource("resources/images/pac/pac4.png"));
        }catch(IOException e){
            System.err.println("Cannot Read Images !");
        }

        //animation timer
        animAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                activeImage = activeImage + addFactor;
                if(activeImage==4 || activeImage==0){
                    addFactor *= -1;
                }
            }
        };
        animTimer = new Timer(40,animAL);
        animTimer.start();


        moveAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                //update logical position
                if((pixelPosition.x % 28 == 0) && (pixelPosition.y % 28 == 0)){
                    if(!isStuck) {
                        switch (activeMove) {
                            case RIGHT:
                                getLogicalPosition().x++;
                                break;
                            case LEFT:
                                getLogicalPosition().x--;
                                break;
                            case UP:
                                getLogicalPosition().y--;
                                break;
                            case DOWN:
                                getLogicalPosition().y++;
                                break;
                        }
                        //send update message
                        parentBoard.dispatchEvent(new ActionEvent(this,Messeges.UPDATE,null));
                    }
                    isStuck = true;
                    animTimer.stop();

                    if(todoMove != MoveType.NONE && isPossibleMove(todoMove) ) {
                        activeMove = todoMove;
                        todoMove = MoveType.NONE;
                    }
                }else{
                    isStuck = false;
                    animTimer.start();
                }

                switch(activeMove){
                    case RIGHT:
                        if((pixelPosition.x >= (parentBoard.getM_x() -1) * 28)&&parentBoard.isCustom){
                            return;
                        }
                        /*if((logicalPosition.x+1 < parentBoard.m_x) && (parentBoard.map[logicalPosition.x+1][logicalPosition.y]>0)){
                            return;
                        }*/
                        if(getLogicalPosition().x >= 0 && getLogicalPosition().x < parentBoard.getM_x() -1 && getLogicalPosition().y >= 0 && getLogicalPosition().y < parentBoard.getM_y() -1 ) {
                            if (parentBoard.getMap()[getLogicalPosition().x + 1][getLogicalPosition().y] > 0) {
                                return;
                            }
                        }
                        pixelPosition.x ++;
                        break;
                    case LEFT:
                        if((pixelPosition.x <= 0)&&parentBoard.isCustom){
                            return;
                        }
                        /*if((logicalPosition.x-1 >= 0) && (parentBoard.map[logicalPosition.x-1][logicalPosition.y]>0)){
                            return;
                        }*/
                        if(getLogicalPosition().x > 0 && getLogicalPosition().x < parentBoard.getM_x() -1 && getLogicalPosition().y >= 0 && getLogicalPosition().y < parentBoard.getM_y() -1 ) {
                            if (parentBoard.getMap()[getLogicalPosition().x - 1][getLogicalPosition().y] > 0) {
                                return;
                            }
                        }
                        pixelPosition.x--;
                        break;
                    case UP:
                        if((pixelPosition.y <= 0)&&parentBoard.isCustom){
                            return;
                        }
                        /*if((logicalPosition.y-1 >= 0) && (parentBoard.map[logicalPosition.x][logicalPosition.y-1]>0)){
                            return;
                        }*/
                        if(getLogicalPosition().x >= 0 && getLogicalPosition().x < parentBoard.getM_x() -1 && getLogicalPosition().y >= 0 && getLogicalPosition().y < parentBoard.getM_y() -1 ) {
                            if(parentBoard.getMap()[getLogicalPosition().x][getLogicalPosition().y-1]>0){
                                return;
                            }
                        }
                        pixelPosition.y--;
                        break;
                    case DOWN:
                        if((pixelPosition.y >= (parentBoard.getM_y() -1) * 28)&&parentBoard.isCustom){
                            return;
                        }
                        /*if((logicalPosition.y+1 < parentBoard.m_y) && (parentBoard.map[logicalPosition.x][logicalPosition.y+1]>0)){
                            return;
                        }*/
                        if(getLogicalPosition().x >= 0 && getLogicalPosition().x < parentBoard.getM_x() -1 && getLogicalPosition().y >= 0 && getLogicalPosition().y < parentBoard.getM_y() -1 ) {
                            if(parentBoard.getMap()[getLogicalPosition().x][getLogicalPosition().y+1]>0){
                                return;
                            }
                        }
                        pixelPosition.y ++;
                        break;
                }

                //send Messege to PacBoard to check collision
                parentBoard.dispatchEvent(new ActionEvent(this,Messeges.COLTEST,null));

            }
        };
        moveTimer = new Timer(9,moveAL);
        moveTimer.start();

    }

    public boolean isPossibleMove(MoveType move){
        if(getLogicalPosition().x >= 0 && getLogicalPosition().x < parentBoard.getM_x() -1 && getLogicalPosition().y >= 0 && getLogicalPosition().y < parentBoard.getM_y() -1 ) {
            switch(move){
                case RIGHT:
                    return !(parentBoard.getMap()[getLogicalPosition().x + 1][getLogicalPosition().y] > 0);
                case LEFT:
                    return !(parentBoard.getMap()[getLogicalPosition().x - 1][getLogicalPosition().y] > 0);
                case UP:
                    return !(parentBoard.getMap()[getLogicalPosition().x][getLogicalPosition().y - 1] > 0);
                case DOWN:
                    return !(parentBoard.getMap()[getLogicalPosition().x][getLogicalPosition().y+1] > 0);
            }
        }
        return false;
    }

    public Image getPacmanImage(){
        return pac[activeImage];
    }

    @Override
    public void keyReleased(KeyEvent ke){
        //
    }

    @Override
    public void keyTyped(KeyEvent ke){
        //
    }

    //Handle Arrow Keys
    @Override
    public void keyPressed(KeyEvent ke){
        switch(ke.getKeyCode()){
            case 37:
                todoMove = MoveType.LEFT;
                break;
            case 38:
                todoMove = MoveType.UP;
                break;
            case 39:
                todoMove = MoveType.RIGHT;
                break;
            case 40:
                todoMove = MoveType.DOWN;
                break;
            case 82:
                parentBoard.dispatchEvent(new ActionEvent(this,Messeges.RESET,null));
                break;
        }
        //System.out.println(ke.getKeyCode());
    }


    public Point getLogicalPosition() {
        return logicalPosition;
    }

    public void setLogicalPosition(Point logicalPosition) {
        this.logicalPosition = logicalPosition;
    }
}
