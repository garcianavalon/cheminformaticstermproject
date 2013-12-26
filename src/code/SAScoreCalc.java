package code;
import org.openscience.cdk.Ring;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.ringsearch.AllRingsFinder;

public class SAScoreCalc {
	
	private IMolecule molecule;
	
	public SAScoreCalc(IMolecule molecule) {
		this.molecule = molecule;
	}

	public double calculateScore(IAtomContainer atomContainer) {
		return 0;
	}
	
	public double calcRingComplexityScore() {
		return 0;
	}
	
	public double calcStereoComplexityScore() {
		return 0;
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
