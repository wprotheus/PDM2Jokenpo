package com.wprotheus.pdm2jokenpo.model;

public class VerificarRodada {
    public int verificarRodada(int userChoice, int appChoice, int condicao) {
        if (appChoice == userChoice) return 2;
        else if (appChoice == condicao) return 1;
        else return 0;
    }
}