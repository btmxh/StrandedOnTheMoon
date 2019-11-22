/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import com.lwjglwrapper.utils.math.MathUtils;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Welcome
 */
public class Utils {
    public static String toVariableNameString(String string) {
        //Miner Robot -> MINER_ROBOT
        return String.join("_", Stream.of(string.split("\\s+")).map(String::toUpperCase).toArray(String[]::new));
    }
    
    public static String toReadableString(String string) {
        //MINER_ROBOT -> Miner Robot
        String[] words = string.split("_");
        for (int i = 0; i < words.length; i++) {
            if(words[i].length() <= 1)  continue;
            words[i] = words[i].toLowerCase();
            words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
        }
        return String.join(" ", words);
    }
    
    public static String join(String delimiter, int start, int end, String... strings) {
        if(start < 0)   return join(delimiter, 0, end, strings);
        else if(end < 0)    return join(delimiter, start, strings.length - 1, strings);
        
        String[] ranged = Arrays.copyOfRange(strings, start, end + 1);
        return String.join(delimiter, ranged);
    }
    
    public static void random(double chance, Runnable exec) {
        double total = 0;
        while((total += Math.random()) < chance) {
            exec.run();
        }
    }
    
    public static <T> boolean equals(T o1, T o2, Function<T, ?>... getters) {
        if(o1 == null)  return o2 == null;
        if(o2 == null)  return false;
        for (Function<T, ?> getter : getters) {
            Object t1 = getter.apply(o1);
            Object t2 = getter.apply(o2);
            if(!Objects.equals(t1, t2))  return false;
        }
        return true;
    }
    
    public static <T> Function<T, ?> getter(Class<T> c, String fieldName) {
        try {
            Field field = c.getField(fieldName);
            return (obj) -> {
                try {
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            };
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static float ifNull(Float val, float ifNull) {
        return val == null? ifNull:val;
    }
    
    public static int ifNull(Integer val, int ifNull) {
        return val == null? ifNull:val;
    }
    
    public static void cosi(Float v) {
        System.out.println(ifNull(v, 420.0f));
    }
    
    public static String percentage(float f, float start, float end, int fractionDigits) {
        float percentage = MathUtils.map(f, start, end, 0f, 100f);
        StringBuilder pattern = new StringBuilder("#.");
        for(int i = 0; i < fractionDigits; i++) pattern.append("#");
        DecimalFormat format = new DecimalFormat(pattern.toString(), new DecimalFormatSymbols(Locale.US));
        format.setDecimalSeparatorAlwaysShown(true);
        format.setMinimumFractionDigits(fractionDigits);
        return format.format(percentage) + "%";
    }   
    
    public static void main(String[] args) {
        cosi(null);
        cosi(69f);
    }
}
