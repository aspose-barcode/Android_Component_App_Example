package com.aspose.example.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.component.barcodescanner.BarcodeRecognitionSettings;
import com.aspose.barcode.component.barcodescanner.BarcodeScannerFragment;
import com.aspose.barcode.component.barcodescanner.BarcodeScannerFragmentSettings;
import com.aspose.barcode.component.barcodescanner.recognitionareaview.BarcodeAreaLineStyle;
import com.aspose.barcode.component.example.R;

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
            barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().getRecognitionAreaSettings().setRecognizeOnlyInRecognitionArea(true);
            barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().getRecognitionAreaSettings().setTwoDLineColor(Color.CYAN);
            barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().getRecognitionAreaSettings().setTwoDAreaCornerRadius(20);
            barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().getRecognitionAreaSettings().setTwoDAreaBarcodeLineStyle(BarcodeAreaLineStyle.DASH_DOT);
            barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().getRecognitionAreaSettings().setOneDLineColor(Color.MAGENTA);
            barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().getRecognitionAreaSettings().setOneDLineWidth(30);
            barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().setCameraResolution(Collections.min(Arrays.asList(ClientSettingsFragment.getCameraResolutions(requireContext())), Comparator.comparingInt(o -> o.getWidth() * o.getHeight())));
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
        barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().setFlashEnabled(true);
        ClientRecognitionSettings clientRecognitionSettings = clientBarcodeScannerFragmentViewModel.getData().getValue();

        barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().getRecognitionAreaSettings().setRecognizeOnlyInRecognitionArea(clientRecognitionSettings.getShowRecognitionArea());
        barcodeRecognitionSettings.setBarCodeReadType(clientRecognitionSettings.getBarCodeReadType());
        barcodeRecognitionSettings.getBarcodeScannerFragmentSettings().getCameraProcessingFragmentSettings().setFlashEnabled(clientRecognitionSettings.getFlashEnabled());

        barcodeRecognitionSettings.setBarcodeRecognitionResultHandler((context, results, settings) ->
        {
            String notRecognizedMessage = "Not recognized";
            String message = notRecognizedMessage;
            if (results.length > 0) {
                message = "";
                for(int i = 0; i < results.length; i++)
                    message += i + ". " + results[i].getCodeText() + ";\n";
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(message);
            dialog.create().show();
            return message.equals(notRecognizedMessage);
        });
    }
}