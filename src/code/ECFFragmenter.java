package code;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * This class provides methods to generate and retrieve The ECF4-Type fragments
 * of an IAtomContainer
 * 
 * @author Trenous, Navalon
 * 
 */
public class ECFFragmenter {
	private ArrayList<AbstractMap.SimpleEntry<String, Integer>> fragmentCount;
	private ArrayList<IAtomContainer> fragmentList;
	private ArrayList<String> fragmentSMILES;
	private IAtomContainer atomContainer;

	/**
	 * constructor
	 */
	public ECFFragmenter() {
		super();
	}

	/**
	 * Generates the fragments for the given parameter. Subsequent calls to
	 * getFragmentsAsContainer() will return the fragments found in
	 * atomContainer
	 * 
	 * @param atomContainer
	 *            The IAtomContainer to be fragmented
	 */
	public void generateFragments(IAtomContainer atomContainer) {
		this.atomContainer = atomContainer;
		this.fragmentList = new ArrayList<IAtomContainer>();
		for (IAtom a : atomContainer.atoms()) { // for each atom, generate
												// fragments
			for (int i = 0; i < 3; i++) {// iterate from one bond to three bond
											// size
				IAtomContainer fragment = new AtomContainer();
				ArrayList<IAtom> atomList = new ArrayList<IAtom>();
				atomList.add(a);
				if ((fragment = recursiveFragmenter(atomList,
						new ArrayList<IBond>(), i)) != null)
					fragmentList.add(fragment); // Only add the fragment if it
												// is distinct
			}
		}
		this.generateCanonicalSmiles();
		this.generateFragmentCount();

	}

	private IAtomContainer recursiveFragmenter(ArrayList<IAtom> lastLayerAtoms,
			ArrayList<IBond> lastLayerBonds, int i) {
		ArrayList<IAtom> nextLayerAtoms = new ArrayList<IAtom>();
		ArrayList<IBond> nextLayerBonds = new ArrayList<IBond>();
		HashMap<IAtom, IAtom> starAtoms = new HashMap<IAtom, IAtom>();
		for (IAtom lastLayerAtom : lastLayerAtoms) {
			for (IAtom nextLayerAtom : atomContainer
					.getConnectedAtomsList(lastLayerAtom)) {
				if (!lastLayerAtoms.contains(nextLayerAtom)
						&& !nextLayerAtoms.contains(nextLayerAtom)
						&& starAtoms.get(nextLayerAtom) == null) {
					if (i != 0) {
						nextLayerAtoms.add(nextLayerAtom);
						nextLayerBonds.add(atomContainer.getBond(lastLayerAtom,
								nextLayerAtom));
					} else {
						IAtom starAtom = new Atom("Y");
						IBond starBond = new Bond(lastLayerAtom, starAtom);
						nextLayerAtoms.add(starAtom);
						nextLayerBonds.add(starBond);
						starAtoms.put(nextLayerAtom, starAtom);
					}
				} else if (!lastLayerBonds.contains(atomContainer.getBond(
						lastLayerAtom, nextLayerAtom))
						&& !nextLayerBonds.contains(atomContainer.getBond(
								lastLayerAtom, nextLayerAtom))) {
					if (starAtoms.get(nextLayerAtom) != null)
						nextLayerBonds.add(new Bond(lastLayerAtom, starAtoms
								.get(nextLayerAtom)));
					else
						nextLayerBonds.add(atomContainer.getBond(lastLayerAtom,
								nextLayerAtom));
				}
			}
		}
		if (nextLayerAtoms.isEmpty())
			return null;
		lastLayerAtoms.addAll(nextLayerAtoms);
		lastLayerBonds.addAll(nextLayerBonds);
		return (i == 0) ? createAtomContainer(lastLayerAtoms, lastLayerBonds)
				: recursiveFragmenter(lastLayerAtoms, lastLayerBonds, i - 1);

	}



	/**
	 * Remove duplicates and store the result as a List of canonical SMILES
	 * Strings paired with their count
	 */
	private void generateFragmentCount() {
		this.fragmentCount = new ArrayList<AbstractMap.SimpleEntry<String, Integer>>();
		while (!fragmentSMILES.isEmpty()) {
			int count = 1;
			String smile = fragmentSMILES.get(0);
			for (int i = 0; i < fragmentSMILES.size(); i++) {
				if (fragmentSMILES.get(i).equals(smile)) {
					fragmentSMILES.remove(i);
					count++;
					i--;
				}
			}
			fragmentCount.add(new AbstractMap.SimpleEntry<String, Integer>(
					smile, new Integer(count)));
		}
	}

	/**
	 * Turns the retreived fragments into a cannical SMILES representation
	 */
	private void generateCanonicalSmiles() {
		SmilesGenerator sg = new SmilesGenerator();
		this.fragmentSMILES = new ArrayList<String>();
		for (IAtomContainer fragment : this.fragmentList)
			fragmentSMILES.add(sg.createSMILES(fragment));

	}

	private IAtomContainer createAtomContainer(ArrayList<IAtom> atoms,
			ArrayList<IBond> bonds) {
		IAtomContainer atomContainer = new AtomContainer();
		for (IAtom atom : atoms)
			atomContainer.addAtom(atom);
		for (IBond bond : bonds)
			atomContainer.addBond(bond);
		return atomContainer;
	}

	/**
	 * Returns the set of fragments generated by the last call of
	 * generateFragments() as Pairs of String and Integer, representing their
	 * canonical SMILES and count, null if the method hasn't been called yet.
	 * 
	 * @return The generated Fragments
	 */
	public ArrayList<AbstractMap.SimpleEntry<String, Integer>> getFragmentsAsSMILES() {
		return this.fragmentCount;
	}

	/**
	 * Returns the set of fragments generated by the last call of
	 * generateFragments() as Pairs of Integers, representing their Hashvalues
	 * and count, null if the method hasn't been called yet.
	 * 
	 * @return The generated Fragments
	 */
	public ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> getFragmentsAsKeyValuePairs() {
		if (this.fragmentCount == null)
			return null;
		ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> keyValuePairs = new ArrayList<AbstractMap.SimpleEntry<Integer, Integer>>();
		for (AbstractMap.SimpleEntry<String, Integer> entry : this.fragmentCount)
			keyValuePairs.add(new SimpleEntry<Integer, Integer>(entry.getKey()
					.hashCode(), entry.getValue()));
		return keyValuePairs;
	}

}
