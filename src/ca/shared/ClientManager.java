package ca.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ClientManager {
    private ArrayList<String> clients = new ArrayList<String>();
    private final static String CLIENT_MANAGER = "clientsmanager.txt";

    public ClientManager() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CLIENT_MANAGER));
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
                this.clients.add(contentLine);
            }

            br.close();
        } catch (FileNotFoundException e) {
            try {
                File file = new File(CLIENT_MANAGER);
                file.createNewFile();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createClientID() {
        String clientID = UUID.randomUUID().toString();
        try {
            FileWriter fw = new FileWriter(CLIENT_MANAGER, true);
            while (clients.contains(clientID)) {
                clientID = UUID.randomUUID().toString();
            }
            fw.write(clientID + System.lineSeparator());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientID;
    }
}
