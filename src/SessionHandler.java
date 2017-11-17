
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author student
 */
public class SessionHandler {
    private static final Set<Session> sessions = new HashSet<>();
    
    public static void addSession(Session session) {
        sessions.add(session);
    }
    public static void removeSession(Session session) {
        sessions.remove(session);
    }
    public static void sendToSession(Session session, String message) {    
        System.out.println("Message from " + session.getId() + ": " + message);         
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void sendToAllConnectedSessions(String message) {
        for(Session s : sessions) {
            System.out.println("Message from " + s.getId() + ": " + message);         
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException ex) {
                Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(message.equalsIgnoreCase("JASIO")){
                try {
                    s.close();
                } catch (IOException ex) {
                    Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void sendToAllConnectedSessionsInRoom(String roomID, String message){
        for(Session s : sessions){
            if(s.getUserProperties().get("roomID").toString().equals(roomID)){
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException ex) {
                    Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
