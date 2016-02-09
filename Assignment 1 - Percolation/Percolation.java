import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {                 // create N-by-N grid, with all sites blocked
  private boolean[][] grid;
  private WeightedQuickUnionUF gridstatus = null;
  private int depth;
  private boolean perc = false;

  public Percolation(int k) {
    if (k <= 0) {
         throw new java.lang.IllegalArgumentException();
    }
    grid = new boolean[k+2][k+2];              // true = open, false = blocked
    gridstatus = new WeightedQuickUnionUF(k * k + 2);    // the connection status, position 0 = TOP, last: percolate controller
    depth = k;
  } 

  public void open(int i, int j) {        // open site (row i, column j) if it is not open already
    checkRange(i, j);                     // check whether it's effective
    if (isOpen(i, j)) return;             // check whether it's already opened
    grid[i][j] = true;                    // OPEN IT.

    int open_nb_cnt = 0;
    int[] nb = {0, 0, 0, 0};              // Neighbors

    /* 1. Check whether is there any open neighborhood around, if only one => just union them
       2. If more than one: check their connectivity. if connected => just union anyone
       3. If not, union all of them.*/
    if (i == 1) {
      gridstatus.union(0, reduceDim(i, j));      // First row, it's connected with TOP (FULL)
    }
    if (i == depth) {
      gridstatus.union(reduceDim(i, j), (depth * depth + 1));
    }

    if (grid[i-1][j] && (i > 1)) {        // Check its neighborhood are open or not
      nb[open_nb_cnt] = reduceDim(i-1, j);
      open_nb_cnt++;
    }
    if (grid[i+1][j] && (i < depth)) { 
      nb[open_nb_cnt] = reduceDim(i+1, j);
      open_nb_cnt++;
    }
    if (grid[i][j-1] && (j > 1)) { 
      nb[open_nb_cnt] = reduceDim(i, j-1);
      open_nb_cnt++;
    }
    if (grid[i][j+1] && (j < depth)) { 
      nb[open_nb_cnt] = reduceDim(i, j+1);
      open_nb_cnt++;
    }

    if (open_nb_cnt == 1) {     // Only one neighbor => Just union them
      gridstatus.union(reduceDim(i, j), nb[0]);
    }

    if (open_nb_cnt >= 2) {     // Two open neighbors, check if neighbors are connected
      gridstatus.union(reduceDim(i, j), nb[0]);
      for (int num = open_nb_cnt-1; num >= 1; num--) {
        if (!gridstatus.connected(reduceDim(i, j), nb[num])) {
          gridstatus.union(reduceDim(i, j), nb[num]);
        }
      }
    }
  }

  private int reduceDim(int i, int j) {    // Convert the 2D array coordinate into 1D
    int pos = (depth)*(i - 1) + j;
    return pos;
  }
  public boolean isOpen(int i, int j) {   // is site (row i, column j) open?
    checkRange(i, j);
    return grid[i][j];
  }
  public boolean isFull(int i, int j) {   // is site (row i, column j) full?
    checkRange(i, j);
    return gridstatus.connected(0, ((depth)*(i - 1) + j));
  }
  public boolean percolates() {           // does the system percolate?
    return gridstatus.connected(0, (depth * depth + 1));
  } 
  private void checkRange(int i, int j) {
    if (i < 1 || j < 1 || i > depth || j > depth) {
      throw new IndexOutOfBoundsException();
    }
  }
  public static void main(String[] args) { } // test client (optional)
}