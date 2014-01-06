package code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

public class FragmentContributionsCalc {

	private static ECFFragmenter fragmenter = new ECFFragmenter();
	private static HashMap<String, Integer> fragments;
	private static HashMap<String, Double> fragmentContributions;
	private static final boolean OUTPUT_ALL_CONTRIBUTIONS = false;
	private static final boolean OUTPUT_CONTRIBUTION_CALC = false;
	
	public static void main(String[] args) throws IOException, CDKException {
		
		fragments = new HashMap<String, Integer>();
		IOHandler handler = new IOHandler();
		IIteratingChemObjectReader<IAtomContainer> reader = handler.getIteratorForFile(args[0]);
		boolean useAromaticity = true;
		int i = 0;
		Long time = System.currentTimeMillis();
		Long time2 = System.currentTimeMillis();
		while (reader.hasNext()) {
			// generate some output
			if(i++ % 10000 == 0) {System.out.println(i + " Molecules Processed, time taken " + format(time2) +" , total" + format(time));
			time2 = System.currentTimeMillis();
			}
			
			updateFragmentFrequencyData(fragmentMolecule(reader.next(),useAromaticity));
		}
		HashMap<String, Double> contributions = calculateContributions();
		if (OUTPUT_ALL_CONTRIBUTIONS)
		for (Entry<String, Double> s: contributions.entrySet())
			System.out.println(s.getKey() + " " + s.getValue());
		String extra = useAromaticity ? "aromatic" : "standard";
		handler.serializeObject(contributions,extra+"map.ser");
		handler.serializeObject(fragments, extra+"count.ser");

        System.out.println("Total Runtime for " + i + " Molecules: " + format(time));
		
	}
	private static String format(Long time) {
		return (System.currentTimeMillis() - time) /1000 + " s";
	}
	/**
	 * 
	 * @param molecule A Molecule to be fragmented
	 * @return The generated fragments
	 * @throws CDKException 
	 */
	public static ArrayList<ECFFragment> fragmentMolecule(IAtomContainer molecule,boolean useAromaticity) throws CDKException{
		fragmenter.generateFragments(molecule,useAromaticity);
		return fragmenter.getFragmentsAsSMILES();
	}
	
	/**
	 * Update the List of fragmentdata with the values in the parameter
	 * @param fragments - An ArrayList of ECFfragments to be inserted in the list of ECFfragments
	 */

	public static void updateFragmentFrequencyData(ArrayList<ECFFragment> newFragments){
		Integer i = null;
		for (ECFFragment fragment : newFragments) {
			if ( (i = fragments.get(fragment.getKey())) != null) 
				fragments.put(fragment.getKey(), i + fragment.getCount());
			else fragments.put(fragment.getKey(), fragment.getCount());
		}
	}
	
	/**
	 * Translate the previously calculated list of fragments and frequency into a list
	 * of Fragments and Contributions
	 */
	public static HashMap<String, Double> calculateContributions() {
		System.out.println("Distinct Fragments retrieved: " + fragments.size());
		// calculate number of fragments that make up 80% of all retrieved fragments
		ArrayList<Integer> values = new ArrayList<Integer>(fragments.values());
		Long sum = 0l;
		for (Integer i : values) 
			sum += i;
		Double cutOff = 0.8 * sum;
		int count = 0;
		System.out.println("Total Count " + sum);
		sum = 0l;
		Collections.sort(values);
		Collections.reverse(values);
		while (sum < cutOff) 
			sum += values.get(count++);
		HashMap<String, Double>  fragmentContributions = new HashMap<String, Double>();
		for (String smiles: fragments.keySet()) {
			fragmentContributions.put(smiles, Math.log( new Double(fragments.get(smiles)) / new Double(count)));
			if (OUTPUT_CONTRIBUTION_CALC)
				System.out.println(smiles + ": " + fragments.get(smiles) + ", " + count + ", resultLog: " + fragmentContributions.get(smiles));
		}
		System.out.println("CutOff : " + count);
		return fragmentContributions;
		
	}
	
	public static double getStoredContributions(ArrayList<ECFFragment> fragments,boolean useAromaticity) throws ClassNotFoundException, IOException  {
		
		if (fragmentContributions == null) {
			IOHandler handler = new IOHandler();
			fragmentContributions = handler.loadStoredFragmentContributions(useAromaticity);
		}
        Double contributionsTotal = 0d;
        Double currentFragmentContribution = null;
        int fragmentCount = 0;
        for (ECFFragment frag : fragments) {
            fragmentCount+= frag.getCount();
        	currentFragmentContribution = fragmentContributions.get(frag.getKey());
        	System.out.println("farg " + frag.getKey() + "cont " + ((currentFragmentContribution != null) ? currentFragmentContribution * frag.getCount() :frag.getCount() * Math.log( new Double(1)/new Double(808) )));
        	contributionsTotal += currentFragmentContribution != null ? currentFragmentContribution * frag.getCount() : frag.getCount() * Math.log( new Double(1)/new Double(808) );
        }
		return contributionsTotal / fragmentCount;	}

	
}
