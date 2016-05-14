package joeylee.com.presentationcontroller;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.DataOutputStream;
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

    Runnable connect = new conncetSocket();
    Thread socketThread = new Thread(connect);


    Socket clientSocket = null;
    DataOutputStream dos;
    String ip_address;
    int ip_port;


    public void onCreate(){

    }

    public IBinder onBind(Intent intent){
        ip_address = intent.getStringExtra("socketAddr");
        ip_port = intent.getIntExtra("socketPort",0);
        return mBinder;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        ip_address = intent.getStringExtra("socketAddr");
        ip_port = intent.getIntExtra("socketPort", 0);
        socketThread.start();
        return super.onStartCommand(intent, flags, startId);
    }




}
