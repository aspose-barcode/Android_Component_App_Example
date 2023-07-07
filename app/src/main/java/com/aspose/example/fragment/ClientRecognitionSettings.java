package com.aspose.example.fragment;

import com.aspose.barcode.barcoderecognition.BaseDecodeType;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.barcoderecognition.MultyDecodeType;

public class ClientRecognitionSettings
{
    private boolean showRecognitionArea = true;
    private boolean flashEnabled = true;
    private BaseDecodeType barCodeReadType = DecodeType.ALL_SUPPORTED_TYPES;
    public boolean getShowRecognitionArea()
    {
        return showRecognitionArea;
    }

    public BaseDecodeType getBarCodeReadType()
    {
        return barCodeReadType;
    }

    public boolean getFlashEnabled() {
        return flashEnabled;
    }

    public void setBarCodeReadType(MultyDecodeType decodeType)
    {
        this.barCodeReadType = decodeType;
    }

    public void setShowRecognitionArea(boolean showRecognitionArea)
    {
        this.showRecognitionArea = showRecognitionArea;
    }

    public void setFlashEnabled(boolean flashEnabled)
    {
        this.flashEnabled = flashEnabled;
    }
}
