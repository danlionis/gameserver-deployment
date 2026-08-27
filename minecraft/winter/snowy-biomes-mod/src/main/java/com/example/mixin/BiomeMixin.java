package com.example.mixin;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

@Mixin(Biome.class)
public abstract class BiomeMixin {
//    @Inject(method = "getTemperature", at = @At("HEAD"), cancellable = true)
//    private void forceSnowyTemperature(CallbackInfoReturnable<Float> cir) {
//        cir.setReturnValue(0.0F); // forces snow
//    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void makeSnowy(CallbackInfo ci) {
        try {
            Class<?> weatherClass = null;
            for (Class<?> innerClass : Biome.class.getDeclaredClasses()) {
                if (innerClass.getSimpleName().equals("class_5482")) { // Biome.Weather
                    weatherClass = innerClass;
                    break;
                }
            }
            assert weatherClass != null;
            Constructor<?> weatherConstructor = weatherClass.getDeclaredConstructors()[0];
            weatherConstructor.setAccessible(true);

            Class<?> tempModClass = null; // Biome.TemperatureModifier
            for (Class<?> innerClass : Biome.class.getDeclaredClasses()) {
                if (innerClass.getSimpleName().equals("class_5484")) { // Biome.Weather
                    tempModClass = innerClass;
                    break;
                }
            }
            assert tempModClass != null;
            Constructor<?> tempModConstructor = tempModClass.getDeclaredConstructors()[0];
            tempModConstructor.setAccessible(true);

            Field weatherField = Biome.class.getDeclaredField("field_26393"); // weather field
            weatherField.setAccessible(true);
            Object oldWeather = weatherField.get(this);

            Method hasPrecipitation = weatherClass.getDeclaredMethod("comp_1187");
            Method oldTemperature = weatherClass.getDeclaredMethod("comp_846");
            Method oldTempMod = weatherClass.getDeclaredMethod("comp_845");
            Method oldDownfall = weatherClass.getDeclaredMethod("comp_846");

            boolean precipitation = (Boolean) hasPrecipitation.invoke(oldWeather);
            float downfall = (Float) oldDownfall.invoke(oldWeather);
            float temp = (Float) oldTemperature.invoke(oldWeather);
            Object tempModifier = oldTempMod.invoke(oldWeather);

            if (precipitation && temp >= 0.15F) {
                Object newWeather = weatherConstructor.newInstance(
                        true,    // keep old
                        Math.min(temp, 0.14F), // override temperature
                        tempModifier,     // keep old
                        downfall          // keep old
                );

                weatherField.set(this, newWeather);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
