package com.jaykallen.barcodegs1;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// Created by jkallen on 4/11/2017.  This method takes in a barcode, loads up a list array with all
// the GS1128 AI's, then parses out all the GS1128 artifacts from the barcode.

public class Gs1Parser {
    static List<Gs1Model> aiValues;

    public static List<Gs1Model> read (String barcode) {
        List<Gs1Model> artifacts = new ArrayList<>();;
        addArtifactsToArray();
        String aiBarcode;
        String dataBarcode;
        int currlength;

        //Replace Ascii 29 with a | character
        barcode = barcode.replaceAll(Character.toString((char)0x1D), "|");
        while (barcode.length() > 1) {
            Log.d("TagGs1Parser", "Parsing=" + barcode + " (" +barcode.length() + " digits)");
            currlength = barcode.length();
            for (int i=0; i<aiValues.size(); i++) {
                // Compare the beginning barcode digits against each AI in the entire Array
                if (barcode.startsWith(aiValues.get(i).getAi())) {
                    // Get the Ai from the barcode
                    aiBarcode = barcode.substring(0, aiValues.get(i).getAiLength());
                    // parse out the Ai from the barcode leaving the rest intact.
                    barcode = barcode.substring(aiValues.get(i).getAiLength(), barcode.length());
                    aiValues.get(i).setAiBarcode(aiBarcode);
                    Log.d("TagGs1Parser", "Found " + aiValues.get(i).getAiName() + ", Ai= " +
                            aiValues.get(i).getAiBarcode() + " (" + aiValues.get(i).getAiLength() +
                            " digits)");
                    if (barcode.length() > aiValues.get(i).getDataLength()) {
                        Log.d("TagGs1Parser", "Parsing " + aiValues.get(i).getDataLength() +
                                " digits from " + barcode);
                        dataBarcode = barcode.substring(0, aiValues.get(i).getDataLength());
                        barcode = barcode.substring(aiValues.get(i).getDataLength());
                    } else {
                        Log.d("TagGs1Parser", "Expected " + aiValues.get(i).getDataLength() + " digits. Found "
                                + barcode.length() + " digits.");
                        dataBarcode = barcode;
                        barcode = "";
                    }
                    if (aiValues.get(i).isVariable() && dataBarcode.contains("|")) {
                        Log.d("TagGs1Parser", "Separator found at position " + dataBarcode.indexOf("|") +
                                " in " + dataBarcode);
                        String subBarcode = dataBarcode;
                        dataBarcode = subBarcode.substring(0, (subBarcode.indexOf("|")));
                        Log.d("TagGs1Parser", "dataBarcode = " + dataBarcode);
                        barcode = subBarcode.substring(subBarcode.indexOf("|")+1, subBarcode.length()) + barcode;
                        Log.d("TagGs1Parser", "barcode = " + barcode);
                    }
                    aiValues.get(i).setDataBarcode(dataBarcode);
                    Log.d("TagGs1Parser", "Found " + aiValues.get(i).getAiName() + ", Data=" +
                            aiValues.get(i).getDataBarcode() + " (" + aiValues.get(i).getDataLength()
                            + " digits)");
                    // if Ai is fixed length and the expected length doesn't match, reject the data.
                    if (!(!aiValues.get(i).isVariable() && aiValues.get(i).getDataLength() != dataBarcode.length())){
                        artifacts.add(new Gs1Model(aiValues.get(i)));
                    }
                    break;
                }
            }
            if (currlength == barcode.length()) {
                Log.d("TagGs1Parser", "Error: Infinite Loop Break!");
                barcode = "";
            }
        }
        return artifacts;
    }


