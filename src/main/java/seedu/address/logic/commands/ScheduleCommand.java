package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCHEDULE;
import static seedu.address.model.Model.COMPARATOR_SHOW_ORIGINAL_ORDER;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDateTime;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.PastDateException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Schedule;

/**
 * Schedules an appointment with the person.
 */
public class ScheduleCommand extends Command {
    public static final String COMMAND_WORD = "schedule";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Schedules an appointment with the client identified "
            + "by the index number used in the displayed client list.\n"
            + "Parameters: "
            + "INDEX (must be a positive integer) "
            + PREFIX_SCHEDULE + "DATETIME\n"
            + "Example: " + COMMAND_WORD + " "
            + "1 "
            + PREFIX_SCHEDULE + "2001-03-02 12:00 "
            + "which refers to 2 March 2001 12pm";

    public static final String MESSAGE_SUCCESS = "You have scheduled an appointment with %1$s.";
    private final Index index;
    private final Schedule schedule;

    /**
     * Creates a ScheduleCommand to schedule an appointment with the specified {@code Person}
     */
    public ScheduleCommand(Index index, Schedule schedule) {
        requireNonNull(index);
        requireNonNull(schedule);

        this.index = index;
        this.schedule = schedule;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            checkPastDate(this.schedule.getSchedule());
        } catch (PastDateException e) {
            throw new CommandException(Messages.MESSAGE_SCHEDULE_PAST);
        }

        List<Person> lastShownList = model.getSortedFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToMeet = lastShownList.get(index.getZeroBased());
        Person metPerson = new Person(
                personToMeet.getName(), personToMeet.getPhone(), personToMeet.getEmail(),
                personToMeet.getAddress(), personToMeet.getBirthday(), personToMeet.getPriority(),
                personToMeet.getRemark(), personToMeet.getLastMet(), this.schedule, personToMeet.getTags(),
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
        if (!(other instanceof ScheduleCommand)) {
            return false;
        }

        ScheduleCommand otherScheduleCommand = (ScheduleCommand) other;
        return index.equals(otherScheduleCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("dateTime", schedule.getSchedule())
                .toString();
    }

    private void checkPastDate(LocalDateTime dateTime) throws PastDateException {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new PastDateException(Messages.MESSAGE_SCHEDULE_PAST);
        }
    }
}
