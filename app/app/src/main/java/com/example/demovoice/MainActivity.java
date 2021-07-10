package com.example.demovoice;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.session.MediaSession;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ai_webza_tec.ai_method;
import com.example.demovoice.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.ai_webza_tec.ai_method.checkForPreviousCallList;
import static com.example.ai_webza_tec.ai_method.clearContactListSavedData;
import static com.example.ai_webza_tec.ai_method.getContactList;
import static com.example.ai_webza_tec.ai_method.makeCall;
import static com.example.ai_webza_tec.ai_method.makeCallFromSavedContactList;
import static com.example.demovoice.Function.fetchName;
import static com.example.demovoice.Function.wishMe;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private TextView textView;
    private TextView textview2;
    private TextToSpeech tts;
    private WifiManager wifiManager;
    private CameraManager cameraManager;
    private String cameraID;


    @Override
    protected void onCreate(Bundle savedInstanceState)//When an Activity first call or launched then this method is responsible to create the activity.
    {
        super.onCreate(savedInstanceState);//run your code in addition to the existing code in the onCreate() of the parent class.
        setContentView(R.layout.activity_main); //sets the XML file as you want as your main layout when the app starts
        findById();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        initializeTextToSpeech();
        initializeResult();



        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED); //To grant permission to use microphone

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraID = cameraManager.getCameraIdList()[0]; // 0 is for back camera and 1 is for front camera
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private void initializeTextToSpeech() {
        tts= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {  //called to signal the completion of the TextToSpeech engine initialization.
                if (tts.getEngines().size()==0) {
                    Toast.makeText(MainActivity.this, "Engine is not available", Toast.LENGTH_SHORT).show();
                }else{
                    String s = wishMe();//wishMe function to greet by time
                    speak("Hii I am ..."+s);

                }
            }
        });
    }

    private void speak(String msg) {
        textview2.setText(msg);
        //// Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(msg,TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void findById(){
        textView = (TextView)findViewById(R.id.textView);
        textview2= (TextView)findViewById(R.id.textView2);
    }
    private void initializeResult() {
        if (SpeechRecognizer.isRecognitionAvailable( this)){
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {

                @Override
                public void onReadyForSpeech(Bundle params) {

                }  //called when the endpointer is ready for the user to start speaking

                @Override
                public void onBeginningOfSpeech() {

                } //the user has started speaking

                @Override
                public void onRmsChanged(float rmsdB) {

                }  //the sound level in the audio stream has changed

                @Override
                public void onBufferReceived(byte[] buffer) {

                }  //more sound has been recieved

                @Override
                public void onEndOfSpeech() {

                }  //Called after the user stops speaking

                @Override
                public void onError(int error) {

                }  //A network or recognition error occured

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    Toast.makeText(MainActivity.this, ""+result.get(0), Toast.LENGTH_SHORT).show();
                    textView.setText(result.get(0));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        responce(result.get(0));
                    } //Called when recognition results are ready
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                } //Reserved for adding future eevents
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void responce(String msg) { //responce we get from tts
        String msgs = msg.toLowerCase();
        if (msgs.indexOf("hi") != -1) {
            String name[] = {"Hi. How may I help you?",
                    "hello ! It's good to hear from you",
                    "Namaste, how can i help you?"};

            {
                Random random = new Random();
                int index = random.nextInt(name.length - 0) + 0;
                speak("" + name[index]);
            }

        }

        if (msgs.indexOf("hey") != -1) {
            String name[] = {"hey there!",
                    "hello! It's nice to hear from you",
                    "hey there, how may i help you?",
                    "Namaste, how can i help you?"};

            {
                Random random = new Random();
                int index = random.nextInt(name.length - 0) + 0;
                speak("" + name[index]);
            }

        }
        if (msgs.indexOf("hello") != -1) {
            String name[] = {"Hello, how may i help you?",
                    "hello! It's nice to hear from you",
                    "Hey there, how may i help you?",
                    "Namaste, how can i help you?"};

            {
                Random random = new Random();
                int index = random.nextInt(name.length - 0) + 0;
                speak("" + name[index]);
            }
        }


        if (msgs.indexOf("what") != -1) {
            if (msgs.indexOf("your") != -1) {
                if (msgs.indexOf("name") != -1) {
                    speak("My name is Vision");
                }//tells the name
            }
        }
        if (msgs.indexOf("time") != -1) {
            if (msgs.indexOf("now") != -1) {
                Date date = new Date();
                String time = DateUtils.formatDateTime(this, date.getTime(), DateUtils.FORMAT_SHOW_TIME);
                speak("The time now is" + time);
            }//tells currunt time
        }


        if (msgs.indexOf("today") != -1) {
            if (msgs.indexOf("date") != -1) {
                SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
                Calendar cal = Calendar.getInstance();
                String todays_date = df.format(cal.getTime());
                speak("the today's date is " + todays_date);
            }//tells currunt date
        }

        if (msgs.indexOf("your") != -1) {
            if (msgs.indexOf("job") != -1) {
                String name[] = {"I have the best job for an assistant, helping you. ",
                        "What can i do for you.",
                        "My job is to make your life easier,",
                        " tell me what can i do for you.",
                        "My job is to help you. I am your Assistant.",
                        "I'm here to help you find info, get stuff done, and have fun.",
                        "I'm professionally useful, whatever you need,",
                        " i'm here to here"};

                {
                    Random random = new Random();
                    int index = random.nextInt(name.length - 0) + 0;
                    speak("" + name[index]);
                }

            }
        }

        if (msgs.indexOf("work") != -1) {
            String name[] = {"I have the best job for an assistant, helping you.What can i do for you?",
                    "My job is to make your life easier,",
                    "My job is to help you. I am your Assistant.",
                    "I'm here to help you find info, get stuff done, and have fun.",
                    "I'm professionally useful, whatever you need,",
                    "I'm here to here"};

            {
                Random random = new Random();
                int index = random.nextInt(name.length - 0) + 0;
                speak("" + name[index]);
            }

        }

        if (msgs.indexOf("your") != -1) {
            if (msgs.indexOf("age") != -1) {

                String name[] = {"I'm still pretty new but I'm already crawling the web like a champion",
                        "I am technically a baby, but I don't throw tantrums, and I am super good at helping others"};

                {
                    Random random = new Random();
                    int index = random.nextInt(name.length - 0) + 0;
                    speak("" + name[index]);
                }

            }
        }
        if (msgs.indexOf("you") != -1) {
            if (msgs.indexOf("marry") != -1) {

                String name[] = {"This is one of those things we'd both have to agree on.",
                        " I'd like to just be frinds. Thank you for the love though",
                        "This is one of those things we'd both have to agree on. ",
                        "I'd prefer to keep our relationship friendly."};

                {
                    Random random = new Random();
                    int index = random.nextInt(name.length - 0) + 0;
                    speak("" + name[index]);
                }

            }
        }

        if (msgs.indexOf("you") != -1) {
            if (msgs.indexOf("married") != -1) {
                String name[] = {"I'm happy to say I feelwhole all on my own. Plus, I never have to share mithai"};
                {
                    Random random = new Random();
                    int index = random.nextInt(name.length - 0) + 0;
                    speak("" + name[index]);
                }

            }
        }
        if (msgs.indexOf("you") != -1) {
            if (msgs.indexOf("are") != -1) {
                if (msgs.indexOf("awesome") != -1) {

                    String name[] = {"Thanks!",
                            "You are!",
                            "Thanks!, You make everyone around you so happy,",
                            " they feel like bubbles floating in the air.",
                            "Thanks, I like to think that beauty comes from within",
                            "Thats so funny. I was just thinking the same about you"};

                    {
                        Random random = new Random();
                        int index = random.nextInt(name.length - 0) + 0;
                        speak("" + name[index]);
                    }

                }
            }
        }
        if (msgs.indexOf("great") != -1) {
            if (msgs.indexOf("you") != -1) {
                if (msgs.indexOf("are") != -1) {

                    String name[] = {"Thanks!",
                            "You are!",
                            "Thanks!, You make everyone around you so happy, ",
                            "they feel like bubbles floating in the air.",
                            "Thanks, I like to think that beauty comes from within",
                            "Great? Me? That's so nice",
                            "Thats so funny. I was just thinking the same about you"};

                    {
                        Random random = new Random();
                        int index = random.nextInt(name.length - 0) + 0;
                        speak("" + name[index]);
                    }
                }
            }
        }

        if (msgs.indexOf("amazing") != -1) {
            if (msgs.indexOf("you") != -1) {
                if (msgs.indexOf("are") != -1) {

                    String name[] = {"Thanks!",
                            "You are!",
                            "Thanks!, You make everyone around you so happy,",
                            " they feel like bubbles floating in the air.",
                            "Thanks, I like to think that beauty comes from within",
                            "It takes someone amazing to know something's amazing. ",
                            "You're amazing, too",
                            "Thats so funny. I was just thinking the same about you"};

                    {
                        Random random = new Random();
                        int index = random.nextInt(name.length - 0) + 0;
                        speak("" + name[index]);
                    }


                }
            }
        }

        if (msgs.indexOf("good") != -1) {
            if (msgs.indexOf("you") != -1) {
                if (msgs.indexOf("are") != -1) {

                    String name[] = {"Thanks!",
                            "You are!",
                            "Thanks!, You make everyone around you so happy, ",
                            "they feel like bubbles floating in the air.",
                            "Thanks, I like to think that beauty comes from within"};

                    {
                        Random random = new Random();
                        int index = random.nextInt(name.length - 0) + 0;
                        speak("" + name[index]);
                    }

                }
            }
        }

        if (msgs.indexOf("who") != -1) {
            if (msgs.indexOf("your") != -1) {
                if (msgs.indexOf("Boss") != -1) {

                    String name[] = {"Guess that would be you.",
                            "You, most certainly are the boss of me.",
                            "I look up to humans who are curious about the world",
                            "You definitely fit the bill",
                            "Thanks, I like to think that beauty comes from within",
                            "You!",
                            "Without doubt, you give me a purpose"};

                    {
                        Random random = new Random();
                        int index = random.nextInt(name.length - 0) + 0;
                        speak("" + name[index]);
                    }

                }
            }
        }

        if (msgs.indexOf("are") != -1) {
            if (msgs.indexOf("you") != -1) {

                String name[] = {"I am Vision",
                        "My name is Vision .. your artificial intelligence",
                        "Your Virtual assistant Vision"};

                {
                    Random random = new Random();
                    int index = random.nextInt(name.length - 0) + 0;
                    speak("" + name[index]);
                }

            }
        }

        if (msgs.indexOf("joke")!= -1) {
            String name[] = {"This one is an acquired taste: Why can't a bicycle stand on its own? Because it's two tired",
                    "I love how in horror movies the person calls out, 'Hello?' As if the ghost will answer'Hey, what's up, I'm in the kitchen. Want a sandwich?'",
                    "This one is an acquired taste: Where do typists go for a drink? The space bar",
                    "Why don't some couples go to gym? Because some relationships don't workout",
                    "Why shouldn't you write with a broken pencil? Because it's pointless!",
                    "What is the most shocking city in the world? It's Electricity! "};

            {
                Random random = new Random();
                int index = random.nextInt(name.length - 0) + 0;
                speak("" + name[index]);
            }

        }
        if (msgs.indexOf("how")!= -1) {
            if (msgs.indexOf("old")!= -1) {
                if (msgs.indexOf("you") != -1) {

                    String name[] = {"I'm still pretty new but I'm already crawling the web like a champion",
                            "I am technically a baby, but I don't throw tantrums, and I am super good at helping others"};

                    {
                        Random random = new Random();
                        int index = random.nextInt(name.length - 0) + 0;
                        speak("" + name[index]);
                    }

                }
            }
        }


        if (msgs.indexOf("open") != -1) {
            if (msgs.indexOf("google") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));//to open youtube
                startActivity(intent);
            }
            if (msgs.indexOf("youtube") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));//to open youtube
                startActivity(intent);
            }
            if (msgs.indexOf("facebook") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));// To open faceboock webpage
                startActivity(intent);
            }
            if (msgs.indexOf("whatsapp") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.whatsapp");//to open whatsapp
                ctx.startActivity(i);
            }
            if (msgs.indexOf("instagram") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.instagram.android");//to open instagram
                ctx.startActivity(i);
            }
            if (msgs.indexOf("spotify") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.spotify.music");//to open spotify
                ctx.startActivity(i);
            }
            if (msgs.indexOf("google photos") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.photos");//to open google maps
                ctx.startActivity(i);
            }
            if (msgs.indexOf("gmail") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.gm");//to open gmail
                ctx.startActivity(i);
            }
            if (msgs.indexOf("maps") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");//to open gogle maps
                ctx.startActivity(i);
            }
            if (msgs.indexOf("playstore") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.android.vending");//to open playstore
                ctx.startActivity(i);
            }
            if (msgs.indexOf("calender") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.android.calender");//to open calender
                ctx.startActivity(i);
            }
            if (msgs.indexOf("drive") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.docs");//to open drive
                ctx.startActivity(i);
            }
            if (msgs.indexOf("twitter") != -1) {
                Context ctx = this;
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.twitter.android");//to open twitter
                ctx.startActivity(i);
            }
        }

        if(msgs.indexOf("call")!= -1){
            final String[] listname ={""};
            final String name = fetchName(msgs);
            Log.d("Name",name);

            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CALL_PHONE
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        if (checkForPreviousCallList(MainActivity.this)) {
                            speak(makeCallFromSavedContactList(MainActivity.this, name));
                        } else {
                            HashMap<String, String> list = getContactList(MainActivity.this, name);
                            if (list.size() > 1) {
                                for (String i : list.keySet()) {
                                    listname[0] = listname[0].concat("..........................!" + i);
                                }
                                speak("Which one sir ? .. There is " + listname[0]);
                            } else if (list.size() == 1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                    makeCall(MainActivity.this, list.values().stream().findFirst().get());
                                    clearContactListSavedData(MainActivity.this);
                                }
                            } else {
                                speak("NO CONTACT FOUND");
                                clearContactListSavedData(MainActivity.this);
                            }
                        }
                    }
                    if (report.isAnyPermissionPermanentlyDenied()) {
                    }
                }
                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();}

            }).check();
        }
        if (msgs.indexOf("search") != -1) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            String[] replace = textView.getText().toString().split("search");
            intent.putExtra(SearchManager.QUERY, String.valueOf(replace[1]));
            startActivity(intent);
        }
        if (msgs.indexOf("play") != -1) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query="));// To open faceboock webpage
            String[] replace = textView.getText().toString().split("play");
            intent.putExtra(SearchManager.QUERY, String.valueOf(replace[1]));
            startActivity(intent);
        }if (msgs.indexOf("bluetooth") != -1) {
            if (msgs.indexOf("on") != -1) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
            if (msgs.indexOf("off") != -1) {
                BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
                bAdapter.disable();
            }
        }

        if (msgs.indexOf("wi-fi") != -1) {
            if (msgs.indexOf("on") != -1) {
                wifiManager.setWifiEnabled(true);
            }
            if (msgs.indexOf("off") != -1) {
                wifiManager.setWifiEnabled(false);
            }

        }
        if (msgs.indexOf("torch") != -1) {
            if (msgs.indexOf("on") != -1) {
                try {
                    cameraManager.setTorchMode(cameraID, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (msgs.indexOf("off") != -1) {
                try {
                    cameraManager.setTorchMode(cameraID, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }



    }


    public void Record(View view) {
        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//The constant ACTION_RECOGNIZE_SPEECH starts an activity that will prompt the user for speech and send it through a speech recognizer.
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, //Informs the recognizer which speech model to prefer when performing ACTION_RECOGNIZE_SPEECH.
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //Use a language model based on free-form speech recognition
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer.startListening(intentRecognizer);
    }

}