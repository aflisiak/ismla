package de.ws1718.ismla.UIMADemo.cpe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.ws1718.ismla.UIMADemo.types.DocumentMetaData;

public class RecursiveFileReader extends CollectionReader_ImplBase {

	private Iterator<File> iterator;

	private String language;

	private int numProcessed;
	private int numTotal;

	private static ArrayList<File> indexFiles(File base) {

		ArrayList<File> rval = new ArrayList<File>();

		for (File f : base.listFiles()) {
			if (!f.getName().startsWith(".")) {
				if (f.isFile()) {
					rval.add(f);
				} else if (f.isDirectory()) {
					ArrayList<File> list = indexFiles(f);
					rval.addAll(list);
				}
			}
		}

		return rval;
	}

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();

		File f = new File((String) getConfigParameterValue("inputDirectory"));

		if (!f.exists() || !f.isDirectory()) {
			throw new ResourceInitializationException();
		}

		ArrayList<File> files = indexFiles(f);
		numTotal = files.size();
		numProcessed = 0;

		iterator = files.iterator();

		language = (String) getConfigParameterValue("language");
	}

	public void getNext(CAS arg0) throws IOException, CollectionException {

		File nextFile = iterator.next();

		StringBuilder sb = new StringBuilder();
		Scanner sc = new Scanner(nextFile);
		while (sc.hasNextLine()) {
			sb.append(sc.nextLine());
		}
		sc.close();

		try {
			JCas jcas = arg0.getJCas();

			jcas.setDocumentText(sb.toString());
			jcas.setDocumentLanguage(language);

			DocumentMetaData meta = new DocumentMetaData(jcas);
			meta.setBegin(0);
			meta.setEnd(jcas.getDocumentText().length());
			meta.setSourcePath(nextFile.getAbsolutePath());
			meta.addToIndexes(jcas);

		} catch (CASException e) {
			e.printStackTrace();
		}

		numProcessed++;
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	public Progress[] getProgress() {
		return new Progress[] {new ProgressImpl(numProcessed, numTotal, Progress.ENTITIES)};
	}

	public boolean hasNext() throws IOException, CollectionException {
		return iterator.hasNext();
	}

}
