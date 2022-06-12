import java.net.Socket;
import java.lang.Runnable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


//This class will be on the Server side
public class ClientHandler implements Runnable{
    private static List<ClientHandler> clientHandlerList=new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;

    public String getClientUsername() {
        return clientUsername;
    }

    private BufferedWriter bufferedWriter;
    private String clientUsername;
    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername=bufferedReader.readLine();
            this.clientHandlerList.add(this);

            broadcastMessage("Server : "+this.clientUsername+" has entered the chat");
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void broadcastMessage(String message){
        for(ClientHandler clientHandler : clientHandlerList){
            if(clientHandler!=this){
                try{
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    //Very important line because a buffer will not be sent down its output stream until its full
                    clientHandler.bufferedWriter.flush();
                }catch(IOException e){
                    closeEverything(socket,bufferedReader,bufferedWriter);
                }
            }
        }
    }

    public void removeCLientHandler(){
        clientHandlerList.remove(this);
        broadcastMessage("SEVER : "+this.clientUsername+" has left the chat");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try{
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        while(this.socket.isConnected()){
            try{
                //listening for messages
                //A blocking operation hence in a separate thread
                String message = this.bufferedReader.readLine();
                this.broadcastMessage(message);
            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                //Very important line
                break;
            }
        }
    }
}
