package com.aspose.example.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aspose.barcode.component.barcodescanner.BarcodeScanner;


public class ClientBarcodeScannerViewModel extends ViewModel
{
    private MutableLiveData<BarcodeScanner> liveData;


    public ClientBarcodeScannerViewModel()
    {
        liveData = new MutableLiveData<>();
    }

    public void setBarcodeScanner(BarcodeScanner preferences)
    {
        liveData.setValue(preferences);
    }

    public LiveData<BarcodeScanner> getData()
    {
        return liveData;
    }
}
