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