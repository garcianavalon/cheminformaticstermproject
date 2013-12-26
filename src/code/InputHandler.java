package code;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;


public class InputHandler {
	public IAtomContainer[] readSDF(String filename) {
		return null;
	}

	public IAtomContainer readSmiles(String smiles) {
		return null;
	}

    public IIteratingChemObjectReader<IAtomContainer> getIteratorForFile(String filename) throws FileNotFoundException {
    	
    	File file = new File(filename);
        //get the extension
        int i = filename.lastIndexOf('.');
        String extension = "";
        if (i > 0) {
            extension = filename.substring(i+1);
        }
        //create reader
        IIteratingChemObjectReader<IAtomContainer> iterativeReader=null;
        
        if (extension.equalsIgnoreCase("sdf")){
        	iterativeReader = new IteratingSDFReader(new FileInputStream(file), DefaultChemObjectBuilder.getInstance());
        } else if (extension.equalsIgnoreCase("smiles") || 
        			extension.equalsIgnoreCase("smi")){ 
        	iterativeReader = new IteratingSMILESReader(new FileInputStream(file), DefaultChemObjectBuilder.getInstance());
        }

        return iterativeReader;
    }
}