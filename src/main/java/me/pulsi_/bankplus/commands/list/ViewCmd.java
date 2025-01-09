package me.pulsi_.bankplus.commands.list;

import me.pulsi_.bankplus.bankSystem.Bank;
import me.pulsi_.bankplus.bankSystem.BankUtils;
import me.pulsi_.bankplus.commands.BPCmdExecution;
import me.pulsi_.bankplus.commands.BPCommand;
import me.pulsi_.bankplus.economy.BPEconomy;
import me.pulsi_.bankplus.utils.BPLogger;
import me.pulsi_.bankplus.utils.BPUtils;
import me.pulsi_.bankplus.utils.texts.BPArgs;
import me.pulsi_.bankplus.utils.texts.BPMessages;
import me.pulsi_.bankplus.values.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewCmd extends BPCommand {

    public ViewCmd(FileConfiguration commandsConfig, String... aliases) {
        super(commandsConfig, aliases);
    }

    @Override
    public List<String> defaultUsage() {
        return Collections.singletonList("%prefix% &cUsage: &7/bank view [player] <bankName>");
    }

    @Override
    public int defaultConfirmCooldown() {
        return 0;
    }

    @Override
    public List<String> defaultConfirmMessage() {
        return Collections.emptyList();
    }

    @Override
    public int defaultCooldown() {
        return 0;
    }

    @Override
    public List<String> defaultCooldownMessage() {
        return Collections.emptyList();
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public boolean skipUsage() {
        return false;
    }

    @Override
    public BPCmdExecution onExecution(CommandSender s, String[] args) {

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            BPMessages.send(s, "Invalid-Player");
            return BPCmdExecution.invalidExecution();
        }

        List<Bank> banks = new ArrayList<>();
        if (args.length == 2) {

            banks.addAll(BankUtils.getAvailableBanks(target));
            if (banks.isEmpty()) {
                BPMessages.send(s, "No-Available-Banks-Others", "%player%$" + target.getName());
                return BPCmdExecution.invalidExecution();
            }

        } else {
            Bank bank = BankUtils.getBank(args[2]);
            if (!BankUtils.exist(bank, s)) return BPCmdExecution.invalidExecution();

            if (!BankUtils.isAvailable(bank, target)) {
                BPMessages.send(s, "Cannot-Access-Bank-Others", "%player%$" + target.getName());
                return BPCmdExecution.invalidExecution();
            }
            banks.add(bank);
        }

        return new BPCmdExecution() {
            @Override
            public void execute() {
                if (banks.size() > 1)
                    BPMessages.send(
                            s,
                            "Multiple-Bank-Others",
                            BPUtils.placeValues(target, BPEconomy.getBankBalancesSum(target))
                    );
                else {
                    Bank bank = banks.get(0);
                    BPMessages.send(
                            s,
                            "Bank-Others",
                            BPUtils.placeValues(target, bank.getBankEconomy().getBankBalance(target), BankUtils.getCurrentLevel(bank, target))
                    );
                }

                if (s instanceof Player && ConfigValues.isViewSoundEnabled())
                    if (!BPUtils.playSound(ConfigValues.getPersonalSound(), (Player) s))
                        BPLogger.warn("Occurred while trying to play PERSONAL sound for player \"" + s.getName() + "\".");

            }
        };
    }

    @Override
    public List<String> tabCompletion(CommandSender s, String[] args) {
        if (args.length == 3)
            return BPArgs.getArgs(args, BankUtils.getAvailableBankNames(Bukkit.getOfflinePlayer(args[1])));
        return null;
    }
}