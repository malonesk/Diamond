/**
 * Created by sdomas on 03/10/16.
 */
public class Node {

    public static byte NO_RESULT = -1;
    public static byte DRAW_PARTY = 0;
    public static byte BLUE_WINS = 1;
    public static byte RED_WINS = 2;
    /* NOTE:
        Node represents what a player may play on the diamond board at a given turn.
        Nevertheless, all possible choices are not represented since it would result in
        a very big tree of possibilities. Thus, some assumption are taken :

        At turn 1, blue player has the choice among 13 cells and this choice represents
        the root of the tree. After that, red player have 12 choices resulting in 12 children for the root node.
        If we try to generate all  possibilities from that turn, their number would be 12! i.e. 479001600.
        It is too big so root has a single child, that will represent the red player choice and not all his
        possibilities.
        After the red player's turn, the blue plays again, and there are only 11 choices. It yields 11! possibilities
        for the rest of the party, which is more reasonable and can be stored in memroy.
     */
    Node[] children;
    byte nbChildren;
    byte idCell;
    /* NOTE:
       turn represent the aboslute turn in the party, thus it ranges from 1 to 12.
       It means that:
          - if turn%2 == 1, it represents a turn of the blue player
          - if turn%2 == 0, it represents a turn of the red player
          - (turn+1)/2 represents the pawn value played by the player at this turn.
       this is why there is no need to keep track of the value of the pawn
     */
    byte turn;
    /* NOTE:
       Normally, apart from nodes that represents leaves, i.e. the node for the 12th turn,
       there is no need to determine if blue/red wins. For convenience, the result is present in all
       nodes, even if it has no meaning.

        result possibles values are:
          - -1 : non applicable
          - 0 : draw
          - 1 : blue wins
          - 2 : red wins
     */
    byte result;

    public Node(int idCell, int turn) {
        this.idCell = (byte)idCell;
        this.turn = (byte)turn;
        if (turn == 1) {
            children = new Node[1];
        }
        else if (turn < 12) {
            children = new Node[13-turn];
        }
        /* NB: nodes at turn 12 have a single child but this child is never used
          since it would represent the position of the void cell, which is useless
          to store in the tree. Furthermore, it saves a lot of place on the heap.
         */
        else {
            children = null;
        }
        nbChildren = 0;
        result = NO_RESULT;
    }

    /* addChildren() :
       create a child node of the current one and put it in the
       array of children. By construction, the child node represents
       the pawn played at turn+1. The paramters idCell is the cell where
       the pawn is played.

       CAUTION : this method DO NOT pout the pawn in the board. It should be
       done outside this method, i.e. in the method that builds the tree, thus
       in the Tree class.
     */
    public Node addChild(int idCell) {
        children[nbChildren] = new Node(idCell,turn+1);
        nbChildren += 1;
        return children[nbChildren-1];
    }


}
