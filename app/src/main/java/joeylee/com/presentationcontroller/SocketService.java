package joeylee.com.presentationcontroller;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Joey on 5/14/2016.
 */
public class SocketService extends Service {
    public SocketService(){}

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        SocketService getService(){
            return SocketService.this;
        }
    }


    Socket clientSocket = null;
//    DataOutputStream dos;
    PrintWriter pw;
    String ip_address;
    int ip_port;
    int direction;

    Runnable connect = new connectSocket();
    Thread socketThread = new Thread(connect);


    public void onCreate(){
        Log.i("DEBUG", "Socket service created");
    }

    public IBinder onBind(Intent intent){
        Log.i("DEBUG", "onBind");
        ip_address = intent.getStringExtra("socketAddr");
        ip_port = intent.getIntExtra("socketPort",0);
        return mBinder;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i("DEBUG", "onStartCommand");
        ip_address = intent.getStringExtra("socketAddr");
        ip_port = intent.getIntExtra("socketPort", 0);
        socketThread.start();
        Log.i("DEBUG", "Socket IP set to " + ip_address);
        Log.i("DEBUG", "Socket port set to " + ip_port);

        return super.onStartCommand(intent, flags, startId);
    }


class connectSocket implements Runnable{

    @Override
    public void run() {
        try{
            clientSocket = new Socket(ip_address, ip_port);
            OutputStream out = clientSocket.getOutputStream();
//            dos = new DataOutputStream(out);
            pw = new PrintWriter(out, true);
            Log.d("DEBUG", "sent hello");
//            dos.writeUTF("Hello from client");
            pw.println("hi from the client");

            while(true){
                if(direction >=8){
                    direction = 0;
                    Log.i("DEBUG", "change slide");
//                    dos.writeUTF("right");
                    pw.println("right");
                }
                if(direction <= -8){
                    direction = 0;
                    Log.i("DEBUG", "change slide");
//                    dos.writeUTF("left");
                    pw.println("left");
                }
                if(direction == 1){
                    direction = 0;
                    Log.i("DEBUG", "last slide");
//                    dos.writeUTF("end");
                    pw.println("end");
                }

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}


}