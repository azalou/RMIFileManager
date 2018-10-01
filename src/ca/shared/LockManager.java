package ca.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LockManager {
	private ArrayList<WorkFile> files = new ArrayList<WorkFile>();
    private final static String LOCK_FILE_NAME = "lockMetadata.txt";
    private static final String CONF_DIR = "configDir";

    public LockManager() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CONF_DIR + File.separator + LOCK_FILE_NAME));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String content = sb.toString();

            String[] contentLines = content.split(System.lineSeparator());
            for (String contentLine : contentLines) {
                String[] parts = contentLine.split("\t");
                if (parts.length >= 2) {
                    String name = parts[0];
                    String lockClientID = parts[1];
                    WorkFile f = new WorkFile(name, lockClientID);
                    this.files.add(f);
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            try {
            	new File(CONF_DIR).mkdirs();
            	
                File file = new File(CONF_DIR + File.separator + LOCK_FILE_NAME);
                file.createNewFile();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLockClientID(String fileName) {
        String clientID = null;
        for (WorkFile f : files) {
            if (f.getName().equalsIgnoreCase(fileName)) {
                clientID = f.getLockClientID();
            }
        }
        return clientID;
    }

    private void syncLockFile() {
        try {
            FileWriter fw = new FileWriter(CONF_DIR + File.separator + LOCK_FILE_NAME, false);
            for (WorkFile f : files) {
                fw.write(f.getName() + "\t" + f.getLockClientID() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lock(String fileName, String lockClientID) {
        WorkFile f = new WorkFile(fileName, lockClientID);
        files.add(f);
        syncLockFile();
    }

    public void unlock(String fileName) {
        int idx = -1;
        for (int i = 0; i < files.size() && idx == -1; i++) {
            if (files.get(i).getName().equalsIgnoreCase(fileName)) {
                idx = i;
            }
        }
        files.remove(idx);
        syncLockFile();
    }

    public boolean tryLock(String fileName, String lockClientID) {
        String currentLockID = getLockClientID(fileName);
        if (currentLockID == null || currentLockID.equalsIgnoreCase(lockClientID)) {
            lock(fileName, lockClientID);
            return true;
        } else {
            return false;
        }
    }
}
