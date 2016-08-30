package com.myappintabsswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;



/**
 * Created by Bishal on 8/15/2015.
 */
public class Loading1 extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen1);
        imageView=(ImageView)findViewById(R.id.waiter_splash);

       /*int fadeInDuration = 2300; // Configure time values here

        imageView.setVisibility(View.INVISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        imageView.setAnimation(animation);*/

        Thread timer= new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {
                    Intent i = new Intent(Loading1.this,FrontOrders.class);
                    finish();
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                }
            }
        };
        timer.start();
    }
}
