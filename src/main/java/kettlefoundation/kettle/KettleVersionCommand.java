package kettlefoundation.kettle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * KettleVersionCommand
 *
 * @author strezz <admin@strezz.org>
 * @since 16/02/2019 - 04:53 PM
 */
public class KettleVersionCommand extends Command {

  protected KettleVersionCommand(String name) {
    super(name);
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    return false;
  }
}
