package clientcommands.server;

import clientcommands.ClientCommandsTest;
import clientcommands.common.CommonProxy;
import clientcommands.common.Markers;

public class ServerProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();

        ClientCommandsTest.log.error(Markers.LAUNCH, "Detected Server Environment");
    }

}
