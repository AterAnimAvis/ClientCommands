package clientcommands.common;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import clientcommands.R;

public interface Markers {

    /* ===================================================================================================== API ==== */

    Marker ROOT    = Internal.marker();
    Marker LAUNCH  = Internal.marker("Launch");
    Marker COMMAND = Internal.marker("ClientCommand");

    /* ================================================================================================ Internal ==== */

    class Internal {

        @Deprecated
        protected static Marker marker() {
            return MarkerManager.getMarker(R.NAME);
        }

        @Deprecated
        protected static Marker marker(String name) {
            return MarkerManager.getMarker(R.NAME + "-" + name).addParents(ROOT);
        }

        @Deprecated
        protected static Marker marker(String name, Marker parent) {
            return MarkerManager.getMarker(R.NAME + "-" + name).addParents(parent);
        }

    }

}
