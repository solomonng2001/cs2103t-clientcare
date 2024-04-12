package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICYID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICYNAME;
import static seedu.address.model.Model.COMPARATOR_SHOW_ORIGINAL_ORDER;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PolicyList;
import seedu.address.model.policy.Policy;


/**
 * Adds a policy to person.
 */
public class AddPolicyCommand extends Command {
    public static final String COMMAND_WORD = "addpolicy";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a new policy to the policy list of the client"
            + " identified by the index number used in the displayed client list.\n"
            + "Parameters: "
            + "INDEX (must be a positive integer) "
            + PREFIX_POLICYNAME + "POLICYNAME "
            + PREFIX_POLICYID + "POLICYID\n"
            + "Example: " + COMMAND_WORD + " "
            + "1 "
            + PREFIX_POLICYNAME + "Travel Bali "
            + PREFIX_POLICYID + "123";

    public static final String MESSAGE_SUCCESS = "You have added a policy to %1$s.";
    private final Index index;
    private final Policy policy;

    /**
     * Creates a AddPolicyCommand to add a policy to specified {@code Person}
     */
    public AddPolicyCommand(Index index, Policy policy) {
        requireNonNull(index);
        requireNonNull(policy);

        this.index = index;
        this.policy = policy;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getSortedFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToAddPolicy = lastShownList.get(index.getZeroBased());

        // Checks for conflicting Policy ID here
        if (personToAddPolicy.isConflictingPolicyId(policy)) {
            throw new CommandException(Messages.MESSAGE_DUPLICATE_POLICY);
        }
        PolicyList updatedPolicyList = personToAddPolicy.getPolicyList();
        updatedPolicyList.addPolicy(policy);

        Person policyAddedPerson = new Person(personToAddPolicy.getName(), personToAddPolicy.getPhone(),
                personToAddPolicy.getEmail(), personToAddPolicy.getAddress(), personToAddPolicy.getBirthday(),
                personToAddPolicy.getPriority(), personToAddPolicy.getRemark(), personToAddPolicy.getLastMet(),
                personToAddPolicy.getSchedule(), personToAddPolicy.getTags(), updatedPolicyList);

        model.setPerson(personToAddPolicy, policyAddedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.setDisplayClient(policyAddedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, personToAddPolicy.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddPolicyCommand)) {
            return false;
        }

        AddPolicyCommand otherAddPolicyCommand = (AddPolicyCommand) other;
        return index.equals(otherAddPolicyCommand.index) && policy.equals(otherAddPolicyCommand.policy);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("addPolicy", policy)
                .toString();
    }
}
