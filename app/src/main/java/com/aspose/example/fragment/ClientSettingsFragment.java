package com.aspose.example.fragment;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.aspose.barcode.barcoderecognition.BaseDecodeType;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.barcoderecognition.MultyDecodeType;
import com.aspose.barcode.component.barcodescanner.BarcodeRecognitionSettings;
import com.aspose.barcode.component.barcodescanner.BarcodeScannerPreferencesFragment;
import com.aspose.example.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ClientSettingsFragment extends BarcodeScannerPreferencesFragment
{
    private static final String ALL_SUPPORTED_TYPES_TITLE_NAME = "All Supported Types";
    private static final String ONE_D_TYPES_TITLE_NAME = "1D";
    private static final String TWO_D_TYPES_TITLE_NAME = "2D";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.client_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initControls(getBarcodeScannerFragmentPreferences());
    }

    public void initControls(BarcodeRecognitionSettings scannerPreferences)
    {
        // Decode types
        ListPreference decodeTypesListPreference = findPreference("decode_types_list_preference");
        initDecodeTypes(decodeTypesListPreference);
        decodeTypesListPreference.setVisible(true);

        // Resolutions
        ListPreference resolutionListPreference = findPreference("resolution_list_preference");
        try {
            initResolutions(resolutionListPreference);
            resolutionListPreference.setVisible(true);
        }
        catch (CameraAccessException e)
        {
            throw new RuntimeException(e);
        }

        // Show recognition area
        SwitchPreferenceCompat switchPreference = findPreference("show_recognition_area");
        switchPreference.setChecked(scannerPreferences.isShowRecognitionArea());
        switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isShowRecognitionArea = (Boolean)newValue;
            scannerPreferences.setShowRecognitionArea((isShowRecognitionArea));
            switchPreference.setChecked(isShowRecognitionArea);
            getBarcodeScannerFragmentPreferences().setShowRecognitionArea(isShowRecognitionArea);
            return true;
        });
        switchPreference.setVisible(true);
    }

    private void initDecodeTypes(ListPreference decodeTypePreference)
    {
        List<String> entries = Arrays.asList(ALL_SUPPORTED_TYPES_TITLE_NAME, ONE_D_TYPES_TITLE_NAME, TWO_D_TYPES_TITLE_NAME);
        decodeTypePreference.setEntries(entries.toArray(new String[0]));
        decodeTypePreference.setEntryValues(entries.toArray(new String[0]));
        decodeTypePreference.setTitle(getDecodeTypeTitle(getBarcodeScannerFragmentPreferences().getBarCodeDecodeType()));
        decodeTypePreference.setValueIndex(0);
        decodeTypePreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            BaseDecodeType chosenDecodeType = getBarcodeScannerFragmentPreferences().getBarCodeDecodeType();
            if (ALL_SUPPORTED_TYPES_TITLE_NAME.equals(newValue))
            {
                chosenDecodeType = DecodeType.ALL_SUPPORTED_TYPES;
            }
            else if (ONE_D_TYPES_TITLE_NAME.equals(newValue))
            {
                chosenDecodeType = DecodeType.TYPES_1D;
            }
            else if (TWO_D_TYPES_TITLE_NAME.equals(newValue))
            {
                chosenDecodeType = DecodeType.TYPES_2D;
            }
            decodeTypePreference.setTitle(getDecodeTypeTitle(chosenDecodeType));
            getBarcodeScannerFragmentPreferences().setBarCodeReadType(chosenDecodeType);
            return true;
        });
    }

    /**
     * Internal
     * @param resolutionListPreference
     */
    public void initResolutions(ListPreference resolutionListPreference) throws CameraAccessException
    {
        List<String> entries = new ArrayList<>();
        List<Size> availableResolutions = Arrays.asList(getCameraResolutions(requireContext()));
        availableResolutions.sort(Comparator.comparingInt(o -> o.getWidth() * o.getHeight()));
        Size chosenResolution = getBarcodeScannerFragmentPreferences().getCameraResolution() == null ? availableResolutions.get(0) :
                getBarcodeScannerFragmentPreferences().getCameraResolution();
        availableResolutions.forEach(resolution -> entries.add(resolution.getWidth() + "x" + resolution.getHeight()));
        resolutionListPreference.setEntries(entries.toArray(new String[0]));
        resolutionListPreference.setEntryValues(entries.toArray(new String[0]));
        resolutionListPreference.setTitle(chosenResolution.getWidth() + "x" + chosenResolution.getHeight());
        resolutionListPreference.setValueIndex(0);
        resolutionListPreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            String titleResolution = (String)newValue;
            Size newResolution = new Size(Integer.parseInt(titleResolution.split("x")[0]),Integer.parseInt(titleResolution.split("x")[1]));
            resolutionListPreference.setTitle(newResolution.getWidth() + "x" + newResolution.getHeight());
            getBarcodeScannerFragmentPreferences().setCameraResolution(newResolution);
            return true;
        });
        getBarcodeScannerFragmentPreferences().setCameraResolution(chosenResolution);
    }

    private static String getDecodeTypeTitle(BaseDecodeType decodeType)
    {
        if (decodeType instanceof MultyDecodeType && ((MultyDecodeType) decodeType).containsAll(DecodeType.ALL_SUPPORTED_TYPES))
            return ALL_SUPPORTED_TYPES_TITLE_NAME;
        if (decodeType instanceof MultyDecodeType && ((MultyDecodeType) decodeType).containsAll(DecodeType.TYPES_1D))
            return ONE_D_TYPES_TITLE_NAME;
        if (decodeType instanceof MultyDecodeType && ((MultyDecodeType) decodeType).containsAll(DecodeType.TYPES_2D))
            return TWO_D_TYPES_TITLE_NAME;
        return ALL_SUPPORTED_TYPES_TITLE_NAME;
    }

    public static Size[] getCameraResolutions(Context context) throws CameraAccessException
    {
        CameraManager cameraManager = ((CameraManager) context.getSystemService(Context.CAMERA_SERVICE));
        StreamConfigurationMap configurationMap = cameraManager.getCameraCharacteristics(String.valueOf(0)).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP); // TODO String.valueOf(0)
        return configurationMap.getOutputSizes(ImageFormat.JPEG);
    }
}