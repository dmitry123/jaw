package Server;

import Core.InternalError;

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
	 * @throws InternalError
	 * @throws IOException
	 */
	public InputStream load(String filePath) throws InternalError, IOException {
		File fileHandle = new File(filePath);
		if (!fileHandle.exists()) {
			throw new Core.InternalError("MimeLoader/load : \"Unable to load file (" + filePath + ")\"");
		}
		return new FileInputStream(
				fileHandle
		);
		/*
		switch (mime.getType()) {
			case IMAGE:
				return loadImage(fileHandle);
			case TEXT:
				return loadText(fileHandle);
			case UNKNOWN:
				throw new InternalError("MimeLoader/load: \"Unable to load file with unsupported mime type (" + mime.getName() + ")\"");
		}
		return null;
		*/
	}

	/**
	 * @param handle - File handle
	 * @return - Just loaded file's data
	 * @throws Core.InternalError
	 * @throws IOException
	 */
	private String loadText(File handle) throws Core.InternalError, IOException {
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
