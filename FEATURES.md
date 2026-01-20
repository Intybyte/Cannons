New features
---------------
- 1.20.6 + Support
- Added Netherite and newer armor protection support
- Simplified Chinese Translation by [SnowCutieOwO](https://github.com/SnowCutieOwO)
- New area commands
- Mohist compatibility (kind of, you need to use particle aiming)
- Folia support
- Custom projectile definitions (Custom gravity, drag, water drag, constant acceleration, Custom model data and more)
- Custom blocks support (Slimefun & ItemsAdder so far, requires custom format)
- Command completions

Fixes:
---------------
- Correct projectile behaviour for projectiles like ender dragon fireballs, tridents etc.
- Fix the ability to pickup projectiles like arrows
- Fix fire falling block on explosion not being recognized by movecraft
- Craft smashing into things invalidated some cannons and created ghost cannons
- Sinking not being handled and creating ghost cannons

Hooks:
---------------
- Vault hook to buy cannons (was there even before fork)
- [Movecraft](https://github.com/Intybyte/Cannons/wiki/Hooks-&-Integrations#movecraft): Movecraft-Cannons support is now integrated (Movecraft Combat hook + Movecraft hook)
- [PlaceholderAPI](https://github.com/Intybyte/Cannons/wiki/Hooks-&-Integrations#placeholder-api) hook

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

FireTask API:
- You can add custom firing behaviour in CannonFireEvent by using addFireTaskCreator

Exchange API:
- You can define your own exchanges for cannons creation requirement, example at [CannonsEXP](https://github.com/Intybyte/CannonsEXP)

Schematic Processing API:
- You can define your own custom blocks to add by defining a namespace and adding it to SchematicWorldProcessorImpl

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