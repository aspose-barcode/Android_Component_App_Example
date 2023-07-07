package com.aspose.example.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClientBarcodeScannerFragmentViewModel  extends ViewModel
{
    private MutableLiveData<ClientRecognitionSettings> liveData;

    public ClientBarcodeScannerFragmentViewModel()
    {
        liveData = new MutableLiveData<>();
    }

    public void setClientRecognitionSettings(ClientRecognitionSettings preferences)
    {
        liveData.setValue(preferences);
    }

    public LiveData<ClientRecognitionSettings> getData()
    {
        return liveData;
    }
}