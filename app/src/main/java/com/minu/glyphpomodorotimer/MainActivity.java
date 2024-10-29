package com.minu.glyphpomodorotimer;

import static android.content.ContentValues.TAG;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphFrame;
import com.nothing.ketchum.GlyphManager;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView timerDisplay;
    EditText inputSecondsText;
    Button startButton;

    Button startPomoTimerButton;

    private GlyphManager glyphManager;
    private GlyphManager.Callback glyphCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timerDisplay = findViewById(R.id.timerDisplay);
        inputSecondsText = findViewById(R.id.inputSecondsText);
        startButton = findViewById(R.id.startButton);
        startPomoTimerButton = findViewById(R.id.pomoTimerButton);

        initializeGlyphCallback();
        glyphManager = GlyphManager.getInstance(getApplicationContext());
        glyphManager.init(glyphCallback);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlyphFrame.Builder builder = glyphManager.getGlyphFrameBuilder();
                GlyphFrame frame = builder.buildChannelC().build();
                long initialTime = Integer.parseInt(inputSecondsText.getText().toString()) * 1000L;
                new CountDownTimer(initialTime, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timerDisplay.setText(convertToTimeFormat(millisUntilFinished));

                        int percentageCompleted = (int) ((millisUntilFinished*100)/initialTime);
                        Log.e(TAG, String.valueOf(percentageCompleted));
                        try {
                            glyphManager.displayProgress(frame, percentageCompleted, true);
                        } catch (GlyphException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFinish() {
                        // When the task is over it will print 00:00:00
                        timerDisplay.setText("00:00");
                        glyphManager.turnOff();
                    }
                }.start();
            }
        });

        startPomoTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlyphFrame.Builder builder = glyphManager.getGlyphFrameBuilder();
                GlyphFrame frame = builder.buildChannelC().build();
                long initialTime = 25 * 60 * 1000L;
                new CountDownTimer(initialTime, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timerDisplay.setText(convertToTimeFormat(millisUntilFinished));

                        int percentageCompleted = (int) ((millisUntilFinished*100)/initialTime);
                        Log.e(TAG, String.valueOf(percentageCompleted));
                        try {
                            glyphManager.displayProgress(frame, percentageCompleted, true);
                        } catch (GlyphException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFinish() {
                        // When the task is over it will print 00:00:00
                        timerDisplay.setText("00:00");
                        glyphManager.turnOff();
                    }
                }.start();
            }
        });

    }

    private void initializeGlyphCallback() {
        glyphCallback = new GlyphManager.Callback() {
            @Override
            public void onServiceConnected(ComponentName componentName) {
                if (Common.is20111()) {
                    glyphManager.register(Common.DEVICE_20111);
                }
                if (Common.is22111()) {
                    glyphManager.register(Common.DEVICE_22111);
                }
                if (Common.is23111()) {
                    glyphManager.register(Common.DEVICE_23111);
                }
                try {
                    glyphManager.openSession();
                } catch(GlyphException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                try {
                    glyphManager.closeSession();
                } catch (GlyphException e) {
                    Log.e(TAG, e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private String convertToTimeFormat(long millis){
        long minutes = (millis/1000)/60;
        long seconds = (millis/1000)%60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}