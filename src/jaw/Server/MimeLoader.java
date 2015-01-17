package jaw.Server;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by dmitry on 17.11.14
 */
public class MimeLoader {

	/**
	 * @param mime - Mime enumeration
	 */
	public MimeLoader(Mime mime) {
		this.mime = mime;
	}

	/**
	 * @param filePath - Path to file to load
	 * @return - Just loaded file's data
	 * @throws Exception
	 */
	public InputStream load(String filePath) throws Exception {
		File fileHandle = new File(filePath);
		if (!fileHandle.exists()) {
			throw new Exception("MimeLoader/load : \"Unable to load file (" + filePath + ")\"");
		}
		try {
			return new FileInputStream(
				fileHandle
			);
		} catch (FileNotFoundException e) {
			throw new Exception("InputStream/load() : \"" + e.getMessage() + "\"");
		}
	}

	/**
	 * @param handle - File handle
	 * @return - Just loaded file's data
	 * @throws Exception
	 * @throws IOException
	 */
	private String loadText(File handle) throws Exception, IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(handle)
				)
		);
		String html = "";
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			html += line + File.separator;
		}
		return html;
	}

	/**
	 * @param handle - File handle
	 * @return - Just loaded file's data
	 * @throws IOException
	 */
	private String loadImage(File handle) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(handle);
		ByteArrayOutputStream byteArrayOutputStream
				= new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, getFileExtension(handle.getName()), byteArrayOutputStream);
		return new String(
				byteArrayOutputStream.toByteArray()
		);
	}

	/**
	 * @param fileName - File's name
	 * @return - File's extension
	 */
	private String getFileExtension(String fileName) {
		int lastIndexOf;
		if ((lastIndexOf = fileName.lastIndexOf(".")) == -1) {
			return "";
		}
		return fileName.substring(lastIndexOf + 1);
	}

	private Mime mime;
}
