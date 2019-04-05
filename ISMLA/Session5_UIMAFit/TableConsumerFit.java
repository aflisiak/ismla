package ws1718.ismla.Exercise.collection.fit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

import ws1718.ismla.Exercise.types.Token;

public class TableConsumerFit extends JCasAnnotator_ImplBase {

	public static final String PARAM_OUTPUT_DIR = "outputDirectory";
	@ConfigurationParameter(name = PARAM_OUTPUT_DIR)
	private String outputDirectory;

	private File outFile;
	private int fileCounter;

	@Override
	public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		super.initialize(uimaContext);

		outFile = new File(outputDirectory);
		if (!outFile.exists() || !outFile.isDirectory()) {
			throw new ResourceInitializationException();
		}
		fileCounter = 0;
	}

	@Override
	public void process(JCas arg0) throws AnalysisEngineProcessException {
		// create a new output file
		File file = new File(outFile.getAbsolutePath() + "/" + fileCounter++ + ".tsv");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (Token token : arg0.getAnnotationIndex(Token.class)) {
			pw.println(token.getCoveredText() + "\t" + token.getPos());
		}

		pw.close();
	}

}
