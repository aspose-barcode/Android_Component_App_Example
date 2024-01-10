package com.aspose.example;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.aspose.barcode.component.barcodescanner.BarcodeScanner;
import com.aspose.barcode.component.example.R;
import com.aspose.example.fragment.ClientBarcodeScannerFragmentViewModel;
import com.aspose.example.fragment.ClientBarcodeScannerViewModel;
import com.aspose.example.fragment.ClientRecognitionSettings;

import androidx.annotation.NonNull;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;

import java.util.Locale;

public class ClientMainActivity extends AppCompatActivity
{
    private ClientBarcodeScannerFragmentViewModel clientBarcodeScannerFragmentViewModel;
    private ClientBarcodeScannerViewModel clientBarcodeScannerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Locale.setDefault(new Locale("en", "US"));

        // Barcode Scanner Activity View Model
        clientBarcodeScannerViewModel = new ViewModelProvider(this).get(ClientBarcodeScannerViewModel.class);
        clientBarcodeScannerViewModel.getData().observe(this, barcodeScanner -> {});
        if(clientBarcodeScannerViewModel.getData().getValue() == null)
        {
            try
            {
                BarcodeScanner scanner = new BarcodeScanner(this);
                clientBarcodeScannerViewModel.setBarcodeScanner(scanner);
            }
            catch (CameraAccessException e)
            {
                e.printStackTrace();
            }
        }

        // Barcode Scanner Fragment View Model
        clientBarcodeScannerFragmentViewModel = new ViewModelProvider(this).get(ClientBarcodeScannerFragmentViewModel.class);

        if(clientBarcodeScannerFragmentViewModel.getData().getValue() == null)
        {
            ClientRecognitionSettings clientRecognitionSettings = new ClientRecognitionSettings();
            clientBarcodeScannerFragmentViewModel.setClientRecognitionSettings(clientRecognitionSettings);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        else
            loadActivityContent();

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        loadActivityContent();
    }

    private void loadActivityContent(){}
}