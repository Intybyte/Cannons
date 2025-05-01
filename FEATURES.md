New features/fixes:
---------------
- 1.20.6 + Support
- Correct projectile behaviour for projectiles like ender dragon fireballs, tridents etc.
- Fix the ability to pickup projectiles like arrows
- Requires Java 17
- Added Netherite and newer armor protection support
- Simplified Chinese Translation by [SnowCutieOwO](https://github.com/SnowCutieOwO)
- New area commands
- Mohist compatibility (kind of, you need to use particle aiming)
- Folia support

Hooks:
---------------
- Vault hook to buy cannons (was there even before fork)
- Movecraft-Cannons support is now integrated (Movecraft Combat hook + Movecraft hook)
- [PlaceholderAPI](./PLACEHOLDERS.md) hook

Optimizations:
---------------
- Better FlyingProjectile lookup
- UserMessage Optimization
- Some CannonManager Optimization
- RNG Optimization (Original created a random number generator every time it needed to be used, now each object has its own Random)
- Distance optimization by using `Location#distanceSquared()` over `Location#distance` when possible
- Aiming shot simulation Optimization
- `CannonAPI#getCannon` should now not create massive lag when there are a lot of designs, and is way faster (some owners stated it was up to x6 faster)
- `/cannons claim` and commands executed in a radius won't deadlock your server anymore, and it is executed on a separate thread

API Changes/New Events:
--------------

Exchange API:
- You can define your own exchanges for cannons creation requirement, example at [CannonsEXP](https://github.com/Intybyte/CannonsEXP)

Projectiles:
- Projectile type events now give the FlyingProjectile
- ProjectilePiercingEvent is now cancellable

Linking:
- New CannonLinkFiringEvent to handle linked cannons operations
- New CannonLinkAimingEvent to handle linked cannons aiming

Other:
- You can now get more data from CannonDestroyEvent, which execute when cannons are broken too
- New CannonPreLoadEvent 
- New CannonRenameEvent
- New CannonGunpowderLoadEvent (gives accurate data on how much gunpowder is loaded)
- ArmorCalculationUtil now handles internal calculations for damage, every method there is public and can be used by an addon