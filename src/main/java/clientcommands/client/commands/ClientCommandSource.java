package clientcommands.client.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mcp.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClientCommandSource implements ISuggestionProvider {

    private final ICommandSource                source;
    private final Vector3d                      pos;
    private final ClientWorld                   world;
    private final int                           permissionLevel;
    private final String                        name;
    private final ITextComponent                displayName;
    private final boolean                       feedbackDisabled;
    @Nullable
    private final Entity                        entity;
    private final ResultConsumer<CommandSource> resultConsumer;
    private final EntityAnchorArgument.Type     entityAnchorType;
    private final Vector2f                      rotation;

    public ClientCommandSource(ClientPlayerEntity player) {
        this(
            player,
            player.getPositionVec(),
            player.getPitchYaw(),
            player.worldClient,
            player.permissionLevel,
            player.getName().getString(),
            player.getDisplayName(),
            player
        );
    }

    public ClientCommandSource(ICommandSource sourceIn, Vector3d posIn, Vector2f rotationIn, ClientWorld worldIn,
                               int permissionLevelIn, String nameIn, ITextComponent displayNameIn,
                               @Nullable Entity entityIn) {
        this(sourceIn, posIn, rotationIn, worldIn, permissionLevelIn, nameIn, displayNameIn, entityIn, false,
             (p_197032_0_, p_197032_1_, p_197032_2_) -> {
             }, EntityAnchorArgument.Type.FEET);
    }

    protected ClientCommandSource(ICommandSource sourceIn, Vector3d posIn, Vector2f rotationIn, ClientWorld worldIn,
                                  int permissionLevelIn, String nameIn, ITextComponent displayNameIn,
                                  @Nullable Entity entityIn, boolean feedbackDisabledIn,
                                  ResultConsumer<CommandSource> resultConsumerIn,
                                  EntityAnchorArgument.Type entityAnchorTypeIn) {
        this.source = sourceIn;
        this.pos = posIn;
        this.world = worldIn;
        this.feedbackDisabled = feedbackDisabledIn;
        this.entity = entityIn;
        this.permissionLevel = permissionLevelIn;
        this.name = nameIn;
        this.displayName = displayNameIn;
        this.resultConsumer = resultConsumerIn;
        this.entityAnchorType = entityAnchorTypeIn;
        this.rotation = rotationIn;
    }

    /* ================================================================================================= Builder ==== */

    public ClientCommandSource withEntity(Entity entityIn) {
        return entity == entityIn ? this
                                  : new ClientCommandSource(source, pos, rotation, world,
                                                            permissionLevel, entityIn.getName().getString(),
                                                            entityIn.getDisplayName(), entityIn,
                                                            feedbackDisabled, resultConsumer,
                                                            entityAnchorType);
    }

    public ClientCommandSource withPos(Vector3d posIn) {
        return pos.equals(posIn) ? this : new ClientCommandSource(source, posIn, rotation, world,
                                                                  permissionLevel, name,
                                                                  displayName, entity,
                                                                  feedbackDisabled, resultConsumer,
                                                                  entityAnchorType);
    }

    public ClientCommandSource withRotation(Vector2f pitchYawIn) {
        return rotation.equals(pitchYawIn) ? this : new ClientCommandSource(source, pos, pitchYawIn, world,
                                                                            permissionLevel, name,
                                                                            displayName, entity,
                                                                            feedbackDisabled, resultConsumer,
                                                                            entityAnchorType);
    }

    public ClientCommandSource withResultConsumer(ResultConsumer<CommandSource> resultConsumerIn) {
        return resultConsumer.equals(resultConsumerIn) ? this : new ClientCommandSource(source, pos,
                                                                                        rotation, world,
                                                                                        permissionLevel,
                                                                                        name,
                                                                                        displayName,
                                                                                        entity,
                                                                                        feedbackDisabled,
                                                                                        resultConsumerIn,
                                                                                        entityAnchorType);
    }

    public ClientCommandSource withResultConsumer(ResultConsumer<CommandSource> resultConsumerIn,
                                                  BinaryOperator<ResultConsumer<CommandSource>> selector) {
        ResultConsumer<CommandSource> result = selector.apply(resultConsumer, resultConsumerIn);
        return withResultConsumer(result);
    }

    public ClientCommandSource withFeedbackDisabled() {
        return feedbackDisabled ? this : new ClientCommandSource(source, pos, rotation, world,
                                                                 permissionLevel, name, displayName,
                                                                 entity, true,
                                                                 resultConsumer, entityAnchorType);
    }

    public ClientCommandSource withPermissionLevel(int level) {
        return level == permissionLevel ? this
                                        : new ClientCommandSource(source, pos, rotation, world,
                                                                  level, name, displayName, entity, feedbackDisabled,
                                                                  resultConsumer, entityAnchorType);
    }

    public ClientCommandSource withMinPermissionLevel(int level) {
        return level <= permissionLevel ? this
                                        : new ClientCommandSource(source, pos, rotation, world,
                                                                  level, name, displayName, entity, feedbackDisabled,
                                                                  resultConsumer, entityAnchorType);
    }

    public ClientCommandSource withEntityAnchorType(EntityAnchorArgument.Type entityAnchorTypeIn) {
        return entityAnchorTypeIn == entityAnchorType ? this : new ClientCommandSource(source, pos,
                                                                                       rotation, world,
                                                                                       permissionLevel,
                                                                                       name, displayName,
                                                                                       entity,
                                                                                       feedbackDisabled,
                                                                                       resultConsumer,
                                                                                       entityAnchorTypeIn);
    }

    public ClientCommandSource withWorld(ClientWorld worldIn) {
        return worldIn == world ? this : new ClientCommandSource(source, pos, rotation, worldIn,
                                                                 permissionLevel, name, displayName,
                                                                 entity, feedbackDisabled,
                                                                 resultConsumer, entityAnchorType);
    }

    public ClientCommandSource withRotation(Entity entityIn, EntityAnchorArgument.Type anchorType) {
        return withRotation(anchorType.apply(entityIn));
    }

    public ClientCommandSource withRotation(Vector3d lookPos) {
        Vector3d vec3d = applyAnchor(entityAnchorType);
        double d0 = lookPos.x - vec3d.x;
        double d1 = lookPos.y - vec3d.y;
        double d2 = lookPos.z - vec3d.z;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3) * (double) (180F / (float) Math.PI))));
        float f1 =
            MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F);

        return withRotation(new Vector2f(f, f1));
    }

    public Vector3d applyAnchor(EntityAnchorArgument.Type type) {
        if (entity == null) return pos;

        switch (type) {
            case EYES:
                return new Vector3d(pos.x, pos.y + (double) entity.getEyeHeight(), pos.z);
            case FEET:
                return pos;
            default:
                throw new IllegalStateException("UnExhaustive EntityAnchorArgument.Type " + type);
        }
    }

    /* ================================================================================================= Getters ==== */

    public ITextComponent getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public boolean hasPermissionLevel(int level) {
        return permissionLevel >= level;
    }

    public Vector3d getPos() {
        return pos;
    }

    public ClientWorld getWorld() {
        return world;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    public Entity assertIsEntity() throws CommandSyntaxException {
        if (entity == null) throw CommandSource.REQUIRES_ENTITY_EXCEPTION_TYPE.create();

        return entity;
    }

    public ClientPlayerEntity asPlayer() throws CommandSyntaxException {
        if (entity instanceof ClientPlayerEntity) return (ClientPlayerEntity) entity;

        throw CommandSource.REQUIRES_PLAYER_EXCEPTION_TYPE.create();
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public EntityAnchorArgument.Type getEntityAnchorType() {
        return entityAnchorType;
    }

    /* ================================================================================================ Feedback ==== */

    public void sendFeedback(ITextComponent message) {
        sendFeedback(message, false);
    }

    public void sendFeedback(ITextComponent message, boolean allowLogging) {
        if (source.shouldReceiveFeedback() && !feedbackDisabled)
            source.sendMessage(message, Util.field_240973_b_ /* DUMMY_UUID */);

        if (allowLogging && source.allowLogging() && !feedbackDisabled) logFeedback(message);
    }

    /**
     * @see CommandSource#logFeedback
     */
    private void logFeedback(ITextComponent message) {
        // NO-OP
    }

    public void sendErrorMessage(ITextComponent message) {
        if (source.shouldReceiveErrors() && !feedbackDisabled) {
            IFormattableTextComponent feedback = new StringTextComponent("")
                .func_230529_a_/* appendSibling */(message)
                .func_240699_a_/* applyTextStyle */(TextFormatting.RED);
            source.sendMessage(feedback, Util.field_240973_b_ /* DUMMY_UUID */);
        }
    }

    public void onCommandComplete(CommandContext<CommandSource> context, boolean success, int result) {
        if (resultConsumer != null) resultConsumer.onCommandComplete(context, success, result);
    }

    /* ===================================================================================== ISuggestionProvider ==== */

    @Nullable
    public ClientPlayNetHandler getConnection() {
        return Minecraft.getInstance().getConnection();
    }

    public Collection<String> getPlayerNames() {
        ClientPlayNetHandler connection = getConnection();

        if (connection == null) return Collections.emptyList();

        return connection.getPlayerInfoMap()
                         .stream()
                         .map(NetworkPlayerInfo::getGameProfile)
                         .map(GameProfile::getName)
                         .collect(Collectors.toList());
    }

    public Collection<String> getTeamNames() {
        return world.getScoreboard().getTeamNames();
    }

    public Collection<ResourceLocation> getSoundResourceLocations() {
        return ForgeRegistries.SOUND_EVENTS.getKeys();
    }

    public Stream<ResourceLocation> getRecipeResourceLocations() {
        ClientPlayNetHandler connection = getConnection();
        if (connection == null) return Stream.empty();
        return connection.getRecipeManager().getKeys();
    }

    public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> context,
                                                                   SuggestionsBuilder suggestionsBuilder) {
        return Suggestions.empty();
    }

    @Override
    public Set<RegistryKey<World>> func_230390_p_() {
        ClientPlayNetHandler connection = getConnection();
        if (connection == null) return Collections.emptySet();
        return connection.func_239164_m_();
    }

    @Override
    public DynamicRegistries func_241861_q() {
        ClientPlayNetHandler connection = getConnection();
        if (connection == null) return DynamicRegistries.func_239770_b_();
        return connection.func_239165_n_();
    }

}
