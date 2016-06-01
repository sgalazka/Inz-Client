package pl.edu.pw.sgalazka.client.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by gałązka on 2016-05-09.
 */
public class EAN13CheckDigitTest {

    private final String EMPTY = "";
    private final String NOT_ENOUGH_CASES= "590333111";
    private final String TOO_MUCH_CASES= "590333111333111";
    private final String WRONG_CHKSUM= "7622300145049";
    private final String GOOD_CHKSUM= "7622300145049";

    @Test
    public void testCheckBarcode_empty() throws Exception {
        assertFalse(EAN13CheckDigit.checkBarcode(EMPTY));
    }

    @Test
    public void testCheckBarcode_notEnoughCases() throws Exception {
        assertFalse(EAN13CheckDigit.checkBarcode(NOT_ENOUGH_CASES));
    }

    @Test
    public void testCheckBarcode_tooMuchCases() throws Exception {
        assertFalse(EAN13CheckDigit.checkBarcode(TOO_MUCH_CASES));
    }

    @Test
    public void testCheckBarcode_wrongCheckSum() throws Exception {
        assertFalse(EAN13CheckDigit.checkBarcode(WRONG_CHKSUM));
    }

    @Test
    public void testCheckBarcode_goodCheckSum() throws Exception {
        assertFalse(EAN13CheckDigit.checkBarcode(GOOD_CHKSUM));
    }
}