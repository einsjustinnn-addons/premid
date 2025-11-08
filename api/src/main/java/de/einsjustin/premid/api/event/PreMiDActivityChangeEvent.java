package de.einsjustin.premid.api.event;

import de.einsjustin.premid.api.PreMiDActivity;
import net.labymod.api.event.Event;

public record PreMiDActivityChangeEvent(PreMiDActivity activity) implements Event {

}
