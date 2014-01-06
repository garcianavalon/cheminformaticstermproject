package code;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		InputHandler handler = new InputHandler();
		IIteratingChemObjectReader<IAtomContainer> reader = handler.getIteratorForFile(args[0]);
		int i = 0;
		Long time = System.currentTimeMillis();
		Long time2 = System.currentTimeMillis();
		while (reader.hasNext()) {
			// generate some output
			if(i++ % 10000 == 0) {System.out.println(i + " Molecules Processed, time taken " + (System.currentTimeMillis() - time2)/ 1000 + "s , total" + (System.currentTimeMillis() - time) + "s " );
			time2 = System.currentTimeMillis();
			}
			
			updateFragmentFrequencyData(fragmentMolecule(reader.next()));
		}
		HashMap<String, Double> contributions = calculateContributions();
		if (OUTPUT_ALL_CONTRIBUTIONS)
		for (Entry<String, Double> s: contributions.entrySet())
			System.out.println(s.getKey() + " " + s.getValue());
		FileOutputStream fos = new FileOutputStream("map.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(contributions);
        oos.close();
        fos = new FileOutputStream("count.ser");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(fragments);
        oos.close();
        System.out.println("Total Runtime for " + i + " Molecules: " + (System.currentTimeMillis() - time) /1000 + " s");
        
		
		
	}

	/**
	 * 
	 * @param molecule A Molecule to be fragmented
	 * @return The generated fragments
	 * @throws CDKException 
	 */
	public static ArrayList<ECFFragment> fragmentMolecule(IAtomContainer molecule) throws CDKException{
		fragmenter.generateFragments(molecule);
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
	
	@SuppressWarnings("unchecked")
	public static double getStoredContributions(ArrayList<ECFFragment> fragments)  {
		
		if (fragmentContributions == null) {
			FileInputStream fis = null;
		try {
			fis = new FileInputStream("map.ser");
		} catch (Exception e) {
			System.err.println("File not found");
			System.out.println(e.getMessage());
		}
        ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(fis);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
        try {
			fragmentContributions = (HashMap<String, Double>) ois.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
