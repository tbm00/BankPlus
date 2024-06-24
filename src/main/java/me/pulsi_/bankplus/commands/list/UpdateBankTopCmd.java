package me.pulsi_.bankplus.commands.list;

import me.pulsi_.bankplus.bankTop.BPBankTop;
import me.pulsi_.bankplus.commands.BPCommand;
import me.pulsi_.bankplus.utils.texts.BPMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class UpdateBankTopCmd extends BPCommand {

    public UpdateBankTopCmd(FileConfiguration commandsConfig, String... aliases) {
        super(commandsConfig, aliases);
    }

    @Override
    public List<String> defaultUsage() {
        return Collections.emptyList();
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
    public boolean skipUsageWarn() {
        return true;
    }

    @Override
    public boolean preCmdChecks(CommandSender s, String[] args) {
        return true;
    }

    @Override
    public void onExecution(CommandSender s, String[] args) {
        BPBankTop.updateBankTop();
        BPBankTop.restartBankTopUpdateTask();
        BPMessages.send(s, "BankTop-Updated");
    }

    @Override
    public List<String> tabCompletion(CommandSender s, String[] args) {
        return null;
    }
}