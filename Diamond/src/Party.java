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
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_BLUE = "\u001B[34m";
    final String ANSI_RESET = "\u001B[0m";
    Board board;
    Tree tree;
    BufferedReader consoleIn;
    Scanner input;
    ArtificialIntelligence IARed;
    ArtificialIntelligence IABlue;
    final int[] positionChiffre = new int[]{20, 24, 28, 52, 56, 60, 64, 90, 94, 98, 124, 128, 154};

    public Party() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        board = new Board();
        tree = new Tree(board);
        input=new Scanner(System.in);
    }

    public void start() {

        board.clearBoard();
/* PLAYER VERSUS PLAYER *//*

        System.out.println("Bleu : Entrer votre 1er coup (entre 0 et 12) : ");

        int coupB1 = input.nextInt();
        tree.setFirstBlueChoice(coupB1);

        System.out.println("Rouge : Entrer votre 1er coup (entre 0 et 12) : ");

        int coupR1 = input.nextInt();
        tree.setFirstRedChoice(coupR1);

        tree.buildTree();

        IARed=new ArtificialIntelligence(tree,board);

        int turn=2;
        while (turn<12) {
            turn++;
            System.out.println("Bleu : Entrer votre "+((turn/2)+1)+"ieme coup (entre 0 et 12) : ");
            int coupB = input.nextInt();
            board.setPawn(coupB,(byte)((turn/2)+1), (byte)turn);

            turn++;
            // IA
            IARed.setB(board);
            //IA.setT(tree);
            Node nLastPlay=IARed.searchLastBluePlayNode(tree.root,(byte)(turn-1));
            int coupR=IARed.computeBestPlay(nLastPlay);
            board.setPawn(coupR, (byte)(turn/2), (byte)turn);

            System.out.println("rouge : tour "+(turn/2)+ "\njoue "+coupR);
        }
        board.computeScore();
        System.out.println("bleu : "+board.blueScore+ " rouge : "+board.redScore);


        */



/* IA VS IA */
        char[] plateauAffichable=buildPlateau();
        affichePlateau(plateauAffichable);

        System.out.println(ANSI_BLUE+"Bleu : "+ANSI_RESET+"Entrer votre 1er coup (entre 0 et 12) : ");
        int coupB1 = input.nextInt();
        tree.setFirstBlueChoice(coupB1);

        updatePlateau(1,coupB1,plateauAffichable);
        affichePlateau(plateauAffichable);

        System.out.println(ANSI_RED+"Rouge : "+ANSI_RESET+"Entrer votre 1er coup (entre 0 et 12) : ");

        int coupR1 = input.nextInt();
        tree.setFirstRedChoice(coupR1);

        updatePlateau(1,coupR1,plateauAffichable);
        affichePlateau(plateauAffichable);

        tree.buildTree();
        IARed=new ArtificialIntelligence(tree,board);
        IABlue=new ArtificialIntelligence(tree,board);
        int turn=2;
        while (turn<12) {
            // IA Bleue
            turn++;
            IABlue.setB(board);
            Node nLastPlayB=IABlue.searchLastBluePlayNode(tree.root,(byte)(turn-1));
            int coupB=IABlue.computeEquitablePlay(nLastPlayB);
            board.setPawn(coupB,(byte)(turn/2+1), (byte)turn);

            updatePlateau((turn/2)+1,coupB,plateauAffichable);
            affichePlateau(plateauAffichable);
            System.out.println(ANSI_BLUE+"bleu :"+ANSI_RESET+ "joue "+coupB);

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            turn++;
            // IA Rouge
            IARed.setB(board);
            Node nLastPlayR=IARed.searchLastBluePlayNode(tree.root,(byte)(turn-1));
            int coupR=IARed.computeBestRedPlay(nLastPlayR);
            board.setPawn(coupR, (byte)(turn/2+6), (byte)turn);

            updatePlateau(turn/2,coupR,plateauAffichable);
            affichePlateau(plateauAffichable);

            System.out.println(ANSI_RED+"rouge :"+ANSI_RESET+" joue "+coupR);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        board.computeScore();
        System.out.println(ANSI_BLUE+"bleu : "+ANSI_RESET+board.blueScore+ANSI_RED+" rouge : "+board.redScore+ANSI_RESET);

    }
    public char[] buildPlateau() {
        char[] charPrint = new char[178];
        int lastLen=0;
        String charToPrint="";
        String sToPrint="";
        int li=0;
        for (int i = 0; i <13; i++) {
            switch (li) {
                case 0 :
                    sToPrint="  *************";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 1 :
                    sToPrint="  *   *   *   **";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 2 :
                    sToPrint="******************";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 3 :
                    sToPrint="*   *   *   *   **";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 4 :
                    sToPrint="******************";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 5 :
                    sToPrint="  *   *   *   **";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 7 :
                    sToPrint="  **************";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 8 :
                    sToPrint="    *   *   **";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 9 :
                    sToPrint="    **********";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 10 :
                    sToPrint="      *   **";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
                case 11 :
                    sToPrint="      ******";
                    charToPrint+=sToPrint;
                    for (int j=0;j<sToPrint.length();j++) charPrint[lastLen+j]=sToPrint.charAt(j);
                    lastLen=charToPrint.length();
                    charPrint[lastLen]='\n';
                    lastLen++;
                    break;
            }
            li++;
        }
        return charPrint;
    }

    public void affichePlateau(char[] tab) {
        for (int i=0; i<tab.length;i++) {

            System.out.print(whichColor(tab[i],i));
            //System.out.print(tab[i]);
        }

    }
    public String whichColor(char val, int indexCharTableau) {
        if (val==' ') return val+"";
        else if (val== '*') return val+"";
        else {
            for (int i=0;i<13;i++) if (positionChiffre[i]==indexCharTableau) {
                if (board.board[i]==-1) return " ";
                else if (board.board[i] > 6) return ANSI_RED+val+ANSI_RESET;
                else return ANSI_BLUE+val+ANSI_RESET;
            }
        }
        return ""+val;
    }


    public void updatePlateau(int valeurPion, int coup, char[] plateau) {

        char val=Integer.toString(valeurPion).charAt(0);
        
        switch(coup) {
            case 0 :
                plateau[positionChiffre[0]]=val;
                break;
            case 1 :
                plateau[positionChiffre[1]]=val;
                break;
            case 2 :
                plateau[positionChiffre[2]]=val;
                break;
            case 3 :
                plateau[positionChiffre[3]]=val;
                break;
            case 4 :
                plateau[positionChiffre[4]]=val;
                break;
            case 5 :
                plateau[positionChiffre[5]]=val;
                break;
            case 6 :
                plateau[positionChiffre[6]]=val;
                break;
            case 7 :
                plateau[positionChiffre[7]]=val;
                break;
            case 8 :
                plateau[positionChiffre[8]]=val;
                break;
            case 9 :
                plateau[positionChiffre[9]]=val;
                break;
            case 10 :
                plateau[positionChiffre[10]]=val;
                break;
            case 11 :
                plateau[positionChiffre[11]]=val;
                break;
            case 12 :
                plateau[positionChiffre[12]]=val;
                break;
        }
    }
}
