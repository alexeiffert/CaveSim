JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	CaveSim.java \
	Multiplexer.java \
	Cutscene.java \
        FireMinigame.java \
        Menu.java \
        PaintMinigame.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
