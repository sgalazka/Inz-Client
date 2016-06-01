package pl.edu.pw.sgalazka.client.utils;

import android.app.Activity;
import android.widget.EditText;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import pl.edu.pw.sgalazka.client.BuildConfig;
import pl.edu.pw.sgalazka.client.R;
import pl.edu.pw.sgalazka.client.activities.AddToDatabase;
import pl.edu.pw.sgalazka.client.activities.ScanProduct;
import pl.edu.pw.sgalazka.client.activities.productList.ProductListChooser;

/**
 * Created by gałązka on 2016-05-06.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class UtilsTest {

    private final String EMPTY = "";
    private final String TOO_MUCH_CASES = "text_with_too_much_cases";
    private final String TOO_LONG_BARCODE = "762230014504000123456789";
    private final String WRONG_CHKSUM_BARCODE = "7622300145049";    //the last should be 0
    private final String TOO_HIGH_PRICE = "1999999";
    private final String TOO_MUCH_COMA_PLACES = "1999.999";



    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCheckName_empty() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabaseName);
        editText.setText(EMPTY);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkName(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_name_empty)));
    }

    @Test
    public void testCheckName_tooMuchCases() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabaseName);
        editText.setText(TOO_MUCH_CASES);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkName(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_name_too_much_cases)));
    }

    @Test
    public void testCheckBarcode_wrongLengthIsCut() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabaseBarcode);
        editText.setText(TOO_LONG_BARCODE);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkBarcode(editText, activity);
        Assert.assertTrue(ret);
    }

    @Test
    public void testCheckBarcode_empty() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabaseBarcode);
        editText.setText(EMPTY);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkBarcode(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_barcode_empty)));
    }

    @Test
    public void testCheckBarcode_wrong() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabaseBarcode);
        editText.setText(WRONG_CHKSUM_BARCODE);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkBarcode(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_barcode_wrong)));
    }

    @Test
    public void testCheckPrice_empty() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabase_price);
        editText.setText(EMPTY);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkPrice(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_price_empty)));
    }

    @Test
    public void testCheckPrice_tooHigh() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabase_price);
        editText.setText(TOO_HIGH_PRICE);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkPrice(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_price_too_high)));
    }

    @Test
    public void testCheckPrice_tooMuchComaPlaces() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabase_price);
        editText.setText(TOO_MUCH_COMA_PLACES);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkPrice(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_price_coma_places)));
    }

    @Test
    public void testCheckAmount_empty() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabaseAmount);
        editText.setText(EMPTY);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkAmount(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_amount_empty)));
    }

    @Test
    public void testCheckAmount_tooHigh() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        EditText editText = (EditText) activity.findViewById(R.id.addToDatabaseAmount);
        editText.setText(TOO_HIGH_PRICE);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkAmount(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_amount_too_high)));
    }

    /*@Test
    public void testCheckVat_empty() throws Exception {
        Activity activity = Robolectric.setupActivity(AddToDatabase.class);
        Spinner spinner = (Spinner) activity.findViewById(R.id.addToDatabase_vat_spinner);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkVat(spinner, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_amount_too_high)));

    }*/

    @Test
    public void testCheckQuantity_empty() throws Exception {
        Activity activity = Robolectric.setupActivity(ScanProduct.class);
        EditText editText = (EditText) activity.findViewById(R.id.scan_product_amount);
        editText.setText(EMPTY);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkQuantity(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_quantity_empty)));
    }

    @Test
    public void testCheckQuantity_tooHigh() throws Exception {
        Activity activity = Robolectric.setupActivity(ScanProduct.class);
        EditText editText = (EditText) activity.findViewById(R.id.scan_product_amount);
        editText.setText(TOO_HIGH_PRICE);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkQuantity(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_quantity_too_high)));
    }

    @Test
    public void testCheckQuantityProductList() throws Exception {
        Activity activity = Robolectric.setupActivity(ProductListChooser.class);
        EditText editText = (EditText) activity.findViewById(R.id.plchooser_quantity);
        editText.setText(EMPTY);

        ShadowLooper.idleMainLooper(100);
        boolean ret = Utils.checkQuantityProductList(editText, activity);
        Assert.assertFalse(ret);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(), IsEqual.equalTo(activity.getString(R.string.toast_amount_empty)));
    }

    @Test
    public void testShowInformDialog() throws Exception {

    }

    @Test
    public void testShowRetryDialog() throws Exception {

    }
}