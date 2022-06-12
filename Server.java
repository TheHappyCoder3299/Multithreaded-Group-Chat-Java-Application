import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;
import java.io.IOException;


//This class will be on the Server side
public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        System.out.println("Server is live now !!!!!");
        try{
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                System.out.println(clientHandler.getClientUsername()+" has joined the chat");
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException e){
                closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try{
            if (serverSocket != null) {
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args)throws IOException {
        int port=1234;
        ServerSocket serverSocket=new ServerSocket(port);
        Server server=new Server(serverSocket);

        //Blocking operation but not necessary to keep this in a separate thread
        //because this is the last line of our method
        server.startServer();



    }
}
