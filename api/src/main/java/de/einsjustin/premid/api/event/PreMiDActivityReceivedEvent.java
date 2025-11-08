package de.einsjustin.premid.api.event;

import de.einsjustin.premid.api.PreMiDActivity;
import net.labymod.api.event.Event;
import java.util.UUID;

public record PreMiDActivityReceivedEvent(UUID uuid, PreMiDActivity activity) implements Event {

}
