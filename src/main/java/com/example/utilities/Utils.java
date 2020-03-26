package com.example.utilities;

import oracle.security.misc.Checksum;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class Utils {

    public static String comingIn(String value) {
        try {
            byte[] enc = Utils.RepConversion.convertHexStringToByte(value);
            byte[] decPwd = Checksum.SHA(enc, null);
            return Utils.Text.toString(decPwd);
        } catch (Throwable var3) {
            System.out.println(var3.getLocalizedMessage());
            return null;
        }
    }

    public static String goingOut(String value) {
        try {
            byte[] b = Utils.Text.getBytes(value);
            byte[] b2 = Checksum.MD5(b, null);
            return Utils.RepConversion.bArray2String(b2);
        } catch (Throwable var3) {
            System.out.println(var3.getLocalizedMessage());
            return null;
        }
    }

    private abstract static class RepConversion {
        private RepConversion() {
        }

        private static byte nibbleToHex(byte nibble) {
            nibble = (byte) (nibble & 15);
            return (byte) (nibble < 10 ? nibble + 48 : nibble - 10 + 65);
        }

        private static String bArray2String(byte[] array) {
            StringBuilder result = new StringBuilder(array.length * 2);

            for (byte b : array) {
                result.append((char) nibbleToHex((byte) ((b & 240) >> 4)));
                result.append((char) nibbleToHex((byte) (b & 15)));
            }

            return result.toString();
        }

        private static int convertCharToInt(char charVal) {
            switch (charVal) {
                case '0':
                    return 0;
                case '1':
                    return 1;
                case '2':
                    return 2;
                case '3':
                    return 3;
                case '4':
                    return 4;
                case '5':
                    return 5;
                case '6':
                    return 6;
                case '7':
                    return 7;
                case '8':
                    return 8;
                case '9':
                    return 9;
                case ':':
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                default:
                    return 48;
                case 'A':
                case 'a':
                    return 10;
                case 'B':
                case 'b':
                    return 11;
                case 'C':
                case 'c':
                    return 12;
                case 'D':
                case 'd':
                    return 13;
                case 'E':
                case 'e':
                    return 14;
                case 'F':
                case 'f':
                    return 15;
            }
        }

        private static byte[] convertHexStringToByte(String refString) {
            byte[] byteArray = new byte[refString.length() / 2];

            for (int i = 0; i < refString.length(); i += 2) {
                int intVal = convertCharToInt(refString.charAt(i));
                int newInt = 240 & intVal << 4;
                byteArray[i / 2] = (byte) newInt;
                intVal = convertCharToInt(refString.charAt(i + 1));
                newInt = 15 & intVal;
                byteArray[i / 2] |= (byte) newInt;
            }

            return byteArray;
        }
    }

    private abstract static class Text {
        private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        private Text() {
        }

        private static String toString(byte[] bytes) {
            return new String(bytes, DEFAULT_CHARSET);
        }

        private static byte[] getBytes(String text) {
            return text.getBytes(DEFAULT_CHARSET);
        }
    }
}

