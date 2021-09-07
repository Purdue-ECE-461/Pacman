import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public abstract class Ghost {
    //Anim Vars
    Timer animTimer;
    ActionListener animAL;

    //Pending Vars
    Timer pendingTimer;
    ActionListener pendingAL;

    //Move Vars
    Timer moveTimer;
    ActionListener moveAL;
    public MoveType activeMove;
    protected boolean isStuck = true;
    boolean isPending = false;

    Timer unWeakenTimer1;
    Timer unWeakenTimer2;
    ActionListener unweak1;
    ActionListener unweak2;
    int unweakBlinks;
    boolean isWhite = false;
    MoveType lastCMove;
    BFSFinder bfs;


    MoveType pendMove = MoveType.UP;

    protected boolean isWeak = false;
    protected boolean isDead = false;

    public boolean isWeak() {
        return isWeak;
    }

    public boolean isDead() {
        return isDead;
    }

    //Image[] pac;
    Image ghostImg;
    int activeImage = 0;
    int addFactor = 1;

    public Point pixelPosition;
    public Point logicalPosition;

    Image[] ghostR;
    Image[] ghostL;
    Image[] ghostU;
    Image[] ghostD;

    Image[] ghostW;
    Image[] ghostWW;
    Image ghostEye;

    int ghostNormalDelay;
    int ghostWeakDelay = 30;
    int ghostDeadDelay = 5;

    BFSFinder baseReturner;

    protected Pacboard parentBoard;

    public Ghost (int x, int y, Pacboard pb, int ghostDelay, String ghostType) {

        logicalPosition = new Point(x,y);
        pixelPosition = new Point(28*x,28*y);

        parentBoard = pb;

        activeMove = MoveType.RIGHT;

        ghostNormalDelay = ghostDelay;

        loadImages();

        //load weak Image
        ghostW = new Image[2];
        try {
            ghostW[0] = ImageIO.read(this.getClass().getResource("resources/images/ghost/blue/1.png"));
            ghostW[1] = ImageIO.read(this.getClass().getResource("resources/images/ghost/blue/3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ghostWW = new Image[2];
        try {
            ghostWW[0] = ImageIO.read(this.getClass().getResource("resources/images/ghost/white/1.png"));
            ghostWW[1] = ImageIO.read(this.getClass().getResource("resources/images/ghost/white/3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ghostEye = ImageIO.read(this.getClass().getResource("resources/images/eye.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //animation timer
        animAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                activeImage = (activeImage + 1) % 2;
            }
        };
        animTimer = new Timer(100,animAL);
        animTimer.start();

        moveAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                if((pixelPosition.x % 28 == 0) && (pixelPosition.y % 28 == 0)){
                    if(!isStuck) {
                        switch (activeMove) {
                            case RIGHT:
                                logicalPosition.x++;
                                break;
                            case LEFT:
                                logicalPosition.x--;
                                break;
                            case UP:
                                logicalPosition.y--;
                                break;
                            case DOWN:
                                logicalPosition.y++;
                                break;
                        }
                        parentBoard.dispatchEvent(new ActionEvent(this,Messeges.UPDATE,null));
                    }


                    activeMove = getMoveAI(ghostType);
                    isStuck = true;

                    //animTimer.stop();
                    //System.out.println("LOGICAL POS :" + logicalPosition.x + " , " + logicalPosition.y);
                    //if(todoMove != moveType.NONE) {
                    //    activeMove = todoMove;
                    //    todoMove = moveType.NONE;
                    //}
                }else{
                    isStuck = false;
                    //animTimer.start();
                }
                // }

                switch(activeMove){
                    case RIGHT:
                        if(pixelPosition.x >= (parentBoard.getM_x() -1) * 28){
                            return;
                        }
                        if((logicalPosition.x+1 < parentBoard.getM_x()) && (parentBoard.getMap()[logicalPosition.x+1][logicalPosition.y]>0) && ((parentBoard.getMap()[logicalPosition.x+1][logicalPosition.y]<26)||isPending)){
                            return;
                        }
                        pixelPosition.x ++;
                        break;
                    case LEFT:
                        if(pixelPosition.x <= 0){
                            return;
                        }
                        if((logicalPosition.x-1 >= 0) && (parentBoard.getMap()[logicalPosition.x-1][logicalPosition.y]>0) && ((parentBoard.getMap()[logicalPosition.x-1][logicalPosition.y]<26)||isPending)){
                            return;
                        }
                        pixelPosition.x --;
                        break;
                    case UP:
                        if(pixelPosition.y <= 0){
                            return;
                        }
                        if((logicalPosition.y-1 >= 0) && (parentBoard.getMap()[logicalPosition.x][logicalPosition.y-1]>0) && ((parentBoard.getMap()[logicalPosition.x][logicalPosition.y-1]<26)||isPending)){
                            return;
                        }
                        pixelPosition.y--;
                        break;
                    case DOWN:
                        if(pixelPosition.y >= (parentBoard.getM_y() -1) * 28){
                            return;
                        }
                        if((logicalPosition.y+1 < parentBoard.getM_y()) && (parentBoard.getMap()[logicalPosition.x][logicalPosition.y+1]>0) && ((parentBoard.getMap()[logicalPosition.x][logicalPosition.y+1]<26)||isPending)){
                            return;
                        }
                        pixelPosition.y ++;
                        break;
                }

                parentBoard.dispatchEvent(new ActionEvent(this,Messeges.COLTEST,null));
            }
        };
        moveTimer = new Timer(ghostDelay,moveAL);
        moveTimer.start();

        unweak1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unWeakenTimer2.start();
                unWeakenTimer1.stop();
            }
        };
        unWeakenTimer1 = new Timer(7000,unweak1);

        unweak2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(unweakBlinks == 10){
                    unweaken();
                    unWeakenTimer2.stop();
                }
                if(unweakBlinks % 2 == 0){
                    isWhite = true;
                }else{
                    isWhite = false;
                }
                unweakBlinks++;
            }
        };
        unWeakenTimer2 = new Timer(250,unweak2);


        pendingAL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPending = false;
                pendingTimer.stop();
            }
        };
        pendingTimer = new Timer(7000,pendingAL);

        baseReturner = new BFSFinder(pb);
        //start AI
        activeMove = getMoveAI(ghostType);

    }

    //load Images from Resource
    public abstract void loadImages();

    // TODO 4a: Extract the implementations here to their respective subclasses
    // TODO 4b: Create a central log method and refactor the repeated logging methods in the various subclasses
    // TODO 5: Remove the ghostType argument and refactor the different lines of code calling the getMoveAI method so they don't pass any arguement.
    // TODO 6: Make this method an abstract method.
    //get Move Based on AI
    public MoveType getMoveAI(String ghostType) {

        if (ghostType == null) {
            return MoveType.NONE;
        }

        switch (ghostType) {
            case "CyanGhost":
                if(isPending){
                    if(isStuck){
                        if(pendMove == MoveType.UP){
                            pendMove = MoveType.DOWN;
                        }else if(pendMove == MoveType.DOWN){
                            pendMove = MoveType.UP;
                        }

                    }
                    System.out.println("The next move for CyanGhost is " + pendMove);
                    return pendMove;
                }

                MoveType CyanNextMove;
                if(isDead) {
                    CyanNextMove = baseReturner.getMove(logicalPosition.x,logicalPosition.y, parentBoard.ghostBase.x,parentBoard.ghostBase.y);
                }else {
                    ArrayList<MoveType> pm = getPossibleMoves("CyanGhost");
                    int i = ThreadLocalRandom.current().nextInt(pm.size());
                    lastCMove = pm.get(i);
                    CyanNextMove = lastCMove;
                }

                System.out.println("The next move for CyanGhost is " + CyanNextMove);
                return CyanNextMove;

            case "PinkGhost":
                if(isPending){
                    if(isStuck){
                        if(pendMove == MoveType.UP){
                            pendMove = MoveType.DOWN;
                        }else if(pendMove == MoveType.DOWN){
                            pendMove = MoveType.UP;
                        }
                    }
                    System.out.println("The next move for PinkGhost is " + pendMove);
                    return pendMove;
                }

                MoveType pinkNextMove;

                if(isDead) {
                    pinkNextMove = baseReturner.getMove(logicalPosition.x,logicalPosition.y, parentBoard.ghostBase.x,parentBoard.ghostBase.y);
                }else {
                    if (lastCMove == null || isStuck) {
                        ArrayList<MoveType> pm = getPossibleMoves("PinkGhost");
                        int i = ThreadLocalRandom.current().nextInt(pm.size());
                        lastCMove = pm.get(i);
                        pinkNextMove = lastCMove;
                    } else {
                        pinkNextMove = lastCMove;
                    }
                }

                System.out.println("The next move for CyanGhost is " + pinkNextMove);
                return pinkNextMove;

            case "RedGhost":
                if(isPending){
                    if(isStuck){
                        if(pendMove == MoveType.UP){
                            pendMove = MoveType.DOWN;
                        }else if(pendMove == MoveType.DOWN){
                            pendMove = MoveType.UP;
                        }

                    }
                    System.out.println("The next move for RedGhost is " + pendMove);
                    return pendMove;
                }

                if(bfs==null) {
                    bfs = new BFSFinder(parentBoard);
                }

                MoveType redNextMove;

                if(isDead) {
                    redNextMove = baseReturner.getMove(logicalPosition.x,logicalPosition.y, parentBoard.ghostBase.x,parentBoard.ghostBase.y);
                }else{
                    redNextMove = bfs.getMove(logicalPosition.x, logicalPosition.y, parentBoard.getPacman().getLogicalPosition().x, parentBoard.getPacman().getLogicalPosition().y);
                }

                System.out.println("The next move for RedGhost is " + redNextMove);
                return redNextMove;

            default:
                return MoveType.NONE;
        }
    }

    //get possible Moves
    public ArrayList<MoveType> getPossibleMoves(String ghostType){
        ArrayList<MoveType> possibleMoves = new ArrayList<>();

        if(logicalPosition.x >= 0 && logicalPosition.x < parentBoard.getM_x() -1 && logicalPosition.y >= 0 && logicalPosition.y < parentBoard.getM_y() -1 ) {
            //System.out.println(this.toString());
            if (!(parentBoard.getMap()[logicalPosition.x + 1][logicalPosition.y] > 0)) {
                possibleMoves.add(MoveType.RIGHT);
            }

            if (!(parentBoard.getMap()[logicalPosition.x - 1][logicalPosition.y] > 0)) {
                possibleMoves.add(MoveType.LEFT);
            }

            if(!(parentBoard.getMap()[logicalPosition.x][logicalPosition.y-1]>0)){
                possibleMoves.add(MoveType.UP);
            }

            if(!(parentBoard.getMap()[logicalPosition.x][logicalPosition.y+1]>0)){
                possibleMoves.add(MoveType.DOWN);
            }
        }

        return possibleMoves;
    }

    public Image getGhostImage(){
        if(!isDead) {
            if (!isWeak) {
                switch (activeMove) {
                    case RIGHT:
                        return ghostR[activeImage];
                    case LEFT:
                        return ghostL[activeImage];
                    case UP:
                        return ghostU[activeImage];
                    case DOWN:
                        return ghostD[activeImage];
                }
                return ghostR[activeImage];
            } else {
                if (isWhite) {
                    return ghostWW[activeImage];
                } else {
                    return ghostW[activeImage];
                }
            }
        }else{
            return ghostEye;
        }
    }


    public void weaken(){
        isWeak = true;
        moveTimer.setDelay(ghostWeakDelay);
        unweakBlinks = 0;
        isWhite = false;
        unWeakenTimer1.start();
    }

    public void unweaken(){
        isWeak = false;
        moveTimer.setDelay(ghostNormalDelay);
    }

    public void die(){
        isDead = true;
        moveTimer.setDelay(ghostDeadDelay);
    }

    public void undie(){
        //Shift Left Or Right
        int r = ThreadLocalRandom.current().nextInt(3);
        if (r == 0) {
            //Do nothing
        }
        if(r==1){
            logicalPosition.x += 1;
            pixelPosition.x += 28;
        }
        if(r==2){
            logicalPosition.x -= 1;
            pixelPosition.x -= 28;
        }
        isPending = true;
        pendingTimer.start();

        isDead = false;
        isWeak = false;
        moveTimer.setDelay(ghostNormalDelay);
    }

}
