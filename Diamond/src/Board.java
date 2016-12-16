/**
 * Created by sdomas on 03/10/16.
 */
public class Board {
    /*
    coupsJoues : tableau d'int content l'historique des coups joués
    intervient dans le calcul du "chemin" menant au denrier coups joués d'un joueur dans l'arbre
    utilisé par les IA
    coupsJoues est update dans la methode setPawn(int idCell, byte valeur, byte turn)
    d'où la modification de la signature de cette méthode
     */
    public int[] coupsJoues;

    public static byte VOID_CELL = -1;
    public static byte NO_NEIGHBOR = -1;
    /* NOTES :
       the board has a diamond shape. Each cell has an index
       ranging from 0 to 12, corresponding to its place in the diamond.
       It is represented below. A group of 2 identical values (for 0 to 9)
       represents the same cell. The values give its index.

        001122
       33445566
        778899
         1011
          12

       Thus, each cell has at most 6 neighbors.

       If this game was real, pawns played by players would be numbered from 1 to 6 but in this class
       the blue pawns are numbered from 1 to 6, and red pawns from 7 to 12.

     */
    byte[] board;
    private byte[][] neighbors;
    int redScore;
    int blueScore;

    public Board() {
        coupsJoues = new int[12];
        board = new byte[13];
        clearBoard();
        neighbors = new byte[13][6];
        // first, initialize all cells as if they have no neighbors, i.e. -1 value
        for(int i=0;i<13;i++) {
            for(int j=0;j<6;j++) {
                neighbors[i][j] = NO_NEIGHBOR;
            }
        }
        // define neighbors for cell 0
        neighbors[0][0] = 1;
        neighbors[0][1] = 3;
        neighbors[0][2] = 4;
        // define neighbors for cell 1
        neighbors[1][0] = 0;
        neighbors[1][1] = 2;
        neighbors[1][2] = 4;
        neighbors[1][3] = 5;
        // define neighbors for cell 2
        neighbors[2][0] = 1;
        neighbors[2][1] = 5;
        neighbors[2][2] = 6;
        // define neighbors for cell 3
        neighbors[3][0] = 0;
        neighbors[3][1] = 4;
        neighbors[3][2] = 7;
        // define neighbors for cell 4
        neighbors[4][0] = 0;
        neighbors[4][1] = 1;
        neighbors[4][2] = 3;
        neighbors[4][3] = 5;
        neighbors[4][4] = 7;
        neighbors[4][5] = 8;
        // define neighbors for cell 5
        neighbors[5][0] = 1;
        neighbors[5][1] = 2;
        neighbors[5][2] = 4;
        neighbors[5][3] = 6;
        neighbors[5][4] = 8;
        neighbors[5][5] = 9;
        // define neighbors for cell 6
        neighbors[6][0] = 2;
        neighbors[6][1] = 5;
        neighbors[6][2] = 9;
        // define neighbors for cell 7
        neighbors[7][0] = 3;
        neighbors[7][1] = 4;
        neighbors[7][2] = 8;
        neighbors[7][3] = 10;
        // define neighbors for cell 8
        neighbors[8][0] = 4;
        neighbors[8][1] = 5;
        neighbors[8][2] = 7;
        neighbors[8][3] = 9;
        neighbors[8][4] = 10;
        neighbors[8][5] = 11;
        // define neighbors for cell 9
        neighbors[9][0] = 5;
        neighbors[9][1] = 6;
        neighbors[9][2] = 8;
        neighbors[9][3] = 11;
        // define neighbors for cell 10
        neighbors[10][0] = 7;
        neighbors[10][1] = 8;
        neighbors[10][2] = 11;
        neighbors[10][3] = 12;
        // define neighbors for cell 11
        neighbors[11][0] = 8;
        neighbors[11][1] = 9;
        neighbors[11][2] = 10;
        neighbors[11][3] = 12;
        // define neighbors for cell 12
        neighbors[12][0] = 10;
        neighbors[12][1] = 11;
    }

    public void clearBoard() {
        for(int i=0;i<13;i++) board[i] = VOID_CELL;
        blueScore = 0;
        redScore = 0;
    }
    /* voidCellIndex():

       returns the index of the frst encountered void cell, i.e.
       a cell that contains -1. Normally, this method shoud only be called
       when the party is over

     */
    private int voidCellIndex() {
        for(int i=0;i<13;i++) if (board[i] == VOID_CELL) return i;
        return -1; // abnormal case.
    }

    /* computeScore() :
       computes the number of points of blue/red pawns around
       the empty cell.

       CAUTION : this method should be called only
       if the party is over, i.e. when there is a single void cell.
     */
    public void computeScore() {
        blueScore = 0;
        redScore = 0;
        int idVoid = voidCellIndex();
        for(int i=0;i<6;i++) {
            if (neighbors[idVoid][i] != NO_NEIGHBOR) {
                if (board[neighbors[idVoid][i]] <= 6) {
                    blueScore += board[neighbors[idVoid][i]];
                }
                else {
                    redScore += (board[neighbors[idVoid][i]]-6);
                }
            }
        }
    }

    /* setPawn() :
       put a pawn of the given value at idCell in the board.
       // RAJOUTE
       update the played idCell in an array

       a byte turn have been added to the parameters to update this array, my bad
     */
    public void setPawn(int idCell, byte value, byte turn) {
        board[idCell] = value;
        coupsJoues[turn-1]=idCell;
    }
    /*
    We split the setPawn() method into one method to actually put a pawn in the board
    and another method to put a falsePawn is the board without updating the coupsJoues array
    This method is only use to build the tree of possibilites in computePossibilities()
     */
    public void setFalsePawn(int idCell, byte value) {
        board[idCell] = value;
    }

}
