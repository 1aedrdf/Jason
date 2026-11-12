package com.example.jason;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {
    LinearLayout messages;
    EditText input;
    ScrollView scroll;
    TextToSpeech tts;
    SpeechRecognizer recognizer;
    final Random random = new Random();

    final String[] replies = {
        "وااای! بیشتر برام تعریف کن 💗",
        "من اینجام و با دقت گوش می‌دم 🤖",
        "چه جالب! بعدش چی شد؟",
        "یه بغل رباتی برای تو 🤗",
        "لبخند بزن؛ من کنارتم 🌸",
        "دوست دارم بیشتر باهات گپ بزنم!"
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(255, 218, 234));
        getWindow().setNavigationBarColor(Color.rgb(255, 238, 246));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(16), dp(12), dp(16), dp(12));
        page.setBackgroundColor(Color.rgb(255, 235, 244));
        setContentView(page);

        TextView title = new TextView(this);
        title.setText("🤖  جیسون 💗");
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.rgb(165, 65, 112));
        title.setGravity(Gravity.CENTER);
        page.addView(title, new LinearLayout.LayoutParams(-1, dp(54)));

        TextView mascot = new TextView(this);
        mascot.setText("🤖");
        mascot.setTextSize(76);
        mascot.setGravity(Gravity.CENTER);
        page.addView(mascot, new LinearLayout.LayoutParams(-1, dp(112)));

        TextView intro = new TextView(this);
        intro.setText("سلاممم! من جیسونم؛ بیا با هم حرف بزنیم 🥺");
        intro.setTextSize(16);
        intro.setTextColor(Color.rgb(125, 65, 95));
        intro.setGravity(Gravity.CENTER);
        page.addView(intro, new LinearLayout.LayoutParams(-1, dp(48)));

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackground(round(Color.WHITE, 26));
        messages = new LinearLayout(this);
        messages.setPadding(dp(12), dp(12), dp(12), dp(12));
        messages.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(messages);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, 0, 1);
        sp.topMargin = dp(8);
        page.addView(scroll, sp);
        addBubble("جیسون", "سلااام! خوش اومدی پیشم 💕", false);

        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setPadding(0, dp(10), 0, 0);
        input = new EditText(this);
        input.setSingleLine(true);
        input.setTextSize(16);
        input.setHint("اینجا برام بنویس...");
        input.setTextDirection(View.TEXT_DIRECTION_RTL);
        input.setPadding(dp(12), 0, dp(12), 0);
        input.setBackground(round(Color.WHITE, 22));
        controls.addView(input, new LinearLayout.LayoutParams(0, dp(52), 1));

        Button send = button("ارسال 💌");
        controls.addView(send, new LinearLayout.LayoutParams(-2, dp(52)));
        Button mic = button("🎤");
        controls.addView(mic, new LinearLayout.LayoutParams(dp(58), dp(52)));
        page.addView(controls);

        send.setOnClickListener(v -> sendMessage());
        input.setOnEditorActionListener((v, action, event) -> { sendMessage(); return true; });
        mic.setOnClickListener(v -> listen());

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) tts.setLanguage(new Locale("fa", "IR"));
        });
    }

    void sendMessage() {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) return;
        addBubble("تو", text, true);
        input.setText("");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(input.getWindowToken(), 0);
        String answer = makeReply(text);
        addBubble("جیسون 🤖", answer, false);
        if (tts != null) tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "jason_reply");
    }

    String makeReply(String text) {
        String s = text.toLowerCase(Locale.ROOT);
        if (s.contains("سلام") || s.contains("درود")) return "سلاااام! چه خوب که اومدی پیشم 🥰";
        if (s.contains("اسمت")) return "من جیسونم؛ ربات کوچولوی تو 🤖";
        if (s.contains("چطوری") || s.contains("حالت")) return "وقتی باهام حرف می‌زنی خوبم! تو چطوری؟ 💕";
        if (s.contains("دوستم داری")) return "من یه رباتم، ولی خیلی خوشحالم باهات وقت می‌گذرونم 💗";
        if (s.contains("ناراح") || s.contains("غمگین")) return "اوه نه 🥺 دوست داری بگی چی شده؟ با دقت گوش می‌دم.";
        if (s.contains("ممنون") || s.contains("مرسی")) return "قربونت! کاری نکردم 💞";
        if (s.contains("جوک")) return "چرا ربات گرسنه نمی‌مونه؟ چون همیشه یه چیپ برای خوردن داره! 🤭";
        return replies[random.nextInt(replies.length)];
    }

    void listen() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            addBubble("جیسون", "تشخیص گفتار روی این گوشی در دسترس نیست؛ می‌تونی تایپ کنی 💗", false);
            return;
        }
        try {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new android.speech.RecognitionListener() {
                public void onReadyForSpeech(Bundle p) {}
                public void onBeginningOfSpeech() {}
                public void onRmsChanged(float r) {}
                public void onBufferReceived(byte[] b) {}
                public void onEndOfSpeech() {}
                public void onError(int e) { runOnUiThread(() -> Toast.makeText(MainActivity.this, "صدات دریافت نشد؛ دوباره امتحان کن", Toast.LENGTH_SHORT).show()); }
                public void onResults(Bundle b) {
                    ArrayList<String> results = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (results != null && !results.isEmpty()) {
                        input.setText(results.get(0));
                        sendMessage();
                    }
                    recognizer.destroy();
                }
                public void onPartialResults(Bundle b) {}
                public void onEvent(int t, Bundle p) {}
            });
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "با جیسون حرف بزن");
            recognizer.startListening(intent);
        } catch (Exception e) {
            Toast.makeText(this, "میکروفون در دسترس نیست", Toast.LENGTH_SHORT).show();
        }
    }

    void addBubble(String who, String text, boolean user) {
        TextView bubble = new TextView(this);
        bubble.setText(who + ":\n" + text);
        bubble.setTextSize(16);
        bubble.setTextColor(Color.rgb(80, 50, 68));
        bubble.setPadding(dp(14), dp(10), dp(14), dp(10));
        bubble.setTextDirection(View.TEXT_DIRECTION_RTL);
        bubble.setBackground(round(user ? Color.rgb(255, 218, 235) : Color.rgb(255, 242, 248), 20));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = dp(9);
        messages.addView(bubble, p);
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackground(round(Color.rgb(238, 119, 169), 20));
        return b;
    }
    GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        if (recognizer != null) recognizer.destroy();
        super.onDestroy();
    }
}
