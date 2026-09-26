# BA Pre-Click to Move

A RuneLite plugin with helpers for the Barbarian Assault lobby, each off by default and turned on with its own checkbox.

- **Points interface boundary.** After a wave you're sent back to the lobby and the wave-complete interface opens. Part of that interface stops your clicks from reaching the scene behind it. This helper outlines exactly that part.
- **Scroll to zoom over the interface.** Scrolling over that same blocking part normally does nothing, because it swallows mouse-wheel events as well as clicks. With this on, scrolling there zooms the camera, just as it does over the scene. It uses the game's own zoom script and follows the game's scroll-to-zoom setting: when that setting is off, this does nothing either.
- **Relative tile.** While you're standing in one of the wave lobbies (the ten 8×8 waiting rooms under the Barbarian Outpost, one per wave), this outlines the tile at a fixed offset from your tile, e.g. 2 North 3 East. It is hidden everywhere else, including the corridors between the rooms and the wave arena. It is drawn above interfaces, so the wave-complete interface can't hide it. It follows your true (server) tile, so it steps from tile to tile instead of sliding while you walk.
- **Different offset on wave 10.** On wave 10 the dispensers sit in different places relative to each role's spawn than on waves 1–9. With this on, the relative tile uses a separate offset in the wave 10 lobby, the room you're put in after winning wave 9. Each room is recognised from your true tile, so the right offset applies from your first tick in the room.

## Config

| Section | Option | Default | Description |
| --- | --- | --- | --- |
| Points interface boundary | Show points interface boundary | off | Turns the boundary outline on |
| Points interface boundary | Scroll to zoom over interface | off | Zoom when scrolling over the blocking part of the interface |
| Points interface boundary | Outline color | red | Color of the outline |
| Points interface boundary | Outline width | 2 | Width of the outline, in pixels |
| Points interface boundary | Fill color | transparent | Optional fill inside the boundary |
| Relative tile | Show relative tile | off | Turns the relative-tile outline on (only shown inside a wave lobby) |
| Relative tile | Tiles north/south + North/South | 0, North | Number of tiles north or south of the player |
| Relative tile | Tiles east/west + East/West | 0, East | Number of tiles east or west of the player |
| Relative tile | Tile color | cyan | Color of the tile outline |
| Relative tile | Different offset on wave 10 | off | Use the wave 10 offset below in the wave 10 lobby |
| Relative tile | Wave 10 tiles north/south + Wave 10 North/South | 0, North | Wave 10 offset north or south of the player |
| Relative tile | Wave 10 tiles east/west + Wave 10 East/West | 0, East | Wave 10 offset east or west of the player |

## How the points interface boundary is computed

The outline doesn't come from hand-measured coordinates. It comes from the same rule the game client uses to block mouse input, which was read from the decompiled client (1.12.39). Each frame, the client walks the widget tree depth first. When it reaches the viewport widget (content type 1337), it adds the scene's menu entries ("Walk here", NPC and object options). Any widget it processes after that and that contains the mouse resets the menu to just "Cancel", throwing those scene entries away. Two kinds of widget do this:

| Blocker | Area blocked |
| --- | --- |
| if3 widget with `noClickThrough` | the widget's bounds clipped to its ancestors' |
| layer mounting an interface with modal mode `MODAL_NOCLICKTHROUGH` | the layer's bounds clipped to its ancestors' |

Widgets that merely have ops or listeners don't block: their menu entries go on top of the scene's, but the scene entries stay. The client also skips some widgets, and their whole subtrees, entirely. An if3 widget is skipped if it is self-hidden, or if it is not a layer, has no listener, and has zero click and op masks. The same resets also throw away the queued mouse-wheel events, and that's why scrolling over these widgets doesn't zoom. An if3 widget with `noScrollThrough` throws away only the mouse-wheel events. So scroll-to-zoom acts on the blocked area plus any `noScrollThrough` widgets.

`BlockedAreaCalculator` repeats this walk. It collects the blockers that belong to the wave-complete interface (group 497), including the layer that mounts it, then intersects the result with the viewport.

## Development

Build and test with JDK 11:

```sh
./gradlew build
```

To launch a development client with the plugin loaded, run `BAPreClickToMovePluginTest.main` from `src/test`.
