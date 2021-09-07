import java.awt.*;

//Finds Path Between two Maze Points Using Breadth-Frist Search (BFS)
public class BFSFinder {

    int[][] map;
    int mx;
    int my;

    public BFSFinder(Pacboard pb){
        this.mx = pb.getM_x();
        this.my = pb.getM_y();
        //init BFS map
        map = new int[pb.getM_x()][pb.getM_y()];
        for(int ii = 0; ii< pb.getM_y(); ii++){
            for(int jj = 0; jj< pb.getM_x(); jj++){
                if(pb.getMap()[jj][ii]>0 && pb.getMap()[jj][ii]<26){
                    map[jj][ii] = 1;
                }else{
                    map[jj][ii] = 0;
                }
            }
        }
    }

    private class MazeCell {
        int x;
        int y;
        boolean isVisited;

        public MazeCell(int x, int y) {
            this.x = x;
            this.y = y;
            isVisited =false;
        }

        public String toString() {
            return "x = " + x + " y = " + y;
        }
    }



    // TODO 3: Can you identify 2 more blocks of code in this method that can be extracted to a different method?
    //Construct Parentship LinkedList
    public MoveType getMove(int x, int y, int tx, int ty) {

        //already reached
        if(x==tx && y==ty){
            return MoveType.NONE;
        }

        //System.out.println("FINDING PATH FROM : " + x + "," + y + " TO " + tx + "," + ty);

        MazeCell[][] mazeCellTable = new MazeCell[mx][my];
        Point[][] parentTable = new Point[mx][my];
        boolean[][] markMat = new boolean[mx][my];

        for (int ii = 0; ii < mx; ii++) {
            for (int jj = 0; jj < my; jj++) {
                markMat[ii][jj] = false;
            }
        }

        MazeCell[] Q = new MazeCell[2000];
        int size = 1;

        MazeCell start = new MazeCell(x, y);
        mazeCellTable[x][y] = start;
        Q[0] = start;
        markMat[x][y] = true;

        for (int k = 0; k < size; k++) {
            int i = Q[k].x;
            int j = Q[k].y;

            // TODO 1: Extract the conditional predicate on line 79 to a separate method
            // TODO 2: Extract the contents of each of the IF blocks below to a seperate method
            //RIGHT
            if ((i + 1)>=0 && (i + 1)<mx && j>=0 && j<my && map[i + 1][j]==0 && !markMat[i + 1][j]) {
                MazeCell m = new MazeCell(i + 1, j);
                mazeCellTable[i + 1][j] = m;
                Q[size] = m;
                size++;
                markMat[i + 1][j] = true;
                parentTable[i + 1][j] = new Point(i, j);
            }


            //LEFT
            if ((i - 1)>=0 && (i - 1)<mx && j>=0 && j<my && map[(i - 1)][j]==0 && !markMat[(i - 1)][j] ) {
                MazeCell m = new MazeCell(i - 1, j);
                mazeCellTable[i - 1][j] = m;
                Q[size] = m;
                size++;
                markMat[i - 1][j] = true;
                parentTable[i - 1][j] = new Point(i, j);
            }

            //UP
            if (i>=0 && i<mx && (j - 1)>=0 && (j - 1)<my && map[i][(j - 1)]==0 && !markMat[i][(j - 1)]) {
                MazeCell m = new MazeCell(i, j - 1);
                mazeCellTable[i][j - 1] = m;
                Q[size] = m;
                size++;
                markMat[i][j - 1] = true;
                parentTable[i][j - 1] = new Point(i, j);
            }

            //DOWN
            if (i>=0 && i<mx && (j + 1)>=0 && (j + 1)<my && map[i][(j + 1)]==0 && !markMat[i][(j + 1)]) {
                MazeCell m = new MazeCell(i, j + 1);
                mazeCellTable[i][j + 1] = m;
                Q[size] = m;
                size++;
                markMat[i][j + 1] = true;
                parentTable[i][j + 1] = new Point(i, j);
            }
        }


        //MazeCell t = mazeCellTable[tx][ty];
        int ttx = tx;
        int tty = ty;
        MazeCell t = mazeCellTable[ttx][tty];
        MazeCell tl = null;
        while (t != start) {
            Point tp = parentTable[ttx][tty];
            ttx = tp.x;
            tty = tp.y;
            tl = t;
            t = mazeCellTable[ttx][tty];
        }

        if (x == tl.x - 1 && y == tl.y) {
            return MoveType.RIGHT;
        }
        if (x == tl.x + 1 && y == tl.y) {
            return MoveType.LEFT;
        }
        if (x == tl.x && y == tl.y - 1) {
            return MoveType.DOWN;
        }
        if (x == tl.x && y == tl.y + 1) {
            return MoveType.UP;
        }
        return MoveType.NONE;
    }

}