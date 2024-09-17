package com.wprotheus.pdm2jokenpo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.wprotheus.pdm2jokenpo.R;
import com.wprotheus.pdm2jokenpo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mBinding;
    private Uri uriVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        uriVideo = Uri.parse("android.resource://" + getPackageName()
                + "/" + R.raw.how_to_play_rps);
        mBinding.vidRegras.setVideoURI(uriVideo);
        prepararLink();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mBinding.ivPlay.setOnClickListener(this);
        mBinding.ivPause.setOnClickListener(this);
        mBinding.ivStop.setOnClickListener(this);
        mBinding.btnJogar.setOnClickListener(this);
    }

    private void prepararLink() { // preparando o conteúdo do edittext para funcionar como um link
        mBinding.tvAutorVideo.setLinksClickable(true);
        mBinding.tvAutorVideo.setAutoLinkMask(Linkify.WEB_URLS);
        mBinding.tvAutorVideo.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(mBinding.tvAutorVideo, Linkify.WEB_URLS);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) { // hide softkwyboard
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof TextInputEditText) { // Aqui o componente que está sendo usado
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.ivPlay.getId()) {
            mBinding.vidRegras.start();
            mBinding.vidRegras.setForeground(null);
        }
        if (v.getId() == mBinding.ivPause.getId()) mBinding.vidRegras.pause();
        if (v.getId() == mBinding.ivStop.getId()) {
            mBinding.vidRegras.stopPlayback();
            mBinding.vidRegras.setVideoURI(uriVideo);
            mBinding.vidRegras.setForeground(getResources().getDrawable(R.drawable.ppt_video));
        }
        if (v.getId() == mBinding.btnJogar.getId()) jogar();
    }

    private void jogar() {
        String nome = mBinding.tietNome.getText().toString();
        if (nome.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), "Informe seu nome", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(getApplicationContext(), Jokenpo.class);
        i.putExtra("nome", nome);
        startActivity(i);
        finish();
    }
}