package com.opencredo.concourse.domain.commands;

import com.opencredo.concourse.data.tuples.Tuple;
import com.opencredo.concourse.domain.common.AggregateId;
import com.opencredo.concourse.domain.common.VersionedName;
import com.opencredo.concourse.domain.time.StreamTimestamp;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The type of a {@link Command}. Used to match commands to {@link com.opencredo.concourse.domain.commands.dispatching.CommandProcessor}s.
 */
public final class CommandType {

    /**
     * Get the {@link CommandType} of the supplied {@link Command}.
     * @param command The {@link Command} to get the type of.
     * @return The {@link Command}'s {@link CommandType}.
     */
    public static CommandType of(Command command) {
        checkNotNull(command, "command must not be null");

        return of(command.getAggregateId().getType(), command.getCommandName());
    }

    /**
     * Create a {@link CommandType} with the given aggregate type and command name.
     * @param aggregateType The aggregate type to which this type of command is addressed.
     * @param commandName The name of the command type.
     * @return The constructed {@link CommandType}.
     */
    public static CommandType of(String aggregateType, VersionedName commandName) {
        checkNotNull(aggregateType, "aggregateType must not be null");
        checkNotNull(commandName, "commandName must not be null");

        return new CommandType(aggregateType, commandName);
    }

    private final String aggregateType;
    private final VersionedName commandName;

    private CommandType(String aggregateType, VersionedName commandName) {
        this.aggregateType = aggregateType;
        this.commandName = commandName;
    }


    @Override
    public boolean equals(Object o) {
        return this == o ||
                (o instanceof CommandType
                        && ((CommandType) o).aggregateType.equals(aggregateType)
                        && ((CommandType) o).commandName.equals(commandName));
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateType, commandName);
    }

    @Override
    public String toString() {
        return aggregateType + "/" + commandName.getFormatted();
    }

    /**
     * Create a {@link Command} of this type, with the supplied properties.
     * @param aggregateId The id of the aggregate to which the command is addressed.
     * @param timestamp The time when the command was issued.
     * @param parameters The command's parameters.
     * @param resultType The {@link Type} returned by processing the command.
     * @return The constructed {@link Command}.
     */
    public Command makeCommand(UUID aggregateId, StreamTimestamp timestamp, Tuple parameters, Type resultType) {
        return Command.of(
                AggregateId.of(aggregateType, aggregateId),
                timestamp,
                commandName,
                parameters,
                resultType);
    }
}
