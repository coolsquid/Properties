package coolsquid.properties.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

import coolsquid.properties.config.ConfigManager;
import coolsquid.properties.network.PacketManager;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

public class CommandProperties extends CommandBase {

	@Override
	public String getName() {
		return "properties";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			sender.sendMessage(new TextComponentString("<Properties> Too few parameters"));
		} else if (args[0].equals("reload")) {
			ConfigManager.load();
			PacketManager.sendConfigsToClient(null);
			sender.sendMessage(new TextComponentString("<Properties> Reloaded scripts"));
		} else if (args[0].equals("dump")) {
			if (args.length == 1) {
				sender.sendMessage(new TextComponentString("<Properties> You need to specify something to dump."));
				return;
			}
			try (BufferedWriter w = new BufferedWriter(
					new OutputStreamWriter(FileUtils.openOutputStream(new File("./logs/properties-dump.txt"))))) {
				w.write("-- DUMP START --");
				w.newLine();
				List<String> l = new ArrayList<>(args.length - 1);
				for (int i = 1; i < args.length; i++) {
					if (args[i].equals("items")) {
						w.newLine();
						w.write("Items:");
						w.newLine();
						for (Item item : Item.REGISTRY) {
							w.write(item.getRegistryName().toString());
							w.newLine();
						}
					} else if (args[i].equals("blocks")) {
						w.newLine();
						w.write("Blocks:");
						w.newLine();
						for (Block item : Block.REGISTRY) {
							w.write(item.getRegistryName().toString());
							w.newLine();
						}
					} else if (args[i].equals("damage_sources")) {
						w.newLine();
						w.write("Damage sources (Vanilla only):");
						w.newLine();
						for (Field field : DamageSource.class.getFields()) {
							if (field.getType() == DamageSource.class) {
								w.write(((DamageSource) field.get(null)).getDamageType());
								w.newLine();
							}
						}
					} else {
						continue;
					}
					l.add(args[i]);
				}
				w.write("-- DUMP END --");
				ITextComponent message = new TextComponentString(
						"<Properties> Successfully dumped " + String.join(", ", l) + " to /logs/properties-dump.txt.");
				message.getStyle()
						.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "logs/properties-dump.txt"));
				sender.sendMessage(message);
			} catch (Exception e) {
				Log.catching(e);
				ITextComponent message = new TextComponentString(
						"<Properties> Something went wrong. See the Properties log for more details.");
				message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "logs/properties.log"));
				sender.sendMessage(message);
			}
		} else {
			sender.sendMessage(new TextComponentString("<Properties> No such subcommand"));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		switch (args.length) {
			case 1:
				return filter(args[0], "reload", "dump");
			default:
				if (args[0].equals("dump")) {
					return filter(args[args.length - 1], "items", "blocks", "actions", "conditions", "targets",
							"target_conditions", "events");
				} else {
					return Collections.emptyList();
				}
		}
	}

	private static List<String> filter(String string, String... strings) {
		Arrays.sort(strings);
		List<String> list = Lists.newArrayList(strings);
		Iterator<String> i = list.iterator();
		while (i.hasNext()) {
			if (!i.next().startsWith(string)) {
				i.remove();
			}
		}
		return list;
	}
}