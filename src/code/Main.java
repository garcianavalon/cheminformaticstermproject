package code;

import java.util.AbstractMap;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.smiles.SmilesParser;

public class Main {



	public static void main(String[] args) throws CDKException{

		SmilesParser   sp  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		//IAtomContainer molecule  = sp.parseSmiles("C1=CC21C=C2");
		IAtomContainer molecule  = sp.parseSmiles("C1CC2CCC1C2");
		RingSearch ringSearch = new RingSearch(molecule);
		List<IAtomContainer> fusedRings = ringSearch.fusedRingFragments();
		IAtomContainer ring = fusedRings.get(0);
		AllRingsFinder arf = new AllRingsFinder();
		IRingSet rs = arf.findAllRings(ring);
		int x;
		x=0;
	}
}
