/**
 * Created by sdomas on 03/10/16.
 */
public class Tree {
    Node root;
    Board board;

    int nbConfigurations;

    public Tree(Board board) {
        root = null;
        this.board = board;
	nbConfigurations = 0;
    }

    /* setFirstBlueChoice():
       create the root node of the tree, that represents the first turn of a party, i.e.
       the cell chosen by the blue player to put its pawn #1

       CAUTION: it also sets the pawn in the board
     */
    public void setFirstBlueChoice(int idCell) {

        root = new Node(idCell,1);
        board.setPawn(idCell,(byte)1);
    }

    /* setFirstRedChoice():
       create the node that represents the second turn of a party, i.e.
       the cell chosen by the red player to put its pawn #1

       CAUTION: it also sets the pawn in the board
     */
    public void setFirstRedChoice(int idCell) {
        root.addChild(idCell);
        board.setPawn(idCell,(byte)7);
    }

    /* buildTree();
        build the tree of all possible evolution of the party, taking into account
        the first choice of blue and red
     */
    public void buildTree() {

        nbConfigurations = 0;
        /*get the single child of the root, i.e. the node that
          represents the first pawn played by red player.
        */
        Node n = root.children[0];
        // call the recursive method that build the tree from that node
        computePossibilities(n);

        System.out.println(" done.");
    }

    /* computePossibilities():
       create all possible children of Node n.
     */
    public void computePossibilities(Node n) {

        /* if n represents the last turn of the party, stop the recursion
           and determine winner
         */
        if (n.turn == 12) {
            board.computeScore();
            int r = board.redScore;
            int b = board.blueScore;
            if (b == r) {
                n.result = Node.DRAW_PARTY;
            }
            // if blue obtains less than red -> blue wins
            else if (b < r) {
                n.result = Node.BLUE_WINS;
            }
            // if red obtains less than blue -> red wins
            else {
                n.result = Node.RED_WINS;
            }
            nbConfigurations += 1;
            if ((nbConfigurations % 1000000) == 0) System.out.print(".");
            return;
        }
        /* determine value of the next pawn that must be played from n.turn
         */
        int nextPawnValue = (n.turn+2)/2; // get the "real" value (i.e. from 1 to 6)
        if ((n.turn+1)%2 == 0) nextPawnValue += 6; // get the value in the board if it is a red one
        // check for all remaining void cells and try to place a pawn
        for(int i=0;i<13;i++) {
            // if the cell i is empty
            if (board.board[i] == Board.VOID_CELL) {
                // place the pawn here
                board.setPawn(i,(byte)nextPawnValue);
                // create the corresponding node in the tree
                Node child = n.addChild(i);
                // recursive call
                computePossibilities(child);
                // remove pawn so that the cell appears to be free
                board.setPawn(i,Board.VOID_CELL);
            }
        }
    }

    /* computeBlueVictories()
        determine recursively in how many cases blue wins
        in the tree that begins with n. This can be done
        by using the value of result attribute in leaves.
     */
    public int computeBlueVictories(Node n) {
        int nb = 0;
        /* A COMPLETER */
        if (n.nbChildren==0) {
            if (n.result == Node.BLUE_WINS) return 1;
        }
        else for (int i = 0; i < n.children.length; i++) {
            nb += computeBlueVictories(n.children[i]);
        }

        return nb;
    }

    /* computeRedVictories()
        determine recursively in how many cases red wins
        in the tree that begins with n. This can be done
        by using the value of result attribute in leaves.
     */
    public int computeRedVictories(Node n) {
        int nb = 0;
        /* A COMPLETER */
        if (n.nbChildren==0) {
            if (n.result==Node.RED_WINS) return 1;
        }
        else for(int i=0;i<n.children.length;i++) {
            nb+=computeRedVictories(n.children[i]);
        }
        return nb;
    }

    /* computeDraws()
        determine recursively in how many cases blue and red
         are draw in the tree that begins with n. This can be done
        by using the value of result attribute in leaves.
     */
    public int computeDraws(Node n) {
        int nb = 0;
        /* A COMPLETER */
        if (n.nbChildren==0) {
            if (n.result==Node.DRAW_PARTY) return 1;
        }
        else for(int i=0;i<n.children.length;i++) {
            nb+=computeDraws(n.children[i]);
        }
        return nb;
    }

    public int computeBestPlay(Node n) {
        /*On calcule le meilleure coup de rouge d'une manière bête et méchante.
        Suivant le dernier coup de bleu, on calcule le coup à jouer qui amènera le plus de victoires de rouge
        selon l'arbre des possibilités (qui est préalablement généré)
         */

        computePossibilities(n);
        /* l'arbre des possiblités est généré,
        /* pour tout les fils de n (tout les prochains coups de rouge)
            on calcule le nombre de victoire rouge dans le sous arbre de racine fils que l'on stocke que l'on compare avec un buffer max
            on sauvegarde le nom fils qui amène le plus de victoire de rouge
            on reotourne l'idCell de ce noeud fils, c'est le meilleur coup que le rouge peut jouer.

         */
        int max=0;
        Node nMax=null;
        for (Node fils : n.children) {
            int nbWin=computeRedVictories(fils);
            if (max<nbWin) {
                max=nbWin;
                nMax=fils;
            }
        }
        return nMax.idCell;
    }
}
