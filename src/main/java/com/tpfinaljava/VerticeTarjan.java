package com.tpfinaljava;

import java.util.Objects;

public class VerticeTarjan {

    public static int idTotal = 0;
    private int id;
    private int indice = -1;
    private int lowLink = -1;
    private boolean enStack = false;

    public VerticeTarjan(){
        id = ++idTotal;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getLowLink() {
        return lowLink;
    }

    public void setLowLink(int lowlink) {
        this.lowLink = lowlink;
    }

    public boolean isEnStack() {
        return enStack;
    }

    public void setEnStack(boolean enStack) {
        this.enStack = enStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerticeTarjan)) return false;
        VerticeTarjan that = (VerticeTarjan) o;
        return id == that.id;
    }

}
