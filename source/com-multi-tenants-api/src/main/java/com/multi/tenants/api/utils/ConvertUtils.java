package com.multi.tenants.api.utils;

public class ConvertUtils {

    private ConvertUtils(){

    }

    public static Long convertStringToLong(String input){
        try {
            return  Long.parseLong(input);
        }catch (Exception e){
            return  Long.valueOf(0);
        }
    }

    public static int convertToCent(double b){
        int i=(int)(b);
        double k = b-(double)i;
        if(k>0.5 && k<1){
            i+=1;
        }
        return i;
    }

    public static Object parseValue(String dataType, String valueData) {
        if (valueData == null || dataType == null) {
            return null;
        }

        try {
            switch (dataType.toLowerCase()) {
                case "integer":
                    return Integer.parseInt(valueData);
                case "double":
                    return Double.parseDouble(valueData);
                case "boolean":
                    return Boolean.parseBoolean(valueData);
                case "string":
                    return valueData;
                default:
                    throw new IllegalArgumentException("Unsupported dataType: " + dataType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing valueData: \"" + valueData + "\" as " + dataType, e);
        }
    }
}