    private static void addArtifactsToArray() {
        // Creates a new artifact object with Ai values, then adds to the array of Ai values.
        aiValues = new ArrayList<>();
        aiValues.add(new Gs1Model("00", "Serial Shipping Container Code(SSCC-18)", 2, "", 18, "", false));
        aiValues.add(new Gs1Model("01", "UPC (GTIN-14)", 2, "", 14, "", false));
        aiValues.add(new Gs1Model("02", "Number of containers", 2, "", 14, "", false));
        aiValues.add(new Gs1Model("10", "Batch Number", 2, "", 20, "", true));
        aiValues.add(new Gs1Model("11", "Production Date", 2, "", 6, "", false));
        aiValues.add(new Gs1Model("13", "Packaging Date", 2, "", 6, "", false));
        aiValues.add(new Gs1Model("15", "Sell by Date (Quality Control)", 2, "", 6, "", false));
        aiValues.add(new Gs1Model("17", "Expiration Date", 2, "", 6, "", false));
        aiValues.add(new Gs1Model("20", "Product Variant", 2, "", 2, "", false));
        aiValues.add(new Gs1Model("21", "Serial Number", 2, "", 20, "", true));
        aiValues.add(new Gs1Model("22", "HIBCC Quantity, Date, Batch and Link", 2, "", 29, "", true));
        aiValues.add(new Gs1Model("23", "Lot Number", 3, "", 19, "", true));
        aiValues.add(new Gs1Model("240", "Additional Product Identification", 3, "", 30, "", true));
        aiValues.add(new Gs1Model("241", "Customer Part Number", 3, "", 30, "", true));
        aiValues.add(new Gs1Model("250", "Secondary Serial Number", 3, "", 30, "", true));
        aiValues.add(new Gs1Model("251", "Reference to source entity", 3, "", 30, "", true));
        aiValues.add(new Gs1Model("253", "Global Document Type Identifier", 3, "", 17, "", true));
        aiValues.add(new Gs1Model("30", "Quantity Each", 2, "", 8, "", true));
        aiValues.add(new Gs1Model("310", "Product Net Weight in kg", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("311", "Product Length/1st Dimension, in meters", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("312", "Product Width/Diameter/2nd Dimension, in meters", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("313", "Product Depth/Thickness/3rd Dimension, in meters", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("314", "Product Area, in square meters", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("315", "Product Volume, in liters", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("316", "product Volume, in cubic meters", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("320", "Product Net Weight, in pounds", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("321", "Product Length/1st Dimension, in inches", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("322", "Product Length/1st Dimension, in feet", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("323", "Product Length/1st Dimension, in yards", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("324", "Product Width/Diameter/2nd Dimension, in inches", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("325", "Product Width/Diameter/2nd Dimension, in feet", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("326", "Product Width/Diameter/2nd Dimension, in yards", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("327", "Product Depth/Thickness/3rd Dimension, in inches", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("328", "Product Depth/Thickness/3rd Dimension, in feet", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("329", "Product Depth/Thickness/3rd Dimension, in yards", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("330", "Container Gross Weight (Kg)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("331", "Container Length/1st Dimension (Meters)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("332", "Container Width/Diameter/2nd Dimension (Meters)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("333", "Container Depth/Thickness/3rd Dimension (Meters)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("334", "Container Area (Square Meters)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("335", "Container Gross Volume (Liters)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("336", "Container Gross Volume (Cubic Meters)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("340", "Container Gross Weight (Pounds)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("341", "Container Length/1st Dimension, in inches", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("342", "Container Length/1st Dimension, in feet", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("343", "Container Length/1st Dimension in, in yards", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("344", "Container Width/Diamater/2nd Dimension, in inches", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("345", "Container Width/Diameter/2nd Dimension, in feet", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("346", "Container Width/Diameter/2nd Dimension, in yards", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("347", "Container Depth/Thickness/Height/3rd Dimension, in inches", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("348", "Container Depth/Thickness/Height/3rd Dimension, in feet", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("349", "Container Depth/Thickness/Height/3rd Dimension, in yards", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("350", "Product Area (Square Inches)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("351", "Product Area (Square Feet)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("352", "Product Area (Square Yards)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("353", "Container Area (Square Inches)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("354", "Container Area (Square Feet)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("355", "Container Area (Suqare Yards)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("356", "Net Weight (Troy Ounces)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("360", "Product Volume (Quarts)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("361", "Product Volume (Gallons)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("362", "Container Gross Volume (Quarts)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("363", "Container Gross Volume (Gallons)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("364", "Product Volume (Cubic Inches)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("365", "Product Volume (Cubic Feet)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("366", "Product Volume (Cubic Yards)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("367", "Container Gross Volume (Cubic Inches)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("368", "Container Gross Volume (Cubic Feet)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("369", "Container Gross Volume (Cubic Yards)", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("37", "Number of Units Contained", 2, "", 8, "", true));
        aiValues.add(new Gs1Model("400", "Customer Purchase Order Number", 3, "", 30, "", true));
        aiValues.add(new Gs1Model("410", "Ship To/Deliver To Location Code (EAN13 or DUNS code)t", 3, "", 13, "", false));
        aiValues.add(new Gs1Model("411", "Bill To/Invoice Location Code (EAN13 or DUNS code)", 3, "", 13, "", false));
        aiValues.add(new Gs1Model("412", "Purchase From Location Code (EAN13 or DUNS code)", 3, "", 13, "", false));
        aiValues.add(new Gs1Model("420", "Ship To/Deliver To Postal Code (Single Postal Authority)", 3, "", 20, "", true));
        aiValues.add(new Gs1Model("421", "Ship To/Deliver To Postal Code (Multiple Postal Authority)", 3, "", 12, "", true));
        aiValues.add(new Gs1Model("8001", "Roll Products – Width/Length/Core Diameter", 4, "", 14, "", false));
        aiValues.add(new Gs1Model("8002", "Electronic Serial Number (ESN) for Cellular Phone", 4, "", 20, "", true));
        aiValues.add(new Gs1Model("8003", "UPC/EAN Number and Serial Number of Returnable Asset", 4, "", 30, "", true));
        aiValues.add(new Gs1Model("8004", "UPC/EAN Serial Identification", 4, "", 30, "", true));
        aiValues.add(new Gs1Model("8005", "Price per Unit of Measure", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("8100", "Coupon Extended Code: Number System and Offer", 4, "", 6, "", false));
        aiValues.add(new Gs1Model("8101", "Coupon Extended Code: Number System, Offer, End of Offer", 4, "", 10, "", false));
        aiValues.add(new Gs1Model("8102", "Coupon Extended Code: Number System preceded by 0", 4, "", 2, "", false));
        aiValues.add(new Gs1Model("90", "Mutually Agreed Between Trading Partners", 2, "", 30, "", true));
    }
}
