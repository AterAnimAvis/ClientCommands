TODO:
---
 * Client Help Command
 * Warning on Override Server Command?
 
 * Forge PR
    * Replace PacketHandler with Hook
    * For Simple Arguments add generic functions?
    * Full System vs Bare Basic vs Just Events
    * ClientCommandSource extends CommandSource?
        - CON: Easy to cause issue with an Argument expecting a Server
        - PRO: All simple Arguments should just work.
        - Prefer un-extend but adds a-lot of stuff to support. 
    * Single CommandDispatcher? harder to pass on-to Server.

TODO: See net.minecraft.command.arguments.ArgumentTypes

| Argument | Command | Suggestions |
|----------|---------|-------------|
| |**Brigadier**| |
| Bool | Works | Works |
| Double | Works | Works |
| Float | Works | Works |
| Integer | Works | Works |
| Long | Works | Works |
| String | Works | Works |
| |**Minecraft**| |
| AngleArgument (1.16) | - | - |
| BlockPosArgument | Custom | Works |
| BlockPredicateArgument | Custom | Works |
| BlockStateArgument | Simple | Works |
| ColorArgument | Simple | Works |
| ColumnPosArgument | Custom | Works |
| ComponentArgument | - | - |
| DimensionArgument | Simple | Works |
| EnchantmentArgument | Simple | Works |
| EntityAnchorArgument | - | - |
| EntityArgument | Custom | Works |
| EntitySummonArgument | Simple | Works |
| FloatRangeArgument | Simple | Works |
| FunctionArgument | Server Side Only | - |
| GameProfileArgument | Custom | Works |
| IntRangeArgument | Simple | Works |
| ItemArgument | Works | Works |
| ItemPredicateArgument | - | - |
| MessageArgument | - | - |
| NBTCompoundTagArgument | - | - | 
| NBTTagArgument | - | - |
| NBTPathArgument | - | - |
| ObjectiveArgument | - | - |
| ObjectiveCriteriaArgument | - | - |
| OperationArgument | - | - |
| ParticleArgument | - | - |
| PotionArgument | Simple | Works |
| ResourceLocationArgument | Simple | Works |
| RotationArgument | - | - |
| ScoreboardSlotArgument | - | - |
| ScoreHolderArgument | - | - |
| SlotArgument | Simple | Works |
| SwizzleArgument | Simple | Works |
| TeamArgument | Custom | ? |
| TimeArgument | Simple | Works |
| UUIDArgument (1.16) | Simple | Works |
| Vec2Argument | Custom | Works |
| Vec3Argument | Custom | Works |

- BrigadierSerializers.registerArgumentTypes();
// 38 v 36 // NEW uuid and angle
- register("entity", EntityArgument.class, new EntityArgument.Serializer());
- register("game_profile", GameProfileArgument.class, new ArgumentSerializer<>(GameProfileArgument::gameProfile));
- register("block_pos", BlockPosArgument.class, new ArgumentSerializer<>(BlockPosArgument::blockPos));
- register("column_pos", ColumnPosArgument.class, new ArgumentSerializer<>(ColumnPosArgument::columnPos));
- register("vec3", Vec3Argument.class, new ArgumentSerializer<>(Vec3Argument::vec3));
- register("vec2", Vec2Argument.class, new ArgumentSerializer<>(Vec2Argument::vec2));
- register("block_state", BlockStateArgument.class, new ArgumentSerializer<>(BlockStateArgument::blockState));
- register("block_predicate", BlockPredicateArgument.class, new ArgumentSerializer<>(BlockPredicateArgument::blockPredicate));
- register("item_stack", ItemArgument.class, new ArgumentSerializer<>(ItemArgument::item));
- register("item_predicate", ItemPredicateArgument.class, new ArgumentSerializer<>(ItemPredicateArgument::itemPredicate));
- register("color", ColorArgument.class, new ArgumentSerializer<>(ColorArgument::color));
- register("component", ComponentArgument.class, new ArgumentSerializer<>(ComponentArgument::component));
- register("message", MessageArgument.class, new ArgumentSerializer<>(MessageArgument::message));
- register("nbt_compound_tag", NBTCompoundTagArgument.class, new ArgumentSerializer<>(NBTCompoundTagArgument::nbt));
- register("nbt_tag", NBTTagArgument.class, new ArgumentSerializer<>(NBTTagArgument::func_218085_a));
- register("nbt_path", NBTPathArgument.class, new ArgumentSerializer<>(NBTPathArgument::nbtPath));
- register("objective", ObjectiveArgument.class, new ArgumentSerializer<>(ObjectiveArgument::objective));
- register("objective_criteria", ObjectiveCriteriaArgument.class, new ArgumentSerializer<>(ObjectiveCriteriaArgument::objectiveCriteria));
- register("operation", OperationArgument.class, new ArgumentSerializer<>(OperationArgument::operation));
- register("particle", ParticleArgument.class, new ArgumentSerializer<>(ParticleArgument::particle));
- register("angle", AngleArgument.class, new ArgumentSerializer<>(AngleArgument::func_242991_a));
- register("rotation", RotationArgument.class, new ArgumentSerializer<>(RotationArgument::rotation));
- register("scoreboard_slot", ScoreboardSlotArgument.class, new ArgumentSerializer<>(ScoreboardSlotArgument::scoreboardSlot));
- register("score_holder", ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
- register("swizzle", SwizzleArgument.class, new ArgumentSerializer<>(SwizzleArgument::swizzle));
- register("team", TeamArgument.class, new ArgumentSerializer<>(TeamArgument::team));
- register("item_slot", SlotArgument.class, new ArgumentSerializer<>(SlotArgument::slot));
- register("resource_location", ResourceLocationArgument.class, new ArgumentSerializer<>(ResourceLocationArgument::resourceLocation));
- register("mob_effect", PotionArgument.class, new ArgumentSerializer<>(PotionArgument::mobEffect));
- register("function", FunctionArgument.class, new ArgumentSerializer<>(FunctionArgument::function));
- register("entity_anchor", EntityAnchorArgument.class, new ArgumentSerializer<>(EntityAnchorArgument::entityAnchor));
- register("int_range", IRangeArgument.IntRange.class, new ArgumentSerializer<>(IRangeArgument::intRange));
- register("float_range", IRangeArgument.FloatRange.class, new ArgumentSerializer<>(IRangeArgument::func_243493_b));
- register("item_enchantment", EnchantmentArgument.class, new ArgumentSerializer<>(EnchantmentArgument::enchantment));
- register("entity_summon", EntitySummonArgument.class, new ArgumentSerializer<>(EntitySummonArgument::entitySummon));
- register("dimension", DimensionArgument.class, new ArgumentSerializer<>(DimensionArgument::getDimension));
- register("time", TimeArgument.class, new ArgumentSerializer<>(TimeArgument::func_218091_a));
- register("uuid", UUIDArgument.class, new ArgumentSerializer<>(UUIDArgument::func_239194_a_));