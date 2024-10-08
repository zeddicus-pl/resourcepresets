# Resource Presets Mod

This is a simple Fabric mod, that allows you to quickly change enabled/disabled resource packs and shaders on/off status (if Iris is installed).


## Commands
- `/resourcepreset save <preset_name>` - saves currently used resource packs and shader status, under the name of `<preset_name>`
- `/resourcepreset delete <preset_name>` - deletes a preset

## UI

The below "star" button appears in: pause screen, server selection list, new game screen

![button](https://github.com/user-attachments/assets/db3a2f4f-dbdb-4cfc-9017-3298a9078589)

This is how a list of presets can look like, after populating it with presets:

![presets](https://github.com/user-attachments/assets/bb8bd6a1-4715-4789-9507-40c8406cfedc)

## Setup

- drop the .jar file into your mods folder.
- run the game, enable/disable the resource packs to prepare the combination you want to save as a preset
- run the `/resourcepresets save <some_name_here>`, for example: `/resourcepresets save Vanilla with some tweaks`
- you will find your preset on the list, when you press the "star" icon on the pause screen

## Development

Run `gradle build`, with Java 17 or above

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
