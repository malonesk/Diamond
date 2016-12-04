#include <stdio.h>
#include <stdlib.h>
#include "diamond.h"

int nbConfigurations;

/**********************************
   functions to mangage the board
***********************************/
board_t* createBoard() {
  board_t* b = NULL;
  /* A COMPLETER :
     - allouer l'espace mémoire pour une board_t et mettre l'adresse dans b
     - "effacer" le plateau
     - mettre NO_NEIGHBOR dans toutes les cases du tableau b->neighbors
     - initialiser neighbors (cf. canevas Java)
  */
  b = (board_t*)malloc(sizeof(board_t));
  //struct board_t board = malloc(13*sizeof(char)+6*13*sizeof(char)+2*sizeof(int))
    clearBoard(b);
  for(int i=0;i<13;i++) {
    for(int j=0;j<6;j++) {
      b->neighbors[i][j] = NO_NEIGHBOR;
    }
  }
  // define neighbors for cell 0
  b->neighbors[0][0] = 1;
  b->neighbors[0][1] = 3;
  b->neighbors[0][2] = 4;
  // define neighbors for cell 1
  b->neighbors[1][0] = 0;
  b->neighbors[1][1] = 2;
  b->neighbors[1][2] = 4;
  b->neighbors[1][3] = 5;
  // define neighbors for cell 2
  b->neighbors[2][0] = 1;
  b->neighbors[2][1] = 5;
  b->neighbors[2][2] = 6;
  // define neighbors for cell 3
  b->neighbors[3][0] = 0;
  b->neighbors[3][1] = 4;
  b->neighbors[3][2] = 7;
  // define neighbors for cell 4
  b->neighbors[4][0] = 0;
  b->neighbors[4][1] = 1;
  b->neighbors[4][2] = 3;
  b->neighbors[4][3] = 5;
  b->neighbors[4][4] = 7;
  b->neighbors[4][5] = 8;
  // define neighbors for cell 5
  b->neighbors[5][0] = 1;
  b->neighbors[5][1] = 2;
  b->neighbors[5][2] = 4;
  b->neighbors[5][3] = 6;
  b->neighbors[5][4] = 8;
  b->neighbors[5][5] = 9;
  // define neighbors for cell 6
  b->neighbors[6][0] = 2;
  b->neighbors[6][1] = 5;
  b->neighbors[6][2] = 9;
  // define neighbors for cell 7
  b->neighbors[7][0] = 3;
  b->neighbors[7][1] = 4;
  b->neighbors[7][2] = 8;
  b->neighbors[7][3] = 10;
  // define neighbors for cell 8
  b->neighbors[8][0] = 4;
  b->neighbors[8][1] = 5;
  b->neighbors[8][2] = 7;
  b->neighbors[8][3] = 9;
  b->neighbors[8][4] = 10;
  b->neighbors[8][5] = 11;
  // define neighbors for cell 9
  b->neighbors[9][0] = 5;
  b->neighbors[9][1] = 6;
  b->neighbors[9][2] = 8;
  b->neighbors[9][3] = 11;
  // define neighbors for cell 10
  b->neighbors[10][0] = 7;
  b->neighbors[10][1] = 8;
  b->neighbors[10][2] = 11;
  b->neighbors[10][3] = 12;
  // define neighbors for cell 11
  b->neighbors[11][0] = 8;
  b->neighbors[11][1] = 9;
  b->neighbors[11][2] = 10;
  b->neighbors[11][3] = 12;
  // define neighbors for cell 12
  b->neighbors[12][0] = 10;
  b->neighbors[12][1] = 11;
  return b;
}

int freeBoard(board_t* board) {


    free(board);
    return 1;
}

void clearBoard(board_t* b) {
  for(int i=0;i<13;i++) b->board[i] = VOID_CELL;
  b->blueScore = 0;
  b->redScore = 0;
}

int voidCellIndex(board_t* b) {
  int id = -1;
  for(int i=0;i<13;i++) {
      if (b->board[i] == VOID_CELL) {
          return i;
      }
  }
    return id;
}

void computeScore(board_t* b) {
  b->blueScore = 0;
  b->redScore = 0;
  int idVoid = voidCellIndex(b);
  for(int i=0;i<6;i++) {
    if (b->neighbors[idVoid][i] != NO_NEIGHBOR) {
      if (b->board[(int)b->neighbors[idVoid][i]] <= (char)6) {
        b->blueScore += b->board[(int)b->neighbors[idVoid][i]];
      }
      else {
        b->redScore += (b->board[(int)b->neighbors[idVoid][i]]-6);
      }
    }
  }
}

void setPawn(board_t* b, int idCell, char value) {
  b->board[idCell] = value;
}


/**********************************
   functions to mangage the nodes
***********************************/

