package com.aspose.example.fragment;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aspose.barcode.License;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.component.barcodescanner.BarcodeRecognitionResultsHandlerParcelable;
import com.aspose.barcode.component.barcodescanner.BarcodeRecognitionSettings;
import com.aspose.barcode.component.barcodescanner.OnBarcodeScannerCompletedCallback;

import com.aspose.example.ClientResultsListener;
import com.aspose.barcode.component.example.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ClientMainFragment extends Fragment
{
    private final static String TAG = "ClientMainFragment";

    public ClientMainFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Button recognizeBarcodeScannerViaActivityButton = requireActivity().findViewById(R.id.button_recognize_scanner);
        Button recognizeBarcodeScannerViaFragmentButton = requireActivity().findViewById(R.id.button_recognize_fragment);
        Button installLicenseFileButton = requireActivity().findViewById(R.id.button_install_license_file);
        Button readLicenseFileButton = requireActivity().findViewById(R.id.button_read_license_file);

        readLicenseFileButton.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                String licenseFileName = getResources().getString(R.string.license_file_name);
                try
                {
                    Activity activity = getActivity();
                    LicenseHandler licenseHandler = new LicenseHandler(licenseFileName, activity);
                    String licenseFileContent = licenseHandler.getLicenseFileContent();
                    dialog.setMessage(licenseFileContent);
                    dialog.create().show();
                }
                catch (Exception e)
                {
                    dialog.setMessage("Cannot read license file " + licenseFileName);
                    dialog.create().show();
                    e.printStackTrace();
                    return;
                }
            }
        }));
        installLicenseFileButton.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                String licenseFileName = getResources().getString(R.string.license_file_name);
                String message = "";
                try
                {
                    Activity activity = getActivity();
                    LicenseHandler licenseHandler = new LicenseHandler(licenseFileName, activity);
                    message = licenseHandler.setupLicense();
                    dialog.setMessage(message);
                    dialog.create().show();
                }
                catch (Exception e)
                {
                    dialog.setMessage("Cannot install license file " + licenseFileName + "\nCause:\n" + e.getMessage());
                    dialog.create().show();
                    e.printStackTrace();
                }
            }
        }));

        recognizeBarcodeScannerViaActivityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ClientResultsListener listener = new ClientResultsListener();
                ClientBarcodeScannerViewModel clientBarcodeScannerViewModel = new ViewModelProvider(requireActivity()).get(ClientBarcodeScannerViewModel.class);
                if (clientBarcodeScannerViewModel.getData().getValue() == null)
                {
                    String mess = "Error occurred, BarcodeScanner instance should be obtained from ClientBarcodeScannerViewModel that should be created in ClienMainActivity";
                    throw new RuntimeException(mess);
                }
                clientBarcodeScannerViewModel.getData().getValue().setOnBarcodeScannerCompletedCallback(new OnBarcodeScannerCompletedCallback()
                {
                    @Override
                    public void onScanFinished(BarcodeRecognitionResultsHandlerParcelable barcodeRecognitionResult)
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                        dialog.setMessage(((ClientResultsListener) barcodeRecognitionResult).resultString);
                        dialog.create().show();
                    }
                });
                BarcodeRecognitionSettings barcodeRecognitionSettings = clientBarcodeScannerViewModel.getData().getValue().getPreferences().getBarcodeRecognitionSettings();
                barcodeRecognitionSettings.setBarCodeReadType(DecodeType.ALL_SUPPORTED_TYPES);
                clientBarcodeScannerViewModel.getData().getValue().launchBarcodeScanner(listener);
            }
        });

        recognizeBarcodeScannerViaFragmentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragmentContainerView, new ClientRecognizeFragment())
                        .commit();
            }
        });
    }

    class LicenseHandler
    {
        String licenseFileName;
        Activity activity;
//        AlertDialog.Builder dialog;

        public LicenseHandler(String licenseFileName, Activity activity)
        {
            this.licenseFileName = licenseFileName;
            this.activity = activity;
        }

        private InputStream readLicenseFile() throws Exception
        {
            AssetManager assetManager = activity.getAssets();
            InputStream inputStream = assetManager.open(licenseFileName);
            return inputStream;
        }

        public String getLicenseFileContent() throws Exception
        {
            String content = "";
            try (InputStream inputStream = readLicenseFile())
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(licenseFileName).append("\n\n");
                while ((receiveString = bufferedReader.readLine()) != null)
                {
                    if (receiveString.equals(""))
                    {
                        stringBuilder.append(System.getProperty("line.separator"));
                    } else
                    {
                        stringBuilder.append(receiveString).append("\n");
                    }
                }
                content = stringBuilder.toString();
                Log.d(TAG, content);
                return content;
            }
        }

        public String setupLicense() throws Exception
        {
            InputStream inputStream = readLicenseFile();
            try
            {
                License lic = new License();
                lic.setLicense(inputStream);
                return "License installed";
            }
            finally
            {
                inputStream.close();
            }
        }
    }
}