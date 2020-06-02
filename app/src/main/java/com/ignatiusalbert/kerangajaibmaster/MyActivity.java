/*
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ignatiusalbert.kerangajaibmaster;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
public class MyActivity extends AppCompatActivity
{
    TextToSpeech t1;
    int counter =5;
    private static final String AD_UNIT_ID ="ca-app-pub-3940256099942544/8691691433";
    private InterstitialAd interstitialAd;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private AdView adView;
    private AudioManager audioManager;
    List<kerangajaib> kerangajaibList;
    DatabaseReference kerangajaib;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.7f;
        int seventyVolume = (int) (maxVolume*percent);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
// Write a message to the database
        kerangajaib = FirebaseDatabase.getInstance().getReference("kerangajaib");
        kerangajaibList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MobileAds.initialize(this, new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        adView = findViewById(R.id.ad_view);
        AdRequest adRequest1 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest1);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID);
        interstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded() {
                //Toast.makeText(MyActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //Toast.makeText(MyActivity.this,"onAdFailedToLoad() with error code: " + errorCode,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdClosed()
            {
                if (!interstitialAd.isLoading() && !interstitialAd.isLoaded())
                {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitialAd.loadAd(adRequest);
                }
            }
        });
        checkPermission();
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(new Locale("id","ID"));
                    //t1.setPitch(10);
                    //t1.setSpeechRate(20);
                }
                    /*
                if (status == TextToSpeech.SUCCESS) {
                    int result = t1.setLanguage(new Locale("id","ID"));
                   // t1.setPitch(20);
                    //t1.setSpeechRate(20);
                    Toast.makeText(MyActivity.this,result, Toast.LENGTH_SHORT).show();
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Toast.makeText(MyActivity.this,"Language not supported", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    {
                    Toast.makeText(MyActivity.this,"Initialization failed", Toast.LENGTH_SHORT).show();
                    }*/
            }
        },"com.google.android.tts");


        /*
        if(!isConnected(MyActivity.this))
        {
            setContentView(R.layout.activity_my);
            AlertDialog alertDialog = new AlertDialog.Builder(MyActivity.this).create();
            alertDialog.setTitle("Tidak ada akses Internet");
            alertDialog.setMessage("Kamu perlu akses internet untuk melanjutkan aplikasi ini");
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            });
            alertDialog.show();
            //buildDialog(MainActivity.this).show();
        }
        //buildDialog(MainActivity.this).show();
        else {
            Toast.makeText(MyActivity.this,"Welcome", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_my);
        }
*/

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"in");
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener()
        {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle)
            {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //displaying the first match
                if (matches != null)
                {
                    //versi 2.0
                    String kata = matches.get(0);
                    String atau = "atau";
                    int index;
                    String[] parts = kata.split(" ", wordCount(kata));
                    String[] c,d;
                    for (int i=0;i<parts.length;i++)
                    {
                        if (parts[i].equals(atau))
                        {
                            index = i;
                            c = Arrays.copyOfRange(parts, 0, index);
                            String str = Arrays.toString(c);
                            str = str.substring(1, str.length()-1).replace(",", "");
                            d = Arrays.copyOfRange(parts, index+1, parts.length);
                            String str2 = Arrays.toString(d);
                            str2 = str2.substring(1, str2.length()-1).replace(",", "");
                            Random rand = new Random();
                            int number = rand.nextInt(10);
                            String id = kerangajaib.push().getKey();
                            if (number == 0)
                            {
                                t1.speak("saya sarankan" + str, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new  kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 1)
                            {
                                t1.speak("lebih cocok" + str2, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str2);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 2)
                            {
                                t1.speak("mending" + str+ "dari pada"+ str2, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 3)
                            {
                                t1.speak("lebih baik" + str2, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 4)
                            {
                                t1.speak("seharusnya" + str +"dibandingkan"+ str2, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 5)
                            {
                                t1.speak("saya yakin" + str2+ "pilihan yang terbaik", TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 6)
                            {
                                t1.speak("tidak diragukan" + str, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 7)
                            {
                                t1.speak("pastinya" + str2, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 8)
                            {
                                t1.speak("jangan ragu untuk pilih" + str, TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            else if (number == 9)
                            {
                                t1.speak( str2+"adalah yang terbaik untuk anda", TextToSpeech.QUEUE_FLUSH, null);
                                kerangajaib percobaan = new kerangajaib(id, str, str2, str);
                                kerangajaib.child(id).setValue(percobaan);
                            }
                            break;
                        }
                        else
                        {
                            t1.speak("tidak ada kata atau", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }
                else
                {
                    t1.speak("coba lagi", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            @Override
            public void onPartialResults(Bundle partialResults)
            {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

        });

        findViewById(R.id.imageButton4).setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        counter--;
                        Toast.makeText(getApplicationContext(),"Ads akan muncul setelah "+String.valueOf(counter)+" pertanyaan lagi",Toast.LENGTH_SHORT).show();
                        if(counter==0)
                        {
                            showInterstitial();
                            counter=5;
                        }

                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

                        break;
                }
                return false;
            }
        });
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    private void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
            {
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
        else
        {

        }

    }
    public boolean isConnected(Context context)
    {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public static int wordCount(String s)
    {
        if (s == null)
            return 0;
        return s.trim().split("\\s+").length;
    }
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }
    /** Called before the activity is destroyed */
    @Override
    public void onDestroy()
    {
        if (adView != null)
        {
            adView.destroy();
        }
        super.onDestroy();
        if (t1 != null)
        {
            t1.stop();
            t1.shutdown();
        }
    }
}
