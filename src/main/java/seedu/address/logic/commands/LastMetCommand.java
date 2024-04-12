package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LASTMET;
import static seedu.address.model.Model.COMPARATOR_SHOW_ORIGINAL_ORDER;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDate;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.FutureDateException;
import seedu.address.model.Model;
import seedu.address.model.person.LastMet;
import seedu.address.model.person.Person;

/**
 * Updates the last met date of the person.
 */
public class LastMetCommand extends Command {
    public static final String COMMAND_WORD = "met";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates the last met of the client identified "
            + "by the index number used in the displayed client list.\n"
            + "Parameters: "
            + "INDEX (must be a positive integer) "
            + PREFIX_LASTMET + "DATE\n"
            + "Example: " + COMMAND_WORD + " "
            + "1 "
            + PREFIX_LASTMET + "2001-02-03"
            + " which refers to 3 Feb 2001";

    public static final String MESSAGE_SUCCESS = "You have met %1$s.";
    private final Index index;
    private final LastMet lastMet;

    /**
     * Creates a LastMetCommand to update last met date of the specified {@code Person}
     */
    public LastMetCommand(Index index, LastMet lastMet) {
        requireNonNull(index);
        requireNonNull(lastMet);

        this.index = index;
        this.lastMet = lastMet;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            checkFutureDate(this.lastMet.getLastMet());
        } catch (FutureDateException e) {
            throw new CommandException(Messages.MESSAGE_LASTMET_FUTURE);
        }

        List<Person> lastShownList = model.getSortedFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToMeet = lastShownList.get(index.getZeroBased());
        Person metPerson = new Person(
                personToMeet.getName(), personToMeet.getPhone(), personToMeet.getEmail(),
                personToMeet.getAddress(), personToMeet.getBirthday(), personToMeet.getPriority(),
                personToMeet.getRemark(), this.lastMet, personToMeet.getSchedule(), personToMeet.getTags(),
                personToMeet.getPolicyList());

        model.setPerson(personToMeet, metPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.setDisplayClient(metPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, personToMeet.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LastMetCommand)) {
            return false;
        }

        LastMetCommand otherMetCommand = (LastMetCommand) other;
        return index.equals(otherMetCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("date", lastMet.getLastMet())
                .toString();
    }

    private void checkFutureDate(LocalDate date) throws FutureDateException {
        if (date.isAfter(LocalDate.now())) {
            throw new FutureDateException(Messages.MESSAGE_LASTMET_FUTURE);
        }
    }
}
