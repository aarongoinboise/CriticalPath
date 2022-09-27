import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Driver program that discovers the activity nodes, EC, LC, and slack times of
 * an adjacency matrix in a text file.
 * 
 * @author Aaron Goin
 */
public class CriticalPath {
	private static ArrayList<PredsAndAdjs> pA;
	private static String horiz;
	private static String primes = "";
	private static int[][] txtTwoD;

	/**
	 * Uses relevant methods to display topological information on the console.
	 * 
	 * @param args the command line arguments used when running a program
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			usage();
		}
		scanAndAssignNodes(args[0]);
		sortTopo();
		postTopoSort();
		discoverPrimeNodes();
		primeWorkOne();
		primeWorkTwo();
		discoverECVals();
		discoverLCVals();
		output();

	}// end of main

	/**
	 * A usage statement is displayed when the args length isn't one, or an improper
	 * file format is used. The program exits as a result.
	 */
	private static void usage() {
		System.out.println("Usage: java CriticalPath <file name>\n");
		System.out.println("file name (required): the name of a file containing a correctly "
				+ "formatted and evenly spaced\nadjacency-matrix, where the axes are equivalent "
				+ "and have all of the nodes, -1 represents\nan edge that doesnt exist, and all "
				+ "other values represent non-negative edge weights between\nexisting nodes. "
				+ "All values in the S column must be -1, and all values in the F row must be\n-1, "
				+ "and all values in the F column must be -1 or 0. All non-negative numbers in "
				+ "rows should\nbe next to each other, no node should point to a previous node,\n"
				+ "and each node must only be one letter.");
		System.exit(1);
	}

