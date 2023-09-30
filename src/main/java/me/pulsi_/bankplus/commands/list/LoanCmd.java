package me.pulsi_.bankplus.commands.list;

import me.pulsi_.bankplus.bankSystem.BankReader;
import me.pulsi_.bankplus.commands.BPCommand;
import me.pulsi_.bankplus.loanSystem.LoanUtils;
import me.pulsi_.bankplus.utils.BPArgs;
import me.pulsi_.bankplus.utils.BPMessages;
import me.pulsi_.bankplus.utils.BPUtils;
import me.pulsi_.bankplus.values.Values;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public class LoanCmd extends BPCommand {

    public LoanCmd(String... aliases) {
        super(aliases);
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public boolean skipUsageWarn() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender s, String[] args) {
        Player p = (Player) s;

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("accept")) {
                LoanUtils.acceptRequest(p);
                return false;
            }

            if (args[1].equalsIgnoreCase("deny")) {
                LoanUtils.denyRequest(p);
                return false;
            }

            if (args[1].equalsIgnoreCase("cancel")) {
                LoanUtils.cancelRequest(p);
                return false;
            }
        }

        if (LoanUtils.sentRequest(p)) {
            BPMessages.send(p, "Loan-Already-Sent");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null || target.equals(s)) {
            BPMessages.send(s, "Invalid-Player");
            return false;
        }

        if (args.length == 2) {
            BPMessages.send(p, "Specify-Number");
            return false;
        }

        String num = args[2];
        if (BPUtils.isInvalidNumber(num, p)) return false;
        BigDecimal amount = new BigDecimal(num);

        String fromBankName = Values.CONFIG.getMainGuiName();
        if (args.length > 3) fromBankName = args[3];

        BankReader fromReader = new BankReader(fromBankName);
        if (!fromReader.exist()) {
            BPMessages.send(p, "Invalid-Bank");
            return false;
        }
        if (!fromReader.isAvailable(p)) {
            BPMessages.send(p, "Cannot-Access-Bank");
            return false;
        }

        String toBankName = Values.CONFIG.getMainGuiName();
        if (args.length > 4) toBankName = args[4];

        BankReader toReader = new BankReader(toBankName);
        if (!toReader.exist()) {
            BPMessages.send(p, "Invalid-Bank");
            return false;
        }
        if (!toReader.isAvailable(target)) {
            BPMessages.send(p, "Cannot-Access-Bank-Others", "%player%$" + target.getName());
            return false;
        }

        if (!confirm(s)) LoanUtils.sendRequest(p, target, amount, fromBankName, toBankName);
        return true;
    }

    @Override
    public List<String> tabCompletion(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return null;

        Player p = (Player) s;
        Player target = args.length > 1 ? Bukkit.getPlayerExact(args[1]) : null;

        if (args.length == 2) {
            if (LoanUtils.sentRequest(p))
                return BPArgs.getArgs(args, "cancel");

            if (LoanUtils.hasRequest(p))
                return BPArgs.getArgs(args, "accept", "deny");
            return null;
        }

        if (args.length == 3)
            return BPArgs.getArgs(args, "1", "2", "3");

        if (args.length == 4)
            return BPArgs.getArgs(args, new BankReader().getAvailableBanks(p));

        if (args.length == 5)
            return BPArgs.getArgs(args, new BankReader().getAvailableBanks(target));

        return null;
    }
}