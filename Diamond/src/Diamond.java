import java.io.*;

/**
 * Created by sdomas on 03/10/16.

 NOTE : this class is the main class.
 */
public class Diamond {

    public static void main(String[] args) {
        /*
        Board board = new Board();
        Tree tree = new Tree(board);
        */
        /* You can modify the 2 following values to test different configurations
           for the first and second turn and to see if blue wins more time than red.
         */
        /*
        int idCellBlue = 0;
        int idCellRed = 1;
        tree.setFirstBlueChoice(idCellBlue);
        tree.setFirstRedChoice(idCellRed);
        tree.buildTree();
        System.out.println("nb configurations: "+tree.nbConfigurations+", nb blue victories: "+tree.computeBlueVictories(tree.root)+", nb red victories: "+tree.computeRedVictories(tree.root)+", nb draws: "+tree.computeDraws(tree.root));
        */
        /* NOTE :

         if you want to start a real party, you should complete the Party class
         as indicated in the comments. You should also uncomment the two next lines
         and comment all the previous ones.
         */
        Party party = new Party();
        int typeParty=party.welcome();
        party.start(typeParty);
    }
}
