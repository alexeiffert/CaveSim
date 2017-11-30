JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	CaveSim.java \
	ClickPanel.java \
	Cutscene.java \
	Multiplexer.java \
        Menu.java \
        FireMinigame.java \
        PaintMinigame.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
