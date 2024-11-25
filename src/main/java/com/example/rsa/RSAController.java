package com.example.rsa;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigInteger;
import java.util.List;

public class RSAController {
    RSABrain brain = new RSABrain();

    @FXML
    void initialize(){
        VK.setWrapText(true);
        SK.setWrapText(true);
        modulus.setWrapText(true);
        chunks.setWrapText(true);
        sif.setWrapText(true);
        desifta.setWrapText(true);
        pp.setWrapText(true);
        pq.setWrapText(true);

    }

    @FXML
    private TextField inputText;

    @FXML
    private TextArea VK;

    @FXML
    private TextArea SK;

    @FXML
    private TextArea pp;

    @FXML
    private TextArea pq;

    @FXML
    private TextArea modulus;

    @FXML
    private TextArea chunks;

    @FXML
    private TextArea sif;

    @FXML
    private TextArea desifta;


    @FXML
    void handleTf(){

        String message = inputText.getText();

        List<Long> decimalChunks = brain.convertToDecimalChunks(message);
        chunks.setText(decimalChunks.toString());
        List<BigInteger> encryptedChunks = brain.encryptMessage(decimalChunks, brain.e, brain.n);
        sif.setText(encryptedChunks.toString());
        String decryptedMessage = brain.decryptMessage(encryptedChunks, brain.d, brain.n);
        desifta.setText(decryptedMessage);
    }
    @FXML
    void handleButton(){
        brain.setP(brain.generatePrvo());
        brain.setQ(brain.generatePrvo());
        modulus.setText(brain.nasobeniePQ(brain.getP(),brain.getQ()).toString());
        brain.setN(brain.nasobeniePQ(brain.getP(),brain.getQ()));
        brain.generateKeys();
        VK.setText(brain.getE().toString());
        SK.setText(brain.getD().toString());
        pp.setText(brain.getP().toString());
        pq.setText(brain.getQ().toString());

    }


}