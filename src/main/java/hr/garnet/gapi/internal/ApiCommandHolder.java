package hr.garnet.gapi.internal;

import hr.garnet.gapi.ApiCommand;
import java.util.Objects;

/**
 * @author vedransmid@gmail.com
 */
public class ApiCommandHolder {
  private Class<? extends ApiCommand> commandClass;
  private ApiCommand commandImpl;

  private ApiCommandHolder() {}

  public ApiCommandHolder(Class<? extends ApiCommand> commandClass) {
    this.commandClass = commandClass;
  }

  public ApiCommandHolder(ApiCommand commandImpl) {
    this.commandImpl = commandImpl;
  }

  public boolean containsImplementation() {
    return Objects.nonNull(commandImpl);
  }

  public ApiCommand getCommandImpl() {
    return commandImpl;
  }

  public Class<? extends ApiCommand> getCommandClass() {
    return commandClass;
  }
}
