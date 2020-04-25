package com.ignatiusalbert.kerangajaibmaster;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Belal on 2/26/2017.
 */
@IgnoreExtraProperties
public class kerangajaib {
    private String percobaan;
    private String pilihan1;
    private String pilihan2;
    private String hasil;

    public kerangajaib(){
        //this constructor is required
    }

    public kerangajaib(String percobaan, String pilihan1, String pilihan2, String hasil) {
        this.percobaan = percobaan;
        this.pilihan1 = pilihan1;
        this.pilihan2 = pilihan2;
        this.hasil = hasil;
    }

    public String getPercobaan() {
        return percobaan;
    }

    public String getPilihan1() {
        return pilihan1;
    }

    public String getPilihan2() {
        return pilihan2;
    }
    public String getHasil() {
        return hasil;
    }
}