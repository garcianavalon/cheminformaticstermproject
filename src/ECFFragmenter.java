import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;


public class ECFFragmenter {
	private IAtomContainerSet fragments;
	private IAtomContainer atomContainer;
	private IAtom starAtom = new Atom("*");
	
	public void generateFragments(IAtomContainer atomContainer) {
		this.atomContainer = atomContainer;
		this.fragments = new AtomContainerSet();
		for (IAtom a : atomContainer.atoms()) { // for each atom, generate fragments
			for (int i = 0; i<3 ; i++) {// iterate from one bond to three bond size
				IAtomContainer fragment = new AtomContainer(); 
				fragment.addAtom(a);
				if ((fragment = recursiveFragmenter(fragment,i)) != null)
					fragments.addAtomContainer(fragment); // Only add the fragment if it is distinct
			}
		}
		// sort Fragments
	}

	private IAtomContainer recursiveFragmenter(IAtomContainer fragment, int i) {
		boolean distinct = false;
		for (IAtom a : fragment.atoms()) {
			for (IAtom b : atomContainer.getConnectedAtomsList(a)) {
				if (!fragment.contains(b)) {
					distinct = true;
					if (i != 0) {
						fragment.addAtom(b);
						fragment.addBond(atomContainer.getBond(a, b));
					}
					else {
						fragment.addAtom(starAtom);
						fragment.addBond(new Bond(a,starAtom));
					}
				} else if (!fragment.contains(atomContainer.getBond(a, b)))
						fragment.addBond(atomContainer.getBond(a, b));
			}
		}
		if (i != 0)
			return recursiveFragmenter(fragment, i-1);
		else return distinct? fragment : null;
	}


	public String[] getFragments() {
		return null;
	}

	public IAtomContainer[] getFragmentsAsContainers() {
		return null;
	}

}
