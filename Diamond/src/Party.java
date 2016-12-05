import java.io.*;
import java.util.*;
/**
 * Created by sdomas on 04/10/16.

   NOTE : this class represents a party and its turns
   It is in charge of creating the board, the tree and to
   ask successively players for their choice. After the first turn of red,
   it computes the tree of all possible evolution of the party, and guess what
   red must play to win.
   Method that compute the decision must be implemented in the Tree class and called
   from Party. There should also be a method in Board to print the board.

 */

public class Party {
    Board board;
    Tree tree;
    BufferedReader consoleIn;
    Scanner input;
    ArtificialIntelligence IA;

    public Party() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        board = new Board();
        tree = new Tree(board);
        input=new Scanner(System.in);
    }

    public void start() {

        board.clearBoard();
        /* A COMPLETER :
           - afficher le plateau de jeu
           - demander son 1er coup au joueur bleu, et mettre à jour l'arbre et le plateau
           - afficher le plateau de jeu
           - demander son 1er coup au joueur rouge, et mettre à jour l'arbre et le plateau
           - calculer l'arbre de tous les coups possibles
           - tant que partie non finie :
              - demander son prochain coup au joueur bleu, et mettre à jour l'arbre et le plateau
              - afficher le plateau de jeu
              - calculer le meilleur prochain cou du rouge et mettre à jour l'arbre et le plateau
               - afficher le plateau de jeu
            - fin tant que
            - afficher vainqueur

         */

        System.out.println("Bleu : Entrer votre 1er coup (entre 0 et 12) : ");
        int coupB1 = input.nextInt();
        tree.setFirstBlueChoice(coupB1);

        System.out.println("Rouge : Entrer votre 1er coup (entre 0 et 12) : ");
        int coupR1 = input.nextInt();
        tree.setFirstRedChoice(coupR1);

        tree.buildTree();
        IA=new ArtificialIntelligence(tree,board);

        int turn=2;
        while (turn<12) {
            turn++;
            System.out.println("Bleu : Entrer votre "+((turn/2)+1)+"ieme coup (entre 0 et 12) : ");
            int coupB = input.nextInt();
            board.setPawn(coupB,(byte)((turn/2)+1), (byte)turn);

            turn++;
            // IA
            IA.setB(board);
            //IA.setT(tree);
            Node nLastPlay=IA.searchLastBluePlayNode(tree.root,(byte)(turn-1));
            int coupR=IA.computeBestPlay(nLastPlay);
            board.setPawn(coupR, (byte)(turn/2), (byte)turn);

            System.out.println("rouge : tour "+(turn/2)+ "joue "+coupR);
        }
        board.computeScore();
        System.out.println("bleu : "+board.blueScore+ " rouge : "+board.redScore);
    }
}