	/**
	 * Parses information from a text file, and adds non-prime nodes with respective
	 * predecessor, and adjacency values. This also initializes the ArrayList that
	 * stores all of the nodes, and puts the initial non-prime node values in the
	 * list.
	 * 
	 * @param filename the name of the text file that contains an adjacency matrix
	 */
	private static void scanAndAssignNodes(String filename) {
		try {
			Scanner s = new Scanner(new File(filename));
			if (!s.hasNextLine()) {
				usage();
			}
			horiz = s.nextLine();
			horiz = horiz.replaceAll("\\s+", "");

			String vert = "";
			ArrayList<Integer> tempG = new ArrayList<Integer>();
			int theDimen = -1;
			while (s.hasNextLine()) {
				String currLine = s.nextLine();
				if (currLine.equals("")) {
					if (s.hasNext()) {
						usage();
					} else {
						break;
					}
				}
				Scanner t = new Scanner(currLine);
				String nxt = t.next();
				if (nxt.length() > 1) {
					usage();
				}
				vert += nxt;
				int currDimen = 0;
				while (t.hasNextInt()) {
					int next = t.nextInt();
					if (next < -1) {
						usage();
					}
					tempG.add(next);
					currDimen++;
				}
				t.close();
				tempG.add(-2);
				if (theDimen == -1) {
					theDimen = currDimen;
				} else {
					if (theDimen != currDimen) {
						usage();
					}
				}
			} // end of scanning
			s.close();

			if (horiz.length() != (tempG.indexOf(-2))) {
				usage();
			}

			if (!horiz.equals(vert) || !horiz.contains("S") || !horiz.contains("F")) {
				usage();
			}

			tempG.removeIf(n -> (n == -2));// remove all -2s

			/* Initialize predsAndAdjs list and add adj vals */
			pA = new ArrayList<PredsAndAdjs>();
			txtTwoD = new int[horiz.length()][horiz.length()];
			for (int i = 0; i < horiz.length(); i++) {
				String node = Character.toString(horiz.charAt(i));
				pA.add(new PredsAndAdjs(node));

				for (int j = 0; j < horiz.length(); j++) {
					int pT = tempG.remove(0);
					txtTwoD[i][j] = pT;
					/* Add each adj val */
					if (pT > -1) {
						pA.get(i).addAdj(Character.toString(horiz.charAt(j)));
						pA.get(i).addAEdge(pT);
					}
				}
			}

			checkTxtVals();

			/* Add pred vals */
			for (int i = 0; i < horiz.length(); i++) {
				PredsAndAdjs n = pA.get(i);
				for (int j = 0; j < horiz.length(); j++) {
					if (txtTwoD[j][i] > -1) {
						n.addPred(Character.toString(horiz.charAt(j)));
						n.addPEdge(txtTwoD[j][i]);
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Unable to load " + filename);
			System.out.println(e.toString() + "\n");
			usage();
		}
	}// end of scan

	/**
	 * Determines if text file has proper S and F values that enable a topological
	 * sequence.
	 */
	private static void checkTxtVals() {
		boolean fCZero = false;
		boolean sCSomething = false;
		for (int i = 0; i < txtTwoD.length; i++) {
			int nonNeg = -2;
			for (int j = 0; j < txtTwoD.length; j++) {
				if (horiz.charAt(i) == 'S' && !sCSomething) {
					if (txtTwoD[i][j] > -1) {
						sCSomething = true;
					}
				}

				/* Check that each non neg value in a column is equal (adjs) */
				if (txtTwoD[j][i] > -1) {
					if (nonNeg != -2 && nonNeg != txtTwoD[j][i]) {
						usage();
					}
					nonNeg = txtTwoD[j][i];
				}

				/* Verify S column is -1 */
				if (horiz.charAt(j) == 'S') {
					if (txtTwoD[i][j] > -1) {
						usage();
					}
				}

				/* and F row is -1 */
				if (horiz.charAt(j) == 'F') {
					if (txtTwoD[j][i] > -1) {
						usage();
					}

					/* Check that F column has at least one 0 */
					if (!fCZero) {
						if (txtTwoD[i][j] == 0) {
							fCZero = true;
						}
					}
				}

			} // inner for loop
			if (horiz.charAt(i) == 'S') {
				/* Check that the S row has at least 1 non-negative value */
				if (!sCSomething) {
					usage();
				}
			}

		} // outer for loop
		if (!fCZero) {
			usage();
		}
	}// end of method

	/**
	 * Assigns start and end times to nodes, as in a DFS search.
	 */
	private static void sortTopo() {
		if (!pA.get(0).getNode().equals("S")) {
			PredsAndAdjs s = pA.remove(getNodeIdxByString("S"));
			pA.add(0, s);
		}

		if (!pA.get(pA.size() - 1).getNode().equals("F")) {
			PredsAndAdjs f = pA.remove(getNodeIdxByString("F"));
			pA.add(f);
		}

		int idx = 0;
		int sFTnum = 1;

		boolean done = false;
		while (!done) {
			PredsAndAdjs curr = pA.get(idx);

			if (curr.getStartT() == -1) {
				curr.setStartT(sFTnum++);
			} else {
				if (!hasANonVisitAdj(curr)) {
					curr.setEndT(sFTnum++);
				}
			}

			if (curr.getNode().equals("S") && curr.getEndT() > -1) {
				done = true;
				break;
			}

			int sI = nextStartTIdx(curr);
			if (sI > -1) {
				idx = sI;
			} else {
				idx = nextEndTIdx(curr);
			}

			if (idx < 0) {
				usage();
			}

		} // end of outer while loop

	}

	/**
	 * @param n the String value of the node
	 * @return the index of the node value in the array of all node values
	 */
	private static int getNodeIdxByString(String n) {
		for (PredsAndAdjs node : pA) {
			if (node.getNode().equals(n)) {
				return pA.indexOf(node);
			}
		}
		return -1;
	}

	/**
	 * Finds the next possible node index that can be assigned a "start" value in a
	 * DFS search.
	 * 
	 * @param hasAdj a node that already has a start value, where possible nodes are
	 *               searched from
	 * @return the index of the node that can be assigned a start value
	 */
	private static int nextStartTIdx(PredsAndAdjs hasAdj) {
		int idx = -1;
		int aIdx = 0;
		int aSize = hasAdj.getAEdgesSize();
		while (aIdx < aSize) {
			PredsAndAdjs poss = getFormerPAByString(hasAdj.getAdj(aIdx));
			if (poss.getStartT() == -1) {
				idx = getNodeIdxByString(hasAdj.getAdj(aIdx));
				break;
			} else {
				aIdx++;
			}
		} // end of inner while loop

		return idx;
	}

	/**
	 * Finds the next possible node index that can be assigned a "end" value in a
	 * DFS search. Returns the node itself if no possible nodes exist.
	 * 
	 * @param hasAdj a node where possible nodes are searched from
	 * @return the index of the node that can be assigned an end value
	 */
	private static int nextEndTIdx(PredsAndAdjs hasPred) {
		if (hasPred.getEndT() < 0) {
			return getNodeIdxByString(hasPred.getNode());
		}

		int idx = -1;
		int pIdx = 0;
		int pSize = hasPred.getPEdgesSize();
		int maxST = -1;

		while (pIdx < pSize) {
			PredsAndAdjs poss = getFormerPAByString(hasPred.getPred(pIdx));
			if (poss.getStartT() > maxST) {
				idx = getNodeIdxByString(hasPred.getPred(pIdx));
				maxST = poss.getStartT();
			}
			pIdx++;
		}

		return idx;
	}

	/**
	 * Determines if a node has an adjacent value that hasn't been visited.
	 * 
	 * @param q the node value that is in question
	 * @return true if the node has an adjacent value that hasn't been visited,
	 *         false if not
	 */
	private static boolean hasANonVisitAdj(PredsAndAdjs q) {
		for (String a : q.getAdjs()) {
			if (getFormerPAByString(a).getStartT() < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sorts the list of node values in a topological sequence. This is done by
	 * their discovered end times in a DFS search. This method works in a reverse
	 * order, excluding the S and F nodes.
	 */
	private static void postTopoSort() {
		int nextIdx = pA.size() - 2;
		int mIdx;
		while (nextIdx > 0) {
			mIdx = findNextFOI(nextIdx);
			PredsAndAdjs n = pA.remove(mIdx);
			pA.add(nextIdx--, n);
		}
	}

	/**
	 * @param nextIdx the index that hasn't been sorted yet
	 * @return the index of the node that has the smallest end time value
	 */
	private static int findNextFOI(int nextIdx) {
		int minFT = Integer.MAX_VALUE;
		int retI = -1;
		for (int i = 1; i < nextIdx + 1; i++) {
			if (pA.get(i).getEndT() < minFT) {
				minFT = pA.get(i).getEndT();
				retI = i;
			}
		}
		return retI;
	}

	/**
	 * Discovers nodes that will be prime, and stores them in the static string
	 * 'primes'.
	 */
	private static void discoverPrimeNodes() {
		/* Discover nodes that will be prime */
		for (PredsAndAdjs a : pA) {
			if (a.getPEdgesSize() > 1) {
				primes += a.getNode();
			}
		}
	}

	/**
	 * Sets the prime values for adjacent nodes.
	 */
	private static void primeWorkOne() {
		for (int i = 0; i < pA.size(); i++) {
			PredsAndAdjs a = pA.get(i);
			for (int j = 0; j < a.getAEdgesSize(); j++) {
				String s = a.getAdjs().get(j);
				for (char c : primes.toCharArray()) {
					if (s.equals(Character.toString(c))) {
						a.setAdjPrime(j, s);
						break;
					}
				}
			}
		}
	}

	/**
	 * Sets nodes with prime predecessors to just have the prime value as the only
	 * predecessor with its respective edge value. Also adds the prime nodes with
	 * its respective values.
	 */
	private static void primeWorkTwo() {
		ArrayList<ArrayList<String>> tempPreds = new ArrayList<ArrayList<String>>();// only care about preds and pedges
		for (int i = 0; i < pA.size(); i++) {
			PredsAndAdjs p = pA.get(i);
			for (char c : primes.toCharArray()) {
				if (p.getNode().equals(Character.toString(c))) {
					int pEdge = p.getPredEdge(0);
					ArrayList<String> preds = new ArrayList<String>();
					for (String s : p.getPreds()) {
						preds.add(s);
					}
					tempPreds.add(preds);
					p.setPEdgesZero();
					p.setPredsPrime(pEdge);
					break;
				}
			}
		}

		/* Defines other values of the prime node */
		for (int i = 0; i < pA.size(); i++) {
			PredsAndAdjs pAndA = pA.get(i);
			for (char c : primes.toCharArray()) {
				if (pAndA.getNode().equals(Character.toString(c))) {
					pA.add(i, new PredsAndAdjs(pAndA.getNode() + "'"));
					pA.get(i).addAdj(pAndA.getNode());
					pA.get(i).addAEdge(pAndA.getPredEdge(0));
					pA.get(i).setPredsAndPEdges(tempPreds.remove(0));
					i++;
					break;
				}
			}
		}
	}

	/**
	 * Discovers the EC values of each node using its topological sequence.
	 */
	private static void discoverECVals() {
		/* Discover eC vals */
		pA.get(0).setEC(0);
		for (int i = 1; i < pA.size(); i++) {
			PredsAndAdjs curr = pA.get(i);
			if (curr.getPEdgesSize() > 1) {
				curr.setEC(getMaxFormerEC(curr));
			} else {
				PredsAndAdjs p = getFormerPAByString(curr.getPred(0));
				curr.setEC(p.getEC() + curr.getPredEdge(0));
			}
		}
	}

	/**
	 * @param p the node in question
	 * @return the maximum EC value of a node's predecessors
	 */
	private static int getMaxFormerEC(PredsAndAdjs p) {
		int max = 0;
		int idx = 0;
		for (String s : p.getPreds()) {
			int poss = getFormerEC(s) + p.getPredEdge(idx);
			if (poss > max) {
				max = poss;
			}
			idx++;
		}
		return max;
	}

	/**
	 * Gets the EC value of a node given it's String value.
	 * 
	 * @param fmr the node's String value
	 * @return the EC value of the node
	 */
	private static int getFormerEC(String fmr) {
		return getFormerPAByString(fmr).getEC();
	}

	/**
	 * @param fmr the String of the node that exists in this class's list
	 * @return the node given it's String node value
	 */
	private static PredsAndAdjs getFormerPAByString(String fmr) {
		for (PredsAndAdjs p : pA) {
			if (p.getNode().equals(fmr)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Discovers the LC values of each node using its topological sequence.
	 */
	private static void discoverLCVals() {
		/* Discover lC vals */
		pA.get(pA.size() - 1).setLC(pA.get(pA.size() - 1).getEC());
		for (int i = pA.size() - 2; i > -1; i--) {
			PredsAndAdjs curr = pA.get(i);
			if (curr.getAEdgesSize() > 1) {
				curr.setLC(getMinFormerLC(curr));
			} else {
				PredsAndAdjs p = getFormerPAByString(curr.getAdj(0));
				curr.setLC(p.getLC() - curr.getAdjEdge(0));
			}
		}
	}

	/**
	 * @param a the node in question
	 * @return the minimum LC value of a node's adjacent values
	 */
	private static int getMinFormerLC(PredsAndAdjs a) {
		int min = Integer.MAX_VALUE;
		int idx = 0;
		for (String s : a.getAdjs()) {
			int poss = getFormerLC(s) - a.getAdjEdge(idx);
			if (poss < min) {
				min = poss;
			}
			idx++;
		}
		return min;
	}

	/**
	 * Gets the LC value of a node given it's String value.
	 * 
	 * @param fmr the node's String value
	 * @return the LC value of the node
	 */
	private static int getFormerLC(String fmr) {
		return getFormerPAByString(fmr).getLC();
	}

	/**
	 * Outputs (on the console) each node's String value, EC, LC, and SlackTime.
	 * This is done after the nodes are arranged in a topological sequence, and
	 * their EC, LC, and SlackTime's are found.
	 */
	private static void output() {
		System.out.println("Activity Node   EC     LC   SlackTime");
		System.out.println("-----------------------------------------------------");
		for (PredsAndAdjs s : pA) {
			if (!s.isPrime()) {
				System.out.println(s.toString());
			}
		}
		System.out.println();
	}

}// end of class