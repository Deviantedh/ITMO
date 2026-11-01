package commands;
import java.util.Objects;

public abstract class Command {
  private final String name;
  private final String description;

  public Command(String name, String description) {
    this.name = name;
    this.description = description;
  }


  @Override
  public String toString() {
    return "Command: " + getName() + " (" + getDescription() + ").";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Command command = (Command) object;
    return Objects.equals(getName(), command.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public abstract boolean apply(String[] arguments);
}