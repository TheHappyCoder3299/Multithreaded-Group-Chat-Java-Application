import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;


//This class will be on the Client side
public class Client{
    private Socket socket ;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;



    public Client(Socket socket , String username) {
        try{
            this.socket = socket;
            this.username = username;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter );
        }
    }

    //Method to read messages from the Client's command line and write it to its buffer
    //Blocking method
    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();


            Scanner scanner=new Scanner(System.in);

            while(socket.isConnected()){

                String messageToSend = scanner.nextLine();

                bufferedWriter.write(username + " : " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    //Method to listen to messages from the server

    //Because we are constantly listening from messages from the server
    //Blocking method
    //Must be in a separate thread

    public void listenForMessage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                String messageFromGroupChat;

                while(socket.isConnected()){
                    try{
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    }catch(IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }

                }
            }
        }).start();
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try{
            if (socket!=null) {
                socket.close();
            }
            if (bufferedReader!=null) {
                bufferedReader.close();
            }
            if (bufferedWriter!=null) {
                bufferedWriter.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args)throws IOException {
        Scanner scanner=new Scanner(System.in);
        System.out.println("Welcome to Group Chat");
        System.out.print("Enter your username : ");

        String username=scanner.nextLine();


        int port=1234;
        Socket socket=new Socket("localhost",port);
        Client client=new Client(socket,username);

        //It is important for the methods to be called in this order
        //Both are blocking methods
        //listenForMessage() will be running on a separate thread
        client.listenForMessage();
        client.sendMessage();

    }


}
