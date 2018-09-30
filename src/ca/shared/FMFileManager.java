package ca.shared;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;

public class FMFileManager {

	private LockManager lm = new LockManager();

	public boolean isSameChecksum(String fic, String checksum) {
		return getChecksum(fic).equals(checksum);

	}

	public WorkFile lock(String fic, String clientID, String checksum) {
		WorkFile result = null;
		if (lm.tryLock(fic, clientID)) {
			String contenu = isSameChecksum(fic, checksum) ? null : readFile(fic);
			result = new WorkFile(fic, clientID, contenu);
		}

		return result;
	}

	public String getChecksum(String fic) {
		String checksum = null;
		String contenu = null;
		try {
			contenu = readFile(fic);			
			byte[] bytesOfMessage = contenu.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(bytesOfMessage);
			BigInteger bigInt = new BigInteger(1, digest);
			checksum = bigInt.toString(16);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return checksum;
	}

	public boolean writeFile(String fic, String contenu, String clientID) {
		if (lm.getLockClientID(fic).equalsIgnoreCase(clientID)) {			
			try {
				FileWriter fw = new FileWriter(fic, false);
				fw.write(contenu);
				lm.unlock(fic);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	public String readFile(String fic) {
		File fichier = new File(fic);
		FileInputStream fis;
		byte[] contenub = null;
		String contenu = null;
		try {
			fis = new FileInputStream(fichier);
			contenub = new byte[(int) fichier.length()];
			fis.read(contenub);
			fis.close();
			contenu = new String(contenub,"UTF8");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contenu;
	}
	


	public boolean createFile(String fic) {
		boolean isFileCreated = false;
		try {
			File file = new File(fic);
			isFileCreated = file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isFileCreated;
	}

	public ArrayList<WorkFile> listFiles() {
		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();
		ArrayList<WorkFile> files = new ArrayList<WorkFile>();

		for (File f : listOfFiles) {
			if (f.isFile()) {
				String fic = f.getName();
				String lockClientID = this.lm.getLockClientID(fic);

				files.add(new WorkFile(fic, lockClientID));
			}
		}

		return files;
	}

}
