package fr.tvbarthel.apps.simplethermometer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple class that represent a color to pick.
 */
public class ColorPick implements Parcelable {
    private String mName;
    private int mColor;

    public ColorPick() {
    }

    public ColorPick(Parcel in) {
        readFromParcel(in);
    }

    public ColorPick(String name, int color) {
        mName = name;
        mColor = color;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mColor);
    }

    private void readFromParcel(Parcel in) {
        mName = in.readString();
        mColor = in.readInt();
    }

    public static Creator<ColorPick> CREATOR = new Creator<ColorPick>() {
        public ColorPick createFromParcel(Parcel source) {
            return new ColorPick(source);
        }

        public ColorPick[] newArray(int size) {
            return new ColorPick[size];
        }
    };
}
