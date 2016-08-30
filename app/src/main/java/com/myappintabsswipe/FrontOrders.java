package com.myappintabsswipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Bishal on 8/26/2015.
 */
public class FrontOrders extends AppCompatActivity {
    TableNoFrontDB frontDB;
    OrderedDBAdapter odbAdapter;
    String[] tableNum;
     int childIndex;
    SharedPreferences sharedData;
    String IPAddress;

    View parentLayout;

    static String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/databases/";
String m;
    public String tablesNo;
    TextView infoIp;
    FloatingActionButton message;
    String r,num;
    ServerSocketThread serverSocketThread;
    FloatingActionButton send,delete;
    ServerSocket serverSocket;


    static final int SocketServerPORT = 8080;
    static final int SocketServerPORT2 = 8181;
    MediaPlayer mMediaPlayer;
    SharedPreferences sTableNo;
    public FrontOrders(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_orders);
        getSupportActionBar().setTitle("On Cafe Kitchen");
        serverSocketThread = new ServerSocketThread();
        serverSocketThread.start();
        sTableNo= getSharedPreferences("Received Table Number", 0);

        r= sTableNo.getString("hello", "Welcome");
        message=(FloatingActionButton)findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, r, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        printTables();






    }


    public void printTables(){



        frontDB=new TableNoFrontDB(FrontOrders.this);
        tableNum = frontDB.getTables();

        ListAdapter bishalsAdapter = new CustomFrontTableAdapter(this,tableNum);
        final ListView bishalsListView = (ListView) findViewById(R.id.testFrontList);
        bishalsListView.setAdapter(bishalsAdapter);
//  android:baselineAligned="true"
      //  android:descendantFocusability="beforeDescendants"
        //childIndex = bishalsListView.getSelectedItemPosition();
        bishalsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                num = bishalsListView.getItemAtPosition(position).toString();
                final FloatingActionButton send=(FloatingActionButton)findViewById(R.id.send);
                final FloatingActionButton delete =(FloatingActionButton)findViewById(R.id.delete);
                send.setVisibility(View.VISIBLE);
                send.show();
                delete.setVisibility(View.VISIBLE);
                delete.show();



                //sendNewStatus(num);
                Toast.makeText(FrontOrders.this,"Table " + num ,Toast.LENGTH_LONG).show();
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendNewStatus(num);
                        send.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        Snackbar.make(view, "Changes send to Waiter", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        frontDB= new TableNoFrontDB(FrontOrders.this);
                        frontDB.deleteProductByName(num);
                        send.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        printTables();
                    }
                });
            }
        });




    }
 public  void sendNewStatus(String tableNo){

     //String fileName = tableNo;
     sharedData= getSharedPreferences("IP Address",0);
     IPAddress= sharedData.getString("ip","192.168.43.1");

     ClientRxThread2 clientRxThread =
             new ClientRxThread2(IPAddress,8181,tableNo);
     clientRxThread.start();
 }



    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "IP Address: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {

            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
    public class ServerSocketThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);

                while (true) {
                    socket = serverSocket.accept();
                    FrontOrders.FileTxThread fileTxThread = new FileTxThread(socket);
                    fileTxThread.start();
                }
            } catch (IOException e) {

                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        }

    }




    //file thread

    public class FileTxThread extends Thread {
        Socket socket;

        String fileName;

        FileTxThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {


            try{


                DataInputStream d = new DataInputStream(socket.getInputStream());
                fileName = d.readUTF();



                File file = new File(DB_PATH, "Table"+ fileName+".db");

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                byte[] bytes;
                FileOutputStream fos = null;
                try {
                    bytes = (byte[])ois.readObject();
                    fos = new FileOutputStream(file);
                    fos.write(bytes);

                } catch (ClassNotFoundException e) {

                    e.printStackTrace();
                } finally {
                    if(fos!=null){
                        fos.close();

                    }

                }

                //socket.close();



            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
            catch (IOException e) {

                e.printStackTrace();
            } finally {
                try {

                    socket.close();
                    frontDB=new TableNoFrontDB(FrontOrders.this);
                    if(!frontDB.CheckIsDataAlreadyInDBorNot(fileName)) {
                        frontDB.addProduct(fileName);
                    }

                    FrontOrders.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            sTableNo= getSharedPreferences("Received Table Number",0);

                            SharedPreferences.Editor editor=sTableNo.edit();

                            editor.putString("hello", "Recent Update on:  Table " + fileName);
                            editor.apply();
                            Toast.makeText(FrontOrders.this,
                                    "Table no" + fileName,
                                    Toast.LENGTH_LONG).show();


                        }
                    });

                   playAlertTone();
                    Intent i = new Intent(FrontOrders.this,FrontOrders.class);
                    finish();


                    startActivity(i);


                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

        }
    }
public void playAlertTone() {
    mMediaPlayer = new MediaPlayer();
    mMediaPlayer = MediaPlayer.create(this, R.raw.notification);
    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    mMediaPlayer.setLooping(false);
    mMediaPlayer.start();
}
    public class ClientRxThread2 extends Thread {
        String dstAddress;
        String fileName;


        int dstPort;



        ClientRxThread2(String address, int port,String fileName) {
            dstAddress = address;
            dstPort = port;
            this.fileName=fileName;

        }

        @Override
        public void run() {
            Socket socket = null;




            try {

                socket = new Socket(dstAddress, dstPort);
                DataOutputStream d = new DataOutputStream(socket.getOutputStream());
                d.writeUTF(fileName);



                File file = new File(DB_PATH, "Table" + fileName + ".db");


                byte[] bytes = new byte[(int) file.length()];
                BufferedInputStream bis;

                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(bytes);
                oos.flush();

                socket.close();


                FrontOrders.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(FrontOrders.this,
                                "Sent Success",
                                Toast.LENGTH_LONG).show();
                    }
                });


            } catch (IOException e) {

                e.printStackTrace();

                final String eMsg = "Something wrong: " + e.getMessage();


                FrontOrders.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {


                        Toast.makeText(FrontOrders.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_waiter_food_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.nextPage:
                SetIP ip= new SetIP(FrontOrders.this);
                ip.show();

                return true;

           default:
                return super.onOptionsItemSelected(item);

        }

    }
}


