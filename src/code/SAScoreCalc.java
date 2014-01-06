package code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.openscience.cdk.Ring;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ShortestPaths;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.stereo.Stereocenters;



public class SAScoreCalc {

	private ECFFragmenter fragmenter;
	private IAtomContainer molecule;

	public SAScoreCalc(IAtomContainer molecule) {
		this.molecule = molecule;
		this.fragmenter = new ECFFragmenter();
	}

	public double calculateScore(IAtomContainer atomContainer,boolean useAromaticity) throws CDKException, ClassNotFoundException, IOException {
		this.molecule = atomContainer;
		this.fragmenter.generateFragments(this.molecule,true);
		double fragmentContributions = FragmentContributionsCalc.getStoredContributions(this.fragmenter.getFragmentsAsSMILES(),useAromaticity);
		double complexityPenalty = this.calcComplexityScore(1,1,1,1);
		System.out.println("Complexity Penalty: " + complexityPenalty + "FragmentContributions: " + fragmentContributions);
		return fragmentContributions - complexityPenalty;
		
	}
	
	public double calcComplexityScore(double ringComplexityWeight, double stereoComplexityWeight, double sizePenaltyWeight,
			double macroCyclePenaltyWeight) throws CDKException {
		return ringComplexityWeight * this.calcRingComplexityScore() + stereoComplexityWeight * this.calcStereoComplexityScore() +
				sizePenaltyWeight*this.calcSizePenalty() + macroCyclePenaltyWeight*this.calcMacroCyclePenalty();
	}
	public double calcRingComplexityScore() throws CDKException {
		int nSpiroAtoms = getNumberOfSpiroAtoms();
		int nRingBridgeAtoms = getNumberOfRingBridgeAtoms();
		return Math.log10(nRingBridgeAtoms + 1) + Math.log10(nSpiroAtoms + 1);
	}
	public int getNumberOfSpiroAtoms() {
		
		RingSearch ringSearch = new RingSearch(molecule);
		List<IAtomContainer> isolatedRings = ringSearch.isolatedRingFragments();
		List<IAtom> checkedAtoms = new ArrayList<IAtom>();
		// iterate over isolated rings
		for(IAtomContainer isolatedRing : isolatedRings){
			//iterate over the atoms of the ring
			for(IAtom atom : isolatedRing.atoms()) {
				//check if the atom is in other ring
				for(IAtomContainer otherIsolatedRing : isolatedRings){
					if(isolatedRing.equals(otherIsolatedRing))
						continue;
					if(otherIsolatedRing.contains(atom)&&
							!checkedAtoms.contains(atom)){
						checkedAtoms.add(atom);
					}
				}
			}
		}
		return checkedAtoms.size();
	}
	public int getNumberOfRingBridgeAtoms() throws CDKException {
		int nRingBridgeAtoms = 0;
		RingSearch ringSearch = new RingSearch(molecule);
		List<IAtomContainer> fusedRings = ringSearch.fusedRingFragments();
		HashMap<IAtom, List<Integer>> subringsForEachAtom = new HashMap<IAtom, List<Integer>>();
		AllRingsFinder arf = new AllRingsFinder();
		for(IAtomContainer ring : fusedRings){
			//decompose in all posible sub-rings 
			IRingSet rs = arf.findAllRings(ring);
			//for each atom in the ring find all containnig subrings
			for(IAtom atom : ring.atoms()){
				IRingSet ringsContaining = rs.getRings(atom);
				//if appears in at least 3 subrings we have a bridge head
				if(ringsContaining.getAtomContainerCount() < 3)
					continue;
				//generate the ids lists
				List<Integer> ringsIDs = new ArrayList<Integer>(3);
				for(IAtomContainer subring : ringsContaining.atomContainers()){
					ringsIDs.add(subring.hashCode());
				}
				//first, check if there is a matching bridgehead already
				IAtom matchingBridgeHeadAtom=findMatchingBridgeHead(subringsForEachAtom,ringsIDs);
				if (matchingBridgeHeadAtom != null){
					//remove this atom from the map
					subringsForEachAtom.remove(matchingBridgeHeadAtom);
					//now, find the shortest path between the bridgeheads
					nRingBridgeAtoms += getShortestDistanceFromToIn(atom, matchingBridgeHeadAtom, ring)-1;
					
				} else {
					//add this to the map until we find the matching bridgehead
					subringsForEachAtom.put(atom, ringsIDs);
				}
				
			}
		}
		
		return nRingBridgeAtoms;
	}
	public IAtom findMatchingBridgeHead(HashMap<IAtom, List<Integer>> map, List<Integer> subringsIDs) {
		IAtom matching = null;
		for (Entry<IAtom, List<Integer>> entry : map.entrySet()) {
	        List<Integer> storedIDs = entry.getValue();
	        for(Integer id : subringsIDs) {
	        	if (!storedIDs.contains(id))
	        		return null;
	        	matching = entry.getKey();
	        }
	    }
		return matching;
	}
	public int getShortestDistanceFromToIn(IAtom from,IAtom to, IAtomContainer in){
		ShortestPaths sp = new ShortestPaths(in, from);
		return sp.distanceTo(to);
	}

	public double calcStereoComplexityScore() {
		int nStereoCenters = 0;
		Stereocenters  centers   = Stereocenters.of(molecule);
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			if (centers.isStereocenter(i)) {
				nStereoCenters += 1;	
			}
		}

		return Math.log10(nStereoCenters +1);
	}

	public double calcMacroCyclePenalty() throws CDKException {
		int nMacroCycles = 0;
		AllRingsFinder finder = new AllRingsFinder();
		Iterable<IAtomContainer> rings = finder.findAllRings(molecule).atomContainers();
		for(IAtomContainer ac : rings){
			Ring ring = (Ring)ac;
			if(ring.getAtomCount() > 8) //a ring of nine or more atoms is considered to be a macrocycle
				nMacroCycles += 1;     //http://en.wikipedia.org/wiki/Macrocycle
		}

		return Math.log10(nMacroCycles +1);
	}

	public double calcSizePenalty(){

		//natoms**1.005 - natoms
		int natoms = molecule.getAtomCount();
		return Math.pow(natoms, 1.005) - natoms;
	}


}
