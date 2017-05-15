package com.jaykallen.barcodegs1;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

/** Created by Jay Kallen on 4/13/17.  Junit / Robolectric testing for GS1128 barcode.
 *  This will test several variations of the GS1128 barcode using different cases such as variable
 *  length fields, etc.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class Gs1128UnitTest {
    @Test
    public void testUpcOnly() {
        List<Gs1Model> answer = Gs1Parser.read("0100000000427237");
        Assert.assertEquals("not equal", "00000000427237", answer.get(0).getDataBarcode());
    }
    @Test
    public void test2FixedLenFieldsSimple() {
        // Kraft Miracle Whip Dr.
        // (01)00021000663712(20)00
        List<Gs1Model> answer = Gs1Parser.read("01000210006637122000");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "00021000663712", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "20", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "00", answer.get(1).getDataBarcode());
    }
    @Test
    public void test2FixedLenFields() {
        // Homestyle White Chip Macadamia
        // (01)00086478342378(10)122401003423701
        List<Gs1Model> answer = Gs1Parser.read("010008647834237810122401003423701");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "00086478342378", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "10", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "122401003423701", answer.get(1).getDataBarcode());
    }
    @Test
    public void test3FixedLenFields() {
        // BW Bacon Egg Cheese Biscuit, 2.7 lbs
        // (01)10077900513623(3202)000270(13)120711
        List<Gs1Model> answer = Gs1Parser.read("0110077900513623320200027013120711");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "10077900513623", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "3202", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "000270", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "13", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "120711", answer.get(2).getDataBarcode());
    }
    @Test
    public void test3DiffFixedLenFields() {
        // Jelly Asst #3, Mfg Date 9/10/12
        // (01)00716037948016(13)120910(240)302A
        List<Gs1Model> answer = Gs1Parser.read("010071603794801613120910240302A");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "00716037948016", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "13", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "120910", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "240", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "302A", answer.get(2).getDataBarcode());
    }
    @Test
    public void test4FixedLenFields() {
        // Beef Back Ribs, 7.8 lbs, Exp: 4/27/17, Lot# j12345
        // (01)00000000427237(3202)000780(17)170427(10)123456
        List<Gs1Model> answer = Gs1Parser.read("010000000042723732020007801717042710123456");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "00000000427237", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "3202", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "000780", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "17", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "170427", answer.get(2).getDataBarcode());
        Assert.assertEquals("not equal", "10", answer.get(3).getAiBarcode());
        Assert.assertEquals("not equal", "123456", answer.get(3).getDataBarcode());
    }
    @Test
    public void test4FixedLenFieldsSimple() {
        // Pepper Jack, 12.00 lbs, Sell By: 02/27/13
        // (01)10025011291138(15)130227(3202)001200(21)604224465326
        List<Gs1Model> answer = Gs1Parser.read("011002501129113815130227320200120021604224465326");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "10025011291138", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "15", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "130227", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "3202", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "001200", answer.get(2).getDataBarcode());
        Assert.assertEquals("not equal", "21", answer.get(3).getAiBarcode());
        Assert.assertEquals("not equal", "604224465326", answer.get(3).getDataBarcode());
    }
    @Test
    public void test4FixedAndShortField() {
        // Skippy Creamy Honeynut Peanut Butter, 12.23 lbs, Best if used by Jul 13, 2017
        // (01)10037600006771(3202)001223(15)170713(21)611092211569
        List<Gs1Model> answer = Gs1Parser.read("011003760000677132020012231517071321611092211569");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "10037600006771", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "3202", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "001223", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "15", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "170713", answer.get(2).getDataBarcode());
        Assert.assertEquals("not equal", "21", answer.get(3).getAiBarcode());
        Assert.assertEquals("not equal", "611092211569", answer.get(3).getDataBarcode());
    }
    @Test
    public void test2VarAnd1FixedLenField() {
        List<Gs1Model> answer = Gs1Parser.read("010000000042723710123456|21620275372440|3202000780");
        // Beef Back Ribs, 7.8 lbs, Lot# 123456, Serial # 620275372440
        // (01)00000000427237(10)123456(21)620275372440(3202)000780
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "00000000427237", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "10", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "123456", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "21", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "620275372440", answer.get(2).getDataBarcode());
        Assert.assertEquals("not equal", "3202", answer.get(3).getAiBarcode());
        Assert.assertEquals("not equal", "000780", answer.get(3).getDataBarcode());
    }
    @Test
    public void test1FixedEndIn2VarFields() {
        // Beef Back Ribs, 7.8 lbs, Lot# 123456, Serial # 620275372440
        // (01)00000000427237(3202)000780(10)123456(21)620275372440
        List<Gs1Model> answer = Gs1Parser.read("0100000000427237320200078010123456|21620275372440");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "00000000427237", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "3202", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "000780", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "10", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "123456", answer.get(2).getDataBarcode());
        Assert.assertEquals("not equal", "21", answer.get(3).getAiBarcode());
        Assert.assertEquals("not equal", "620275372440", answer.get(3).getDataBarcode());
    }
    @Test
    public void test3VarFields() {
        // Made up barcode
        // (01)00000000427237(3202)000780(10)123456(21)620275372440(240)302A
        List<Gs1Model> answer = Gs1Parser.read("0100000000427237320200078010123456|21620275372440|240302A");
        Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
        Assert.assertEquals("not equal", "00000000427237", answer.get(0).getDataBarcode());
        Assert.assertEquals("not equal", "3202", answer.get(1).getAiBarcode());
        Assert.assertEquals("not equal", "000780", answer.get(1).getDataBarcode());
        Assert.assertEquals("not equal", "10", answer.get(2).getAiBarcode());
        Assert.assertEquals("not equal", "123456", answer.get(2).getDataBarcode());
        Assert.assertEquals("not equal", "21", answer.get(3).getAiBarcode());
        Assert.assertEquals("not equal", "620275372440", answer.get(3).getDataBarcode());
        Assert.assertEquals("not equal", "240", answer.get(4).getAiBarcode());
        Assert.assertEquals("not equal", "302A", answer.get(4).getDataBarcode());
    }
    @Test
    public void testMalformedGs1() {
        // This should bring back nothing.
        List<Gs1Model> answer = Gs1Parser.read("01000374660472941517301110L1536");
        if (!answer.isEmpty()) {
            Assert.assertEquals("not equal", "01", answer.get(0).getAiBarcode());
            Assert.assertEquals("not equal", "00037466047294", answer.get(0).getDataBarcode());
            Assert.assertEquals("not equal", "15", answer.get(1).getAiBarcode());
            Assert.assertEquals("not equal", "173011", answer.get(1).getDataBarcode());
            Assert.assertEquals("not equal", "10", answer.get(2).getAiBarcode());
            Assert.assertEquals("not equal", "L1536", answer.get(2).getDataBarcode());
        }
    }
    @Test
    public void testBareUpc() {
        // This should bring back nothing.
        List<Gs1Model> answer = Gs1Parser.read("874411000092");
        if (!answer.isEmpty()) {
            Assert.assertEquals("not equal", "", answer.get(0).getDataBarcode());
        }
    }
    @Test
    public void testItemNumber() {
        // This should bring back nothing.
        List<Gs1Model> answer = Gs1Parser.read("311050");
        if (!answer.isEmpty()) {
            Assert.assertEquals("not equal", "", answer.get(0).getDataBarcode());
        }
    }
    @Test
    public void testRandomTrash1() {
        // This should bring back nothing.
        List<Gs1Model> answer = Gs1Parser.read("2345645982735982473524739852398579859273598273");
        if (!answer.isEmpty()) {
            Assert.assertEquals("not equal", "", answer.get(0).getDataBarcode());
        }
    }
    @Test
    public void testRandomTrash2() {
        // This should bring back nothing.
        List<Gs1Model> answer = Gs1Parser.read("93479847356973596798567987369847369879836984");
        if (!answer.isEmpty()) {
            Assert.assertEquals("not equal", "", answer.get(0).getDataBarcode());
        }
    }
    @Test
    public void testRandomTrash3() {
        // This should bring back nothing.
        List<Gs1Model> answer = Gs1Parser.read("725920967209679243769082437601761765464356436");
        if (!answer.isEmpty()) {
            Assert.assertEquals("not equal", "", answer.get(0).getDataBarcode());
        }
    }

}