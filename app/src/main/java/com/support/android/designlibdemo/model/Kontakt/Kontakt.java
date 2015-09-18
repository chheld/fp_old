package com.support.android.designlibdemo.model.Kontakt;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.support.android.designlibdemo.R;

public class Kontakt implements Parcelable {

    private int mIcon;      // Icon des Kontakts
    @SerializedName("PERSONNR")
    private String PERSONNR;
    @SerializedName("NAME")
    private String NAME;
    @SerializedName("VORNAME")
    private String VORNAME;
    @SerializedName("FIRMANR")
    private String FIRMANR;
    @SerializedName("KTXT")
    private String KTXT;
    @SerializedName("ADRESSENR")
    private String ADRESSENR;


    public Kontakt() {
        super();
        this.mIcon = R.drawable.ic_contact;
    }

    public Kontakt(String a) {
        super();
        this.PERSONNR = a;
    }

    protected Kontakt(Parcel in) {
        mIcon = in.readInt();
        PERSONNR = in.readString();
        NAME = in.readString();
        VORNAME = in.readString();
        FIRMANR = in.readString();
        KTXT = in.readString();
        ADRESSENR = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIcon);
        dest.writeString(PERSONNR);
        dest.writeString(NAME);
        dest.writeString(VORNAME);
        dest.writeString(FIRMANR);
        dest.writeString(KTXT);
        dest.writeString(ADRESSENR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Kontakt> CREATOR = new Creator<Kontakt>() {
        @Override
        public Kontakt createFromParcel(Parcel in) {
            return new Kontakt(in);
        }

        @Override
        public Kontakt[] newArray(int size) {
            return new Kontakt[size];
        }
    };

    public int getIcon() {
        return mIcon;
    }


    /**
     *
     * @return
     * The PERSONNR
     */
    public String getPERSONNR() {
        return PERSONNR;
    }

    /**
     *
     * @param PERSONNR
     * The PERSONNR
     */
    public void setPERSONNR(String PERSONNR) {
        this.PERSONNR = PERSONNR;
    }

    /**
     *
     * @return
     * The NAME
     */
    public String getNAME() {
        return NAME;
    }

    /**
     *
     * @param NAME
     * The NAME
     */
    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    /**
     *
     * @return
     * The VORNAME
     */
    public String getVORNAME() {
        return VORNAME;
    }

    /**
     *
     * @param VORNAME
     * The VORNAME
     */
    public void setVORNAME(String VORNAME) {
        this.VORNAME = VORNAME;
    }

    /**
     *
     * @return
     * The FIRMANR
     */
    public String getFIRMANR() {
        return FIRMANR;
    }

    /**
     *
     * @param FIRMANR
     * The FIRMANR
     */
    public void setFIRMANR(String FIRMANR) {
        this.FIRMANR = FIRMANR;
    }

    /**
     *
     * @return
     * The KTXT
     */
    public String getKTXT() {
        return KTXT;
    }

    /**
     *
     * @param KTXT
     * The KTXT
     */
    public void setKTXT(String KTXT) {
        this.KTXT = KTXT;
    }

    /**
     *
     * @return
     * The ADRESSENR
     */
    public String getADRESSENR() {
        return ADRESSENR;
    }

    /**
     *
     * @param ADRESSENR
     * The ADRESSENR
     */
    public void setADRESSENR(String ADRESSENR) {
        this.ADRESSENR = ADRESSENR;
    }

}
