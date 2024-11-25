package com.example.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class RSABrain {
    public BigInteger p; // Prvé náhodné prvočíslo
    public BigInteger q; // Druhé náhodné prvočíslo
    public BigInteger n; // Modul pre verejný a súkromný kľúč (n = p * q)
    public BigInteger e; // Verejný exponent
    public BigInteger d; // Súkromný exponent
    public String Text; // Pôvodná správa

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getQ() {
        return q;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getD() {
        return d;
    }

    public void setD(BigInteger d) {
        this.d = d;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    /**
     * Generuje náhodné veľké prvočíslo s pravdepodobnosťou primality 100 iterácií.
     */
    public BigInteger generatePrvo() {
        SecureRandom random = new SecureRandom();
        BigInteger prime;
        do {
            // Vytvor náhodné číslo ako reťazec
            String number = String.valueOf(1 + random.nextInt(9));
            for (int i = 1; i < 13; i++) { // Pridaj ďalších 12 číslic
                number += random.nextInt(10);
            }
            prime = new BigInteger(number); // Preveď reťazec na číslo BigInteger
        } while (!prime.isProbablePrime(100)); // Test primality (100 iterácií)
        return prime;
    }

    /**
     * Vypočíta násobenie dvoch čísel (p * q), čo predstavuje modul RSA.
     */
    public BigInteger nasobeniePQ(BigInteger first, BigInteger second) {
        return first.multiply(second);
    }

    /**
     * Vypočíta Eulerovu funkciu φ(n) = (p - 1) * (q - 1).
     */
    public BigInteger euler(BigInteger p, BigInteger q) {
        BigInteger jeden = new BigInteger("1");
        return (p.subtract(jeden)).multiply(q.subtract(jeden));
    }

    /**
     * Počíta modulárny inverz dôležitý pre výpočet súkromného kľúča.
     * Algoritmus používa rozšírený Euklidov algoritmus.
     */
    public BigInteger modInverse(BigInteger a, BigInteger m) {
        BigInteger m0 = m;
        BigInteger y = BigInteger.ZERO, x = BigInteger.ONE;
        if (m.equals(BigInteger.ONE)) {
            return BigInteger.ZERO; // Ak modulo je 1, inverz neexistuje
        }
        while (a.compareTo(BigInteger.ONE) > 0) {
            BigInteger q = a.divide(m); // Kvocient
            BigInteger t = m;

            // Uprav hodnoty
            m = a.mod(m);
            a = t;
            t = y;

            y = x.subtract(q.multiply(y));
            x = t;
        }
        if (x.compareTo(BigInteger.ZERO) < 0) {
            x = x.add(m0); // Uprav na kladnú hodnotu
        }
        return x;
    }

    /**
     * Generuje RSA kľúče: verejný `(n, e)` a súkromný `(n, d)`.
     */
    public void generateKeys() {
        e = new BigInteger("65537"); // Verejný exponent (bežne používaná hodnota)
        BigInteger phi = euler(p, q); // Eulerova funkcia
        d = modInverse(e, phi); // Súkromný exponent
    }

    /**
     * Konvertuje text na zoznam "hyperčísel", ktoré sú dekódované do binárnych blokov.
     */
    public List<Long> convertToDecimalChunks(String text) {
        List<Long> decimalChunks = new ArrayList<>();

        int chunkSize = 7; // Maximálna dĺžka jedného bloku textu
        int textLength = text.length();

        for (int i = 0; i < textLength; i += chunkSize) {
            // Získaj blok textu po 7 znakoch
            String chunk = text.substring(i, Math.min(i + chunkSize, textLength));

            // Vytvor binárny reťazec
            StringBuilder binaryString = new StringBuilder();
            for (char c : chunk.toCharArray()) {
                // Konvertuj každý znak na 9-bitovú binárnu reprezentáciu
                String binaryChar = String.format("%09d", Integer.parseInt(Integer.toBinaryString(c)));
                binaryString.append(binaryChar);
            }

            // Preveď binárny reťazec na desiatkové číslo
            long decimalValue = Long.parseUnsignedLong(binaryString.toString(), 2);
            decimalChunks.add(decimalValue);
        }

        return decimalChunks;
    }

    /**
     * Šifruje správy pomocou verejného kľúča `(e, n)`.
     */
    public List<BigInteger> encryptMessage(List<Long> decimalChunks, BigInteger e, BigInteger n) {
        List<BigInteger> encryptedChunks = new ArrayList<>();
        for (long chunk : decimalChunks) {
            BigInteger encrypted = BigInteger.valueOf(chunk).modPow(e, n); // Šifrovanie pomocou modulo operácie
            encryptedChunks.add(encrypted);
        }
        return encryptedChunks;
    }

    /**
     * Dešifruje šifrované správy pomocou súkromného kľúča `(d, n)`.
     */
    public String decryptMessage(List<BigInteger> encryptedChunks, BigInteger d, BigInteger n) {
        StringBuilder message = new StringBuilder();
        for (BigInteger chunk : encryptedChunks) {
            BigInteger decrypted = chunk.modPow(d, n); // Dešifrovanie modulo operácie
            long decimalValue = decrypted.longValue();
            String binaryString = Long.toBinaryString(decimalValue);

            // Uprav binárny reťazec na násobok 9
            while (binaryString.length() % 9 != 0) {
                binaryString = "0" + binaryString;
            }

            // Rozdeľ binárny reťazec na znaky
            for (int i = 0; i < binaryString.length(); i += 9) {
                int charCode = Integer.parseInt(binaryString.substring(i, i + 9), 2); // Každý znak preveď z binárneho kódu
                message.append((char) charCode);
            }
        }
        return message.toString();
    }

    public static void main(String[] args) {
        RSABrain brain = new RSABrain();

        // Generovanie prvočísel a kľúčov
        brain.p = brain.generatePrvo();
        brain.q = brain.generatePrvo();
        brain.n = brain.nasobeniePQ(brain.p, brain.q);
        brain.generateKeys();

        System.out.println("P = " + brain.p);
        System.out.println("Q = " + brain.q);
        System.out.println("N = " + brain.n);
        System.out.println("E = " + brain.e);
        System.out.println("D = " + brain.d);

        // Text na šifrovanie
        String message = "Útok na cenka je o 9:00!";
        List<Long> decimalChunks = brain.convertToDecimalChunks(message);
        System.out.println("Decimal Chunks: " + decimalChunks);

        // Šifrovanie správy
        List<BigInteger> encryptedChunks = brain.encryptMessage(decimalChunks, brain.e, brain.n);
        System.out.println("Encrypted Chunks: " + encryptedChunks);

        // Dešifrovanie správy
        String decryptedMessage = brain.decryptMessage(encryptedChunks, brain.d, brain.n);
        System.out.println("Decrypted Message: " + decryptedMessage);
    }
}
