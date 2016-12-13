/**
 * Created by malonesk on 05/12/2016.
 */
public class ArtificialIntelligence {
    private Tree t;
    private Board b;
    public ArtificialIntelligence(Tree t, Board b) {
        this.t=t;
        this.b=b;
    }
    public Node searchLastPlayNode(Node n, byte turn) {
        if(n.idCell==b.coupsJoues[turn-1] && n.turn==turn) {return n;}
        for (Node fils : n.children) {
            if(fils.idCell==b.coupsJoues[fils.turn-1]) {
                return searchLastPlayNode(fils,turn);
            }
        }
        return null;
    }
    public int computeBestRedPlay(Node n) {
        /*On calcule le meilleure coup de rouge d'une manière bête et méchante.
        Suivant le dernier coup de bleu, on calcule le coup à jouer qui amènera le plus de victoires de rouge
        selon l'arbre des possibilités (qui est préalablement généré)
         */
        /* pour tout les fils de n (tout les prochains coups de rouge)
            on calcule le nombre de victoire rouge dans le sous arbre de racine fils que l'on
            stocke que l'on compare avec un buffer max
            on sauvegarde le nom fils qui amène le plus de victoire de rouge
            on retourne l'idCell de ce noeud fils, c'est le meilleur coup que le rouge peut jouer.

         */
        int max=0;
        Node nMax=null;
        if (n.nbChildren!=0) {
            for (Node fils : n.children) {
                int nbWin=t.computeRedVictories(fils);
                if (max<nbWin) {
                    max=nbWin;
                    nMax=fils;
                }
            }
        } else nMax=n;
        if (nMax==null) return -1;
        return nMax.idCell;
    }
    public int computeBestLastRedPlay(Node n) {
        int r1 = n.children[0].result;
        int r2 = n.children[1].result;
        if (r1==r2) {
            return n.children[0].idCell;
        } else {
            if (r1==Node.RED_WINS) return n.children[0].idCell; else if (r2==Node.RED_WINS) return n.children[1].idCell;
            else if (r1==Node.DRAW_PARTY) return n.children[0].idCell; else if (r2==Node.DRAW_PARTY) return n.children[1].idCell;
            else return n.children[1].idCell;
        }
    }

    public int computeBestBluePlay(Node n) {
        int max=0;
        Node nMax=null;
        for (Node fils : n.children) {
            int nbWin=t.computeBlueVictories(fils);
            if (max<nbWin) {
                max=nbWin;
                nMax=fils;
            }
        }
        if (nMax==null) return -1;
        return nMax.idCell;
    }

    public int computeEquitableFirstPlayer(Node n) {
        /*Nous voulons ici jouer le noeud en dessous duquel les victoires rouges et bleues sont les plus proches.*/
        /*On doit donc, pour chaque fils de n
        *   calculer le nb de victoire rouge
        *   Calculer le nb de victoires bleues
        *   evaluer leur difference
        *   choisir le noeud ou la difference est minimum
        */
        int diff=t.computeBlueVictories(n)*2; //initialisation a une grande valeur
        int buff;
        Node nDiffMin=null;
        for (Node fils : n.children) {
            int redV=t.computeRedVictories(fils);
            int redB=t.computeBlueVictories(fils);
            buff=redB-redV;
            if (diff>buff) {
                diff=buff; //on veut le minimum de diff, ce qui equivaut a vouloir le max de redV par rapport a blueV
                nDiffMin=fils;
            }
        }
        if (nDiffMin==null) return -1;
        return nDiffMin.idCell;
    }

    public int computeEquitableSecondPlayer(Node n) {
        int min=t.computeRedVictories(n)*2; //initialisation a une grande valeur
        int buff;
        Node nMin=null;
        if (n.nbChildren!=0) {
            for (Node fils : n.children) {
                buff = t.computeRedVictories(fils);
                if (min > buff) {
                    min = buff;
                    nMin = fils;
                }
            }
        } else return n.children[0].idCell;
        if (nMin==null) return -2;
        return nMin.idCell;
    }





    public Tree getT() {
        return t;
    }

    public void setT(Tree t) {
        this.t = t;
    }

    public Board getB() {
        return b;
    }

    public void setB(Board b) {
        this.b = b;
    }
}
