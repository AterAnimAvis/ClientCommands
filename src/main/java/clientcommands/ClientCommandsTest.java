package clientcommands;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import clientcommands.common.Markers;
import clientcommands.client.ClientProxy;
import clientcommands.common.CommonProxy;
import clientcommands.server.ServerProxy;

@Mod(R.ID)
public class ClientCommandsTest {

    public static CommonProxy proxy;
    public static Logger      log = LogManager.getLogger(R.NAME);

    public ClientCommandsTest() {
        log.info(Markers.LAUNCH, "Initializing");

        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        proxy.init();

        log.info(Markers.LAUNCH, "Initialized");
    }

}
