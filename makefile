JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	CaveSim.java \
	Multiplexer.java \
        Menu.java \
	Cutscene.java \
        FireMinigame.java \
        PaintMinigame.java \
        HuntMinigame.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
