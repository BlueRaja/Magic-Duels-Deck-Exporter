# Magic Duels Deck Builder
A tool to allow importing/exporting decks between Magic Duels and Magic Assistant, to ease the pain of deckbuilding

## Installation Instructions
1. Download and install [Magic Assistant](https://sourceforge.net/projects/mtgbrowser/).  Run it and let it update.  Keep track of where you save the workspace.
2. In Magic Assistant, open the card filter dialog _(looks like three arrows point right)_. Click _'Set filter'_, then make sure the following sets all have icons:  Battle for Zendikar, Eldritch Moon, Kaladesh, Magic Origins, Oath of the Gatewatch, Shadows over Innistrad.  If any are missing _(as they were for me)_, go to file --> Update Magic Cards Database, and download the missing sets.
3. Download [the latest release of this software](https://github.com/BlueRaja/Magic-Duels-Deck-Builder/releases) and extract it somewhere.
4. Open `Settings.bat` in a text editor and update the values to match your machine.

## Usage instructions
1. _(Optional)_ Open Magic Duels and create a new deck.  Decks can only be edited within Magic Assistant, not created.
2. _(Optional)_ Backup your Magic Duels profile before running.
3. Run `Import - Duels to Assist.bat`
4. Open Magic Assistant and edit your decks however you'd like them.  The cards you have available are under the "Magic Duels collection" collection.
_Note: Due to the way Magic Assistant works, moving cards from "Magic Duels collection" to a deck removes them from your collection.  To work around this, either ctrl+click when dragging cards, or run these steps again before editing a second deck._
5. When finished, close Magic Assistant and run `Export - Assist to Duels.bat`

That's it!