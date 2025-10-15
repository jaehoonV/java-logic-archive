package utils;

import java.util.function.Supplier;

public class StringsUtils {
    public static boolean isEmpty( String string ) {
        return( string == null || "".equals( string ) );
    }

    public static boolean isEmptyTrimmed( String string ) {
        return( string == null || "".equals( string.trim() ) );
    }

    public static boolean hasText( String string ) {
        return !isEmptyTrimmed( string );
    }

    public static boolean hasText( Object string ) {
        if( string == null ) return false;
        return hasText( string.toString() );
    }

    public static boolean isBlank( String string ) {
        return isEmptyTrimmed( string );
    }

    public static boolean isBlank( Object string ) {
        if( string == null ) return true;
        return isBlank( string.toString() );
    }

    public static String defaultValue( String string, Supplier<String> other ) {
        if( isEmpty( string ) ) {
            return other.get();
        } else {
            return string;
        }
    }

    public static String defaultValue( Object string, Supplier<String> other ) {
        if(string == null){
            return other.get();
        }
        return defaultValue( string.toString(), other );
    }

    public static String defaultValue( Object string, String defaultValue ) {
        return defaultValue( string, () -> defaultValue );
    }
}
