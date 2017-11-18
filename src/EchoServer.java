import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.inject.Inject;
import javax.websocket.EncodeException;
 
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
 

/**
 * @ServerEndpoint gives the relative name for the end point
 * This will be accessed via ws://localhost:8080/EchoChamber/echo
 * Where "localhost" is the address of the host,
 * "EchoChamber" is the name of the package
 * and "echo" is the address to access this class from the server
 */
@ServerEndpoint(value = "/echo/{roomnumber}")
public class EchoServer {
    int i =0;
    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was
     * successful.
     */    
 
   
    @OnOpen
    public void onOpen(Session session, @PathParam("roomnumber") String roomnumber){
        System.out.println(session.getId() + " has opened a connection");
            session.getUserProperties().put("roomnumber", roomnumber);
            SessionHandler.openSessions.put(String.valueOf(session.getId()), session);
            SessionHandler.addSession(session);
            SessionHandler.sendToSession(session, "LATA");
            
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){
        i++;

        String room = (String) session.getUserProperties().get("roomnumber");
        try{
            for (Session s : session.getOpenSessions()){
                if (s.isOpen() && s.getUserProperties().get("roomnumber").equals(room)){
                    //s.getBasicRemote().sendObject(message);
                    if(i>40) {
                        s.getBasicRemote().sendObject("jasio");
                    }
                    if(message.equals("piesek")) {
                        s.getBasicRemote().sendObject("szczeka");
                    }
                    else {
                        s.getBasicRemote().sendObject(message);
                    }
                }
            }
        }
        catch (IOException| EncodeException e){
           
        }
    }
 
    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
        SessionHandler.removeSession(session);
       
    }
   
    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, error);
    }
}