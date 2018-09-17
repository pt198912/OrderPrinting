package am.example.printer.data;

import android.util.Log;

import java.io.IOException;

import am.util.printer.PrintCommands;
import am.util.printer.PrinterWriter;

public class CustomPrintWriter extends PrinterWriter {
    public CustomPrintWriter() throws IOException {
    }
    private int width=380;
    public CustomPrintWriter(int parting,int width) throws IOException {
        super(parting);
        this.width=width;
    }

    @Override
    protected int getLineWidth() {
        return 16;
    }

    @Override
    protected int getLineStringWidth(int textSize) {
        switch (textSize) {
            default:
            case 0:
                return 31;
            case 1:
                return 15;
        }
    }

    @Override
    protected int getDrawableMaxWidth() {
        return  width;
    }

    @Override
    public void setFontSize(int size) throws IOException {
        write(fontSizeSetBig(size));
    }
    public static byte[] fontSizeSetBig(int num) {
        byte realSize = 0;
        switch (num) {
            case 0:
                realSize = 0;
                Log.d("pengtao", "fontSizeSetBig: 0");
                break;
            case 1:
                realSize = 8;
                Log.d("pengtao", "fontSizeSetBig: 12");
                break;
            case 2:
                realSize = 16;
                break;
            case 3:
                realSize = 24;
                break;
            case 4:
                realSize = 32;
                break;
            case 5:
                realSize = 40;
                break;
            case 6:
                realSize = 48;
                break;
            case 7:
                realSize = 56;
                break;
        }
        return selectCharacterSize(realSize);
    }

    public static byte[] selectCharacterSize(int n) {
        return new byte[]{29, 15, (byte) n};
    }

}
