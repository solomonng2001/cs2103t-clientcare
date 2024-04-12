package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICYID;
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


/**
 * Delete a policy from person.
 */
public class DeletePolicyCommand extends Command {
    public static final String COMMAND_WORD = "deletepolicy";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a policy from the policy list of the client"
            + " identified by the index number used in the displayed client list.\n"
            + "Parameters: "
            + "INDEX (must be a positive integer) "
            + PREFIX_POLICYID + "POLICYID\n"
            + "Example: " + COMMAND_WORD + " "
            + "1 "
            + PREFIX_POLICYID + "123";

    public static final String MESSAGE_SUCCESS = "You have deleted Policy #%1$s from %2$s.";
    private final Index index;
    private final String policyId;

    /**
     * Creates a DeletePolicyCommand to delete a policy from specified {@code Person}
     */
    public DeletePolicyCommand(Index index, String policyId) {
        requireNonNull(index);
        requireNonNull(policyId);

        this.index = index;
        this.policyId = policyId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getSortedFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToDeletePolicy = lastShownList.get(index.getZeroBased());

        // Checks if Policy ID exists
        if (!personToDeletePolicy.hasPolicyID(policyId)) {
            throw new CommandException(Messages.MESSAGE_POLICY_NOT_FOUND);
        }
        PolicyList updatedPolicyList = personToDeletePolicy.getPolicyList();
        updatedPolicyList.deletePolicy(policyId);

        Person policyDeletedPerson = new Person(personToDeletePolicy.getName(), personToDeletePolicy.getPhone(),
                personToDeletePolicy.getEmail(), personToDeletePolicy.getAddress(), personToDeletePolicy.getBirthday(),
                personToDeletePolicy.getPriority(), personToDeletePolicy.getRemark(), personToDeletePolicy.getLastMet(),
                personToDeletePolicy.getSchedule(), personToDeletePolicy.getTags(), updatedPolicyList);

        model.setPerson(personToDeletePolicy, policyDeletedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.setDisplayClient(policyDeletedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, policyId, policyDeletedPerson.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeletePolicyCommand)) {
            return false;
        }

        DeletePolicyCommand otherDeletePolicyCommand = (DeletePolicyCommand) other;
        return index.equals(otherDeletePolicyCommand.index) && policyId.equals(otherDeletePolicyCommand.policyId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("deletePolicy", policyId)
                .toString();
    }
}
