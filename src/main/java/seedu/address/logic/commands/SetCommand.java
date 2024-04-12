package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.LastMet;
import seedu.address.model.person.Person;
import seedu.address.storage.SetStorage;

/**
 * Sets the LastMet Overdue time period for all Clients.
 */
public class SetCommand extends Command {
    public static final String COMMAND_WORD = "set";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets the LastMet overdue time period.\n"
            + "Parameters: "
            + "NUMBER_OF_DAYS (must be an integer and be at least 0)\n"
            + "Example: " + COMMAND_WORD + " "
            + "90.";

    public static final String MESSAGE_SUCCESS = "LastMet overdue time period has been set to %1$s days.";
    /**
     * Creates a LastMetCommand to update last mete date of the specified {@code Person}
     */
    private int overdueTimePeriod;

    /**
     * Creates a SetCommand to update last met overdue duration of the LastMet class
     */
    public SetCommand(Integer numberOfDays) {
        requireNonNull(numberOfDays);

        this.overdueTimePeriod = numberOfDays;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        LastMet.setLastMetDuration(this.overdueTimePeriod);
        List<Person> lastShownList = model.getSortedFilteredPersonList();

        int numberOfClients = lastShownList.size();

        for (int i = 0; i < numberOfClients; i++) {
            //Index index = Index.fromZeroBased(i);
            Person personToCheck = lastShownList.get(i);
            personToCheck.getLastMet().checkOverdue();
        }

        SetStorage.setData(this.overdueTimePeriod);
        return new CommandResult(String.format(MESSAGE_SUCCESS, this.overdueTimePeriod));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SetCommand)) {
            return false;
        }

        SetCommand otherSetCommand = (SetCommand) other;
        return overdueTimePeriod == (otherSetCommand.overdueTimePeriod);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("overdueTimePeriod", overdueTimePeriod)
                .toString();
    }
}
