# BarcodeGs1
Android application which connects to a barcode reader using the Zebra EMDK. 
A callback is used to alert the listener within the activity.  
The user has the ability to parse the barcode values using the GS1-128 standardized system. 
An array of GS1 objects are used to hold onto the GS1 values parsed from the barcode.  
Junit and Espresso testing are setup to test the GS1 parsing with various values to ensure the function works correctly.
Uses Butterknife for field binding and dynamic button association.
