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
    Scanner input;
    ArtificialIntelligence IARed;
    ArtificialIntelligence IABlue;
    final int[] positionChiffre = new int[]{19, 23, 27, 50, 54, 58, 62, 88, 92, 96, 122, 126, 152};
    boolean slowmode; //0 pour une party avec 2,5s d'attente entre chaque tour, 1 pour une party sans attente
    int typeParty; // 0 IA hard vs IA hard, 1 PvsIA normal, 2 PvsIA hard, 3 IA normal vs IA hard

    public Party() {
        board = new Board();
        tree = new Tree(board);
        input=new Scanner(System.in);
    }

    public void start(int typeParty) {
        this.typeParty=typeParty;
/* PLAYER VERSUS IA */
        if (typeParty==1 || typeParty==2) {
            char[] plateauAffichable = initPvsIA();
            int turn = 2;
            while (turn < 12) {
                wait2sec();
                turn++;

                System.out.println(ANSI_BLUE + "Bleu :" + ANSI_RESET + " Entrer votre " + ((turn / 2) + 1) + "ieme coup (entre 0 et 12) : ");
                int coupB = input.nextInt();
                board.setPawn(coupB, (byte) ((turn / 2) + 1), (byte) turn);
                updatePlateau((turn / 2) + 1, coupB, plateauAffichable);
                affichePlateau(plateauAffichable);
                System.out.println(ANSI_BLUE + "Vous " + ANSI_RESET + "jouez " + coupB);

                wait2sec();
                turn++;
                // IA
                IARed.setB(board);
                Node nLastPlay = IARed.searchLastPlayNode(tree.root, (byte) (turn - 1));
                int coupR;
                if (turn<12) {
                    if (typeParty==1) coupR = IARed.computeEquitableSecondPlayer(nLastPlay);
                    else coupR=IARed.computeBestRedPlay(nLastPlay);
                } else coupR=IARed.computeBestLastRedPlay(nLastPlay);

                board.setPawn(coupR, (byte) (turn / 2 + 6), (byte) turn);
                updatePlateau(turn / 2, coupR, plateauAffichable);
                affichePlateau(plateauAffichable);
                System.out.println(ANSI_RED + "rouge " + ANSI_RESET + "joue " + coupR);
            }
            afficheScore();
        }
        else if (typeParty==0 || typeParty==3) {
            char[] plateauAffichable=initIAvsIA();
            int turn=2;
            while (turn<12) {
                // IA Bleue
                turn++;
                IABlue.setB(board);
                Node nLastPlayB=IABlue.searchLastPlayNode(tree.root,(byte)(turn-1));
                int coupB;
                if (typeParty==0) coupB=IABlue.computeBestBluePlay(nLastPlayB);
                else coupB=IABlue.computeEquitableFirstPlayer(nLastPlayB);
                board.setPawn(coupB,(byte)(turn/2+1), (byte)turn);

                updatePlateau((turn/2)+1,coupB,plateauAffichable);
                affichePlateau(plateauAffichable);
                System.out.println(ANSI_BLUE+"bleu :"+ANSI_RESET+ "joue "+coupB);

                wait2sec();
                turn++;
                // IA Rouge
                IARed.setB(board);
                Node nLastPlayR=IARed.searchLastPlayNode(tree.root,(byte)(turn-1));
                int coupR;
                if (turn<12) {
                     coupR=IARed.computeBestRedPlay(nLastPlayR);
                } else coupR=IARed.computeBestLastRedPlay(nLastPlayR);
                board.setPawn(coupR, (byte)(turn/2+6), (byte)turn);

                updatePlateau(turn/2,coupR,plateauAffichable);
                affichePlateau(plateauAffichable);

                System.out.println(ANSI_RED+"rouge :"+ANSI_RESET+" joue "+coupR);
                wait2sec();
            }
            afficheScore();
        }






/* IA VS IA */
/*
        char[] plateauAffichable=initIAvsIA();
        int turn=2;
        while (turn<12) {
            // IA Bleue
            turn++;
            IABlue.setB(board);
            Node nLastPlayB=IABlue.searchLastBluePlayNode(tree.root,(byte)(turn-1));
            int coupB=IABlue.computeEquitableFirstPlayer(nLastPlayB);
            board.setPawn(coupB,(byte)(turn/2+1), (byte)turn);

            updatePlateau((turn/2)+1,coupB,plateauAffichable);
            affichePlateau(plateauAffichable);
            System.out.println(ANSI_BLUE+"bleu :"+ANSI_RESET+ "joue "+coupB);

            wait2sec();
            turn++;
            // IA Rouge
            IARed.setB(board);
            Node nLastPlayR=IARed.searchLastBluePlayNode(tree.root,(byte)(turn-1));
            int coupR=IARed.computeBestRedPlay(nLastPlayR);
            board.setPawn(coupR, (byte)(turn/2+6), (byte)turn);

            updatePlateau(turn/2,coupR,plateauAffichable);
            affichePlateau(plateauAffichable);

            System.out.println(ANSI_RED+"rouge :"+ANSI_RESET+" joue "+coupR);
            wait2sec();
        }
        board.computeScore();
        System.out.println(ANSI_BLUE+"bleu : "+ANSI_RESET+board.blueScore+ANSI_RED+" rouge : "+board.redScore+ANSI_RESET);
*/
    }
    public int welcome() {
        System.out.println("***************************************************");
        System.out.println("*                "+ANSI_RED+"LE JEU DU DIAMANT"+ANSI_RESET+"                *");
        System.out.println("*  ETES-VOUS PRET A AFFRONTER NOS MEILLEURES IA ? *");
        System.out.println("*                                                 *");
        System.out.println("*         "+ANSI_BLUE+"Choisissez un partie de partie :"+ANSI_RESET+"        *");
        System.out.println("* Usage : 0 = IA hard vs IA hard                  *");
        System.out.println("*         1 = Player vs IA normal                 *");
        System.out.println("*         "+ANSI_RED+"2 = Player vs IA hard"+ANSI_RESET+"                   *");
        System.out.println("*         3 = IA normal vs IA hard                *");
        System.out.println("* Choisir : ");
        int tP=input.nextInt();
        if (tP>=0 && tP<4) typeParty=tP; else return welcome();
        if (typeParty==2) System.out.println("*                 "+ANSI_RED+"Bonne chance...!"+ANSI_RESET+"                 *");
        else System.out.println("*                   "+ANSI_BLUE+"Bon choix !"+ANSI_RESET+"                   *");
        System.out.println("*                                                 *");
        System.out.println("************** LA PARTIE VA COMMENCER **************");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        wait2sec();
        return typeParty;
    }

    public char[] initIAvsIA() {
        board.clearBoard();
        char[] plateauAffichable=buildPlat();
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
        return plateauAffichable;
    }
    public char[] initPvsIA() {
        board.clearBoard();
        char[] plateauAffichable=buildPlat();
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
        return plateauAffichable;
    }
    public char[] buildPlat() {

        String sToPrint= "  ************\n  *   *   *   *\n****************\n*   *   *   *   *\n*****************\n  *   *   *   *\n  *************\n    *   *   *\n    *********\n      *   *\n      *****\n";
        char[] charPrint = new char[sToPrint.length()];
        for (int i=0;i<sToPrint.length();i++) charPrint[i]=sToPrint.charAt(i);
        return charPrint;
    }
    public void affichePlateau(char[] tab) {
        for (int i=0; i<tab.length;i++) System.out.print(whichColor(tab[i],i));
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
    public void afficheScore() {
        board.computeScore();
        System.out.println("***********************");
        System.out.println("*        "+ANSI_BLUE+board.blueScore+":"+ANSI_RED+board.redScore+ANSI_RESET+"        *");
        System.out.println("*                     *");
        if (board.blueScore<board.redScore) System.out.println("* JOUEUR "+ANSI_BLUE+"BLEU"+ANSI_RESET+" GAGNE!  *");
        else if (board.redScore<board.blueScore) System.out.println("* JOUEUR "+ANSI_RED+"ROUGE"+ANSI_RESET+" GAGNE! *");
        else System.out.println("* C'EST UN MATCH NUL! *");
        System.out.println("*                     *");
        System.out.println("***********************");
    }
    public void wait2sec() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
