package com.aspose.example.fragment;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.component.barcodescanner.BarcodeRecognitionSettings;
import com.aspose.barcode.component.barcodescanner.BarcodeScannerFragment;
import com.aspose.example.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientRecognizeFragment extends Fragment
{

    ClientBarcodeScannerFragmentViewModel clientBarcodeScannerFragmentViewModel;
    private CheckBox showRecognitionAreaCheckbox;
    private CheckBox enableFlashlightCheckbox;
    private RadioGroup barcodeTypeRadiogroup;

    public ClientRecognizeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        clientBarcodeScannerFragmentViewModel = new ViewModelProvider(requireActivity()).get(ClientBarcodeScannerFragmentViewModel.class);
        clientBarcodeScannerFragmentViewModel.getData().observe(this, this::initContent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client_recognize, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        showRecognitionAreaCheckbox = view.findViewById(R.id.showRecognitionAreaCheckBox);
        enableFlashlightCheckbox = view.findViewById(R.id.enableFlashLightCheckBox);
        barcodeTypeRadiogroup = view.findViewById(R.id.barcode_type_radiogroup);

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragmentContainerView, new ClientMainFragment())
                        .commit();
            }
        });
        initContent(clientBarcodeScannerFragmentViewModel.getData().getValue());
    }

    private void initContent(ClientRecognitionSettings clientRecognitionSettings)
    {
        BarcodeScannerFragment barcodeScannerFragment = (BarcodeScannerFragment) getChildFragmentManager().findFragmentByTag("barcode_scanner_fragment_tag");
        applyBarcodeScannerFragmentSettings(barcodeScannerFragment.getBarcodeRecognitionSettings());

        barcodeScannerFragment.setPreferencesFragment(new ClientSettingsFragment());
        barcodeScannerFragment.setShowPreferencesButton(true);

        BarcodeRecognitionSettings barcodeRecognitionSettings = barcodeScannerFragment.getBarcodeRecognitionSettings();

        try {
            barcodeRecognitionSettings.setCameraResolution(Collections.min(Arrays.asList(ClientSettingsFragment.getCameraResolutions(requireContext())), Comparator.comparingInt(o -> o.getWidth() * o.getHeight())));
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        showRecognitionAreaCheckbox.setOnCheckedChangeListener((compoundButton, checked) ->
        {
            clientRecognitionSettings.setShowRecognitionArea(checked);
            applyBarcodeScannerFragmentSettings(barcodeScannerFragment.getBarcodeRecognitionSettings());
        });
        enableFlashlightCheckbox.setOnCheckedChangeListener((compoundButton, checked) ->
        {
            clientRecognitionSettings.setFlashEnabled(checked);
            applyBarcodeScannerFragmentSettings(barcodeScannerFragment.getBarcodeRecognitionSettings());
        });


        barcodeTypeRadiogroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId)
            {
                case R.id.radio_1D:
                    clientRecognitionSettings.setBarCodeReadType(DecodeType.TYPES_1D);
                    break;
                case R.id.radio_2D:
                    clientRecognitionSettings.setBarCodeReadType(DecodeType.TYPES_2D);
                    break;
                default:
                    clientRecognitionSettings.setBarCodeReadType(DecodeType.ALL_SUPPORTED_TYPES);

            }
            applyBarcodeScannerFragmentSettings(barcodeScannerFragment.getBarcodeRecognitionSettings());
        });
    }

    private void applyBarcodeScannerFragmentSettings(BarcodeRecognitionSettings barcodeRecognitionSettings)
    {
        ClientRecognitionSettings clientRecognitionSettings = clientBarcodeScannerFragmentViewModel.getData().getValue();

        barcodeRecognitionSettings.setShowRecognitionArea(clientRecognitionSettings.getShowRecognitionArea());
        barcodeRecognitionSettings.setBarCodeReadType(clientRecognitionSettings.getBarCodeReadType());
        barcodeRecognitionSettings.setFlashEnabled(clientRecognitionSettings.getFlashEnabled());

        barcodeRecognitionSettings.setBarcodeRecognitionResultHandler((context, results, settings) ->
        {
            String message = "Not recognized";
            if (results.length > 0) {
                message = results[0].getCodeText();
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setMessage(message);
            dialog.create().show();
            return true;
        });
    }
}