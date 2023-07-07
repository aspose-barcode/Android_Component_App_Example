package com.aspose.example;

import android.content.Context;
import android.os.Parcel;

import com.aspose.barcode.barcoderecognition.BarCodeResult;
import com.aspose.barcode.barcoderecognition.BarcodeSettings;
import com.aspose.barcode.component.barcodescanner.BarcodeRecognitionResultsHandlerParcelable;

public class ClientResultsListener implements BarcodeRecognitionResultsHandlerParcelable
{
    public String resultString;

    public ClientResultsListener() {}

    protected ClientResultsListener(Parcel in) {
        resultString = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resultString);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClientResultsListener> CREATOR = new Creator<ClientResultsListener>() {
        @Override
        public ClientResultsListener createFromParcel(Parcel in) {
            return new ClientResultsListener(in);
        }

        @Override
        public ClientResultsListener[] newArray(int size) {
            return new ClientResultsListener[size];
        }
    };

    @Override
    public boolean processResult(Context context, BarCodeResult[] results, BarcodeSettings settings)
    {
        if(results == null || results.length < 1)
            return true;

        resultString = results[0].getCodeText();
        return false;
    }
}
