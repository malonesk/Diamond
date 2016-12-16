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
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_RESET = "\u001B[0m";
    public final int HARD_VS_HARD = 0;
    public final int P_VS_EASY = 1;
    public final int P_VS_HARD = 2;
    public final int EASY_VS_HARD = 3;

    private Board board;
    private Tree tree;
    private Scanner input;
    private ArtificialIntelligence IARed;
    private ArtificialIntelligence IABlue;
    private final int[] positionChiffre; //les positions des chiffres dans le tableau d'affichage du plateau
    private boolean isPlayable;

    public Party() {
        board = new Board();
        tree = new Tree(board);
        input=new Scanner(System.in);
        /*
        La position des chiffres dans le tableau de caractère d'affichage du plateau
         */
        positionChiffre = new int[]{19, 23, 27, 50, 54, 58, 62, 88, 92, 96, 122, 126, 152};
        isPlayable=false;
        IARed = new ArtificialIntelligence(tree,board);
        IABlue = new ArtificialIntelligence(tree,board);
    }

    public void start(int typeParty, boolean slowmode) {
/* PLAYER VERSUS IA */
        if (typeParty==P_VS_EASY || typeParty==P_VS_HARD) {
            char[] plateauAffichable = initParty(typeParty);
            int turn = 2;
            int coupB=-1;
            while (turn < 12) {
                isPlayable=false;
                if (slowmode) wait2sec();
                turn++;
                do {
                    System.out.print(ANSI_BLUE + "Bleu :" + ANSI_RESET + " Entrer votre " + ((turn / 2) + 1) + "ieme coup (entre 0 et 12) : ");
                    coupB = input.nextInt();
                    isPlayable(coupB);
                } while (!isPlayable);
                System.out.println();
                board.setPawn(coupB, (byte) ((turn / 2) + 1), (byte) turn);
                updatePlateau((turn / 2) + 1, coupB, plateauAffichable);
                affichePlateau(plateauAffichable);
                System.out.println(ANSI_BLUE + "Vous " + ANSI_RESET + "jouez " + coupB);

                if (slowmode) wait2sec();
                turn++;
                // IA
                IARed.setB(board);
                Node nLastPlay = IARed.searchLastPlayedNode(tree.root, (byte) (turn - 1));
                int coupR;
                if (turn<12) {
                    if (typeParty==P_VS_EASY) coupR = IARed.computeBadRedPlayer(nLastPlay);
                    else coupR=IARed.computeBestRedPlay(nLastPlay);
                } else coupR=IARed.computeBestLastRedPlay(nLastPlay);

                board.setPawn(coupR, (byte) (turn / 2 + 6), (byte) turn);
                updatePlateau(turn / 2, coupR, plateauAffichable);
                affichePlateau(plateauAffichable);
                System.out.println(ANSI_RED + "Rouge : " + ANSI_RESET + "joue " + coupR);
            }
            afficheScore();
        }
/* IA vs IA */
        else if (typeParty==HARD_VS_HARD || typeParty==EASY_VS_HARD) {
            char[] plateauAffichable=initParty(typeParty);
            int turn=2;
            while (turn<12) {
                // IA Bleue
                turn++;
                IABlue.setB(board);
                Node nLastPlayB=IABlue.searchLastPlayedNode(tree.root,(byte)(turn-1));
                int coupB;
                if (typeParty==HARD_VS_HARD) coupB=IABlue.computeBestBluePlay(nLastPlayB);
                else coupB=IABlue.computeBadBluePlayer(nLastPlayB);
                board.setPawn(coupB,(byte)(turn/2+1), (byte)turn);

                updatePlateau((turn/2)+1,coupB,plateauAffichable);
                affichePlateau(plateauAffichable);
                System.out.println(ANSI_BLUE+"Bleu : "+ANSI_RESET+ "joue "+coupB);

                if (slowmode) wait2sec();
                turn++;
                // IA Rouge
                IARed.setB(board);
                Node nLastPlayR=IARed.searchLastPlayedNode(tree.root,(byte)(turn-1));
                int coupR;
                if (turn<12) {
                     coupR=IARed.computeBestRedPlay(nLastPlayR);
                } else coupR=IARed.computeBestLastRedPlay(nLastPlayR);
                board.setPawn(coupR, (byte)(turn/2+6), (byte)turn);

                updatePlateau(turn/2,coupR,plateauAffichable);
                affichePlateau(plateauAffichable);

                System.out.println(ANSI_RED+"Rouge :"+ANSI_RESET+" joue "+coupR);
                if (slowmode) wait2sec();
            }
            afficheScore();
        }
    }

    private void isPlayable(int coup) {
        if (coup<0 || coup > 12) {
            isPlayable=false;
            return;
        }
        for (int i=0;i<positionChiffre.length;i++) {
            if (board.board[coup]!=-1) {
                isPlayable=false;
                return;
            }
        }
        isPlayable=true;
    }

    public int[] welcome() {
        int typePartie;
        System.out.println("***************************************************");
        System.out.println("*                "+ANSI_RED+"LE JEU DU DIAMANT"+ANSI_RESET+"                *");
        System.out.println("*  ETES-VOUS PRET A AFFRONTER NOS MEILLEURES IA ? *");
        System.out.println("*                                                 *");
        System.out.println("*         "+ANSI_BLUE+"Choisissez un partie de partie :"+ANSI_RESET+"        *");
        System.out.println("* Usage : 0 = IA hard vs IA hard                  *");
        System.out.println("*         1 = Player vs IA normal                 *");
        System.out.println("*         "+ANSI_RED+"2 = Player vs IA hard"+ANSI_RESET+"                   *");
        System.out.println("*         3 = IA normal vs IA hard                *");
        System.out.println("* Choisir :                                       *");
        int tP=input.nextInt();
        if (tP>=0 && tP<4) typePartie=tP; else return welcome();
        if (typePartie==2) System.out.println("*                 "+ANSI_RED+"Bonne chance...!"+ANSI_RESET+"                *");
        else System.out.println("*                   "+ANSI_BLUE+"Bon choix !"+ANSI_RESET+"                   *");
        System.out.println("*                                                 *");
        System.out.println("*                 Choisissez un mode :            *");
        System.out.println("* Usage : 0 = 2.5sec d'attente a chaque coup joué *");
        System.out.println("*         1 = Pas d'attente                       *");
        System.out.println("* Choisir :                                       *");
        int sM=input.nextInt();
        if (!(sM>=0 && sM<=1)) return welcome();
        System.out.println("*                                                 *");
        if (typePartie==2) System.out.println("*             "+ANSI_RED+"Preparez-vous au carnage."+ANSI_RESET+"           *");
        System.out.println("************** LA PARTIE VA COMMENCER *************");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        int[] res = new int[2];
        res[0]=typePartie;
        res[1]=sM;
        wait2sec();
        return res;
    }

    public char[] initParty(int typePartie) {
        board.clearBoard();
        char[] plateauAffichable=buildPlat();
        affichePlateau(plateauAffichable);
        int coupB1;
        if (typePartie==P_VS_EASY||typePartie==P_VS_HARD) {
            do {
                System.out.println(ANSI_BLUE + "Bleu : " + ANSI_RESET + "Entrer votre 1er coup (entre 0 et 12) : ");
                coupB1 = input.nextInt();
                isPlayable(coupB1);
            } while (!isPlayable);

            tree.setFirstBlueChoice(coupB1);
            updatePlateau(1, coupB1, plateauAffichable);
            affichePlateau(plateauAffichable);
        } else {
            int computedPawn;
            if (typePartie==P_VS_HARD) computedPawn=IARed.computeFirstRedPlay(board);
            else computedPawn=IARed.computeFirtRandomRedPlay(board);
            board.setPawn(computedPawn, (byte)1, (byte)2);
            tree.setFirstBlueChoice(computedPawn);
            updatePlateau(1, computedPawn, plateauAffichable);
            affichePlateau(plateauAffichable);
        }

        int computedRedPawn=IARed.computeFirstRedPlay(board);
        tree.setFirstRedChoice(computedRedPawn);
        updatePlateau(1,computedRedPawn,plateauAffichable);
        affichePlateau(plateauAffichable);

        tree.buildTree();
        IARed=new ArtificialIntelligence(tree,board);
        if (typePartie==HARD_VS_HARD || typePartie==EASY_VS_HARD) IABlue=new ArtificialIntelligence(tree,board);
        return plateauAffichable;
    }
    private char[] buildPlat() {

        String sToPrint= "   ***********\n  *   *   *   *\n ***************\n*   *   *   *   *\n *************** \n  *   *   *   *\n   *********** \n    *   *   *\n     ******* \n      *   *\n       *** \n";
        char[] charPrint = new char[sToPrint.length()];
        for (int i=0;i<sToPrint.length();i++) charPrint[i]=sToPrint.charAt(i);
        return charPrint;
    }
    private void affichePlateau(char[] tab) {
        for (int i=0; i<tab.length;i++) System.out.print(whichColor(tab[i],i));
    }

    private String whichColor(char val, int indexCharTableau) {
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
    private void updatePlateau(int valeurPion, int coup, char[] plateau) {
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
    private void afficheScore() {
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
    private void wait2sec() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
