# gostak-translator
Translates "Gostakian," the language in Carl Muckenhoupt's 2001 text adventure game The Gostak, into English.

NOTE: The Gostak is a puzzle game, and using this translator will spoil the game for those who wish to play it.
I created this translator as a learning exercise and because I am a fan of Muckenhoupt's creation.
I encourage everyone interested in made-up languages or decoding puzzles to try it out themselves:  
http://ifdb.tads.org/viewgame?id=w5s3sv43s3p98v45

#### Usage:
Run "GostakTranslator.jar" (the Java Runtime Environment must be installed on your system). The "gostak_dictionary.txt" file must be in the same directory as "GostakTranslator.jar". When using the "Translate File" option, target files should be in .txt or .log format.

#### Sample Gostakian Text:
"Finally, here you are. At the delcot of tondam, where doshes deave. But the doshery lutt is crenned with glauds.
Glauds! How rorm it would be to pell back to the bewl and distunk them, distunk the whole delcot, let the drokes
uncren them. But you are the gostak. The gostak distims the doshes. And no glaud will vorl them from you."

#### The Language:
The language in Carl Muckenhoupt's game is not named, but I call it "Gostakian" for ease of reference. It
follows English syntax, and includes many English words: articles, prepositions, being verbs, and certain
others are left in English, but most of the verbs and nouns are completely alien. Many of the objects and
actions being described are also alien, in the strong sense of having no exact analogue on Earth. Because
the game is text-only, most of the challenge is extracting meaning from context clues, and many of the
definitions used in this translator are my own guesses. Furthermore, many Gostakian words are left
unchanged where I felt they had no translation, such as the names of creatures (including the titular
gostaks themselves).

#### Translation:
The input is tokenized using whitespace as a delimiter. Newlines are preserved in the output.

The translator is straightforward and unsophisticated. It makes simple word-to-word conversions with some
considerations for plurals and suffixes, but otherwise does not try to determine the part of speech,
tense, or other linguistic analysis. Word meanings were chosen with an attempt for them to make sense in
most use cases, but for words like "deave" which can mean "exist", "stay", or "location" in various contexts,
translation will occasionally be awkward until I make the parser more sophisticated.

#### Special Thanks:
Special thanks to David Welbourn, whose notes on the game helped fill gaps in my own: http://www.plover.net/~davidw/gostak.html