node_t* createNode(int idCell, int turn) {
  node_t* n = NULL;
  /* A COMPLETER :
     - allouer l'espace mémoire pour un node_t et mettre son adresse dans n
     - initialiser les champs idCell, turn, result, nbChildren (cf. canevas Java)
     - allouer l'espace mémoire pour children en fonction de turn (cf. canevas Java et énoncé)
  */
  n=(node_t*)malloc(sizeof(node_t));
  n->idCell = (char)idCell;
  n->turn = (char)turn;
    n->result = NO_RESULT;
  if (turn == 1) {
    n->children=(node_t**)malloc(sizeof(node_t*));//modif
      //n->children[0]=malloc(sizeof(node_t));
  }
  else if (turn < 12) {
    n->children = (node_t**)malloc((13-turn)*sizeof(node_t*)); //modif
      //for (int i=0;i<(13-turn);i++) n->children[i]=(node_t*)malloc((13-turn)*sizeof(node_t));
  }
    /* NB: nodes at turn 12 have a single child but this child is never used
      since it would represent the position of the void cell, which is useless
      to store in the tree. Furthermore, it saves a lot of place on the heap.
     */
  else {
    n->children = NULL;
  }
  n->nbChildren = (char)0;

  return n;
}
int freeNode(node_t* node) {
    for (int i=0;i<node->nbChildren;i++) {
        freeNode(node->children[i]);
    }
    free(node->children);
    free(node);
    return 1;
}

node_t* addChild(node_t* n, int idCell) {
  node_t* child = NULL;
  /* A COMPLETER : 
     - créer un nouveau noeud child avec comme paramètre idCell et n->turn+1
     - ajouter child aux fils de n et incrémenter son nombre de fils     
  */
    child=createNode(idCell,n->turn+(char)1);
  n->children[(int)n->nbChildren]=child;
  n->nbChildren=n->nbChildren+(char)1;
  return child;
}

/**********************************
   functions to mangage the tree
***********************************/

tree_t* createTree() {

  tree_t* t = NULL;
  /* A COMPLETER :
     - allouer l'espace mémoire pour un tree_t et mettre son adresse dans t
     - initialiser le champ root
  */
  t=(tree_t*)malloc(sizeof(tree_t));
  t->root=NULL;
  //t->root=(node_t*)sizeof(node_t);
  return t;
}

int freeTree(tree_t* tree) {
    freeNode(tree->root);
    free(tree);
    return 1;
}


void setFirstBlueChoice(tree_t* t, board_t* b, int idCell) {
  /* A COMPLETER : cf. canevas Java
  */
  //t->root=(node_t*)malloc(sizeof(node_t));

    t->root=createNode(idCell,(char)1);
  setPawn(b,idCell,(char)1);
}

void setFirstRedChoice(tree_t* t, board_t* b, int idCell) {
  /* A COMPLETER : cf. canevas Java
   */
  addChild(t->root,idCell);
    //t->root->children[0]=createNode(idCell,(char)1);
  setPawn(b,idCell,(char)1);
}

void buildTree(tree_t* t, board_t* b) {
    /*get the single child of the root, i.e. the node that
      represents the first pawn played by red player.
    */
    nbConfigurations=0;
    //node_t* n = t->root->children[0];
    //printf("node fils de root : %p\n",t);
    // call the recursive method that build the tree from that node
    computePossibilities(t->root->children[0],b);

    printf(" done.");
}

void computePossibilities(node_t* n, board_t* b) {

  /* A COMPLETER : cf. canevas Java
  */
  if (n->turn == (char)12) {
    computeScore(b);
      int r = b->redScore;
      int bl = b->blueScore;
      if (bl == r) {
      n->result = DRAW_PARTY;
    }
      // if blue obtains less than red -> blue wins
      else if (bl < r) {
      n->result = BLUE_WINS;
    }
      // if red obtains less than blue -> red wins
    else {
      n->result = RED_WINS;
    }
    nbConfigurations += 1;
    if ((nbConfigurations % 1000000) == 0) printf(".");
    return;
  }
  /* determine value of the next pawn that must be played from n.turn
   */

  int nextPawnValue = ((int)n->turn+2)/2;// get the "real" value (i.e. from 1 to 6)
  if ((((int)n->turn+1)%2) == 0) nextPawnValue += 6; // get the value in the board if it is a red one
  // check for all remaining void cells and try to place a pawn
  for(int i=0;i<13;i++) {
    // if the cell i is empty
    if (b->board[i] == VOID_CELL) {
      // place the pawn here
      setPawn(b, i,(char)nextPawnValue);
      // create the corresponding node in the tree
      node_t* child=(node_t*)malloc(sizeof(node_t));
       child=addChild(n, i);
      // recursive call
      computePossibilities(child,b);
      // remove pawn so that the cell appears to be free
      setPawn(b,i,VOID_CELL);
    }
  }



}

int computeBlueVictories(node_t* n) {
  int nb = 0;
  if (n->nbChildren==0) {
    if (n->result==BLUE_WINS) return 1;
  }
  else for(int i=0; i<n->nbChildren;i++) {
    nb+=computeBlueVictories(n->children[i]);
  }
  return nb;
}

int computeRedVictories(node_t* n) {
  int nb = 0;
  if (n->nbChildren==0) {
    if (n->result==RED_WINS) return 1;
  }
  else for(int i=0; i<n->nbChildren;i++) {
      nb+=computeRedVictories(n->children[i]);
    }
  return nb;

}

int computeDraws(node_t* n) {
  int nb = 0;
  if (n->nbChildren==0) {
    if (n->result==DRAW_PARTY) return 1;
  }
  else for(int i=0; i<n->nbChildren;i++) {
      nb+=computeDraws(n->children[i]);
    }
  return nb;

}


