package ws1718.ismla.Exercise.collection.fit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import ws1718.ismla.Exercise.types.DocumentMetaData;

/**
 * A component that recursively prepares all files in a directory for UIMA
 * processing.
 * 
 * @author bjoern
 *
 */
public class RecursiveFileReaderFit extends CasCollectionReader_ImplBase {

	public static final String PARAM_INPUT_DIR = "inputDirectory";
	@ConfigurationParameter(name = PARAM_INPUT_DIR)
	private String inputDirectory;

	public static final String PARAM_LANG = "language";
	@ConfigurationParameter(name = PARAM_LANG)
	private String language;

	// files to be processed
	private Iterator<File> fileIter;
	// number of processed files
	private int numProcessed;
	// total number of files
	private int numTotal;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		File f = new File(inputDirectory);
		if (!f.exists() || !f.isDirectory()) {
			throw new ResourceInitializationException();
		}
		ArrayList<File> files = indexFiles(f);
		fileIter = files.iterator();
		numProcessed = 0;
		numTotal = files.size();

	}

	public void getNext(CAS arg0) throws IOException, CollectionException {

		File nextFile = fileIter.next();
		// read string content of text file
		StringBuilder docText = new StringBuilder();
		Scanner sc = new Scanner(nextFile);
		while (sc.hasNextLine()) {
			docText.append(sc.nextLine());
		}
		sc.close();

		// get JCas
		JCas jcas = null;
		try {
			jcas = arg0.getJCas();
		} catch (CASException e) {
			throw new CollectionException(e);
		}

		jcas.setDocumentText(docText.toString());
		jcas.setDocumentLanguage(language);

		// add meta information about the document
		DocumentMetaData docMetaData = new DocumentMetaData(jcas);
		docMetaData.setBegin(0);
		docMetaData.setEnd(docText.toString().length());
		docMetaData.setSourcePath(nextFile.getAbsolutePath());
		docMetaData.addToIndexes(jcas);

		// for the progress information
		numProcessed++;
	}

	public void close() throws IOException {
		// do nothing
	}

	public boolean hasNext() throws IOException, CollectionException {
		return fileIter.hasNext();
	}

	/**
	 * Indexes all files recursively starting from a base directory. Ignores
	 * hidden file starting with a dot.
	 * 
	 * @param base
	 *            the starting directory.
	 * @return a list of all files.
	 */
	private static ArrayList<File> indexFiles(File base) {
		ArrayList<File> rval = new ArrayList<File>();

		for (File f : base.listFiles()) {
			// if it is a non-hidden file
			if (!f.getName().startsWith(".")) {
				if (f.isFile()) {
					rval.add(f);
				} else if (f.isDirectory()) {
					rval.addAll(indexFiles(f));
				}
			}
		}

		return rval;
	}

	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(numProcessed, numTotal, Progress.ENTITIES) };
	}

}
