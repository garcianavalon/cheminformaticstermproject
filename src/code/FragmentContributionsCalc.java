package code;
import java.util.ArrayList;
import org.openscience.cdk.interfaces.IAtomContainer;

public class FragmentContributionsCalc {

	private ECFFragmenter fragmenter;
	
	
	public static void main(String[] args) {
		
		
	}

	/**
	 * 
	 * @param molecule A Molecule to be fragmented
	 * @return The generated fragments
	 */
	public ArrayList<ECFFragment> fragmentMolecule(IAtomContainer molecule){
		fragmenter.generateFragments(molecule);
		return fragmenter.getFragmentsAsSMILES();
	}
	
	/**
	 * Update the List of fragmentdata with the values in the parameter
	 * @param fragments - An ArrayList of ECFfragments to be inserted in the list of ECFfragments
	 */
	public void updateFragmentFrequencyData(ArrayList<ECFFragment> fragments){
		
	}
	
	/**
	 * Translate the previously calculated list of fragments and frequency into a list
	 * of Fragments and Contributions
	 */
	public void calculateContributions() {
		
	}
	
	public double getStoredContributions(ArrayList<ECFFragment> fragments) {
		return 0;
	}

	

}
