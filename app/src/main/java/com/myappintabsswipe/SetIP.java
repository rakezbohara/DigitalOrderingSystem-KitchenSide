package com.myappintabsswipe;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Bishal on 9/6/2015.
 */
public class SetIP extends Dialog implements View.OnClickListener  {
    String discount;
    EditText eDiscount;
    TextView setip;

    public SetIP(Context context) {
        super(context);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_ip);
        Button dOkBtn=(Button)findViewById(R.id.dOkBtn);
        dOkBtn.setOnClickListener(this);

        setTitle(getIpAddress());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dOkBtn:

                eDiscount= (EditText)findViewById(R.id.eDiscount);

                discount= eDiscount.getText().toString();
                if (discount==null){
                    discount="0";
                   Toast.makeText(getContext(),"Ip is saved:= "+discount,Toast.LENGTH_LONG).show();
                }
                SharedPreferences preferences = this.getContext().getSharedPreferences("IP Address", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();

                editor.putString("ip",discount);
                editor.apply();
                Toast.makeText(getContext(),"Ip is saved:= "+discount,Toast.LENGTH_LONG).show();
                dismiss();
                break;
            default:
                break;
        }
    }


}
