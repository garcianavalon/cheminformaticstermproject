package code;

import org.openscience.cdk.Ring;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.stereo.Stereocenters;



public class SAScoreCalc {
	
	private IAtomContainer molecule;
	
	public SAScoreCalc(IAtomContainer molecule) {
		this.molecule = molecule;
	}

	public double calculateScore(IAtomContainer atomContainer) {
		return 0;
	}
	
	public double calcRingComplexityScore() {
		double nSpiroAtoms = getNumberOfSpiroAtoms();
		double nRingBridgeAtoms = getNumberOfRingBridgeAtoms();
		return Math.log10(nRingBridgeAtoms + 1) + Math.log10(nSpiroAtoms + 1);
	}
	public double getNumberOfSpiroAtoms() {
		return 0;
	}
	public double getNumberOfRingBridgeAtoms() {
		return 0;
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
