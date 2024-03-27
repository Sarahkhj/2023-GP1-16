package com.example.myapplication324;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication324.databinding.ActivityHelpGuideBinding;

import io.github.muddz.styleabletoast.StyleableToast;


public class HelpGuide extends  DrawerBaseActivity {
    ActivityHelpGuideBinding activityHelpGuideBinding;
    TextView t1,t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHelpGuideBinding = ActivityHelpGuideBinding.inflate(getLayoutInflater());
        setContentView(activityHelpGuideBinding.getRoot());
        allocateActivityTitle("Help Guide");
        t1 = findViewById(R.id.helptext);
        t2 = findViewById(R.id.email);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open a YouTube video
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/vNENWl8cm08?si=doMf3cawjEPYgHXN")); // Replace VIDEO_ID with the actual YouTube video ID
                startActivity(intent);
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to send an email
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:sbtestgp@gmail.com")); // only email apps should handle this
                //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sb@gmail.com"}); // recipient's email
                intent.putExtra(Intent.EXTRA_SUBJECT, "Your subject here"); // email subject
                intent.putExtra(Intent.EXTRA_TEXT, "Email body text."); // email body text

                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Handle the situation where no email app is available
                    StyleableToast.makeText(HelpGuide.this, "No email application is installed." , Toast.LENGTH_SHORT, R.style.mytoast).show();

                }
            }
        });



    }
}