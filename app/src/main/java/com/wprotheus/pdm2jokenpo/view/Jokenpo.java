package com.wprotheus.pdm2jokenpo.view;

import android.content.Intent;
import android.graphics.fonts.FontStyle;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.wprotheus.pdm2jokenpo.R;
import com.wprotheus.pdm2jokenpo.databinding.ActivityJokenpoBinding;
import com.wprotheus.pdm2jokenpo.model.PedraPapelTesoura;
import com.wprotheus.pdm2jokenpo.model.VerificarRodada;

import java.util.Locale;

public class Jokenpo extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {
    private final String EMAIL = "ana.guimaraes@ifto.edu.br";
    private final String SUBJECT = "Resultado da Partida";
    private final String MSG = "Escolha uma das opções abaixo";
    private StringBuilder nome;
    private ActivityJokenpoBinding mBinding;
    private AlertDialog.Builder builder;
    private TextToSpeech toSpeech;
    private String[] resultados;
    private int userVitoria = 0;
    private int appVitoria = 0;
    private int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mBinding = ActivityJokenpoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nome = new StringBuilder(getIntent().getStringExtra("nome"));
        resultados = getResources().getStringArray(R.array.resultado);
        toSpeech = new TextToSpeech(getApplicationContext(), this);
        builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mBinding.ivStone.setOnClickListener(this);
        mBinding.ivPaper.setOnClickListener(this);
        mBinding.ivScissors.setOnClickListener(this);
        mBinding.btnRetornar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mBinding.ivStone.getId() == v.getId())
            mostrarResultado(new VerificarRodada().verificarRodada(0, escolhaDoApp(), 2));
        if (mBinding.ivPaper.getId() == v.getId())
            mostrarResultado(new VerificarRodada().verificarRodada(1, escolhaDoApp(), 0));
        if (mBinding.ivScissors.getId() == v.getId())
            mostrarResultado(new VerificarRodada().verificarRodada(2, escolhaDoApp(), 1));
        if (mBinding.btnRetornar.getId() == v.getId()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private int escolhaDoApp() {
        int appChoice = new PedraPapelTesoura().jokenpo();
        mostrarFormaAppChoice(appChoice);
        return appChoice;
    }

    private void mostrarFormaAppChoice(int appChoice) {
        switch (appChoice) {
            case 0:
                mBinding.ivPChoice.setImageResource(R.drawable.pedra);
                break;
            case 1:
                mBinding.ivPChoice.setImageResource(R.drawable.papel);
                break;
            case 2:
                mBinding.ivPChoice.setImageResource(R.drawable.tesoura);
                break;
        }
    }

    private void mostrarResultado(int resultadoRodada) {
        String resultado;
        if (contador < 3) {
            switch (resultadoRodada) {
                case 2:
                    resultado = resultados[2];
                    appVitoria++;
                    userVitoria++;
                    atualizaPlacar();
                    break;
                case 1:
                    resultado = resultados[1];
                    userVitoria++;
                    atualizaPlacar();
                    break;
                default:
                    resultado = resultados[0];
                    appVitoria++;
                    atualizaPlacar();
                    break;
            }
            contador++;
            mensagemRodada(resultadoRodada, resultado);
        }
    }

    private void atualizaPlacar() {
        mBinding.tvAppWin.setText(String.valueOf(appVitoria));
        mBinding.tvUserWin.setText(String.valueOf(userVitoria));
    }

    private void mensagemRodada(int resultadoRodada, String resultado) {
        builder.setTitle(nome + ":\n")
                .setMessage(resultado)
                .setPositiveButton(R.string.txt_ok, (dialog, which) -> {
                    if (contador <= 2) solicitarEscolha();
                    if (contador == 3) verificarVencedor();
                });
        publicarMensagem(resultadoRodada);
    }

    private void verificarVencedor() {
        if (appVitoria == userVitoria) {
            contador--;
            mensagemRodada(2, getString(R.string.txt_draw_game));
        } else if (appVitoria > userVitoria) {
            mensagemPartida(0, getString(R.string.txt_perdeu));
            encerrarPartida(getString(R.string.txt_perdeu));
        } else {
            mensagemPartida(1, getString(R.string.txt_ganhou));
            encerrarPartida(getString(R.string.txt_ganhou));
        }
    }

    private void mensagemPartida(int resultadoRodada, String resultado) {
        builder.setTitle(nome + ":\n")
                .setMessage(resultado)
                .setPositiveButton(R.string.txt_ok, null);
        publicarMensagem(resultadoRodada);
    }

    private void publicarMensagem(int resultadoRodada) {
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView textView = dialog.findViewById(android.R.id.message);
        if (textView != null) prepararTexto(textView, resultadoRodada);
    }

    private void prepararTexto(TextView textView, int resultadoRodada) {
        textView.setTextSize(35);
        textView.setFontFeatureSettings(String.valueOf(FontStyle.FONT_WEIGHT_MAX));
        switch (resultadoRodada) {
            case 2:
                textView.setTextColor(getResources().getColor(R.color.draw, getTheme()));
                break;
            case 1:
                textView.setTextColor(getResources().getColor(R.color.win, getTheme()));
                break;
            default:
                textView.setTextColor(getResources().getColor(R.color.lose, getTheme()));
                break;
        }
    }

    private void encerrarPartida(String resultado) {
        desativarFormas();
        nome.append(", ")
                .append(resultado.toLowerCase().charAt(0))
                .append(resultado.substring(1));
        enviarEmail();
    }

    private void enviarEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
        i.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
        i.putExtra(Intent.EXTRA_TEXT, nome.toString());
        i.setType("text/plain");
        i.setPackage("com.google.android.gm");
        startActivity(i);
    }

    private void desativarFormas() {
        mBinding.ivStone.setEnabled(false);
        mBinding.ivPaper.setEnabled(false);
        mBinding.ivScissors.setEnabled(false);
    }

    @Override
    public void onInit(int status) {
        Locale locale = new Locale("pt", "BR");
        if (status == TextToSpeech.SUCCESS) {
            int resultLocale = toSpeech.setLanguage(locale);
            toSpeech.setSpeechRate(0.7f);
            if (resultLocale == TextToSpeech.LANG_MISSING_DATA ||
                    resultLocale == TextToSpeech.LANG_NOT_SUPPORTED)
                Log.e("Problemas", "Problemas com o idioma escolhido.");
            else
                solicitarEscolha();
        } else
            Log.e("Problemas", "Problemas com o TextToSpeech.");
    }

    private void solicitarEscolha() {
        toSpeech.speak(MSG, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}