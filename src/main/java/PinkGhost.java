import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;


public class PinkGhost extends Ghost {

    public PinkGhost(int x, int y, Pacboard pb){
        super(x,y,pb,6, "PinkGhost");
    }

    @Override
    public void loadImages(){
        ghostR = new Image[2];
        ghostL = new Image[2];
        ghostU = new Image[2];
        ghostD = new Image[2];
        try {
            ghostR[0] = ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/1.png"));
            ghostR[1] = ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/3.png"));
            ghostL[0] = ImageHelper.flipHor(ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/1.png")));
            ghostL[1] = ImageHelper.flipHor(ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/3.png")));
            ghostU[0] = ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/4.png"));
            ghostU[1] = ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/5.png"));
            ghostD[0] = ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/6.png"));
            ghostD[1] = ImageIO.read(this.getClass().getResource("resources/images/ghost/pink/7.png"));
        }catch(IOException e){
            System.err.println("Cannot Read Images !");
        }
    }

}
