import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class GhostTest {

    Pacboard pacBoard;
    Ghost cyanGhost, pinkGhost, redGhost;

    @Test
    public void getMoveAI() {

        pacBoard = Mockito.mock(Pacboard.class);

        Mockito.when(pacBoard.getM_x()).thenReturn(27);
        Mockito.when(pacBoard.getM_y()).thenReturn(29);

        int[][] dataMap = {{10, 20, 20, 20, 20, 20, 20, 20, 20, 11, 0, 0, 0, 10, 20, 11, 0, 0, 0, 10, 20, 20, 20, 20, 20, 20, 20, 20, 11}, {24, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 24}, {24, 0, 1, 6, 2, 0, 1, 2, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 1, 2, 0, 1, 6, 2, 0, 24}, {24, 0, 5, 9, 7, 0, 5, 7, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 5, 7, 0, 5, 9, 7, 0, 24}, {24, 0, 5, 9, 7, 0, 5, 7, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 5, 7, 0, 5, 9, 7, 0, 24}, {24, 0, 4, 8, 3, 0, 4, 3, 0, 13, 20, 20, 20, 12, 0, 13, 20, 20, 20, 12, 0, 4, 3, 0, 4, 8, 3, 0, 24}, {24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24}, {24, 0, 1, 6, 2, 0, 1, 6, 6, 6, 6, 6, 6, 2, 0, 1, 6, 6, 6, 6, 6, 6, 2, 0, 1, 6, 2, 0, 24}, {24, 0, 5, 9, 7, 0, 4, 8, 8, 8, 8, 8, 8, 3, 0, 4, 8, 8, 8, 8, 8, 8, 3, 0, 5, 9, 7, 0, 24}, {24, 0, 5, 9, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 9, 7, 0, 24}, {24, 0, 5, 9, 7, 0, 1, 2, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 1, 2, 0, 5, 9, 7, 0, 24}, {24, 0, 4, 8, 3, 0, 5, 7, 0, 5, 7, 0, 10, 20, 20, 20, 11, 0, 5, 7, 0, 5, 7, 0, 4, 8, 3, 0, 24}, {24, 0, 0, 0, 0, 0, 5, 7, 0, 4, 3, 0, 25, 0, 0, 0, 24, 0, 4, 3, 0, 5, 7, 0, 0, 0, 0, 0, 24}, {14, 20, 20, 20, 21, 0, 5, 7, 0, 0, 0, 0, 26, 0, 0, 0, 24, 0, 0, 0, 0, 5, 7, 0, 19, 20, 20, 20, 16}, {24, 0, 0, 0, 0, 0, 5, 7, 0, 1, 2, 0, 22, 0, 0, 0, 24, 0, 1, 2, 0, 5, 7, 0, 0, 0, 0, 0, 24}, {24, 0, 1, 6, 2, 0, 5, 7, 0, 5, 7, 0, 13, 20, 20, 20, 12, 0, 5, 7, 0, 5, 7, 0, 1, 6, 2, 0, 24}, {24, 0, 5, 9, 7, 0, 4, 3, 0, 4, 3, 0, 0, 0, 0, 0, 0, 0, 4, 3, 0, 4, 3, 0, 5, 9, 7, 0, 24}, {24, 0, 5, 9, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 9, 7, 0, 24}, {24, 0, 5, 9, 7, 0, 1, 6, 6, 6, 6, 6, 6, 2, 0, 1, 6, 6, 6, 6, 6, 6, 2, 0, 5, 9, 7, 0, 24}, {24, 0, 4, 8, 3, 0, 4, 8, 8, 8, 8, 8, 8, 3, 0, 4, 8, 8, 8, 8, 8, 8, 3, 0, 4, 8, 3, 0, 24}, {24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24}, {24, 0, 1, 6, 2, 0, 1, 2, 0, 10, 20, 20, 20, 11, 0, 10, 20, 20, 20, 11, 0, 1, 2, 0, 1, 6, 2, 0, 24}, {24, 0, 5, 9, 7, 0, 5, 7, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 5, 7, 0, 5, 9, 7, 0, 24}, {24, 0, 5, 9, 7, 0, 5, 7, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 5, 7, 0, 5, 9, 7, 0, 24}, {24, 0, 4, 8, 3, 0, 4, 3, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 4, 3, 0, 4, 8, 3, 0, 24}, {24, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 24, 0, 24, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 24}, {13, 20, 20, 20, 20, 20, 20, 20, 20, 12, 0, 0, 0, 13, 20, 12, 0, 0, 0, 13, 20, 20, 20, 20, 20, 20, 20, 20, 12}};

        Mockito.when(pacBoard.getMap()).thenReturn(dataMap);

        Pacman pacman = Mockito.mock(Pacman.class);

        Point point = new Point(1, 5);
        Mockito.when(pacman.getLogicalPosition()).thenReturn(point);

        Mockito.when(pacBoard.getPacman()).thenReturn(pacman);

        cyanGhost = new CyanGhost(17, 12, pacBoard);

        MoveType cyanGhostMoveType = cyanGhost.getMoveAI("CyanGhost");

        assertNotNull(cyanGhostMoveType);

        pinkGhost = new PinkGhost(17, 12, pacBoard);

        MoveType pintGhostMoveType = pinkGhost.getMoveAI("PinkGhost");

        assertNotNull(pintGhostMoveType);

        redGhost = new RedGhost(17, 12, pacBoard);

        MoveType redGhostMoveType = redGhost.getMoveAI("RedGhost");

        assertNotNull(redGhostMoveType);
    }
}
