# Magic Duels Deck Exporter
A tool to allow importing/exporting decks between Magic Duels and Magic Assistant, to ease the pain of deckbuilding

## Installation Instructions
1. Download and install [Magic Assistant](https://sourceforge.net/projects/mtgbrowser/).  Run it and let it update.  Keep track of where you save the workspace.
2. In Magic Assistant, open the card filter dialog _(looks like three arrows point right)_. Click _'Set filter'_, then make sure the following sets all have icons:
  * _Aether Revolt_
  * _Amonkhet_
  * _Battle for Zendikar_
  * _Eldritch Moon_
  * _Kaladesh_
  * _Magic Origins_
  * _Oath of the Gatewatch_
  * _Shadows over Innistrad_
 
  If any are missing _(as they were for me)_, go to file --> Update Magic Cards Database, and download the missing sets.
3. Download [the latest `Magic.Duels.Deck.Exporter.zip` file](https://github.com/BlueRaja/Magic-Duels-Deck-Exporter/releases/latest) and extract it somewhere.
4. Open `Settings.bat` in a text editor and update the values to match your machine.

## Usage instructions
1. _(Optional)_ Open Magic Duels and create a new deck.  Decks can only be edited within Magic Assistant, not created.
2. _(Optional)_ Backup your Magic Duels profile before running.
3. Run `Import - Duels to Assist.bat`
4. Open Magic Assistant and edit your decks however you'd like them.  The cards you have available are under the "Magic Duels collection" collection.
5. When finished, close Magic Assistant and run `Export - Assist to Duels.bat`

## FAQ

### When I run the .bat files, I get 'java not found'
Make sure you have the [latest version of Java installed](https://java.com/en/download/).  
If that doesn't fix it, make sure [java.exe is in your `PATH` environment variable](http://docs.oracle.com/javase/7/docs/webnotes/install/windows/jdk-installation-windows.html#path).

### When I move a card from my Magic Duels collection to a deck, it disappears from my collection. What gives?
This is just how Magic Assistant works, which is unfortunate for our use-case.

To work around it, either:
* ctrl+click to copy the card while dragging, or
* Rerun the steps 'usage instructions' between editing decks to reimport your card list

### Exporting to Magic Duels gives `MalformedByteSequenceException: Invalid byte 2 of 2-byte UTF-8 sequence`
This is a bug some users have reported when their deck names contain non-English characters.  Removing the characters seems to fix the issue.

I've been unable to reproduce this error, so I can't fix it.  If you encounter this bug, I'd appreciate if you could export your decks from Magic Assistant and post them to the issues-tracker.

---

_Thanks to [spirolone](http://www.slightlymagic.net/forum/viewtopic.php?f=99&t=17931) for his work reverse-engineering the Magic Duels file format_
