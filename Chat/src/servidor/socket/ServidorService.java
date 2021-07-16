/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor.socket;

import Cliente.bean.ChatMessage;
import Cliente.bean.ChatMessage.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Robson de Jesus
 */
public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(5555);
            
            System.out.println("Servidor online!");

            while (true) {
                socket = serverSocket.accept();

                new Thread(new ListenerSocket(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   
    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            try {
                ChatMessage message = null;

                while ((message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)) {
                        boolean isConnect = connect(message, output);

                        if (isConnect) {
                            mapOnlines.put(message.getName(), output);
                            sendOnlines();
                        }

                    } else if (action.equals(Action.DISCONNETCT)) {
                        disconnect(message, output);
                        return;
                    } else if (action.equals(Action.SEND_ONE)) {
                        sendOne(message, output);
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAll(message);
                    } else if (action.equals(Action.USERS_ONLINE)) {

                    }
                }
            } catch (IOException ex) {
                //disconnect(message, output);
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private boolean connect(ChatMessage message, ObjectOutputStream output) {
        if (mapOnlines.size() == 0) {
            message.setText("YES");
            sendOne(message, output);
            return true;
        }

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getName())) {
                message.setText("NO");
                sendOne(message, output);
                return false;

            } else {
                message.setText("YES");
                sendOne(message, output);
                return true;
            }
        }

        return false;
    }
    
    private void disconnect(ChatMessage message, ObjectOutputStream output){
        mapOnlines.remove(message.getName());
        
        message.setText(" até logo!");
        
        message.setAction(Action.SEND_ONE);
        
        sendAll(message);
        
        System.out.println("User " + message.getName()+" sai da sala");
        
    }

    private void sendOne(ChatMessage message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     private void sendAll(ChatMessage message) {
         
         for(Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()){
             if(!kv.getKey().equals(message.getName())){
                  message.setAction(Action.SEND_ONE);
                 try {
                     kv.getValue().writeObject(message);
                 } catch (IOException ex) {
                     Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
         }
         
    }
     
    private void sendOnlines() {
        Set<String> setNames = new HashSet<String>();
        
        for(Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()){
            setNames.add(kv.getKey());
        }
        
        ChatMessage message = new ChatMessage();
        message.setAction(Action.USERS_ONLINE);
        message.setSetOnlines(null);
        
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            try {
                kv.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }

}
