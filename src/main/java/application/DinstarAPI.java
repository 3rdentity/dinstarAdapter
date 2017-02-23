package application;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

/**
 * Created by santiago.barandiaran on 23/2/2017.
 */
public interface DinstarAPI extends Library {
    String JNA_LIBRARY_NAME = "libdwgsms";
    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(DinstarAPI.JNA_LIBRARY_NAME);
    DinstarAPI INSTANCE = (DinstarAPI) Native.loadLibrary(DinstarAPI.JNA_LIBRARY_NAME, DinstarAPI.class);

    /**
     * Original signature : <code>void print_something(const char*)</code><br>
     * <i>native declaration : dwg.h:209</i>
     */
    void print_something(String str);
    void dwg_stop_server();

}
