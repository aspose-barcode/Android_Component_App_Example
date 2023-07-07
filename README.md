## How to embed a Barcode Component into an application to get access to scanning and recognition functionality

1. Add aspose-barcode-android-xx.xx.aar to dependencies   
   Add following dependencies as well  
   ```groovy
   dependencies {
   implementation(name:'aspose-barcode-android-22.11', ext:'aar')
   implementation 'androidx.appcompat:appcompat:1.5.1'
   implementation 'com.google.android.material:material:1.6.1'
   implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
   implementation 'androidx.preference:preference:1.2.0'
   implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1'
   testImplementation 'junit:junit:4.13.2'
   androidTestImplementation 'androidx.test.ext:junit:1.1.3'
   androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
   }
   ```
2. Create classes to use com.aspose.barcode.component.barcodescanner.BarcodeScanner  
    
   - BarcodeScanner needs reference to instance of the current application activity because
       the scanning and recognition will be fulfilling inside of component's activity.
    ```java
    public class MainActivity extends AppCompatActivity
    {
      private BarcodeScannerViewModel model;
      @Override
      protected void onCreate(Bundle savedInstanceState)
      {
         model = new ViewModelProvider(this).get(BarcodeScannerViewModel.class);
         BarcodeScanner scanner = new BarcodeScanner(this);
         model.setBarcodeScanner(scanner);
      }
    }
    ```
    - Configure BarcodeScanner.    
       Customer has the opportunity to configure list of supported barcode types, resolution of the camera,  
       GUI's settings.
   ```java
      BarcodeScannerPreferences scannerPreferences = scanner.getPreferences();
      List<Size> availableResolutions  = new ArrayList<>(preferences.getBarcodeScannerResolution().getAvailableResolutions());
   ```
   ```java
      BarcodeScannerPreferences scannerPreferences = scanner.getPreferences();
      MultyDecodeType availableDecodeTypes = new MultyDecodeType(preferences.getBarcodeScannerDecodeType().getAvailableDecodeTypes()); 
   ```

   ```java   
      BarcodeScannerPreferences scannerPreferences = scanner.getPreferences();
      SwitchPreferenceCompat barcodeScannerSettingsSwitchPreference = findPreference("switchVisibilityBarcodeScanner");
      SwitchPreferenceCompat decodeTypesSwitchPreference = findPreference("switchVisibilityDecodeTypes");
      SwitchPreferenceCompat resolutionSwitchPreference = findPreference("switchVisibilityResolutions");
      SwitchPreferenceCompat recognitionAreaSwitchPreference = findPreference("switchVisibilityRecognitionArea");
      barcodeScannerSettingsSwitchPreference.setChecked(preferences.getBarcodeScannerGUIParameters().isVisibleScannerSettings());
      decodeTypesSwitchPreference.setChecked(preferences.getBarcodeScannerGUIParameters().isVisibleChooseBarcodeType());
      resolutionSwitchPreference.setChecked(preferences.getBarcodeScannerGUIParameters().isVisibleChooseResolution());
      recognitionAreaSwitchPreference.setChecked(preferences.getBarcodeScannerGUIParameters().isVisibleRecognitionArea());
   ```

   - To inform the customer's activity that BarcodeScanner completed the scanning and recognition process the event
   handler should be added
     ```java
      BarcodeScannerViewModel barcodeScannerViewModel = new ViewModelProvider(requireActivity()).get(BarcodeScannerViewModel.class);
      recognizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientResultsHandler handler = new ClientResultsHandler();
                barcodeScannerViewModel.getData().getValue().setOnScanFinishedListener(new ScanFinishedListener() {
                    @Override
                    public void onScanFinished(RecognitionHandler recognitionHandler) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                        dialog.setMessage(((ClientResultsHandler)recognitionHandler).resultString);
                        dialog.create().show();
                    }
                });
                barcodeScannerViewModel.getData().getValue().launchBarcodeScanner(handler);
            }
        });
     ```

   ScanFinishedListener -is interface with single method
     ```java
      void onScanFinished(RecognitionHandler recognitionHandler)
     ```
   from which the client can get the result of scanning and recognition of barcode.

3.Launch the scanning process     
   Customer should implement single-method-interface RecognitionHandler
   ```java
   public interface RecognitionHandler extends Parcelable
   {
      boolean processResult(BarCodeResult[] results);
   }
   ```

   - The simple example:
   ```java
   @Override
   public boolean processResult(BarCodeResult[] results)
   {
      if (results == null || results.length < 1)
      {
        return (true);
      }
      resultString = results[0].getCodeText();
      return false;
    }
   ```
   This overriden method will be invoked by BarcodeScannerActivity after completition of the image recognizing.
   Customer's code can process the result without closing the camera.
   If customer's code decides that the results are not satisfactory and the scan process 
   should be repeated the method ```processResult```  
   should return ```true```.
   If this method returns ```true``` the BarcodeScannerActivity will close and process returns to customer's application activity.   
    
   

