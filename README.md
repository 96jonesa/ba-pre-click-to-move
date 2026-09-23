# BA Pre-Click to Move

A RuneLite plugin with two helpers for the Barbarian Assault lobby, each off by default and turned on with its own checkbox.

- **Points interface boundary.** After a wave you're sent back to the lobby and the wave-complete interface opens. Part of that interface stops your clicks from reaching the scene behind it. This helper outlines exactly that part.
- **Relative tile.** In the lobby (the basement under the Barbarian Outpost, map region 10322; not the wave arena), this helper outlines the tile at a fixed offset from your tile, e.g. 2 North 3 East. It is drawn above interfaces, so the wave-complete interface can't hide it. It follows your true (server) tile, so it steps from tile to tile instead of sliding while you walk.

## Config

| Section | Option | Default | Description |
| --- | --- | --- | --- |
| Points interface boundary | Show points interface boundary | off | Turns the boundary outline on |
| Points interface boundary | Outline color | red | Color of the outline |
| Points interface boundary | Outline width | 2 | Width of the outline, in pixels |
| Points interface boundary | Fill color | transparent | Optional fill inside the boundary |
| Relative tile | Show relative tile | off | Turns the relative-tile outline on |
| Relative tile | Tiles north/south + North/South | 0, North | Number of tiles north or south of the player |
| Relative tile | Tiles east/west + East/West | 0, East | Number of tiles east or west of the player |
| Relative tile | Tile color | cyan | Color of the tile outline |

## How the points interface boundary is computed

The outline doesn't come from hand-measured coordinates. It comes from the same rule the game client uses to block mouse input, which was read from the decompiled client (1.12.39). Each frame, the client walks the widget tree depth first. When it reaches the viewport widget (content type 1337), it adds the scene's menu entries ("Walk here", NPC and object options). Any widget it processes after that and that contains the mouse resets the menu to just "Cancel", throwing those scene entries away. Two kinds of widget do this:

| Blocker | Area blocked |
| --- | --- |
| if3 widget with `noClickThrough` | the widget's bounds clipped to its ancestors' |
| layer mounting an interface with modal mode `MODAL_NOCLICKTHROUGH` | the layer's bounds clipped to its ancestors' |

Widgets that merely have ops or listeners don't block: their menu entries go on top of the scene's, but the scene entries stay. The client also skips some widgets, and their whole subtrees, entirely. An if3 widget is skipped if it is self-hidden, or if it is not a layer, has no listener, and has zero click and op masks. `BlockedAreaCalculator` repeats this walk. It collects the blockers that belong to the wave-complete interface (group 497), including the layer that mounts it, then intersects the result with the viewport.

## Development

Build and test with JDK 11:

```sh
./gradlew build
```

To launch a development client with the plugin loaded, run `BAPreClickToMovePluginTest.main` from `src/test`.
