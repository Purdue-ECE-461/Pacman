import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.*;

import javax.swing.*;

import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PacboardTest {

    Pacboard pacBoard;

    @Test
    public void getGhosts() {

        JLabel scoreBoardLabel = Mockito.mock(JLabel.class);
        PacWindow pacWindow = Mockito.mock(PacWindow.class);
        MapData mapData = getMapFromResource("resources/maps/map1_c.txt");

        pacBoard = new Pacboard(scoreBoardLabel, mapData, pacWindow);

        List<Ghost> ghosts = pacBoard.get_ghosts(mapData);

        assertNotNull(ghosts);

        assertThat(ghosts.size(), is(equalTo(6)));

    }

    public MapData getMapFromResource(String relPath){
        String mapStr = "";
        try {
            Scanner scn = new Scanner(this.getClass().getResourceAsStream(relPath));
            StringBuilder sb = new StringBuilder();
            String line;
            while(scn.hasNextLine()){
                line = scn.nextLine();
                sb.append(line).append('\n');
            }
            mapStr = sb.toString();
        }catch(Exception e){
            System.err.println("Error Reading Map File !");
        }
        if("".equals(mapStr)){
            System.err.println("Map is Empty !");
        }
        return MapEditor.compileMap(mapStr);
    }

}