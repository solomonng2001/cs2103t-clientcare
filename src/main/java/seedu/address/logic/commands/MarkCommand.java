package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.COMPARATOR_SHOW_ORIGINAL_ORDER;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.ScheduleAlreadyDoneException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Schedule;

/**
 * Marks a schedule with a Client as done.
 */
public class MarkCommand extends Command {
    public static final String COMMAND_WORD = "mark";


    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks the schedule of the client identified "
            + "by the index number used in the displayed client list.\n"
            + "Parameters: "
            + "INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " "
            + "1 ";

    public static final String MESSAGE_SUCCESS = "Your schedule with %1$s has been marked as done.";
    private final Index index;
    /**
     * Creates a MarkCommand to complete the schedule of the specified {@code Person}
     */
    public MarkCommand(Index index) {
        requireNonNull(index);

        this.index = index;
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getSortedFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToMeet = lastShownList.get(index.getZeroBased());

        try {
            checkIsDone(personToMeet.getSchedule());
        } catch (ScheduleAlreadyDoneException e) {
            throw new CommandException(Messages.MESSAGE_SCHEDULE_DONE);
        }

        Person metPerson = new Person(
                personToMeet.getName(), personToMeet.getPhone(), personToMeet.getEmail(),
                personToMeet.getAddress(), personToMeet.getBirthday(), personToMeet.getPriority(),
                personToMeet.getRemark(), personToMeet.getLastMet(), personToMeet.getSchedule(), personToMeet.getTags(),
                personToMeet.getPolicyList());

        metPerson.getSchedule().markIsDone();
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
        if (!(other instanceof MarkCommand)) {
            return false;
        }

        MarkCommand otherMarkCommand = (MarkCommand) other;
        return index.equals(otherMarkCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .toString();
    }

    private void checkIsDone(Schedule schedule) throws ScheduleAlreadyDoneException {
        if (schedule.getIsDone()) {
            throw new ScheduleAlreadyDoneException(Messages.MESSAGE_SCHEDULE_DONE);
        }
    }
}
