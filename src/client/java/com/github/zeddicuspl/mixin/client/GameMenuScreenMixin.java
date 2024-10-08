package com.github.zeddicuspl.mixin.client;

import com.github.zeddicuspl.ResourcePresetsPlugin;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
	public GameMenuScreenMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("RETURN"), method = "init")
	private void run(CallbackInfo info) {
		ButtonWidget presetsButton = ResourcePresetsPlugin.getInstance().getPresetsButton(this);
		if (presetsButton != null) {
			this.addDrawableChild(presetsButton);
		}
	}
}
