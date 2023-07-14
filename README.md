## How to use an application
    
1. You should add aspose-barcode-android-xx.xx.aar to dependencies   
   Add following dependencies as well  
   ```groovy
   dependencies {
   implementation(name:'aspose-barcode-android-23.6', ext:'aar')
   }
   ```
   or put aspose-barcode-android-xx.x.aar to lib folder and add it to dependencies like that:
   ```groovy
   dependencies {
   implementation fileTree(dir: "libs", include: "aspose-barcode-android-23.6.aar")
   }
   ```
2. You might request trial license at
     https://purchase.aspose.com/temporary-license/
  and place it to app/src/main/assets folder. Expected name of license file is 'Aspose.BarCode.Android.Java.lic'.
  But this name can be changed in the file 'strings.xml' .
  If you run without license the certain restrictions will be applied.
3. Launch application. Click button Setup License. If license was installed the appropriate message will appear.
4. Click button "Recognize via activity" to test recognition functionality implemented with using component's
   BarcodeScannerActivity
5.  Click button "Recognize via fragment" to test recognition functionality implemented with using component's
    BarcodeScannerFragment
6. Hover a square frame on the image and click the green button at the bottom.

## How to embed a Barcode Component into an application to get access to scanning and recognition functionality
1. Create classes to use com.aspose.barcode.component.barcodescanner.BarcodeScanner  
    
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

2.Launch the scanning process     
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
    
   

