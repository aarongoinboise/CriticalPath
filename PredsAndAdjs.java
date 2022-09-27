import java.util.ArrayList;

/**
 * A node with references to predecessors, adjacent nodes, and edge values to
 * each, as well as start/end times in a DFS search, and the earliest/latest
 * completion times in a topological sequence.
 * 
 * @author Aaron Goin
 */
public class PredsAndAdjs {
	private String node;
	private ArrayList<String> preds;
	private ArrayList<Integer> pEdges;// indexes match preds
	private ArrayList<String> adjs;
	private ArrayList<Integer> aEdges;// indexes match adjs
	private int eC;
	private int lC;
	private int startT;
	private int endT;

	/**
	 * A node initialized with empty/non-valid values.
	 * 
	 * @param n the string value of the node, should be one letter or a letter and
	 *          apostrophe for prime values
	 */
	public PredsAndAdjs(String n) {
		node = n;
		preds = new ArrayList<String>();
		adjs = new ArrayList<String>();
		pEdges = new ArrayList<Integer>();
		aEdges = new ArrayList<Integer>();
		eC = lC = startT = endT = -1;
	}

	/**
	 * @return the string value of the node
	 */
	public String getNode() {
		return node;
	}

	/**
	 * @return the start time of the node in a DFS search
	 */
	public int getStartT() {
		return startT;
	}

	/**
	 * @param newST the start time of the node, set when discovered
	 */
	public void setStartT(int newST) {
		startT = newST;
	}

	/**
	 * @return the end time of the node in a DFS search
	 */
	public int getEndT() {
		return endT;
	}

	/**
	 * @param newET the end time of the node, set when discovered
	 */
	public void setEndT(int newET) {
		endT = newET;
	}

	/**
	 * @param idx the index of an adjacent value in this node's adjs, which is
	 *            different from the index of the node
	 * @return the adjacent string value
	 */
	public String getAdj(int idx) {
		return adjs.get(idx);
	}

	/**
	 * @return all adjacent String values of the node
	 */
	public ArrayList<String> getAdjs() {
		return adjs;
	}

	/**
	 * @param a the adjacent string value being added
	 */
	public void addAdj(String a) {
		adjs.add(a);
	}

	/**
	 * @param idx the index of an adjacent edge in this node's aEdges, equaling the
	 *            index of the adjacent String value in adjs
	 * @return the adjacent edge value
	 */
	public int getAdjEdge(int idx) {
		return aEdges.get(idx);
	}

	/**
	 * @param aE the edge value going towards an adjacent String value
	 */
	public void addAEdge(int aE) {
		aEdges.add(aE);
	}

	/**
	 * @return the number of adjacent values of this node
	 */
	public int getAEdgesSize() {
		return aEdges.size();
	}

	/**
	 * @param i the index of the adjacent value that will become prime
	 * @param a the String value of the adjacent value becoming prime, without the
	 *          apostrophe
	 */
	public void setAdjPrime(int i, String a) {
		adjs.remove(i);
		adjs.add(i, a + "'");
		aEdges.remove(i);
		aEdges.add(i, 0);
	}

	/**
	 * @param idx the index of an predecessor value in this node's preds, which is
	 *            different from the index of the node
	 * @return the predecessor string value
	 */
	public String getPred(int idx) {
		return preds.get(idx);
	}

	/**
	 * @return all predecessor String values of the node
	 */
	public ArrayList<String> getPreds() {
		return preds;
	}

	/**
	 * @param d the predecessor string value being added
	 */
	public void addPred(String d) {
		preds.add(d);
	}

	/**
	 * @param idx the index of an predecessor edge in this node's pEdges, equaling
	 *            the index of the predecessor String value in preds
	 * @return the predecessor edge value
	 */
	public int getPredEdge(int idx) {
		return pEdges.get(idx);
	}

	/**
	 * @param pE the edge value going towards an predecessor String value
	 */
	public void addPEdge(int pE) {
		pEdges.add(pE);
	}

	/**
	 * @return the number of predecessor values of this node
	 */
	public int getPEdgesSize() {
		return pEdges.size();
	}

	/**
	 * Sets all edge values in pEdges equal to zero.
	 */
	public void setPEdgesZero() {
		int s = this.getPEdgesSize();
		pEdges.removeAll(pEdges);
		pEZ(s);
	}

	/**
	 * Completes the method setPEdgesZero, by adding back 's' number of pEdges with
	 * a zero value.
	 * 
	 * @param s the number of pEdges
	 */
	private void pEZ(int s) {
		int i = 0;
		while (i < s) {
			pEdges.add(0);
			i++;
		}
	}

	/**
	 * Makes the only predecessor a prime node, used when this node is a "non-prime
	 * counterpart". Example: this node is a, and it's only predecessor will become
	 * a'.
	 * 
	 * @param pEdge the edge length between this node and its prime counterpart
	 */
	public void setPredsPrime(int pEdge) {
		preds.removeAll(preds);
		preds.add(node + "'");
		pEdges.removeAll(pEdges);
		pEdges.add(pEdge);
	}

	/**
	 * Sets predecessor values equal to a new set of strings, and its edges equal to
	 * zero, used for setting prime node predecessors.
	 * 
	 * @param newPreds the new String predecessor values of this prime node
	 */
	public void setPredsAndPEdges(ArrayList<String> newPreds) {
		preds = newPreds;
		int s = newPreds.size();
		pEdges.removeAll(pEdges);
		pEZ(s);
	}
	
	/**
	 * @return the EC value
	 */
	public int getEC() {
		return eC;
	}

	/**
	 * @param newEC the EC value, when discovered
	 */
	public void setEC(int newEC) {
		eC = newEC;
	}
	
	/**
	 * @return the LC value
	 */
	public int getLC() {
		return lC;
	}
	
	/**
	 * @param newLC the LC value, when discovered
	 */
	public void setLC(int newLC) {
		lC = newLC;
	}
	
	/**
	 * @return true if the node is prime, false if not
	 */
	public boolean isPrime() {
		if (node.contains("'")) {
			return true;
		}
		return false;
	}

	/**
	 * A String with the node String, EC, LC, and SlackTime, which is LC minus EC,
	 * formatted to fit the header in the driver's output.
	 */
	public String toString() {
		String ret = node;
		int minusP = 0;
		int minusECL = String.valueOf(eC).length() - 1;
		int minusLCL = String.valueOf(lC).length() - 1;

		if (node.contains("'")) {
			minusP = 1;
		}

		ret += new String(new char[15 - minusP]).replace("\0", " ");
		ret += eC;
		ret += new String(new char[6 - minusECL]).replace("\0", " ");
		ret += lC;
		ret += new String(new char[7 - minusLCL]).replace("\0", " ");
		ret += lC - eC;
		ret += new String(new char[3]).replace("\0", " ");
		return ret;
	}
}
