package com.github.zeddicuspl;

import com.github.zeddicuspl.model.Config;
import com.github.zeddicuspl.screen.PresetsScreen;
import com.github.zeddicuspl.util.ConfigUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.irisshaders.iris.api.v0.IrisApi;

import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.*;

public class ResourcePresetsPlugin implements ClientModInitializer {
	public PresetsScreen presetsScreen;
	private static ResourcePresetsPlugin instance;
	private Config config = ConfigUtil.read();
	private boolean useIris = false;

	@Override
	public void onInitializeClient() {
		instance = this;
		config = ConfigUtil.read();
        FabricLoader.getInstance().getModContainer("iris").ifPresent(iris -> useIris = true);
        registerCommand();
	}

	public static ResourcePresetsPlugin getInstance() {
		return instance;
	}

	public ButtonWidget getPresetsButton(Screen screen) {
		if (config.getSelectedPreset().isBlank()) {
			if (!getPresetNames().isEmpty()) {
				setSelectedPreset(getPresetNames().get(0));
			}
		}
		return new ButtonWidget.Builder(Text.of("⭐"),
				(buttonWidget) -> { openPresetsScreen(screen); }
		).dimensions(screen.width - 30, 10, 20, 20).build();
	}

	public List<ResourcePackProfile> getEnabledProfiles(ResourcePackManager resourcePackManager) {
		return filterManageableProfiles(resourcePackManager.getEnabledProfiles().stream().toList());
	}

	public List<String> getPresetNames() {
		return config.getPresets().keySet().stream().toList();
	}

	public String getSelectedPreset() {
		return config.getSelectedPreset();
	}

	public void setSelectedPreset(String preset) {
		boolean shadersEnabled = isShadersEnabled(preset);
		if (useIris) {
			IrisApi.getInstance().getConfig().setShadersEnabledAndApply(shadersEnabled);
		}
		MinecraftClient.getInstance().getResourcePackManager().setEnabledProfiles(getResourceList(preset));
		MinecraftClient.getInstance().reloadResources();
		config.setSelectedPreset(preset);
		ConfigUtil.save(config);
	}

	public void openPresetsScreen(Screen parentScreen) {
		this.presetsScreen = new PresetsScreen(parentScreen);
		MinecraftClient.getInstance().setScreen(this.presetsScreen);
	}

	private List<String> getResourceList(String presetName) {
		return config.getPresets().get(presetName).stream()
				.filter(preset -> !preset.equals("shaders-enabled")).toList();
	}

	private boolean isShadersEnabled(String presetName) {
		return config.getPresets().get(presetName).contains("shaders-enabled");
	}

	private void registerCommand() {
		LiteralArgumentBuilder<ServerCommandSource> cmd = literal("resourcepreset")
			.then(argument("action", StringArgumentType.string())
				.suggests((context, builder) -> builder
						.suggest("save")
						.suggest("delete")
						.buildFuture()
				)
				.then(argument("preset_name", StringArgumentType.greedyString())
					.suggests((context, builder) -> {
						String action = context.getArgument("action", String.class);
						if ("delete".equals(action)) {
							config.getPresets().keySet().forEach(builder::suggest);
							return builder.buildFuture();
						}
                        return null;
                    })
					.executes(context -> {
						switch (StringArgumentType.getString(context, "action")) {
							case "save":
								savePreset(StringArgumentType.getString(context, "preset_name"), context);
								break;
							case "delete":
								deletePreset(StringArgumentType.getString(context, "preset_name"), context);
								break;
							default:
								context.getSource().sendFeedback(() -> Text.translatable("Unknown command \"%s\"", StringArgumentType.getString(context, "action")), false);
								break;
						}
						return 1;
					})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(cmd));
	}

	private List<ResourcePackProfile> filterManageableProfiles(List<ResourcePackProfile> profiles) {
		return profiles.stream().filter((profile) -> (!profile.isAlwaysEnabled() && !profile.isPinned())).toList();
	}

	private void savePreset(String presetName, CommandContext<ServerCommandSource> context) {
		ResourcePackManager resourcePackManager = MinecraftClient.getInstance().getResourcePackManager();
		List<String> resources = getEnabledProfiles(resourcePackManager).stream().map(ResourcePackProfile::getName).collect(Collectors.toList());
		if (useIris && IrisApi.getInstance().getConfig().areShadersEnabled()) {
			resources.add("shaders-enabled");
		}
		config.getPresets().put(presetName, resources);
		ConfigUtil.save(config);
		context.getSource().sendFeedback(() -> Text.translatable("Successfully saved preset"), false);
	}

	private void deletePreset(String presetName, CommandContext<ServerCommandSource> context) {
		config.getPresets().remove(presetName);
		ConfigUtil.save(config);
		context.getSource().sendFeedback(() -> Text.translatable("Successfully deleted preset"), false);
	}
}
