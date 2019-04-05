package de.ws1718.ismla.UIMADemo.cpe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.impl.cpm.container.NetworkCasProcessorImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

import de.ws1718.ismla.UIMADemo.types.Token;

public class TableConsumer extends CasConsumer_ImplBase {

	File outFile;
	
	private int fileCounter;
	
	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		
		String outDir = (String)getConfigParameterValue("outputDir");
		
		outFile = new File(outDir);
		
		if(!outFile.exists() || !outFile.isDirectory()){
			throw new ResourceInitializationException();
		}
		fileCounter = 0;
	}
	
	public void processCas(CAS arg0) throws ResourceProcessException {
		
		try {
			JCas jcas = arg0.getJCas();
			
			File file = new File(outFile + "/" + fileCounter++ +".tsv");
			
			PrintWriter pw = new PrintWriter(file);
			
			for(Token token : jcas.getAnnotationIndex(Token.class)){
				String text = token.getCoveredText();
				String pos = token.getPos();
				
				pw.println(text + "\t" + pos);
				
			}
			pw.close();
				
				
		} catch (CASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
