package forestsim

import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.builder.TextImageBuilder
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.api.color.TextColor
import org.codetome.zircon.api.builder.TerminalBuilder as TBuilder
import org.codetome.zircon.api.terminal.Terminal as T

object Terminal {
    private val terminal: T = TBuilder.newBuilder()
            .initialTerminalSize(Size.of(Forest.SIZE * 2, Forest.SIZE))
            .build()

    init {
        terminal.flush()
    }

    fun draw(color: TextColor = ANSITextColor.BLACK, secondary: TextColor = color) {
        drawChar(color)
        drawChar(secondary)
    }

    private fun drawChar(color: TextColor) {
        terminal.draw(TextImageBuilder
                .newBuilder()
                .filler(TextCharacterBuilder.DEFAULT_CHARACTER
                        .withBackgroundColor(color))
                .build(),
                terminal.getCursorPosition())
        terminal.moveCursorForward()
    }

    fun flush() {
        terminal.flush()
        terminal.putCursorAt(Position.TOP_LEFT_CORNER)
    }

    fun close() {
        terminal.close()
    }
}