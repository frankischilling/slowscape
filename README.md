# SlowScape

SlowScape is a RuneLite plugin for the Slow Ricky / SlowScape challenge, where an
account does one counted action per unlock period.

The plugin tracks the current RuneScape profile's daily SlowScape state, spends
the action when a counted action is detected, and blocks additional counted
actions until the next configured unlock time unless an exception is enabled.

## Features

- One-action-per-period tracking stored per RuneScape profile.
- Automatic blocking for extra counted menu actions after the daily action is
  spent.
- Chat messages when an action is spent, blocked, reset, or allowed through an
  exception.
- A status overlay showing:
  - Date
  - Status: Available, Spent, or Override
  - Action count
  - Next unlock date
  - Time left until unlock
  - Extra actions used when override is active
  - First action spent today
- A top-center blocked-action popup when a counted action is stopped.
- Popup and overlay text fitting so long action names stay inside their boxes.
- Manual reset option to clear today's action count and first-action record.
- Manual unlock time setting with a text field for `h:mm` and an AM/PM dropdown.
- Optional unlock timer display.
- Optional daily exception mode that allows extra actions while still counting
  them.
- Login baselines for XP, inventory, and equipment so simply logging in does not
  spend the action from existing game state.
- Production continuation handling for actions such as make, smith, cook, spin,
  craft, fletch, smelt, and tan, so a production activity can create multiple
  outputs as one SlowScape action.

## Counted Actions

SlowScape treats a "thing" as any action that changes game state, produces an
output, or accomplishes something.

Examples of counted actions include:

- Interacting with NPCs, objects, players, world entities, and ground items.
- Using an item on another item, NPC, object, player, or ground item.
- Widget or inventory actions with verbs such as buy, sell, withdraw, deposit,
  make, smith, cook, spin, claim, collect, cast, teleport, equip, wear, wield,
  eat, drink, bury, drop, use, clean, fill, empty, craft, fletch, smelt, tan,
  pay, trade, store, take, place, and remove.
- XP gained after login.
- Inventory or equipment changes after login.

Examples of free actions include:

- Walking.
- Canceling menus.
- Examining objects, NPCs, items, and ground items.
- RuneLite overlay and plugin menu actions.
- Menu setup steps such as selecting an item before using it on a target.

## Settings

`Allow extra actions today`
: Lets counted actions through for the current day and counts them as extra
actions. The overlay changes to Override while this is enabled.

`Show overlay`
: Toggles the SlowScape status overlay.

`Show blocked alert`
: Toggles the temporary top-center popup shown when a counted action is blocked.

`Unlock time`
: Sets the local time when a new SlowScape action becomes available. Use `h:mm`,
such as `12:00` or `9:30`.

`Unlock AM/PM`
: Selects whether the unlock time is AM or PM.

`Show unlock timer`
: Shows or hides the next unlock date and remaining time on the overlay.

`Reset today`
: Clears today's action count, first action, and last action, then turns itself
back off.

## Rules

The current SlowScape rules are documented in [SLOWSCAPE-RULES.MD](SLOWSCAPE-RULES.MD).

## Notes

- SlowScape is a best-effort helper. It uses RuneLite events and menu clicks to
  classify actions, but only the player can confirm an edge case follows the
  challenge rules.
- The plugin does not automate gameplay or send actions for the player. It only
  observes, counts, and blocks user-clicked counted actions when the daily action
  is already spent.
- State is stored through RuneLite profile configuration, so different game
  profiles can have separate SlowScape daily records.

## Development

Use Java 11 when building or running the plugin.

Windows:

```bat
.\gradlew.bat test
.\gradlew.bat run
```

Linux/macOS:

```bash
./gradlew test
./gradlew run
```

If you use a Jagex account, follow RuneLite's development-client login guide:
https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts
