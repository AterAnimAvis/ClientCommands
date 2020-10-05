package clientcommands.client.commands.arguments.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import clientcommands.client.commands.ClientCommandSource;

/**
 * Client Counterpart to {@link EntitySelector}
 */
public class ClientEntitySelector {

    private final int                                          limit;
    private final boolean                                      includeNonPlayers;
    private final boolean                                      currentWorldOnly;
    private final Predicate<Entity>                            filter;
    private final MinMaxBounds.FloatBound                      distance;
    private final Function<Vector3d, Vector3d>                 positionGetter;
    @Nullable
    private final AxisAlignedBB                                aabb;
    private final BiConsumer<Vector3d, List<? extends Entity>> sorter;
    private final boolean                                      self;
    @Nullable
    private final String                                       username;
    @Nullable
    private final UUID                                         uuid;
    @Nullable
    private final EntityType<?>                                type;
    private final boolean                                      checkPermission;

    public ClientEntitySelector(EntitySelector selector) {
        this(
            selector.getLimit(),
            selector.includesEntities(),
            selector.isWorldLimited(),
            selector.filter,
            selector.distance,
            selector.positionGetter,
            selector.aabb,
            selector.sorter,
            selector.isSelfSelector(),
            selector.username,
            selector.uuid,
            selector.type,
            selector.checkPermission
        );
    }

    public ClientEntitySelector(int limit, boolean includeEntities, boolean currentWorld,
                                Predicate<Entity> filter, MinMaxBounds.FloatBound distance,
                                Function<Vector3d, Vector3d> positionGetter, @Nullable AxisAlignedBB aabb,
                                BiConsumer<Vector3d, List<? extends Entity>> sorter, boolean self,
                                @Nullable String username, @Nullable UUID uuid,
                                @Nullable EntityType<?> type, boolean permissions) {
        this.limit = limit;
        this.includeNonPlayers = includeEntities;
        this.currentWorldOnly = currentWorld;
        this.filter = filter;
        this.distance = distance;
        this.positionGetter = positionGetter;
        this.aabb = aabb;
        this.sorter = sorter;
        this.self = self;
        this.username = username;
        this.uuid = uuid;
        this.type = type;
        this.checkPermission = permissions;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean includesEntities() {
        return this.includeNonPlayers;
    }

    public boolean isSelfSelector() {
        return this.self;
    }

    public boolean isWorldLimited() {
        return this.currentWorldOnly;
    }

    private void checkPermission(ISuggestionProvider source) throws CommandSyntaxException {
        if (this.checkPermission && !source.hasPermissionLevel(2)) throw EntityArgument.SELECTOR_NOT_ALLOWED.create();
    }

    public Entity selectOne(ClientCommandSource source) throws CommandSyntaxException {
        checkPermission(source);
        List<? extends Entity> list = select(source);

        if (list.isEmpty()) throw EntityArgument.ENTITY_NOT_FOUND.create();
        if (list.size() > 1) throw EntityArgument.TOO_MANY_ENTITIES.create();

        return list.get(0);
    }

    public List<? extends Entity> select(ClientCommandSource source) throws CommandSyntaxException {
        checkPermission(source);

        if (!includeNonPlayers || username != null) return selectPlayers(source);

        //TODO: TEST UUID of Entity from Server
        if (uuid != null) {
            for (Entity entity : source.getWorld().getAllEntities()) {
                if (entity.getUniqueID().equals(uuid)) return Lists.newArrayList(entity);
            }
            return Collections.emptyList();
        }

        Vector3d vec3d = positionGetter.apply(source.getPos());
        Predicate<Entity> predicate = updateFilter(vec3d);

        if (self) {
            if (predicate.test(source.getEntity())) return Lists.newArrayList(source.getEntity());

            return Collections.emptyList();
        }

        List<Entity> list = Lists.newArrayList();
        getEntities(list, source.getWorld(), vec3d, predicate);
        return sortAndLimit(vec3d, list);
    }

    /**
     * Gets all entities matching this selector, and adds them to the passed list.
     */
    private void getEntities(List<Entity> result, ClientWorld worldIn, Vector3d pos, Predicate<Entity> predicate) {
        if (aabb != null) {
            result.addAll(worldIn.getEntitiesWithinAABB(type, aabb.offset(pos), predicate));
        } else {
            result.addAll(getEntities(worldIn, type, predicate));
        }
    }

    private List<Entity> getEntities(ClientWorld world, @Nullable EntityType<?> type,
                                     Predicate<? super Entity> predicate) {
        List<Entity> list = Lists.newArrayList();
        for (Entity entity : world.getAllEntities()) {
            if ((type == null || entity.getType() == type) && predicate.test(entity)) {
                list.add(entity);
            }
        }

        return list;
    }

    public PlayerEntity selectOnePlayer(ClientCommandSource source) throws CommandSyntaxException {
        checkPermission(source);

        List<PlayerEntity> list = selectPlayers(source);

        if (list.size() != 1) throw EntityArgument.PLAYER_NOT_FOUND.create();

        return list.get(0);
    }

    public List<PlayerEntity> selectPlayers(ClientCommandSource source) throws CommandSyntaxException {
        checkPermission(source);

        if (username != null) {
            return source.getWorld().getPlayers()
                         .stream()
                         .filter(player -> Objects.equals(
                             player.getGameProfile().getName(), username))
                         .collect(Collectors.toList());
        }

        if (uuid != null) {
            return source.getWorld().getPlayers()
                         .stream()
                         .filter(player -> Objects.equals(
                             player.getGameProfile().getId(), uuid))
                         .collect(Collectors.toList());
        }

        Vector3d vec3d = this.positionGetter.apply(source.getPos());
        Predicate<Entity> predicate = this.updateFilter(vec3d);

        if (self) {
            if (predicate.test(source.asPlayer())) return Lists.newArrayList(source.asPlayer());

            return Collections.emptyList();
        }

        List<PlayerEntity> players = source.getWorld()
                                           .getPlayers()
                                           .stream()
                                           .filter(predicate)
                                           .collect(Collectors.toList());

        return sortAndLimit(vec3d, players);
    }

    /**
     * Returns a modified version of the predicate on this selector that also checks the AABB and distance.
     */
    private Predicate<Entity> updateFilter(Vector3d pos) {
        Predicate<Entity> predicate = filter;

        if (aabb != null) {
            AxisAlignedBB axisalignedbb = aabb.offset(pos);
            predicate = predicate.and((entity) -> axisalignedbb.intersects(entity.getBoundingBox()));
        }

        if (!distance.isUnbounded()) {
            predicate = predicate.and((entity) -> distance.testSquared(entity.getDistanceSq(pos)));
        }

        return predicate;
    }

    private <T extends Entity> List<T> sortAndLimit(Vector3d pos, List<T> entities) {
        if (entities.size() > 1) sorter.accept(pos, entities);

        return entities.subList(0, Math.min(limit, entities.size()));
    }

}
