package co.lotc.core.bukkit.command;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.lotc.core.agnostic.Sender;
import co.lotc.core.bukkit.wrapper.BukkitSender;
import co.lotc.core.command.ParameterType;

public final class SenderTypes {

	private SenderTypes() { }

	public static final Function<Sender, CommandSender> UNWRAP_SENDER = s->((BukkitSender) s).getHandle();
	public static final Function<Sender, Player> UNWRAP_PLAYER = UNWRAP_SENDER.andThen(s->(s instanceof Player)? ((Player) s):null);
	public static final Supplier<List<String>> PLAYER_COMPLETER = ()->Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
	
	public static void registerCommandSenderType() {
		Commands.defineArgumentType(CommandSender.class).senderMapper(UNWRAP_SENDER).register();
	}
	
	public static void registerPlayerType() {
		new ParameterType<>(Player.class)
			.senderMapper(UNWRAP_PLAYER)
			.mapperWithSender((send,s)->{
				if("@p".equals(s)) s = send.getName();
				else if("@s".equals(s)) s = send.getName();
				
				if(s.length() == 36) {
					try {return Bukkit.getPlayer(UUID.fromString(s));}
					catch(IllegalArgumentException e) {return null;}
				} else {
					return Bukkit.getPlayer(s);
				}
			})
			.completer(PLAYER_COMPLETER)
			.register();
	}
	
	@SuppressWarnings("deprecation")
	public static void registerOfflinePlayerType() {
		new ParameterType<>(OfflinePlayer.class)
		.mapperWithSender((send,s)->{
			if("@p".equals(s)) s = send.getName();
			
			UUID u = uuidFromString(s);
			
			OfflinePlayer op = null;
			if(u != null) {
				op = Bukkit.getOfflinePlayer(u);
			} else {
				op = Bukkit.getOfflinePlayer(s); //Deprecated
			}

			if(op.hasPlayedBefore()) return op;
			else return null;
		})
		.completer(PLAYER_COMPLETER)
		.register();
	}
	
	public static UUID uuidFromString(String s) {
		if(s.length() == 32)
			s = s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
			
		if(s.length() == 36) {
			try {return UUID.fromString(s);}
			catch(IllegalArgumentException e) {return null;}
		}
		return null;
	}
	
}
