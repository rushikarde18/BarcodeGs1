package com.jaykallen.barcodegs1;

// Created by jkallen on 4/11/2017. This object holds a single Gs1128 artifact. This can be used in
// the resultant array of artifacts or the initial array of artifact to parse the barcode.

public class Gs1Model {
    private static Gs1Model mInstance = null;
    private String aiName;
    private String ai;
    private int aiLength;
    private String aiBarcode;
    private int dataLength;
    private String dataBarcode;
    private boolean isVariable;

    public static Gs1Model getInstance(){
        if(mInstance == null) {
            mInstance = new Gs1Model();
        }
        return mInstance;
    }

    public Gs1Model(String ai, String aiName, int aiLength, String aiBarcode, int dataLength,
                    String dataBarcode, boolean isVariable){
        // Create object with all Ai Values.
        this.aiName=aiName;
        this.ai=ai;
        this.aiLength=aiLength;
        this.aiBarcode=aiBarcode;
        this.dataLength=dataLength;
        this.dataBarcode=dataBarcode;
        this.isVariable=isVariable;
    }

    public Gs1Model(Gs1Model copy){
        // Copy constructor to copy an object from a list array to the instance.
        this.aiName=copy.aiName;
        this.ai=copy.ai;
        this.aiLength=copy.aiLength;
        this.aiBarcode=copy.aiBarcode;
        this.dataLength=copy.dataLength;
        this.dataBarcode=copy.dataBarcode;
        this.isVariable=copy.isVariable;
        mInstance = this;
    }

    public Gs1Model(){
        // Initialize all values to prevent NPE's.
        this.aiName="";
        this.ai="";
        this.aiLength=0;
        this.aiBarcode="";
        this.dataLength=0;
        this.dataBarcode="";
        this.isVariable=false;
    }

    public String getAiName() {
        return aiName;
    }

    public void setAiName(String aiName) {
        this.aiName = aiName;
    }

    public String getAi() {
        return ai;
    }

    public void setAi(String ai) {
        this.ai = ai;
    }

    public int getAiLength() {
        return aiLength;
    }

    public void setAiLength(int aiLength) {
        this.aiLength = aiLength;
    }

    public String getAiBarcode() {
        return aiBarcode;
    }

    public void setAiBarcode(String aiBarcode) {
        this.aiBarcode = aiBarcode;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public String getDataBarcode() {
        return dataBarcode;
    }

    public void setDataBarcode(String dataBarcode) {
        this.dataBarcode = dataBarcode;
    }

    public boolean isVariable() {
        return isVariable;
    }

    public void setVariable(boolean variable) {
        isVariable = variable;
    }
}
