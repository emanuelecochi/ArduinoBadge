

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtilities {

	/**
	 * leggo il contentuo di un file in una string
	 */
	public static String read(final File input) throws FileProblemException {
		try {

			StringBuffer buffer = new StringBuffer();

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)));

			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
				buffer.append("\n");
			}

			reader.close();

			return buffer.toString();

		} catch (FileNotFoundException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		} catch (IOException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		}
	}

	/**
	 * @param output
	 *            - the output file
	 * @param content
	 *            - the content to write
	 * @throws FileProblemException
	 */
	public static void write(final File output, final String content) throws FileProblemException {

		try {

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
			writer.write(content + "\n");
			writer.close();

		} catch (FileNotFoundException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		} catch (IOException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		}

	}

	/**
	 * @param input
	 *            - il file di input
	 * @return - l'output Ë una lista contenente le righe del file
	 * @throws FileProblemException
	 *             - Ë una eccezione che useremo per segnalare tutti i problemi
	 */
	public static List<String> readLines(final File input) throws FileProblemException {
		try {

			// inizializzo la lista che conterr√† le righe lette
			List<String> lines = new ArrayList<String>();

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)));

			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				lines.add(line);
			}

			reader.close();

			return lines;

		} catch (FileNotFoundException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		} catch (IOException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		}
	}

	/**
	 * data una lista di stringhe, le scrivo su file
	 */
	public static void writeLines(final File output, final List<String> lines) throws FileProblemException {

		try {

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));

			for (String line : lines) {
				writer.write(line + "\n");
			}

			writer.close();

		} catch (FileNotFoundException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		} catch (IOException e) {
			throw new FileProblemException("Problemi nel leggere il file!");
		}

	}

}
