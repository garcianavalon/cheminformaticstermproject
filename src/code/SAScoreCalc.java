package code;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

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
	
	public double calcMacroCyclePenalty() {
		return 0;
	}
	
	public double calcSizePenalty(){
		
		//natoms**1.005 - natoms
		int natoms = molecule.getAtomCount();
		return Math.pow(natoms, 1.005) - natoms;
	}
	

}
