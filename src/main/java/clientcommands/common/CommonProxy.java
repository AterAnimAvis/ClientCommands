package clientcommands.common;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.apache.commons.lang3.tuple.Pair;

public class CommonProxy {

    public void init() {

        /* Register Display Test */
        registerDisplayTest(ModLoadingContext.get());
    }

    /* ================================================================================================= Utility ==== */

    /**
     * Registers a {@link ExtensionPoint#DISPLAYTEST} extension point so we don't cause Forge to show a incompatibility
     * warning with servers who don't have the mod installed. <br/> Thanks <a href="https://github.com/phit">@phit</a>
     *
     * @see <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7209">MinecraftForge#7209</a>
     */
    protected void registerDisplayTest(ModLoadingContext context) {
        /* The existence of this mod doesn't actually matter so return IGNORESERVERONLY */
        Supplier<String> getVersion = () -> FMLNetworkConstants.IGNORESERVERONLY;
        BiPredicate<String, Boolean> testVersion = (remote, isServer) -> true;

        context.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(getVersion, testVersion));
    }

    protected void registerEventListener(Object target) {
        MinecraftForge.EVENT_BUS.register(target);
    }

    protected void registerModEventListener(Object target) {
        FMLJavaModLoadingContext.get().getModEventBus().register(target);
    }

}
