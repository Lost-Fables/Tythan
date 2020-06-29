package co.lotc.core.bukkit.command;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DefaultArgs {
	private static final BrigadierProvider provider = BrigadierProvider.get();

	private DefaultArgs() {}

	// World
	public static void buildWorldParameter() {
		Commands.defineArgumentType(World.class)
				.defaultName("World")
				.completer((s,$) -> getWorldStringList())
				.mapperWithSender((sender, world) -> Bukkit.getWorld(world))
				.register();
	}

	private static List<String> getWorldStringList() {
		List<String> output = new ArrayList<>();
		for (World world : Bukkit.getWorlds()) {
			output.add(world.getName());
		}
		return output;
	}

	// Material
	public static void buildMaterialParameter() {
		Commands.defineArgumentType(Material.class)
		.brigadierType(provider.argumentItemStack())
		.mapper( parse.andThen(ItemStack::getType) )
		.defaultName("item")
		.defaultError("please specify a valid item name")
		.register();
	}

	// ItemStack
	public static void buildItemStackParameter() {
		Commands.defineArgumentType(ItemStack.class)
			.brigadierType(provider.argumentItemStack())
			.mapper(parse)
			.defaultName("itemstack")
			.defaultError("please specify a valid item name")
			.register();
	}
	
	private static Function<String, ItemStack> parse = input->{
		try {
			Object argumentPredicateItemStack = provider.argumentItemStack().parse(new StringReader(input));
			Object nmsStack = provider.getItemStackParser().invoke(argumentPredicateItemStack, 1, false);
			return MinecraftReflection.getBukkitItemStack(nmsStack);
		} catch (CommandSyntaxException e) { //User Parsing error
			return null;
		} catch (Exception e) { //Unexpected error
			e.printStackTrace();
			return null;
		}
	};

	// Color
	public static void buildBungeeColorParameter() {
		Commands.defineArgumentType(net.md_5.bungee.api.ChatColor.class)
				.defaultName("Color")
				.completer((s,$) -> Arrays.stream(net.md_5.bungee.api.ChatColor.values()).map(object -> Objects.toString(object, null)).collect(Collectors.toList()))
				.mapperWithSender((sender, color) -> net.md_5.bungee.api.ChatColor.of(color))
				.register();
	}

	public static void buildBukkitColorParameter() {
		Commands.defineArgumentType(org.bukkit.ChatColor.class)
				.defaultName("Color")
				.completer((s,$) -> Arrays.stream(org.bukkit.ChatColor.values()).map(object -> Objects.toString(object, null)).collect(Collectors.toList()))
				.mapperWithSender((sender, color) -> org.bukkit.ChatColor.valueOf(color))
				.register();
	}
	
}
